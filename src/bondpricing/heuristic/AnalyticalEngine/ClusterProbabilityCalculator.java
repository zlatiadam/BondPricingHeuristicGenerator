/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.AnalyticalEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Zlati
 */
public class ClusterProbabilityCalculator {
    
    private String[] fieldsToAnalyze;
    
    
    public ClusterProbabilityCalculator(String[] fieldsToAnalyze){
        this.fieldsToAnalyze = fieldsToAnalyze;
    }
    
    //String: the field ID
    public HashMap<String, FieldErrorDescriptive> generateFieldErrorDescriptives(ArrayList<Sample> samples) {

        HashMap<String, FieldErrorDescriptive> descriptives = new HashMap<String, FieldErrorDescriptive>();

        //get the mean and standard deviation of the errors for each field
        for (int i = 0; i < fieldsToAnalyze.length; i++) {

            //System.out.println(fieldsToAnalyze[i]);

            double mean = 0.0;
            double deviation = 0.0;

            double sum = 0.0;
            int cntr = 0;

            double temp_value = 0.0;

            //calculate the error mean for the given field
            for (int j = 0; j < samples.size(); j++) {

                if (samples.get(j).getAlteredValues().containsKey(fieldsToAnalyze[i])) {
                    temp_value = samples.get(j).getError();

                    //check if the field has even been changed
                    //System.out.println(temp_value);


                    sum += temp_value;
                    cntr++;

                }
            }
            mean = sum / cntr;

            //calculate the deviation for the given field
            sum = 0.0;
            for (int j = 0; j < samples.size(); j++) {

                if (samples.get(j).getAlteredValues().containsKey(fieldsToAnalyze[i])) {

                    temp_value = samples.get(j).getError();

                    sum += Math.pow(temp_value - mean, 2);
                }

            }
            deviation = Math.sqrt(sum / cntr);

            descriptives.put(fieldsToAnalyze[i], new FieldErrorDescriptive(fieldsToAnalyze[i], mean, deviation));
        }

        return descriptives;
    }
    
    
    
    public ArrayList<ErrorClusterProbabilities> generateErrorClusterProbabilities(ArrayList<ErrorCluster> errorClusters, ArrayList<Sample> samples){
        
        ArrayList<ErrorClusterProbabilities> errorClusterProbabilitiesList = new ArrayList<ErrorClusterProbabilities>();
        
        //generate descriptive statistics
        HashMap<String, FieldErrorDescriptive> descriptives = generateFieldErrorDescriptives(samples);
        
        //generate a probability for every error cluster
        for(int i=0; i<errorClusters.size(); i++){
            
            //select current cluster
            ErrorCluster errorCluster = errorClusters.get(i);
            
            //create an object to wrap together the probabilities
            ErrorClusterProbabilities errorClusterProbabilities = new ErrorClusterProbabilities(errorCluster.getError());
            
            //generate a probability for each field that may have caused an error in the current cluster
            double[] probabilities = new double[errorCluster.getSampleMap().keySet().size()];
            
            
            //count the errors in the cluster
            int totalErrorsInCluster = 0;
            
            Iterator it = errorCluster.getSampleMap().keySet().iterator();
            
            int j = 0;
            
            while(it.hasNext()){
                String key = it.next().toString();
                
                //store the occurences of a field
                probabilities[j] = errorCluster.getSampleMap().get(key);
                
                totalErrorsInCluster += probabilities[j];
                j++;
            }
            
            
            //calculate the probabilities
            for(j=0; j<errorCluster.getSampleMap().keySet().size(); j++){
                probabilities[j] = probabilities[j] / totalErrorsInCluster;
            }
            
            //weigh the probabilities of an error range by its complementer distance from the mean of a field
            //multiplied by the complementer of the field's standard deviation
            //explanation:  if there are for example 1 - 1 errors in an error range (error cluster) then
            //              their probabilities would be the same; although some of these points may be 
            //              closer to a mean of error of a field, so it's more likely that this field would
            //              cause errors in this range
            //              this applies to the deviation as well - if two errors are equally close to two centers
            //              of field errors the one with the lower deviation is more likely to cause it
            
            //weigh the probabilities with their complementary distance from their center
            //and their complementary deviation
            //the idea above might be considered as a modified Mahalanobis distance
            
            j = 0;
            for(Map.Entry<String, Integer> entry : errorCluster.getSampleMap().entrySet()){
                double weightBasedOnDistanceFromCenter = 1-Math.sqrt(Math.pow(errorCluster.getError()-descriptives.get(entry.getKey()).getErrorMean(),2));
                double weightBasedOnDeviation = 1-descriptives.get(entry.getKey()).getErrorDeviation();
                errorClusterProbabilities.getSampleMap().put(entry.getKey(), probabilities[j]*weightBasedOnDistanceFromCenter*weightBasedOnDeviation);
                j++;
            }
            
            errorClusterProbabilitiesList.add(errorClusterProbabilities);
        }
        
        return errorClusterProbabilitiesList;
    }
}
