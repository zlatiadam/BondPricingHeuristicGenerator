/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic;

import bondpricing.heuristic.AnalyticalEngine.*;
import bondpricing.heuristic.AnalyticalEngine.Sample;
import bondpricing.heuristic.XmlEngine.*;
import bondpricing.heuristic.XmlEngine.XSDRestrictions.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Zlati
 */
public class HeuristicGenerator {
    
    public static void main(String[] args){
        
        ConfigurationManager cm = new ConfigurationManager();
        try{
            cm.load(null);

            //load XMLEngine section settings
            boolean modifierIsFile = false;
            
            if(cm.get("ModifierSourceIsFile").equals("true")){
                modifierIsFile = true;
            }else{
                modifierIsFile = false;
            }
            
            String sourceFile = cm.get("ModifierSchemaSource");
            
            boolean generatorWriteToFile = true;
            
            if(cm.get("GeneratorWriteToFile").equals("true")){
                generatorWriteToFile = true;
            }else{
                generatorWriteToFile = false;
            }
            
            String generatorDestination = cm.get("GeneratorDestination");
            
            //initialize XMLEngine component            
            XMLEngine xmlEngine = new XMLEngine(modifierIsFile, sourceFile, generatorWriteToFile, generatorDestination);
            
            int maxSamplesPerFile = Integer.parseInt(cm.get("MaxSamplesPerFile"));
            
            boolean useMistypeSimulation = true;
            if(cm.get("UseMistypeSimulation").equals("true")){
                useMistypeSimulation = true;
            }else{
                useMistypeSimulation = false;
            }
            
            String[] fieldsToAnalyze = cm.get("FieldsToAnalyze").split(",");
            String sampleGeneratorSourceDirectory = cm.get("SampleGeneratorSourceDirectory");
            double mistypeLikelihood = Double.parseDouble(cm.get("MistypeLikelihood"));
            int mistypeRange = Integer.parseInt(cm.get("MistypeRange"));
            int generationDepth = Integer.parseInt(cm.get("GenerationDepth"));
            
            AnalyticalEngine analyticalEngine = new AnalyticalEngine(
                                                        maxSamplesPerFile,
                                                        useMistypeSimulation,
                                                        fieldsToAnalyze,
                                                        sampleGeneratorSourceDirectory,
                                                        mistypeLikelihood,
                                                        mistypeRange,
                                                        generationDepth
                                                     );
            
            
            //System.out.println(analyticalEngine.mistype("111111",4));
            ArrayList<Sample> samples = analyticalEngine.generateSamples(xmlEngine);
            
            /*
            for(int i=0; i<samples.size(); i++){
                //System.out.print(samples.get(i).getOriginalValues().keySet().size());
                Iterator i1 = samples.get(i).getAlteredValues().keySet().iterator();
                System.out.println("=============================");
                System.out.println(samples.get(i).getError());
                while(i1.hasNext()){
                    
                    String field = (String)i1.next();
                    //System.out.println(field +"="+ samples.get(i).getOriginalValues().get(field));
                    System.out.println(field);
                }
                
            }
            */
            /*
            XMLParser parser = new XMLParser();
            
            DataModifier dm = new DataModifier(cm);
            
            //RestrictionManager rm = new RestrictionManager(parser.parse(true, "temp_schema2.xsd"));
            RestrictionManager rm = new RestrictionManager(parser.parse(true, "bond_schema.xsd"));
            //RestrictionManager rm = new RestrictionManager(parser.parse(true, "newXmlSchema.xsd"));
            //RestrictionManager rm = new RestrictionManager(parser.parse(true, "FPML.xsd"));
            
            rm.extractNodesOfTypes();
            
            rm.extractRestrictions();
            
            rm.printRestrictions("bond/nominal");
            
            */
            
            
            
            HashMap<String,FieldErrorDescriptive> d = analyticalEngine.generateFieldErrorDescriptives(samples);
            
            for(Map.Entry<String, FieldErrorDescriptive> entry : d.entrySet()){
                
                System.out.println(entry.getKey()+", mean:"+entry.getValue().getErrorMean()+", std:"+entry.getValue().getErrorDeviation());
                
            }
            
            System.out.println("=====================");
            
            ArrayList<ErrorCluster> e = analyticalEngine.generateErrorClusters(10, samples);
            
            
            System.out.println(e.size());
            
            for(int i=0; i<e.size(); i++){
                Iterator it = e.get(i).getSampleMap().keySet().iterator();
                
                System.out.println(e.get(i).getError());
                
                while(it.hasNext()){
                    String key = it.next().toString();
                    System.out.println(key+":"+e.get(i).getSampleMap().get(key));
                }
            }
            
            
            
            
            //System.in.read();
            
            System.out.println("=====================");
            
            ArrayList<ErrorClusterProbabilities> l = analyticalEngine.generateErrorClusterProbabilities(e, samples);
            
            for(int i=0; i<l.size(); i++){
                System.out.println(l.get(i).getError());
                
                for(Map.Entry<String, Double> entry : l.get(i).getSampleMap().entrySet()){
                    System.out.println(entry.getKey()+", p="+entry.getValue());
                }
            }
            
            String heuristic = analyticalEngine.generatePermutations(l);
            
            PrintWriter writer = new PrintWriter("BondPricingCorrectionHeuristic.xml");
            writer.println(heuristic);
            writer.close();
            
            System.out.println(heuristic);
            
        }catch(IOException e){
            System.out.println("The configuration file could not be loaded! Check whether the application has the required rights to read/write files!");
        }
    }
}
