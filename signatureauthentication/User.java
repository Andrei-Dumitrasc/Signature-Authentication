/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrei.signatureauthentication;

import java.io.File;
import java.util.HashSet;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.MathArrays;

/**
 *
 * @author Andrei
 */
public class User {

    private int id;
    private int nr_signatures = 40;
    private Signature[] signatures;
    private double minSignCorr; // minimum Correlation between all pairs of Signatures
    private double corrThreshold = .3; // if corr between 2 signatures is below this, discard them


    public double getMinSignCorr() {
        return minSignCorr;
    }

    public void setMinSignCorr(double minSignCorr) {
        this.minSignCorr = minSignCorr;
    }   

    public int getNr_signatures() {
        return nr_signatures;
    }

    public int getId() {
        return id;
    }
    
    public User(Signature[] s){
        this.signatures=s;
        this.id=1;
        this.nr_signatures=s.length;
    }
    
    public User(File fileDir, int nr_signatures) {
        this.nr_signatures = nr_signatures;
        this.id = 1;
        signatures = new Signature[nr_signatures];
        for (int id_signature = 1; id_signature <= nr_signatures; id_signature++) {
            signatures[id_signature - 1] = ManagerIO.loadSignature(fileDir, id_signature);
        }
    }

    public Signature[] getSignatures() {
        return signatures;
    }

    public void setSignatures(Signature[] signatures) {
        this.signatures = signatures;
    }

    // returns ids of signatures that correlate with others below threshold
    public HashSet<Integer> findIncongruousSignatures() {
        minSignCorr = 1;
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < signatures.length; i++) {
            double[] s1 = MathArrays.concatenate(signatures[i].getX().toArray(),
                    signatures[i].getY().toArray());
            for (int j = i + 1; j < signatures.length; j++) {
                double[] s2 = MathArrays.concatenate(signatures[j].getX().toArray(),
                        signatures[j].getY().toArray());
                double corr = new PearsonsCorrelation().correlation(s1, s2);
                if (corr < minSignCorr) {
                    minSignCorr = corr;
                }
                if (corr < corrThreshold) {
                    set.add(i + 1);
                }
            }
        }
        // System.out.println("minSignCorr=");
        // System.out.println(minSignCorr);
        return set;
    }

    public double getCorrThreshold() {
        return corrThreshold;
    }

}
