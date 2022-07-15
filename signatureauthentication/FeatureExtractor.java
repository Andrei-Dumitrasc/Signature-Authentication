/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrei.signatureauthentication;
import hep.aida.IHistogram2D;
import hep.aida.ref.Histogram1D;
import hep.aida.ref.Histogram2D;
import java8.util.J8Arrays;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.analysis.function.Tan;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.MathArrays;

/**
 *
 * @author Andrei
 */
public class FeatureExtractor {

    private double limSup(ArrayRealVector x) {
        return StatUtils.mean(x.toArray()) + 3 * Math.sqrt(StatUtils.variance(x.toArray()));
    }

    private double limInf(ArrayRealVector x) {
        return StatUtils.mean(x.toArray()) - 3 * Math.sqrt(StatUtils.variance(x.toArray()));
    }

    private double[] getHistogram1D(RealVector x, int bins, double minEdge,
            double maxEdge, boolean normalize) {

        Histogram1D h = new Histogram1D("", bins, minEdge, maxEdge);
        J8Arrays.stream(x.toArray()).forEach(h::fill);
        double sum = (double) h.entries();
        if (sum==0)
            normalize=false;
        if (normalize) {
            return  MathArraysCont.IntStreamRange(bins)
                    .mapToDouble(i -> h.binEntries(i) / sum).toArray();
        }        
        return  MathArraysCont.IntStreamRange(bins)
                .mapToDouble(i -> h.binEntries(i)).toArray();
    }

    private double[] getHistogram2D(RealVector x, int binsX, double minEdgeX,
            double maxEdgeX, RealVector y, int binsY, double minEdgeY,
            double maxEdgeY, boolean normalize) {

        IHistogram2D h = new Histogram2D("", binsX, minEdgeX, maxEdgeX, binsY, minEdgeY, maxEdgeY);
         MathArraysCont.IntStreamRange(x.getDimension())
                 .forEach(i -> h.fill(x.getEntry(i), y.getEntry(i)));
        double sum = (double) h.entries();
        if (sum==0)
            normalize=false;
        if (normalize) {            
            return MathArraysCont.IntStreamRange(binsX).flatMap(ex
                    -> MathArraysCont.IntStreamRange(binsY).map(ey
                            -> h.binEntries(ex, ey)))
                    .mapToDouble(e -> e / sum).toArray();
        }
        return MathArraysCont.IntStreamRange(binsX).flatMap(ex
                -> MathArraysCont.IntStreamRange(binsY).map(ey
                        -> h.binEntries(ex, ey))).asDoubleStream().toArray();
    }

    public double[] extractFeatures(Signature s) {
        ArrayRealVector x = s.getX();
        ArrayRealVector y = s.getY();
        ArrayRealVector x1 = ((ArrayRealVector) x.getSubVector(1, x.getDimension() - 1)).
                subtract(x.getSubVector(0, x.getDimension() - 1));
        ArrayRealVector x2 = ((ArrayRealVector) x1.getSubVector(1, x1.getDimension() - 1)).
                subtract(x1.getSubVector(0, x1.getDimension() - 1));
        ArrayRealVector y1 = ((ArrayRealVector) y.getSubVector(1, y.getDimension() - 1)).
                subtract(y.getSubVector(0, y.getDimension() - 1));
        ArrayRealVector y2 = ((ArrayRealVector) y1.getSubVector(1, y1.getDimension() - 1)).
                subtract(y1.getSubVector(0, y1.getDimension() - 1));

        ArrayRealVector theta = y.ebeDivide(x).map(new Tan()).map(new Inverse());
        ArrayRealVector t1 = ((ArrayRealVector) theta.getSubVector(1, theta.getDimension() - 1)).
                subtract(theta.getSubVector(0, theta.getDimension() - 1));
        ArrayRealVector t2 = ((ArrayRealVector) t1.getSubVector(1, t1.getDimension() - 1)).
                subtract(t1.getSubVector(0, t1.getDimension() - 1));

        ArrayRealVector r = x.map(new Power(2)).add(y.map(new Power(2))).map(new Sqrt());
        ArrayRealVector r1 = ((ArrayRealVector) r.getSubVector(1, r.getDimension() - 1)).
                subtract(r.getSubVector(0, r.getDimension() - 1));
        ArrayRealVector r2 = ((ArrayRealVector) r1.getSubVector(1, r1.getDimension() - 1)).
                subtract(r1.getSubVector(0, r1.getDimension() - 1));

        // 1D histograms
        double[] X1 = getHistogram1D(x1, 12, limInf(x1), limSup(x1), true);
        double[] X2 = getHistogram1D(x2, 12, limInf(x2), limSup(x2), true);
        double[] Y1 = getHistogram1D(y1, 12, limInf(y1), limSup(y1), true);
        double[] Y2 = getHistogram1D(y2, 12, limInf(y2), limSup(y2), true);
        double[] T1 = getHistogram1D(t1, 16, -Math.PI, Math.PI, true);
        double[] T2 = getHistogram1D(t2, 24, -Math.PI, Math.PI, true);
        double[] R1 = getHistogram1D(r1, 16, 0, limSup(r1), true);
        double[] R2 = getHistogram1D(r2, 16, 0, limSup(r2), true);

        // 2D histograms
        int[] b1={6,4};
        double[] X1X2 = getHistogram2D(x1.getSubVector(0, x1.getDimension() - 1),
                b1[0], limInf(x1), limSup(x1), x2, b1[1], limInf(x2), limSup(x2), true);
        double[] Y1Y2 = getHistogram2D(y1.getSubVector(0, y1.getDimension() - 1),
                b1[0], limInf(y1), limSup(y1), y2, b1[1], limInf(y2), limSup(y2), true);

        int[] b2={8,4};
        int n1 = r1.getDimension() / 2;
        double[] T1R1F = getHistogram2D(t1.getSubVector(0, n1), b2[0], -Math.PI, Math.PI,
                r1.getSubVector(0, n1), b2[1], 0, limSup(r1), true);
        double[] T1R1S = getHistogram2D(t1.getSubVector(n1, t1.getDimension() - n1), b2[0], -Math.PI, Math.PI,
                r1.getSubVector(n1, r1.getDimension() - n1), b2[1], 0, limSup(r1), true);

        int n2 = r2.getDimension() / 2;
        double[] T2R2F = getHistogram2D(t2.getSubVector(0, n2), b2[0], -Math.PI, Math.PI,
                r2.getSubVector(0, n2), b2[1], 0, limSup(r2), true);
        double[] T2R2S = getHistogram2D(t2.getSubVector(n2, t2.getDimension() - n2), b2[0], -Math.PI, Math.PI,
                r2.getSubVector(n2, r2.getDimension() - n2), b2[1], 0, limSup(r2), true);

        double[] T1R2F = getHistogram2D(t1.getSubVector(0, n1), b2[0], -Math.PI, Math.PI,
                r2.getSubVector(0, n1), b2[1], 0, limSup(r1), true);
        double[] T1R2S = getHistogram2D(t1.getSubVector(n1, t1.getDimension() - n1 - 1), b2[0], -Math.PI, Math.PI,
                r2.getSubVector(n1, r2.getDimension() - n1), b2[1], 0, limSup(r1), true);

        return MathArrays.concatenate(X1, X2, Y1, Y2, T1, T2, R1, R2, X1X2,
                Y1Y2, T1R1F, T1R1S, T2R2F, T2R2S, T1R2F, T1R2S);
    }
}
