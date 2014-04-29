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
public class ErrorClusterProbabilities {
    private double error;
    
    //1: field, 2: samples of that field type
    private HashMap<String,Double> sampleMap;
    
    public ErrorClusterProbabilities(double error){
        this.error = error;
        this.sampleMap = new HashMap<String, Double>();
    }
    
    public ErrorClusterProbabilities(double error, HashMap<String, Double> sampleMap) {
        this.error = error;
        this.sampleMap = sampleMap;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public HashMap<String, Double> getSampleMap() {
        return sampleMap;
    }

    public void setSampleMap(HashMap<String, Double> sampleMap) {
        this.sampleMap = sampleMap;
    }
}
