package com.drumichiro.body_tapping_phone;

import junit.framework.Assert;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class NmfUnitTest {

    static final private double MARGIN = 0.001;

    private void setValue(double[][] template, double[] observation) {
        template[0][0] = 0.0;
        template[0][1] = 1.0;
        template[0][2] = 3.0;
        template[0][3] = 1.0;
        template[1][0] = 3.0;
        template[1][1] = 0.0;
        template[1][2] = 0.0;
        template[1][3] = 0.0;

        observation[0] = 6.0;
        observation[1] = 3.0;
        observation[2] = 9.0;
        observation[3] = 3.0;
    }

    private double[] reconstruct(double[] weight, double[][] template) {
        int features = template[0].length;
        double[] construction = new double[features];
        for (int i1=0; i1<weight.length; ++i1) {
            for (int i2=0; i2<features; ++i2) {
                construction[i2] += weight[i1] * template[i1][i2];
            }
        }
        return construction;
    }

    @Test
    public void simpleFactorization() throws Exception {
        int templates = 2;
        int features = 4;

        NmfWithEuclid nmf = new NmfWithEuclid(templates, features);
        double[][] template = nmf.getTemplate();
        double[] observation = new double[features];
        setValue(template, observation);

        nmf.factorize(observation, 10, true);
        double[] weight = nmf.getWeight();

        Assert.assertTrue(3.0 - MARGIN < weight[0]);
        Assert.assertTrue(weight[0] < 3.0 + MARGIN);
        Assert.assertTrue(2.0 - MARGIN < weight[1]);
        Assert.assertTrue(weight[1] < 2.0 + MARGIN);

        double[] construction = reconstruct(weight, template);
        for (int i1=0; i1<features; ++i1) {
            Assert.assertTrue(observation[i1] - MARGIN < construction[i1]);
            Assert.assertTrue(construction[i1] < observation[i1] + MARGIN);
        }
    }

    @Test
    public void semiBlindFactorization() throws Exception {
        int templates = 2;
        int features = 4;

        NmfWithEuclid nmf = new NmfWithEuclid(templates, features);
        double[][] template = nmf.getTemplate();
        double[] observation = new double[features];
        setValue(template, observation);

        nmf.factorize(observation, 10, false);
        double[] weight = nmf.getWeight();

        Assert.assertTrue(3.0 - MARGIN < weight[0]);
        Assert.assertTrue(weight[0] < 3.0 + MARGIN);
        Assert.assertTrue(2.0 - MARGIN < weight[1]);
        Assert.assertTrue(weight[1] < 2.0 + MARGIN);

        double[] construction = reconstruct(weight, template);
        for (int i1=0; i1<features; ++i1) {
            Assert.assertTrue(observation[i1] - MARGIN < construction[i1]);
            Assert.assertTrue(construction[i1] < observation[i1] + MARGIN);
        }
    }

    @Test
    public void zeroFactorization() throws Exception {
        int templates = 1000;
        int features = 1000;

        NmfWithEuclid nmf = new NmfWithEuclid(templates, features);
        double[][] template = nmf.getTemplate();
        double[] observation = new double[features];

        nmf.factorize(observation, 100, true);
        double[] weight = nmf.getWeight();

        for (int i1=0; i1<templates; ++i1) {
            Assert.assertTrue(0.0 <= weight[i1]);
            Assert.assertTrue(weight[i1] < MARGIN);
            for (int i2=0; i2<features; ++i2) {
                Assert.assertTrue(0.0 <= template[i1][i2]);
                Assert.assertTrue(template[i1][i2] < MARGIN);
            }
        }
    }

    @Test
    public void invalidValueHandling() throws Exception {
        try {
            NmfBase nmf = new NmfWithEuclid(0, 1);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "# of templates is less than zero.");
        }
        try {
            NmfBase nmf = new NmfWithEuclid(-1, 1);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "# of templates is less than zero.");
        }
        try {
            NmfBase nmf = new NmfWithEuclid(1, 0);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "# of features is less than zero.");
        }
        try {
            NmfBase nmf = new NmfWithEuclid(1, -1);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "# of features is less than zero.");
        }
        try {
            NmfBase nmf = new NmfWithEuclid(1, 1);
            nmf.factorize(null, 1, false);
            throw new Exception();
        }
        catch (NullPointerException e) {
        }
        try {
            NmfBase nmf = new NmfWithEuclid(1, 1);
            nmf.factorize(new double[1], 0, false);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "# of iterations is less than zero.");
        }
        try {
            NmfBase nmf = new NmfWithEuclid(1, 1);
            nmf.factorize(new double[1], -1, false);
            throw new Exception();
        }
        catch (AssertionError e) {
            Assert.assertEquals(e.getMessage(), "# of iterations is less than zero.");
        }
    }
}
