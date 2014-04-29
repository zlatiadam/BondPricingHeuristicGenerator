/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

/**
 *
 * @author Zlati
 */
public class ConfigurationManager {
    //path of the configuration file which should be maintained
    private String path = null;
    
    //Map of the configurations
    private HashMap<String, String> Settings = null;

    /*
     * Default constructor
     */
    public ConfigurationManager() {
        this.Settings = new HashMap<String, String>();
    }

    /*
     * Constructor
     * @path: the path of the configuration file which should be maintained
     */
    public ConfigurationManager(String path) {
        this.path = path;
        this.Settings = new HashMap<String, String>();
    }
    
    /*
     * Gets the assigned value of a setting
     * @key: the key which identifies the setting in the Map
     */
    public String get(String key) {
        return Settings.get(key);
    }
    
    /*
     * Removes an existing setting
     * @key: the key which identifies which setting should be removed
     */
    public String remove(String key) {
        return Settings.remove(key);
    }
    
    /*
     * Adds or resets a settings
     * @key: the key of the new/existing setting
     * @value: the value to be assigned to the key
     */
    public void set(String key, String value) {
        Settings.put(key, value);
    }
    
    /*
     * Loads an existing configuration file
     * @path: the path of the configuration file which should be maintained
     */
    public void load(String path) throws IOException{

        String filepath = null;
        
        if (path != null) {
            filepath = path;
        } else if (this.path != null) {
            filepath = this.path;
        } else {
            //if no paths were given the default is the Config.conf in the 
            filepath = "Config.conf";
        }
        
        File f = new File(filepath);
        
        //if there is no configuration file create a default
        if(!f.exists()){
            setBasicSettings();
        }
        
        //buffered reader - reads the config file line-by-line
        BufferedReader br = null;
        
        try{
            br = new BufferedReader(new FileReader(filepath));
            
            String line;
            String[] key_value_pair = null;
            
            //indicates which engine's parameters are currently processed
            String engine = null;
            
            while ((line = br.readLine()) != null) {
                
                //check whether the row is not commented or not an empty line
                if(!line.startsWith("#") && !line.isEmpty()){
                    
                    //split the lines by the = to get the key-value pairs
                    key_value_pair = line.split("=");
                    
                    if(key_value_pair[0]!=null && key_value_pair[1]!=null)
                        //also trim the keys and values if spaces or tabs are used in the config
                        Settings.put(key_value_pair[0].trim(), key_value_pair[1].trim());
                }
            }
            
        }catch(IOException e){
            System.err.println(e.getMessage());
        }finally{
            br.close();
        }
 
        
        
    }

    
    
    public void setBasicSettings() throws IOException {
        String basicsettings = 
                  "# Configuration file for the bond pricing heuristic generator\n"
                + "\n"
                + "#-------------------\n"
                + "# XML Engine Section\n"
                + "#-------------------\n"
                + "\n"
                + "## The XSD schema will be provided in a file\n"
                + "# 0: false\n"
                + "# 1: true\n"
                + "ModifierSourceIsFile=1\n"
                + "\n"
                + "## The default source path of the XSD\n"
                + "ModifierSchemaSource=schema.xsd\n"
                + "\n"
                + "## The generated XML-s should be written to file\n"
                + "#  0: false\n"
                + "#  1: true\n"
                + "GeneratorWriteToFile=1\n"
                + "\n"
                + "## The default destination directory for the new XML-s\n"
                + "GeneratorDestination=";
        
        Writer writer = null;
        
        try{
            writer = new FileWriter("Config.conf");
            writer.write(basicsettings);
        }catch(IOException e){
            System.err.println(e.getMessage());
        }finally{
            writer.close();
        }
    }
    
    /*
     * saves the changed settings
     * NOT IMPLEMENTED, SINCE THIS FUNCTIONALITY IS NOT REQUIRED CURRENTLY
     */
    public void save(String path) {
    }
}
