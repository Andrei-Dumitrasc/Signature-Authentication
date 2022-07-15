package com.andrei.signatureauthentication;

import java8.util.J8Arrays;
import java8.util.function.Supplier;
import java8.util.stream.Stream;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.MathArrays;

/**
 *
 * @author Andrei
 */
public class TemplateGenerator {

    private double  c1 = 0.02, c2 = 0.002;
    private double[] epsilon;
    private int feat_len;
    private DescriptiveStatistics[] stats;
    private double[] quant;
    private int nr_train_samples;

    public TemplateGenerator(int nr_train_samples) {
        this.nr_train_samples = nr_train_samples;
        epsilon = MathArrays.concatenate(MathArraysCont.zeros(c1, 4 * 12 + 16 + 24),
                MathArraysCont.zeros(c1, 2 * 16), MathArraysCont.zeros(c1, 2 * 6 * 4),
                MathArraysCont.zeros(c1, 6 * 8 * 4));
        feat_len = epsilon.length;
        stats = MathArraysCont.IntStreamRange(feat_len)
                .mapToObj(i -> new DescriptiveStatistics(nr_train_samples))
                .toArray(DescriptiveStatistics[]::new);
    }

    public double[] acquireUserTemplate(Signature[] signatures) {
        // template generation
        Supplier<Stream<DescriptiveStatistics>> stats_stream_supp
                = () -> J8Arrays.stream(stats);
        double beta = 1.5;
        // only for training samples
        Supplier<Stream<Signature>> sign_stream_supp
                = () -> J8Arrays.stream(signatures).limit(nr_train_samples);

        stats_stream_supp.get().forEach(s -> s.clear());
        sign_stream_supp.get().forEach(sig -> scatterFeatures(sig.getFeatures()));
        quant = stats_stream_supp.get().mapToDouble(s -> beta * Math.sqrt(s.getVariance())).toArray();
        stats_stream_supp.get().forEach(s -> s.clear());
        sign_stream_supp.get().forEach(sig -> scatterFeatures(MathArrays.ebeDivide(
                sig.getFeatures(), MathArrays.ebeAdd(quant, epsilon))));
        return stats_stream_supp.get().mapToDouble(s -> s.getMean()).toArray();
    }

    public double[] acquireSignatureTemplate(Signature sig) {
        return MathArrays.ebeDivide(sig.getFeatures(), MathArrays.ebeAdd(quant, epsilon));
    }

    private void scatterFeatures(double[] features) {
        MathArraysCont.IntStreamRange(features.length)
                .forEach(i -> stats[i].addValue(features[i]));
    }

    public void setQuant(double[] quant) {
        this.quant = quant;
    }

    public double[] getQuant() {
        return quant;
    }

}
