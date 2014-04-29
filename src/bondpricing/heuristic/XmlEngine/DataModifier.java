/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine;

import bondpricing.heuristic.ConfigurationManager;
import bondpricing.heuristic.XmlEngine.XSDRestrictions.RestrictionManager;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Zlati
 */
public class DataModifier {

    private Document schema = null;
    private String schemaPath = null;
    private RestrictionManager restrictionManager = null;

    class GenerationData {

        private Document XML = null;
        private String[] newFieldValues = null;

        public GenerationData() {
        }

        public GenerationData(Document XML, String[] newFieldValues) {
            this.XML = XML;
            this.newFieldValues = newFieldValues;
        }

        public Document getXML() {
            return XML;
        }

        public String[] getNewFieldValue() {
            return newFieldValues;
        }
    }

    /*
     * Default constructor
     */
    public DataModifier() {
    }

    /*
     * If the configuration is set to have a default schema to use
     * this constructor will automatically load it
     */
    public DataModifier(ConfigurationManager cm) {

        if (cm != null) {

            if (cm.get("ModifierSourceIsFile").equals("true")) {

                if (cm.get("ModifierSchemaSource") != null) {
                    XMLParser parser = new XMLParser();
                    this.schema = parser.parse(true, cm.get("ModifierSchemaSource"));
                }
            }
        }


    }

    public DataModifier(Document schema, String schemaPath) {
        this.schema = schema;
        this.schemaPath = schemaPath;
        this.restrictionManager = new RestrictionManager(schema,schemaPath);

        //load the restrictions in the manager
        this.restrictionManager.extractNodesOfTypes();
        this.restrictionManager.extractRestrictions();

    }

    public Document setSchema(Document schema) {
        Document old_schema = this.schema;
        this.schema = schema;
        this.restrictionManager.setXSD(schema);

        return old_schema;
    }

    public Document getSchema(Document schema) {
        return this.schema;
    }

    public void loadSchema(boolean isFile, String source) {
        XMLParser parser = new XMLParser();
        this.schema = parser.parse(isFile, source);
        this.restrictionManager.setXSD(schema);

        //load the restrictions in the manager
        this.restrictionManager.extractNodesOfTypes();
        this.restrictionManager.extractRestrictions();
    }

    public GenerationData modify(Document xml, String[] fields, String[] values) {

        GenerationData generationData = null;
       
        
        //check whether there are data
        if (xml != null && fields != null && fields.length > 0) {

            for (int i = 0; i < fields.length; i++) {
                //retrive the node specified by field "xpath"
                Node node = getNode(fields[i], xml);

                if (node != null) {
                    //if the node was found modify its value

                    if (values != null && values[i]!=null) {
                        //validate the value against the schema

                        //System.out.println(validateValue(field, value));

                                                

                    
                        //generate a random value based on the schema
                        String randomValue = restrictionManager.generateValue(fields[i], values[i]);
                        
                        //if no restriction applies to the field set the value to the one that was defined in the
                        //values parameter (at this point that parameter has already been "mistyped"
                        if(randomValue == null){
                            randomValue = values[i];
                        }
                        
                        node.setTextContent(randomValue);
                        values[i] = randomValue;
                        
                        node.setTextContent(values[i]);
                    }
                }


            }
            generationData = new GenerationData(xml, values);
            //System.out.println(getNode(field,xml).getTextContent());
            
        } else {
            //if there were no data return null
            generationData = null;
        }

        return generationData;
    }

    /*
     * Returns the node from the XML Document structure
     * identified by field
     * @field: the XPath-like identifier of a node
     * @xml: the base xml that should be searched for a node
     * @return: the found node or null
     */
    public Node getNode(String field, Document xml) {
        //if there are data the modification should be done

        //extract all the parent node names from the field parameter
        /*
         * The field parameter is in the following format:
         * Node1/Node2/Node3/...Node_n
         * Node1 is the parent of Node2 and also the root;
         * Node2 is the parent of Node3, and so on...
         */

        //extract the node list from field
        String[] parent_node_list = field.split("/");

        //set the first node to the root
        Node node = xml.getDocumentElement();

        if (!node.getNodeName().equals(parent_node_list[0])) {
            //if the root in field and the xml doesn't match the whole search is useless, null should be returned
            node = null;
        } else {
            //try to find the element in the xml
            //the 0th iteration is the "if" above which checks the roots



            for (int i = 1; i < parent_node_list.length; i++) {
                //iterate through all the parent nodes defined in field

                Pattern attribute = Pattern.compile("\\[(.*)\\]");
                Matcher m = attribute.matcher(parent_node_list[i]);

                //the current element defined in field has an attribute which should be modified

                String node_value, attribute_value = null;

                if (m.find()) {
                    node_value = parent_node_list[i].substring(0, m.start());
                    attribute_value = parent_node_list[i].substring(m.start() + 1, m.end() - 1);
                } else {
                    node_value = parent_node_list[i];
                }


                if (node.getChildNodes() != null) {

                    for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                        //iterate through all the children of the given parent node to find the next defined in field

                        if (node.getChildNodes().item(j).getNodeName().equals(node_value)) {
                            //set the current node to the proper child node
                            node = node.getChildNodes().item(j);

                            if (attribute_value != null) {
                                //there's an attribute of this node which has to be returned
                                node = node.getAttributes().getNamedItem(attribute_value);
                            }

                            //break the inner cycle, the node in this depth is found
                            break;
                        } else if (j == node.getChildNodes().getLength() - 1) {
                            //the node specified in field couldn't be found, the XML doesn't contain it
                            node = null;
                        }

                    }
                }

                if (node == null) {
                    //break the cycle, since one of the nodes couldn't be found
                    break;
                }

            }
        }

        return node;
    }

    /*
     * Validates the given value of a field against the XSD schema
     */
    private boolean validateValue(String field, String value) {



        throw new Error("Uninmplemented method!");
    }

    /*
     * Collects all the restrictions defined in the XSD schema
     */
    private void generateRestrictionTable(Document xsd) {
    }
    /*
     * Unused functions
     * These functions are not used in the current version
     */
}
