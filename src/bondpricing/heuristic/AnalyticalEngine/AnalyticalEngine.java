/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.AnalyticalEngine;

import bondpricing.heuristic.XmlEngine.XMLEngine;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Zlati
 */
public class AnalyticalEngine {
    
    private SampleGenerator sampleGenerator = null;
    private ErrorClusterer errorClusterer = null;
    private ClusterProbabilityCalculator clusterProbabilityCalculator = null;
    
    static class ValueComparator implements Comparator<String> {

        Map<String, Double> base;

        ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        @Override
        public int compare(String a, String b) {
            if (base.get(a) <= base.get(b)) {
                return 1;
            } else {
                return -1;
            }
        }
    }
    
    public AnalyticalEngine(){
        
    }
    
    public AnalyticalEngine(int maxSamplesPerFile, boolean useMistypeSimulation, String[] fieldsToAnalyze, String sampleGeneratorSourceDirectory, double mistypeLikelihood, int mistypeRange, int generationDepth){
        this.sampleGenerator = new SampleGenerator(maxSamplesPerFile,useMistypeSimulation,fieldsToAnalyze,sampleGeneratorSourceDirectory,mistypeLikelihood,mistypeRange, generationDepth);
        this.errorClusterer = new ErrorClusterer(fieldsToAnalyze);
        this.clusterProbabilityCalculator = new ClusterProbabilityCalculator(fieldsToAnalyze);
    }
    
    
    public ArrayList<Sample> generateSamples(XMLEngine xmlEngine){
        return sampleGenerator.generateSamples(xmlEngine);
    }
    
    public SampleGenerator getSampleGenerator() {
        return sampleGenerator;
    }

    public void setSampleGenerator(SampleGenerator sampleGenerator) {
        this.sampleGenerator = sampleGenerator;
    }

    
    /*
     * Test-only functions
     */
    public String mistype(String text, Integer minErrors){
        return sampleGenerator.simulateMistyping(text,minErrors);
    }
    
    public HashMap<String, FieldErrorDescriptive> generateFieldErrorDescriptives(ArrayList<Sample> samples){
        return clusterProbabilityCalculator.generateFieldErrorDescriptives(samples);
    }
    
    public ArrayList<ErrorCluster> generateErrorClusters(double numClusters, ArrayList<Sample> samples){
        return errorClusterer.generateErrorClusters(numClusters, samples);
    }
    
    public ArrayList<ErrorClusterProbabilities> generateErrorClusterProbabilities(ArrayList<ErrorCluster> errorClusters, ArrayList<Sample> samples){
        return clusterProbabilityCalculator.generateErrorClusterProbabilities(errorClusters, samples);
    }
    
    public String generatePermutations(ArrayList<ErrorClusterProbabilities> errorClusterProbabilities){
        
        String heuristic =  "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>" + "\n" +
                            "<heuristic>" + "\n";
        
        TreeMap<String, Double> sorted = null;
        
        for(int i=0; i<errorClusterProbabilities.size(); i++){
            heuristic +=    "   <error>" + "\n" +
                            "       <id>"+i+"</id>" + "\n" +
                            "       <value>"+errorClusterProbabilities.get(i).getError()+"</value>" + "\n" +
                            "       <permutations>" + "\n" ;
            
            ValueComparator vc = new ValueComparator(errorClusterProbabilities.get(i).getSampleMap());
            sorted = new TreeMap<String, Double>(vc);
        
            sorted.putAll(errorClusterProbabilities.get(i).getSampleMap());
            
            
            
            for(Map.Entry<String, Double> entry : sorted.entrySet()){
                heuristic+= "           <permutation>" + "\n" +
                            "               <likelihood>"+entry.getValue()+"</likelihood>" + "\n" +
                            "               <wrong_par_num>1</wrong_par_num>" + "\n" +
                            "               <wrong_par>"+entry.getKey()+"</wrong_par>" + "\n" +
                            "           </permutation>" + "\n" ;
                            
            }
            
            heuristic +=    "       </permutations>" + "\n" +
                            "   </error>" + "\n" ;
        }
        
        heuristic += "</heuristic>";
        
        
        return heuristic;
    }
}
