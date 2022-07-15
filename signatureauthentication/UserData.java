package com.andrei.signatureauthentication;

import java.io.Serializable;

public class UserData implements Serializable {
    private double[] template;
    private double[] quantizer;
    private int signatureLength;
    private double authThreshold; // authentication Threshold: separates genuine signatures from forgeries
    
     public UserData(double[] template, double[] quantizer, double authThreshold, int signatureLength){
         this.template = template;
         this.quantizer = quantizer;
         this.authThreshold = authThreshold;   
         this.signatureLength = signatureLength;         
     }
    
    public double[] getQuantizer() {
        return quantizer;
    }

    public void setQuantizer(double[] quantizer) {
        this.quantizer = quantizer;
    }
    
    public int getSignatureLength() {
        return signatureLength;
    }

    public void setSignatureLength(int signatureLength) {
        this.signatureLength = signatureLength;
    }
    
     public double getAuthThreshold() {
        return authThreshold;
    }

    public void setAuthThreshold(double authThreshold) {
        this.authThreshold = authThreshold;
    }

    public void setTemplate(double[] template) {
        this.template = template;
    }

    public double[] getTemplate() {
        return template;
    }
}