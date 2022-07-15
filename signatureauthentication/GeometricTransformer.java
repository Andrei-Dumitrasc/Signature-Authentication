/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrei.signatureauthentication;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

/**
 *
 * @author Andrei
 */
public class GeometricTransformer {

    public static Signature translate(Signature s, double x, double y) {
        Signature localSign = s.copy();
        localSign.getX().mapAddToSelf(x);
        localSign.getY().mapAddToSelf(y);
        return localSign;
    }

    public static Signature rotate(Signature s, double radians) {
        Signature localSign = s.copy();
        Array2DRowRealMatrix coords = new Array2DRowRealMatrix(localSign.getLength(), 2);
        coords.setColumnVector(0, localSign.getX());
        coords.setColumnVector(1, localSign.getY());
        double[][] rotationMatrix = {{Math.cos(radians), -Math.sin(radians)},
        {Math.sin(radians), Math.cos(radians)}};

        coords = coords.multiply(new Array2DRowRealMatrix(rotationMatrix));
        localSign.setX(new ArrayRealVector(coords.getColumn(0)));
        localSign.setY(new ArrayRealVector(coords.getColumn(1)));

        return localSign;
    }

    public static Signature scale(Signature s, double x, double y) {
        Signature localSign = s.copy();
        localSign.getX().mapMultiplyToSelf(x);
        localSign.getY().mapMultiplyToSelf(y);
        return localSign;
    }
}
