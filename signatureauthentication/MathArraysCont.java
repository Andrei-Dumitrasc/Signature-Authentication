/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrei.signatureauthentication;

import java.util.Arrays;
import static java.util.Arrays.copyOfRange;
import org.apache.commons.math3.util.MathArrays;
import static org.apache.commons.math3.util.MathArrays.concatenate;
import java8.util.stream.IntStream;
import java8.util.J8Arrays;
/**
 *
 * @author Andrei
 */
public class MathArraysCont {
    
    public static double[] zeros(double zero, int size){
        double[] a=new double[size];
        Arrays.fill(a,zero);
        return a;
    }

    public static double[] removeAt(double[] vector, int[] indices) {
        // expects sorted(ASC), unique, positive indices

        if (indices.length == 0) {
            return vector;
        }

        double[] result = {};
        int s = 0;
        for (int i : indices) {
            if (i - 1 < 0) {
                s = i + 1;
                continue;
            }
            result = concatenate(result, copyOfRange(vector, s, i));
            s = i + 1;
        }
        result = concatenate(result, copyOfRange(vector,
                indices[indices.length - 1] + 1, vector.length));
        return result;
    }

    public static int[] removeAt(int[] vector, int[] indices) {
        // expects sorted(ASC), unique, positive indices        
        double[] dvector = J8Arrays.stream(vector)
                .mapToDouble(i -> i).toArray();
        double[] result = removeAt(dvector, indices);
        return J8Arrays.stream(result).mapToInt(d -> (int) d).toArray();
    }

    public static double[] linspace(double start, double end, int nr_points) {
        return J8Arrays.stream(MathArrays.natural(nr_points))
                .mapToDouble(point -> point / ((double) nr_points - 1)
                * (end - start) + start).toArray();
    }    
    
    public static IntStream IntStreamRange(int endEx)
    {        
        return J8Arrays.stream(MathArrays.natural(endEx));
    }
    
}
