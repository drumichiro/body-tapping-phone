package com.drumichiro.body_tapping_phone;

import junit.framework.Assert;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class FeatureExtractorUnitTest {

    static final private int GENERAL_BUFFERS = 20;
    static final private int GENERAL_OBSERVATIONS = 3;
    static final private double MARGIN = 0.001;

    @Test
    public void boundaryArgument() throws Exception {
        new FeatureExtractor(1, GENERAL_OBSERVATIONS);
        new FeatureExtractor(GENERAL_BUFFERS, 1);
        // Possible argument is input for upper boundary testing to consider memory allocation.
        new FeatureExtractor(65536, GENERAL_OBSERVATIONS);
        new FeatureExtractor(GENERAL_BUFFERS, 65536);
        FeatureExtractor extractor = new FeatureExtractor(GENERAL_BUFFERS, GENERAL_OBSERVATIONS);
        int features = extractor.getFeatures();
        extractor.extractFeature(new double[features]);
        extractor.extractFeature(new double[features + 1], 1);
        extractor.extractFeature(new double[features + 65536], 65536);
    }

    @Test
    public void invalidArgument() throws Exception {
        try {
            new FeatureExtractor(0, GENERAL_OBSERVATIONS);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The argument, buffers, is not positive.", e.getMessage());
        }
        try {
            new FeatureExtractor(GENERAL_BUFFERS, 0);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The argument, observations, is not positive.", e.getMessage());
        }
        try {
            new FeatureExtractor(GENERAL_BUFFERS, Integer.MAX_VALUE / 2 + 1);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The argument, observations, is too large.", e.getMessage());
        }

        FeatureExtractor extractor = new FeatureExtractor(GENERAL_BUFFERS, GENERAL_OBSERVATIONS);
        int features = extractor.getFeatures();
        try {
            extractor.addObservation(null);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The argument, observation, is nullptr.", e.getMessage());
        }
        try {
            extractor.addObservation(new double[GENERAL_OBSERVATIONS - 1]);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The length of observation is different from initial length.", e.getMessage());
        }
        try {
            extractor.addObservation(new double[GENERAL_OBSERVATIONS + 1]);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The length of observation is different from initial length.", e.getMessage());
        }
        try {
            extractor.extractFeature(null);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The argument, feature, is nullptr.", e.getMessage());
        }
        try {
            extractor.extractFeature(new double[0]);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The ending of feature is excess.", e.getMessage());
        }
        try {
            extractor.extractFeature(new double[0], extractor.getFeatures() + 1);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("The ending of feature is excess.", e.getMessage());
        }
        try {
            extractor.extractFeature(new double[features]);
            extractor.extractFeature(new double[features], -1);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals("Offset is negative.", e.getMessage());
        }
    }

    @Test
    public void simpleFeatureExtraction() throws Exception {
        int observations = 4;
        FeatureExtractor extractor = new FeatureExtractor(GENERAL_BUFFERS, observations);
        double[] feature = new double[observations * 2];
        for (int i1=0; i1<GENERAL_BUFFERS; ++i1) {
            // Check feature value before adding observation.
            extractor.extractFeature(feature);
            for (double feat : feature) {
                Assert.assertTrue(0.0 <= feat);
            }
            // Zero, plus, minus, alternately...
            extractor.addObservation(new double[] {0.0, 1.5678, -0.1234, -1.0});
            extractor.addObservation(new double[] {0.0, 0.4322, -1.8766, 1.0});
        }
        extractor.extractFeature(feature);
        Assert.assertEquals(0.0, feature[0]);
        Assert.assertTrue(GENERAL_BUFFERS - MARGIN < feature[1]);
        Assert.assertTrue(GENERAL_BUFFERS + MARGIN > feature[1]);
        Assert.assertEquals(0.0, feature[2]);
        Assert.assertEquals(0.0, feature[3]);
        Assert.assertEquals(0.0, feature[observations]);
        Assert.assertEquals(0.0, feature[observations + 1]);
        Assert.assertTrue(GENERAL_BUFFERS - MARGIN < feature[observations + 2]);
        Assert.assertTrue(GENERAL_BUFFERS + MARGIN > feature[observations + 2]);
        Assert.assertEquals(0.0, feature[observations + 3]);

        // Add value more.
        for (int i1=0; i1<GENERAL_BUFFERS; ++i1) {
            // Check feature value before adding observation.
            extractor.extractFeature(feature);
            for (double feat : feature) {
                Assert.assertTrue(0.0 <= feat);
            }
            // Zero, plus, minus, alternately...
            extractor.addObservation(new double[] {0.0, 0.1234, -1.5678, -1.0});
            extractor.addObservation(new double[] {0.0, 1.8766, -0.4322, 1.0});
        }
        // The result is not changed.
        extractor.extractFeature(feature);
        Assert.assertEquals(0.0, feature[0]);
        Assert.assertTrue(GENERAL_BUFFERS - MARGIN < feature[1]);
        Assert.assertTrue(GENERAL_BUFFERS + MARGIN > feature[1]);
        Assert.assertEquals(0.0, feature[2]);
        Assert.assertEquals(0.0, feature[3]);
        Assert.assertEquals(0.0, feature[observations]);
        Assert.assertEquals(0.0, feature[observations + 1]);
        Assert.assertTrue(GENERAL_BUFFERS - MARGIN < feature[observations + 2]);
        Assert.assertTrue(GENERAL_BUFFERS + MARGIN > feature[observations + 2]);
        Assert.assertEquals(0.0, feature[observations + 3]);

        double weight = 1234.0;
        extractor.setWeight(weight);
        extractor.extractFeature(feature);
        Assert.assertEquals(0.0, feature[0]);
        Assert.assertTrue(GENERAL_BUFFERS * weight - MARGIN < feature[1]);
        Assert.assertTrue(GENERAL_BUFFERS * weight + MARGIN > feature[1]);
        Assert.assertEquals(0.0, feature[2]);
        Assert.assertEquals(0.0, feature[3]);
        Assert.assertEquals(0.0, feature[observations]);
        Assert.assertEquals(0.0, feature[observations + 1]);
        Assert.assertTrue(GENERAL_BUFFERS * weight - MARGIN < feature[observations + 2]);
        Assert.assertTrue(GENERAL_BUFFERS * weight + MARGIN > feature[observations + 2]);
        Assert.assertEquals(0.0, feature[observations + 3]);
    }
}
