/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.AnalyticalEngine;

import java.util.ArrayList;

/**
 *
 * @author Zlati
 */
public class ErrorClusterer {

    private String[] fieldsToAnalyze;

    public ErrorClusterer() {
        fieldsToAnalyze = null;
    }

    public ErrorClusterer(String[] fields) {
        this.fieldsToAnalyze = fields;
    }

    

    public ArrayList<ErrorCluster> generateErrorClusters(double numClusters, ArrayList<Sample> samples) {

        ArrayList<ErrorCluster> errorClusters = new ArrayList<ErrorCluster>();

        Double frequency = 1 / numClusters;

        double previousErrorCluster = 0.0;

        for (double i = frequency; i <= 1.0; i += frequency) {

            ErrorCluster errorCluster = new ErrorCluster(i); //create an error index that identifies the cluster

            //get all the samples that belong in that error range
            for (int j = 0; j < samples.size(); j++) {
                
                //get the first altered value (since the system works only with one altered value at present
                //this is just fine

                if (!samples.get(j).getAlteredValues().values().isEmpty())
                {
                    //get the first key associated with the only altered field
                    String alteredValueKey = samples.get(j).getAlteredValues().keySet().iterator().next();
                    
                    
                    //check whether the error is in the appropriate range
                    if (samples.get(j).getError() > previousErrorCluster && samples.get(j).getError() <= i) {

                        //check whether the field is already in the cluster's map, increment its counter
                        if (errorCluster.getSampleMap().containsKey(alteredValueKey)) {
                            Integer cntr = errorCluster.getSampleMap().get(alteredValueKey);
                            errorCluster.getSampleMap().put(alteredValueKey, ++cntr);
                        } else {
                            //the field is not already in the cluster's map, add it
                            errorCluster.getSampleMap().put(alteredValueKey, 1);
                        }


                    }
                }

            }

            errorClusters.add(errorCluster);
            previousErrorCluster += frequency;
        }


        return errorClusters;
    }
}
