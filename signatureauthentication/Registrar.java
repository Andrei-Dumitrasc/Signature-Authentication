/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrei.signatureauthentication;

import java8.util.J8Arrays;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import java8.util.function.Supplier;
import java8.util.stream.Stream;

import org.apache.commons.math3.stat.StatUtils;

/**
 * @author Andrei
 */
public class Registrar {

    private User user;
    private UserData userData;
    private int nr_signatures = 5;
    private File fileDir;
    private int length;

    public Registrar(File fileDir) {
        this.fileDir = fileDir;
    }

    // returns indices of bad signatures
    public HashSet<Integer> attemptRegistration() {
        user = new User(fileDir, nr_signatures);

        Signature[] signatures = user.getSignatures();
        Preprocessor pp = new Preprocessor();
        FeatureExtractor f = new FeatureExtractor();
        int interp_len = (int) StatUtils.max(J8Arrays.stream(signatures)
                .mapToDouble(s -> s.getLength()).toArray());
        for (int id = 0; id < signatures.length; id++) {
            Signature s = signatures[id];
            s = pp.normalize(s);
            s = pp.resample(s, interp_len);
            // s.setFeatures(f.extractFeatures(s));
            signatures[id] = s;
        }
        user.setSignatures(signatures);

        return user.findIncongruousSignatures();
    }

    public UserData generateUserData() {
        Supplier<Stream<Signature>> sign_stream_supp = () -> J8Arrays
                .stream(user.getSignatures());
        // feature extraction
        FeatureExtractor f = new FeatureExtractor();
        sign_stream_supp.get().forEach(sig -> sig.setFeatures(f.extractFeatures(sig)));
        // template generation
        TemplateGenerator t = new TemplateGenerator(nr_signatures);
        this.length = user.getSignatures()[0].getLength();
        userData = new UserData(t.acquireUserTemplate(user.getSignatures()), t.getQuant()
                , 0.0, length);
        userData.setAuthThreshold(computeThreshold());
        ManagerIO.deleteSignatures(fileDir);
        return userData;
    }

    private double computeThreshold() {
        Authenticator a = new Authenticator();
        double[] distances = new double[nr_signatures];

        for (int id = 1; id <= nr_signatures; id++) {
            a.isAuthentic(fileDir, userData, id);
            distances[id - 1] = a.getDistance();
        }
        Arrays.sort(distances);
        double mean = StatUtils.mean(distances);
        double std = Math.sqrt(StatUtils.variance(distances));
        double med = distances[2];
        return 0.081690958 + 0.687232155 * mean + (-0.653747262) * std + 0.525814624 * med;
    }

}
