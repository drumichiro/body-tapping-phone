package com.drumichiro.body_tapping_phone;

/**
 * Created by drumichiro on 2016/11/03.
 */
public class NmfWithEuclid extends NmfBase {

    public NmfWithEuclid(int templates, int features) {
        super(templates, features);
    }

    protected double[] updateWeight(
            double[] weight,
            final double[][] template,
            final double[] construction,
            final double[] observation) {
        for (int i1=0; i1<templates; ++i1) {
            double upper = 0.0;
            double lower = EPS;
            for (int i2=0; i2<features; ++i2) {
                upper += observation[i2] * template[i1][i2];
            }
            for (int i2=0; i2<features; ++i2) {
                lower += construction[i2] * template[i1][i2];
            }
            weight[i1] *= upper / lower;
        }
        return weight;
    }

    protected double[][] updateTemplate(
            double[][] template,
            final double[] weight,
            final double[] construction,
            final double[] observation) {
        for (int i1=0; i1<templates; ++i1) {
            for (int i2=0; i2<features; ++i2) {
                double upper = observation[i2];
                double lower = construction[i2] + EPS;
                template[i1][i2] *= upper / lower;
            }
        }
        return template;
    }
}
