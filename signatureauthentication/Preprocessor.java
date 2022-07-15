/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrei.signatureauthentication;

import java.util.Arrays;
import java.util.stream.IntStream;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.MathArrays;
import java8.util.J8Arrays;

/**
 *
 * @author Andrei
 */
public class Preprocessor {

    public Signature concatenateStrokes(Signature signature) {
        Signature s = signature.copy();
        int[] indices = MathArraysCont.IntStreamRange(s.getLength()).toArray();
        Boolean[] is_end = J8Arrays.stream(s.getPenDown()).mapToObj(si -> si == 0).toArray(Boolean[]::new);
        int[] ends = J8Arrays.stream(indices).filter(i -> is_end[i] == true).toArray();
        ArrayRealVector x = s.getX(), y = s.getY();
        double xd, yd;

        for (int i = 1; i < ends.length; i++) {
            int k = ends[i];
            xd = -x.getEntry(k) + x.getEntry(k - 1);
            yd = -y.getEntry(k) + y.getEntry(k - 1);
            x.setSubVector(k, x.getSubVector(k, x.getDimension() - k)
                    .mapAddToSelf(xd));
            y.setSubVector(k, y.getSubVector(k, y.getDimension() - k)
                    .mapAddToSelf(yd));
        }
        s.setX(new ArrayRealVector(MathArraysCont.removeAt(x.toArray(), ends)));
        s.setY(new ArrayRealVector(MathArraysCont.removeAt(y.toArray(), ends)));
        s.setLength(s.getLength() - ends.length);
        return s;
    }

    public Signature normalizePosition(Signature signature) {
        return GeometricTransformer.translate(signature,
                -signature.getX().getMinValue() + 1,
                -signature.getY().getMinValue() + 1);
    }

    public Signature normalizeRotation(Signature signature) {
        Signature s = signature.copy();
        ArrayRealVector x = s.getX(), y = s.getY();

        double dx = x.getEntry(s.getLength() - 1) - x.getEntry(0);
        double dy = y.getEntry(s.getLength() - 1) - y.getEntry(0);
        double slope = dy / dx;
        double tanVal = slope;
        double angleRad = Math.atan(tanVal);

        if (dx < 0) {
            angleRad = angleRad - Math.PI;
        }
        return GeometricTransformer.rotate(s, angleRad);
    }

    public Signature normalizeScale(Signature signature) {
        double scaleFactor = new ArrayRealVector(new double[]{signature.getX().getMaxValue(),
            signature.getY().getMaxValue()}).getMaxValue();
        return GeometricTransformer.scale(signature, 1 / scaleFactor, 1 / scaleFactor);
    }

    public Signature normalize(Signature signature) {
        return normalizePosition(
                concatenateStrokes(
                        normalizeScale(
                                normalizeRotation(signature))));

    }

    public Signature resample(Signature signature, int new_length) {
        Signature s = signature.copy();
        ArrayRealVector x = s.getX(), y = s.getY();
        int length = s.getLength();
        UnivariateInterpolator interpolator = new SplineInterpolator();
        double[] new_range = MathArraysCont.linspace(0, length - 1, new_length);
        double[] old_range = J8Arrays.stream(MathArrays.natural(length))
                .mapToDouble(r -> r).toArray();

        UnivariateFunction interp_funX = interpolator
                .interpolate(old_range, x.toArray());
       s.setX(new ArrayRealVector(J8Arrays.stream(new_range).map(v -> interp_funX.value(v)).toArray()));

        UnivariateFunction interp_funY = interpolator
                .interpolate(old_range, y.toArray());
        s.setY(new ArrayRealVector(J8Arrays.stream(new_range).map(v -> interp_funY.value(v)).toArray()));
        s.setLength( new_length);

        return s;
    }

}
