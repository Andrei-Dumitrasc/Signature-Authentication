/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrei.signatureauthentication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import java8.util.J8Arrays;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.MathArrays;

/**
 * @author Andrei
 */
public class Signature implements Serializable {

    private ArrayRealVector x, y;
    private double[] features;
    private int[] penDown;
    private int length, id;

    public Signature() {
    }

    public int[] getPenDown() {
        return penDown;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPenDown(int[] penDown) {
        this.penDown = penDown;
    }

    public Signature(int id, ArrayList<Float> x, ArrayList<Float> y, ArrayList<Integer> penDown) {
        this.id = id;
        this.length = x.size();

        this.x = new ArrayRealVector(J8Arrays.stream(x.toArray(new Float[length]))
                .mapToDouble(Float::doubleValue).toArray());
        this.y = new ArrayRealVector(J8Arrays.stream(y.toArray(new Float[length]))
                .mapToDouble(Float::doubleValue).toArray());
        this.penDown = J8Arrays.stream(penDown.toArray(new Integer[length]))
                .mapToInt(Integer::intValue).toArray();
    }

    public void setFeatures(double[] features) {
        this.features = features;
    }

    public int getId() {
        return id;
    }

    public double[] getFeatures() {
        if (features == null)
            System.out.println("no features for sign: " + id);
        return features;
    }

    public ArrayRealVector getX() {
        return x;
    }

    public ArrayRealVector getY() {
        return y;
    }

    public int getLength() {
        return length;
    }

    public void setX(ArrayRealVector x) {
        this.x = x;
    }

    public void setY(ArrayRealVector y) {
        this.y = y;
    }

    public Signature copy() {
        Signature copied = new Signature();
        if (features != null) {
            copied.setFeatures(Arrays.copyOf(features, features.length));
        }
        copied.setId(id);
        copied.setLength(length);
        if (penDown != null) {
            copied.setPenDown(Arrays.copyOf(penDown, penDown.length));
        }
        if (x != null) {
            copied.setX(x.copy());
        }
        if (y != null) {
            copied.setY(y.copy());
        }
        return copied;
    }


}
