/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.AnalyticalEngine;

import java.util.HashMap;

/**
 *
 * @author Zlati
 */
public class Sample {
    private HashMap<String,String> originalValues;
    private HashMap<String,String> alteredValues;
    private double error;
    
    public Sample(){
        originalValues = new HashMap<String,String>();
        alteredValues = new HashMap<String,String>();
        error = 0;
    }
    
    public Sample(HashMap<String,String> originalValues, HashMap<String,String> alteredValues, double error){
        this.originalValues = originalValues;
        this.alteredValues = alteredValues;
        this.error = error;
    }

    public HashMap<String, String> getOriginalValues() {
        return originalValues;
    }

    public void setOriginalValues(HashMap<String, String> originalValues) {
        this.originalValues = originalValues;
    }

    public HashMap<String, String> getAlteredValues() {
        return alteredValues;
    }

    public void setAlteredValues(HashMap<String, String> alteredValues) {
        this.alteredValues = alteredValues;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }
    
    
    
    
    
}
