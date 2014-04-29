/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine;

import bondpricing.heuristic.XmlEngine.DataModifier.GenerationData;
import java.io.File;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Zlati
 */
public class XMLEngine implements XML_Engine_interface{
    
    private XMLParser parser = null;
    private DataModifier modifier = null;
    private XMLGenerator generator = null;
    
    private boolean modifierIsFile = true;
    private String modifierSchema = null;
    
    private boolean generatorWriteToFile = true;
    private String generatorDestination = "/";
    
    
    public XMLEngine(){
        
    }
    
    public XMLEngine(boolean modifierIsFile, String modifierSchema, boolean generatorWTF, String generatorDest){
        this.modifierIsFile = modifierIsFile;
        this.modifierSchema = modifierSchema;
        
        this.generatorWriteToFile = generatorWTF;
        this.generatorDestination = generatorDest;
        
        this.parser = new XMLParser();
        
        String schemaPath = new File(modifierSchema).getPath();
        
        this.modifier = new DataModifier(parser.parseFileWithIncludes(modifierSchema),schemaPath);
        
        
        
        this.generator = new XMLGenerator(0, generatorWTF, generatorDest);
    }
    
    public void setParser(){
        
    }
    
    public void setModifier(boolean isFile, String schema, boolean generatorWTF, String generatorDest){
        this.modifierIsFile = isFile;
        this.modifierSchema = schema;
        modifier.setSchema(parser.parseFileWithIncludes(schema));
        
        this.generatorWriteToFile = generatorWTF;
        this.generatorDestination = generatorDest;
    }
    
    public void setGenerator(){
        
    }
    
    public Document parse(boolean isFile, String source){
        
        return parser.parse(isFile, source);
    }
    
    public GenerationData modify(Document xml, String[] field, String[] value){
        return modifier.modify(xml, field, value);
    }
    
    public String[] generate(Document xml, boolean wtf, String dest){
        return generator.generate(xml, wtf, dest);
    }
    
    @Override
    public String[] generate_modified_xml(String XML_file_path, String[] Fields_to_change, String[] Changes_to_values) {
        
        String[] generation_data = null;
        
        if(XML_file_path != null && Fields_to_change != null){
            Document original_xml = parse(true, XML_file_path);
            
            GenerationData modData = modify(original_xml, Fields_to_change, Changes_to_values);
            String[] genData = generate(modData.getXML(), generatorWriteToFile, generatorDestination);
            
            //the first value should be the path to the created file
            generation_data = new String[Fields_to_change.length+1];
            generation_data[0] = genData[1];
            
            //from the second value on should be the list of new values of the given fields
            for(int i=1; i<generation_data.length; i++)
                generation_data[i] = modData.getNewFieldValue()[i-1];
        }
        
        return generation_data;
    }

    @Override
    public String get_value_of_field_from_xml(boolean xmlIsFile, String xmlPath, String field) {
        /*Document xml = parser.parse(xmlIsFile, xmlPath);
        Node n = modifier.getNode(field, xml);
        return n.getTextContent();
        */
        return parse_xml_into_hashmap(xmlIsFile, xmlPath).get(field);
    }

    @Override
    public HashMap<String, String> parse_xml_into_hashmap(boolean xmlIsFile, String xmlSource) {
        return parser.parseIntoHashMap(parser.parse(xmlIsFile, xmlSource));
    }
    
}
