/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.AnalyticalEngine;

import bondpricing.heuristic.XmlEngine.XMLEngine;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Zlati
 */
public class SampleGenerator {

    private int maxSamplesPerFile;
    private boolean useMistypeSimulation;
    private String[] fieldsToAnalyze;
    private String sampleGeneratorSourceDirectory;
    private double mistypeLikelihood;
    private int mistypeRange;
    private int generationDepth;
    
    public SampleGenerator(int maxSamplesPerFile, boolean useMistypeSimulation, String[] fieldsToAnalyze, String sampleGeneratorSourceDirectory, double mistypeLikelihood, int mistypeRange, int generationDepth) {
        this.maxSamplesPerFile = maxSamplesPerFile;
        this.useMistypeSimulation = useMistypeSimulation;
        this.fieldsToAnalyze = fieldsToAnalyze;
        this.sampleGeneratorSourceDirectory = sampleGeneratorSourceDirectory;
        this.mistypeLikelihood = mistypeLikelihood;
        this.mistypeRange = mistypeRange;
        this.generationDepth = generationDepth;
    }
    
    //this function is not ready to handle multi-depth sample generation
    public ArrayList<Sample> generateSamples(XMLEngine xmlEngine) {
        ArrayList<Sample> retval = new ArrayList<Sample>();

        File sourceFolder = new File(sampleGeneratorSourceDirectory);

        File[] filesInSourceFolder = sourceFolder.listFiles();

        //iterate through all the XML files which should be modified
        if (filesInSourceFolder != null && filesInSourceFolder.length > 0) {

            String fieldContent = null;

            for (int i = 0; i < filesInSourceFolder.length; i++) {
                if (filesInSourceFolder[i].isFile()) {
                    
                    
                    /*
                     * double original_price = MS.blackbox(xmlPath);
                     */
                    
                    //generate altered XML files from the original, each time with different changes in the values of fields
                    for (int j = 0; j < fieldsToAnalyze.length; j++) {

                        for (int k = 0; k < maxSamplesPerFile; k++) {

                            fieldContent = xmlEngine.get_value_of_field_from_xml(true, filesInSourceFolder[i].getPath(), fieldsToAnalyze[j]);

                            String[] xmlGenerationInfo = null;

                            if (useMistypeSimulation) {
                                //set the 2nd parameter to set the top limit of errors, null otherwise
                                //if set, the number of errors is around the parameter
                                fieldContent = simulateMistyping(fieldContent, 1);
                                
                                xmlGenerationInfo = xmlEngine.generate_modified_xml(filesInSourceFolder[i].getPath(), new String[]{fieldsToAnalyze[j]}, new String[]{fieldContent});

                            } else {

                                xmlGenerationInfo = xmlEngine.generate_modified_xml(filesInSourceFolder[i].getPath(), new String[]{fieldsToAnalyze[j]}, new String[]{fieldContent});

                            }
                            
                            HashMap<String,String> alteredValues = new HashMap<String,String>();
                            alteredValues.put(fieldsToAnalyze[j], xmlGenerationInfo[1]);
                            
                            
                            /*
                             * double new_price = MS.blackbox(newXmlPath);
                             * 
                             * double price_error = | (new_price - original_price) / original_price |
                             */
                            
                            //Simulate Morgan-Stanley pricing blackbox
                            double price_error = new Random().nextDouble();
                            
                            
                            //create a Sample object and add it to the returned array
                            retval.add(new Sample(xmlEngine.parse_xml_into_hashmap(true,xmlGenerationInfo[0]), alteredValues, price_error));
                            
                        }


                    }


                }
            }

        }

        return retval;
    }

    public String simulateMistyping(String text, Integer maxErrors) {
        String retval = null;

        double mistype_chain_likelihood = mistypeLikelihood;
        int mistype_chain_length = 0;

        Random rand = new Random();

        if (maxErrors == null) {

            //determine the number of mistypings according to their likelihood (each character has 'mistypeLikelihood' to be mistyped)
            for (int i = 0; i < text.length(); i++) {
                if (rand.nextDouble() < mistypeLikelihood) {
                    mistype_chain_length++;
                }
            }

        } else if (maxErrors <= text.length()) {
            mistype_chain_length = maxErrors;
        }

        int[] mistypePositions = new int[mistype_chain_length];

        boolean alreadyAltered = false;

        //generate n random positions in the text where the characters will be "mistyped"
        for (int i = 0; i < mistype_chain_length; i++) {
            alreadyAltered = false;

            while (!alreadyAltered) {
                //generate random position where the mistyping "happened"
                int newPos = rand.nextInt(text.length());

                //check if that character is already mistyped
                for (int j = 0; j <= i; j++) {
                    if (mistypePositions[j] == newPos) {
                        alreadyAltered = true;
                        break;
                    }
                }
                if (!alreadyAltered) {
                    //store the new position
                    mistypePositions[i] = newPos;

                    //stop the while cycle
                    alreadyAltered = true;

                } else {
                    //keep the cycle alive
                    alreadyAltered = false;
                }

            }
        }


        //change the generated positions
        StringBuilder stb = new StringBuilder(text);

        for (int i = 0; i < mistypePositions.length; i++) {
            //generate a random value in the character's range
            int shiftBy = rand.nextInt(mistypeRange) + 1; //the random is 0 inclusive, param. exclusive --> need the + 1


            char newChar = text.charAt(mistypePositions[i]);

            try {
                Double.parseDouble(text);

                //if the text was numeric
                if (newChar != '.') {
                    newChar = (char) (rand.nextInt(10) + 48); //#48 => '0'
                }

            } catch (NumberFormatException e) {
                //the text wasn't numeric
                if (rand.nextBoolean()) {
                    //shift right
                    newChar = (char) (newChar + shiftBy);
                } else {
                    //shift left
                    newChar = (char) (newChar - shiftBy);
                }
            }

            stb.setCharAt(mistypePositions[i], newChar);

        }
        retval = stb.toString();

        return retval;
    }
}
