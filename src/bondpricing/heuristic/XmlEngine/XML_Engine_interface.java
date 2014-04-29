/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine;

import java.util.HashMap;
import org.w3c.dom.Document;

/**
 *
 * @author Zlati
 */
public interface XML_Engine_interface {
    
    String[] generate_modified_xml(String XML_file_path, String[] Fields_to_change, String[] Changes_to_values);
    String get_value_of_field_from_xml(boolean xmlIsFile, String xmlSource, String field);
    HashMap<String,String> parse_xml_into_hashmap(boolean xmlIsFile, String xmlSource);
}
