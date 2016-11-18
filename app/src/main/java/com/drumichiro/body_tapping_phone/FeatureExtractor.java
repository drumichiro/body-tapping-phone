package com.drumichiro.body_tapping_phone;

import junit.framework.Assert;

import java.util.Arrays;

/**
 * Created by drumichiro on 2016/11/12.
 */
public class FeatureExtractor {

    private final int features;
    private final int observations;
    private int position;
    private double weight;
    private double[][] buffer;

    public FeatureExtractor(int buffers, int observations) {
        Assert.assertTrue("The argument, observations, is not positive.", 0 < observations);
        this.observations = observations;
        features = observations * 2;
        Assert.assertTrue("The argument, observations, is too large.", 0 < features);
        Assert.assertTrue("The argument, buffers, is not positive.", 0 < buffers);
        buffer = new double[buffers][observations];
        position = 0;
        weight = 1.0;
    }

    public int getFeatures() {
        return features;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void addObservation(double[] observation) {
        double[] targetBuffer = buffer[position];
        Assert.assertNotNull("The argument, observation, is nullptr.", observation);
        Assert.assertTrue("The length of observation is different from initial length.",
                targetBuffer.length == observation.length);
        System.arraycopy(observation, 0, targetBuffer, 0, observation.length);
        position = (position + 1) % buffer.length;
    }

    public void extractFeature(double[] feature) {
        extractFeature(feature, 0);
    }

    public void extractFeature(double[] feature, int offset) {
        Assert.assertNotNull("The argument, feature, is nullptr.", feature);
        Assert.assertTrue("Offset is negative.", 0 <= offset);
        int boundary = observations + offset;
        Assert.assertTrue("The ending of feature is excess.",
                boundary + observations <= feature.length);
        Arrays.fill(feature, offset, boundary, 0.0);
        for (int i1=0; i1<buffer.length; ++i1) {
            for (int i2=0; i2<observations; ++i2) {
                feature[i2 + offset] += buffer[i1][i2];
            }
        }

        // Assign negative values as a positive feature value.
        for (int i1=0; i1<observations; ++i1) {
            int positiveIndex = i1 + offset;
            int negativeIndex = positiveIndex + observations;
            feature[positiveIndex] *= weight;
            if (0.0 > feature[positiveIndex]) {
                feature[negativeIndex] = - feature[positiveIndex];
                // Clear positive value.
                feature[positiveIndex] = 0.0;
            }
            else {
                // Clear negative value feature.
                feature[negativeIndex] = 0.0;
            }
        }
    }
}
