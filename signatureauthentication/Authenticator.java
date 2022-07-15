/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrei.signatureauthentication;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.MathArrays;

import java.io.File;
import java.util.Arrays;

/**
 * @author Andrei
 */
public class Authenticator {

    private String scores;
    private double distance;

    public boolean isAuthentic(File fileDir, UserData u, int id) {
        double[] template = u.getTemplate();
        double[] candidateTemplate;
        double templateNorm1 = new ArrayRealVector(template).getL1Norm();
        double threshold = u.getAuthThreshold();
        Preprocessor p = new Preprocessor();

        Signature candidate = ManagerIO.loadSignature(fileDir, id);
        candidate = p.normalize(candidate);
        candidate = p.resample(candidate, u.getSignatureLength());
        FeatureExtractor f = new FeatureExtractor();
        candidate.setFeatures(f.extractFeatures(candidate));
        TemplateGenerator t = new TemplateGenerator(1);
        t.setQuant(u.getQuantizer());
        candidateTemplate = t.acquireSignatureTemplate(candidate);
        distance = new ArrayRealVector(MathArrays
                .ebeSubtract(candidateTemplate, template))
                .getL1Norm() / templateNorm1;

        // System.out.println(Arrays.toString(template));
        // System.out.println(Arrays.toString(candidateTemplate));
                
        scores = "" + Math.round(distance * 100) / 100.0 + " ? "
                + Math.round(threshold * 100) / 100.0;

//        System.out.println(distance/templateNorm1);
//        System.out.println(u.getAuthThreshold());
        return distance < threshold;
    }
    
    public boolean isAuthentic(File fileDir, UserData u) {
        return isAuthentic(fileDir, u, 6);
    }

    public String getScores() {
        return scores;
    }
    
    public double getDistance() {
        return distance;
    }
}
