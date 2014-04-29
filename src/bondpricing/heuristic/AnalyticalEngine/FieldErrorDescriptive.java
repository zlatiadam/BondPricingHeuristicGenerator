/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.AnalyticalEngine;

/**
 *
 * @author Zlati
 */
public class FieldErrorDescriptive {
    private String field;
    private double errorMean;
    private double errorDeviation;

    public FieldErrorDescriptive(String field, double errorMean, double errorDeviation) {
        this.field = field;
        this.errorMean = errorMean;
        this.errorDeviation = errorDeviation;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public double getErrorMean() {
        return errorMean;
    }

    public void setErrorMean(double errorMean) {
        this.errorMean = errorMean;
    }

    public double getErrorDeviation() {
        return errorDeviation;
    }

    public void setErrorDeviation(double errorDeviation) {
        this.errorDeviation = errorDeviation;
    }
    
    
}
