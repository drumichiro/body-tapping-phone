package com.drumichiro.body_tapping_phone;

import junit.framework.Assert;

import java.util.Arrays;

/**
 * Created by drumichiro on 2016/11/03.
 */
public abstract class NmfBase {
    protected final static double EPS = 0.0000000001;
    protected int templates;
    protected int features;
    private double[] construction;
    private double[] weight;
    private double[][] template;

    //////////////////////////////////////
    protected abstract double[] updateWeight(
            double[] weight,
            final double[][] template,
            final double[] construction,
            final double[] observation);

    protected abstract double[][] updateTemplate(
            double[][] template,
            final double[] weight,
            final double[] construction,
            final double[] observation);

    //////////////////////////////////////
    public NmfBase(int templates, int features) {
        Assert.assertTrue("# of templates is less than zero.", 0 < templates);
        Assert.assertTrue("# of features is less than zero.", 0 < features);
        this.templates  = templates;
        this.features = features;
        weight = new double[templates];
        template = new double[templates][features];
        construction = new double[features];
        Arrays.fill(weight, 1.0 / templates);
    }

    private double[] reconstruct(
            double[] construction,
            final double[] weight,
            final double[][] model) {
        Arrays.fill(construction, 0.0);
        for (int i1=0; i1<templates; ++i1) {
            for (int i2 = 0; i2< features; ++i2) {
                construction[i2] += weight[i1] * model[i1][i2];
            }
        }
        return construction;
    }

    public void factorize(double[] observation, int iterations, boolean isUpdatingTemplate) {
        Assert.assertTrue("# of iterations is less than zero.", 0 < iterations);
        Arrays.fill(weight, 1.0 / templates);
        for (int i1=0; i1<iterations; ++i1) {
            construction = reconstruct(construction, weight, template);
            weight = updateWeight(weight, template, construction, observation);
            if (isUpdatingTemplate) {
                construction = reconstruct(construction, weight, template);
                template = updateTemplate(template, weight, construction, observation);
            }
        }
    }

    public double[] getWeight() {
        return weight;
    }

    public double[][] getTemplate() {
        return template;
    }
}
