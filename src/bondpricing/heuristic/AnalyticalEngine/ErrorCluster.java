/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.AnalyticalEngine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Zlati
 */
public class ErrorCluster {
    
    private double error;
    
    //1: field, 2: samples of that field type
    private HashMap<String,Integer> sampleMap;
    
    public ErrorCluster(double error){
        this.error = error;
        this.sampleMap = new HashMap<String, Integer>();
    }
    
    public ErrorCluster(double error, HashMap<String, Integer> sampleMap) {
        this.error = error;
        this.sampleMap = sampleMap;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public HashMap<String, Integer> getSampleMap() {
        return sampleMap;
    }

    public void setSampleMap(HashMap<String, Integer> sampleMap) {
        this.sampleMap = sampleMap;
    }
    
}
