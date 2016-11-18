package com.drumichiro.body_tapping_phone;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MathAuxUnitTest {

    static final private int ARRAY_LENGTH = 1024;

    @Test
    public void argmaxTest() throws Exception {
        double[] value = new double[ARRAY_LENGTH];
        Arrays.fill(value, 0.0);
        Assert.assertEquals(0, MathAux.argmax(value));
        value[0] = 10.0;
        Assert.assertEquals(0, MathAux.argmax(value));
        value[1] = 20.0;
        Assert.assertEquals(1, MathAux.argmax(value));
        value[ARRAY_LENGTH - 1] = 30.0;
        Assert.assertEquals(ARRAY_LENGTH - 1, MathAux.argmax(value));
        value[0] = 20.0;
        Assert.assertEquals(ARRAY_LENGTH - 1, MathAux.argmax(value));

        Arrays.fill(value, -1.0);
        Assert.assertEquals(0, MathAux.argmax(value));
        value[0] = 10.0;
        Assert.assertEquals(0, MathAux.argmax(value));
        value[1] = 20.0;
        Assert.assertEquals(1, MathAux.argmax(value));
        value[ARRAY_LENGTH - 1] = 30.0;
        Assert.assertEquals(ARRAY_LENGTH - 1, MathAux.argmax(value));
        value[0] = 20.0;
        Assert.assertEquals(ARRAY_LENGTH - 1, MathAux.argmax(value));

        Arrays.fill(value, 1.0);
        Assert.assertEquals(0, MathAux.argmax(value));
        value[0] = 10.0;
        Assert.assertEquals(0, MathAux.argmax(value));
        value[1] = 20.0;
        Assert.assertEquals(1, MathAux.argmax(value));
        value[ARRAY_LENGTH - 1] = 30.0;
        Assert.assertEquals(ARRAY_LENGTH - 1, MathAux.argmax(value));
        value[0] = 20.0;
        Assert.assertEquals(ARRAY_LENGTH - 1, MathAux.argmax(value));
    }
}
