package com.findata.api.service;

import com.findata.api.model.dto.StockPrediction;
import com.findata.api.model.entity.PriceHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionService {

    private final PriceHistoryService priceHistoryService;

    private static final int MIN_HISTORY_DAYS = 60;
    private static final int LAG_DAYS = 5;
    private static final int PREDICTION_HORIZON = 5;
    private static final double TRAIN_TEST_SPLIT = 0.8;
    private static final int FEATURE_WINDOW = 20;

    public StockPrediction predictPrices(String ticker) {
        log.info("Generating price predictions for ticker: {}", ticker);

        List<PriceHistory> prices = priceHistoryService.getPriceHistory(ticker);

        if (prices.size() < MIN_HISTORY_DAYS) {
            log.warn("Insufficient data for prediction. Need {} days, have {}",
                    MIN_HISTORY_DAYS, prices.size());
            throw new IllegalArgumentException(
                    String.format("Need at least %d days of data for prediction", MIN_HISTORY_DAYS));
        }

        TrainingData trainingData = prepareTrainingData(prices);
        TrainTestSplit split = splitTrainTest(trainingData);
        OLSMultipleLinearRegression model = trainModel(split.trainFeatures, split.trainTargets);

        StockPrediction.ModelMetrics metrics = evaluateModel(
                model, split.testFeatures, split.testTargets, split.trainSize, split.testSize);

        log.info("Model trained - RMSE: {}, MAE: {}, R^2: {}",
                metrics.getRmse(), metrics.getMae(), metrics.getRSquared());

        List<StockPrediction.DailyPrediction> predictions =
                generateFuturePredictions(model, prices, metrics.getRmse());

        return StockPrediction.builder()
                .ticker(ticker)
                .modelType("ridge_regression")
                .predictionDate(LocalDate.now())
                .predictions(predictions)
                .metrics(metrics)
                .build();
    }

    private TrainingData prepareTrainingData(List<PriceHistory> prices) {
        List<double[]> features = new ArrayList<>();
        List<Double> targets = new ArrayList<>();

        int maxIndex = prices.size() - FEATURE_WINDOW;

        for (int i = 1; i < maxIndex; i++) {
            double[] featureVector = createFeatureVector(prices, i);
            double target = prices.get(i - 1).getClose().doubleValue();

            features.add(featureVector);
            targets.add(target);
        }

        log.debug("Prepared {} training examples from {} days of price data",
                features.size(), prices.size());

        return new TrainingData(features, targets);
    }

    private double[] createFeatureVector(List<PriceHistory> prices, int index) {
        double[] features = new double[8];

        for (int lag = 0; lag < LAG_DAYS; lag++) {
            features[lag] = prices.get(index + lag).getClose().doubleValue();
        }

        features[5] = calculateSimpleMA(prices, index, 10);
        features[6] = calculateSimpleMA(prices, index, 20);
        features[7] = calculateVolatility(prices, index, 5);

        return features;
    }

    private double calculateSimpleMA(List<PriceHistory> prices, int index, int window) {
        double sum = 0.0;
        for (int i = 0; i < window; i++) {
            sum += prices.get(index + i).getClose().doubleValue();
        }
        return sum / window;
    }

    private double calculateVolatility(List<PriceHistory> prices, int index, int window) {
        List<Double> returns = new ArrayList<>();

        for (int i = 0; i < window - 1; i++) {
            double today = prices.get(index + i).getClose().doubleValue();
            double yesterday = prices.get(index + i + 1).getClose().doubleValue();
            double dailyReturn = (today - yesterday) / yesterday;
            returns.add(dailyReturn);
        }

        double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double variance = returns.stream()
                .mapToDouble(r -> Math.pow(r - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    private TrainTestSplit splitTrainTest(TrainingData data) {
        int totalSize = data.features.size();
        int trainSize = (int) (totalSize * TRAIN_TEST_SPLIT);

        double[][] trainFeatures = data.features.subList(0, trainSize).toArray(double[][]::new);
        double[] trainTargets = data.targets.subList(0, trainSize).stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double[][] testFeatures = data.features.subList(trainSize, totalSize).toArray(double[][]::new);
        double[] testTargets = data.targets.subList(trainSize, totalSize).stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        log.debug("Train/test split - Train: {} samples, Test: {} samples",
                trainSize, totalSize - trainSize);

        return new TrainTestSplit(trainFeatures, trainTargets, testFeatures, testTargets,
                trainSize, totalSize - trainSize);
    }

    private OLSMultipleLinearRegression trainModel(double[][] features, double[] targets) {
        OLSMultipleLinearRegression model = new OLSMultipleLinearRegression();
        model.newSampleData(targets, features);

        double[] coefficients = model.estimateRegressionParameters();
        log.debug("Model trained with {} features. Intercept: {}",
                features[0].length, coefficients[0]);

        return model;
    }

    private StockPrediction.ModelMetrics evaluateModel(
            OLSMultipleLinearRegression model,
            double[][] testFeatures,
            double[] testTargets,
            int trainSize,
            int testSize) {

        double[] predictions = new double[testTargets.length];

        for (int i = 0; i < testFeatures.length; i++) {
            predictions[i] = predict(model, testFeatures[i]);
        }

        double rmse = calculateRMSE(predictions, testTargets);
        double mae = calculateMAE(predictions, testTargets);
        double rSquared = calculateRSquared(predictions, testTargets);

        return StockPrediction.ModelMetrics.builder()
                .rmse(BigDecimal.valueOf(rmse).setScale(2, RoundingMode.HALF_UP))
                .mae(BigDecimal.valueOf(mae).setScale(2, RoundingMode.HALF_UP))
                .rSquared(BigDecimal.valueOf(rSquared).setScale(4, RoundingMode.HALF_UP))
                .trainSize(trainSize)
                .testSize(testSize)
                .build();
    }

    private double predict(OLSMultipleLinearRegression model, double[] features) {
        double[] params = model.estimateRegressionParameters();

        double prediction = params[0];
        for (int i = 0; i < features.length; i++) {
            prediction += params[i + 1] * features[i];
        }

        return prediction;
    }

    private double calculateRMSE(double[] predictions, double[] actuals) {
        double sumSquaredError = 0.0;
        for (int i = 0; i < predictions.length; i++) {
            double error = predictions[i] - actuals[i];
            sumSquaredError += error * error;
        }
        return Math.sqrt(sumSquaredError / predictions.length);
    }

    private double calculateMAE(double[] predictions, double[] actuals) {
        double sumAbsoluteError = 0.0;
        for (int i = 0; i < predictions.length; i++) {
            sumAbsoluteError += Math.abs(predictions[i] - actuals[i]);
        }
        return sumAbsoluteError / predictions.length;
    }

    private double calculateRSquared(double[] predictions, double[] actuals) {
        double mean = 0.0;
        for (double actual : actuals) {
            mean += actual;
        }
        mean /= actuals.length;

        double ssRes = 0.0;
        double ssTot = 0.0;
        for (int i = 0; i < predictions.length; i++) {
            double residual = actuals[i] - predictions[i];
            double deviation = actuals[i] - mean;
            ssRes += residual * residual;
            ssTot += deviation * deviation;
        }

        return 1.0 - (ssRes / ssTot);
    }

    private List<StockPrediction.DailyPrediction> generateFuturePredictions(
            OLSMultipleLinearRegression model,
            List<PriceHistory> prices,
            BigDecimal rmse) {

        List<StockPrediction.DailyPrediction> predictions = new ArrayList<>();
        List<PriceHistory> extendedPrices = new ArrayList<>(prices);

        LocalDate lastDate = prices.get(0).getDate();

        for (int day = 1; day <= PREDICTION_HORIZON; day++) {
            double[] features = createFeatureVector(extendedPrices, 0);
            double predictedPrice = predict(model, features);

            BigDecimal predicted = BigDecimal.valueOf(predictedPrice)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal confidenceMargin = rmse.multiply(BigDecimal.valueOf(2.0));

            LocalDate predictionDate = lastDate.plusDays(day);

            predictions.add(StockPrediction.DailyPrediction.builder()
                    .date(predictionDate)
                    .predictedPrice(predicted)
                    .confidenceLower(predicted.subtract(confidenceMargin)
                            .setScale(2, RoundingMode.HALF_UP))
                    .confidenceUpper(predicted.add(confidenceMargin)
                            .setScale(2, RoundingMode.HALF_UP))
                    .build());

            PriceHistory syntheticPrice = PriceHistory.builder()
                    .ticker(prices.get(0).getTicker())
                    .date(predictionDate)
                    .close(predicted)
                    .build();
            extendedPrices.add(0, syntheticPrice);
        }

        return predictions;
    }

    private record TrainingData(List<double[]> features, List<Double> targets) {}

    private record TrainTestSplit(
            double[][] trainFeatures,
            double[] trainTargets,
            double[][] testFeatures,
            double[] testTargets,
            int trainSize,
            int testSize
    ) {}
}
