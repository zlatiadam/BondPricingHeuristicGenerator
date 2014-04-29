/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

import bondpricing.heuristic.ConfigurationManager;
import bondpricing.heuristic.XmlEngine.XMLParser;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Zlati
 */
public class RestrictionManager {

    private Document XSD = null;
    private String xsdPath = null;
    private HashMap<String, ArrayList<restriction>> restrictions = null;
    private HashMap<String, ArrayList<Node>> nodesOfTypes = null;
    //stores the namespace defined in the XSD
    private String nameSpace = null;
    //collision detection is currently unimplemented, since there are validators that have this ability
    //later on it may be implemented if there'll be a need for it
    private HashMap<String, ArrayList<Collision>> collisions = null;

    public RestrictionManager() {
        this.restrictions = new HashMap<String, ArrayList<restriction>>();
        this.collisions = new HashMap<String, ArrayList<Collision>>();
        this.nodesOfTypes = new HashMap<String, ArrayList<Node>>();
    }

    public RestrictionManager(Document XSD, String xsdPath) {
        this.XSD = XSD;
        this.xsdPath = xsdPath;
        this.restrictions = new HashMap<String, ArrayList<restriction>>();
        this.collisions = new HashMap<String, ArrayList<Collision>>();
        this.nodesOfTypes = new HashMap<String, ArrayList<Node>>();

        if (XSD != null) {
            this.nameSpace = XSD.getDocumentElement().getNodeName().replaceFirst(":(.*)", "");
        }
    }

    public Document getXSD() {
        return XSD;
    }

    public void setXSD(Document XSD) {
        this.XSD = XSD;
    }

    public HashMap<String, ArrayList<restriction>> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(HashMap<String, ArrayList<restriction>> restrictions) {
        this.restrictions = restrictions;

    }
    /*
     * Extracts all the restrictions from the XSD schema
     */

    public void extractRestrictions() {
        if (XSD != null) {

            //query all the restriction elements (which may contain more restrictions) in the XSD
            NodeList rest_block_list = XSD.getElementsByTagName(nameSpace + ":restriction");

            //extract info about all the restrictions
            for (int i = 0; i < rest_block_list.getLength(); i++) {
                Node node = rest_block_list.item(i);

                //extract restriction base
                String rest_base_type = node.getAttributes().getNamedItem("base").getTextContent();

                //extract restrictions from the restriction block
                NodeList rest_list = node.getChildNodes();

                //if already found an enumeration, just add the new values to the group
                enumeration existing_enum = null;

                for (int j = 0; j < rest_list.getLength(); j++) {
                    Node rest_node = rest_list.item(j);

                    if (rest_node.getNodeType() == Node.ELEMENT_NODE) {

                        //cut off the namespace from the type 
                        String rest_type = rest_node.getNodeName().split(":")[1];

                        //get the value of the restriction
                        String val = rest_node.getAttributes().getNamedItem("value").getTextContent();



                        //get all the fields that have this restriction
                        ArrayList<String> fields = getFirstNamedParent(node);

                        //create the according restriction object for every field
                        for (int k = 0; k < fields.size(); k++) {


                            restriction rest = createRestrictionOfType(fields.get(k), rest_base_type, rest_type, val, existing_enum);

                            if (rest != null && rest.getRestrictionName().equals("enumeration")) {
                                existing_enum = (enumeration) rest;
                            }

                            //add the new restriction to the restriction list of the field
                            if (rest != null) {
                                if (restrictions.get(fields.get(k)) != null) {
                                    //the field is already in the Map

                                    restrictions.get(fields.get(k)).add(rest);
                                } else {
                                    ArrayList<restriction> al = new ArrayList<restriction>();
                                    al.add(rest);
                                    restrictions.put(fields.get(k), al);
                                }
                            }

                        }
                    }

                }
            }
        }
        
        //TODO:törölni
        //printRestrictions("Fid1Instruments/Fid1Bond/General/CouponClassCode");
        //System.out.println(restrictions.isEmpty());
    }

    /*
     * Simplified DFS, gets every xs:element or xs:attribute node's type - if it has one
     */
    public void extractNodesOfTypes() {

        if (XSD != null) {

            ArrayList<Node> DFSList = new ArrayList<Node>();

            //find the xs:element in the xs:schema which will be the root
            for (int i = 0; i < XSD.getChildNodes().getLength(); i++) {
                if (XSD.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                    DFSList.add(XSD.getChildNodes().item(i));
                    break;
                }
            }

            
            while (!DFSList.isEmpty()) {

                if (DFSList.get(0).hasAttributes()) {

                    Node typeAttributeNode = DFSList.get(0).getAttributes().getNamedItem("type");

                    if (typeAttributeNode != null) {

                        String typeName = typeAttributeNode.getTextContent();

                        if (!typeName.matches(nameSpace + ":(.*)")) {
                            //avoid built-in types

                            //if the typename contains a : extract the string that follows it
                            //System.out.println(typeName);
                            if (typeName.split(":").length > 1) {
                                typeName = typeName.split(":")[1];
                            }

                            if (nodesOfTypes.get(typeName) != null) {
                                //there are elements and/or attributes with this type already 
                                //just add this node to their list
                                nodesOfTypes.get(typeName).add(DFSList.get(0));
                            } else {
                                //there aren't any elements and/or attributes with this type
                                //create the first HashMap<String, ArrayList<Node>> element for it
                                ArrayList<Node> al = new ArrayList<Node>();
                                al.add(DFSList.get(0));

                                nodesOfTypes.put(typeName, al);
                            }
                        }
                    }

                }

                //if the node had a type attribute it has been added to the list

                //we're finished with this node, put its children at the end of the DFSList
                for (int i = 0; i < DFSList.get(0).getChildNodes().getLength(); i++) {
                    DFSList.add(DFSList.get(0).getChildNodes().item(i));
                }

                //remove the current node from the start of the list
                DFSList.remove(0);
            }

            //System.out.println(nodesOfTypes.get("orderidtype").size());

        }

    }

    public ArrayList<String> getFirstNamedParent(Node node) {

        ArrayList<String> retval = new ArrayList<String>();

        //System.out.println("recrusive call: "+node.getNodeName());

        if (node == null || node.getNodeName().equals(nameSpace + ":schema")) {
            //the node is the root node, it shouldn't return a node name, just a blank string
            //also this is the end of the recursion
            //System.out.println("Root reached");
            retval.add("");
        } else {

            //System.out.print("Node is not root, name:");

            boolean parentWithNameFound = false;

            while (!parentWithNameFound) {

                if (node.hasAttributes()) {
                    //check if the node has attributes

                    if (node.getAttributes().getNamedItem("name") != null) {
                        //if the node has a name attribute check it's type /xs:element, xs:attribute, xs:simpleType, xs:complexType/

                        String nodeName = node.getAttributes().getNamedItem("name").getTextContent();

                        //System.out.println(nodeName);

                        parentWithNameFound = true;

                        if (node.getNodeName().equals(nameSpace + ":element")) {
                            //
                            //System.out.println("Node was an xs:element");

                            ArrayList<String> al = getFirstNamedParent(node.getParentNode());

                            //System.out.println("parents discovered");

                            for (int i = 0; i < al.size(); i++) {
                                if (al.get(i).equals("")) {
                                    retval.add(nodeName);
                                } else {
                                    retval.add(al.get(i) + "/" + nodeName);
                                }
                                //System.out.println(al.get(i) + "/" + nodeName);
                            }

                        } else if (node.getNodeName().equals(nameSpace + ":attribute")) {

                            ArrayList<String> al = getFirstNamedParent(node.getParentNode());

                            //System.out.println("parents discovered");

                            for (int i = 0; i < al.size(); i++) {
                                if (al.get(i).equals("")) {
                                    retval.add(nodeName);
                                } else {
                                    retval.add(al.get(i) + "[" + nodeName + "]");
                                }
                                //System.out.println(al.get(i) + "[" + nodeName+"]");
                            }

                        } else if (node.getNodeName().equals(nameSpace + ":simpleType") || node.getNodeName().equals(nameSpace + ":complexType")) {

                            //get the elements/attributes that use this type
                            //System.out.println(nodeName);
                            ArrayList<Node> nodesOfThisType = nodesOfTypes.get(nodeName);
                            ArrayList<String> parentNodes = null;

                            if (nodesOfThisType != null) {

                                for (int i = 0; i < nodesOfThisType.size(); i++) {
                                    parentNodes = getFirstNamedParent(nodesOfThisType.get(i));

                                    //add the current nodes
                                    for (int j = 0; j < parentNodes.size(); j++) {
                                        retval.add(parentNodes.get(j));
                                    }
                                }
                            } else {
                                /*
                                 * if this block is reached that means there's no root element defined in the XSD
                                 * or a node between the root and a restriction
                                 */
                                //retval.add("");
                                //System.out.println("Warning: there's no root element defined in the XSD!");
                            }

                        }

                    } else {
                        node = node.getParentNode();
                    }
                } else {
                    node = node.getParentNode();
                }
            }
        }

        return retval;
    }

    /*
     * Extracts the restrictions assigned to an element
     */
    public ArrayList<restriction> getRestrictionsOfField(String field) {

        //find the approprite element node in the XSD with a simplified DFS
        Node node = XSD.getDocumentElement();

        ArrayList<Node> nodesToCheck = new ArrayList<Node>();

        nodesToCheck.add(node);

        while (!nodesToCheck.isEmpty()) {
            //check the type of the element
        }

        return null;
    }

    public restriction createRestrictionOfType(String field, String rest_base_type, String rest_type, String val, enumeration existing_enum) {

        restriction retval = null;

        if (rest_type.equals("enumeration")) {
            if (existing_enum == null) {
                retval = new enumeration(field, rest_base_type);
                ((enumeration) retval).insertValue(val);
            } else {
                existing_enum.insertValue(val);
            }

        } else if (rest_type.equals("fractionDigits")) {
            retval = new fractionDigits(field, rest_base_type, Integer.parseInt(val));

        } else if (rest_type.equals("length")) {
            retval = new length(field, rest_base_type, Integer.parseInt(val));

        } else if (rest_type.equals("maxExclusive")) {
            retval = new maxExclusive(field, rest_base_type, Double.parseDouble(val));

        } else if (rest_type.equals("maxInclusive")) {
            retval = new maxInclusive(field, rest_base_type, Double.parseDouble(val));

        } else if (rest_type.equals("maxLength")) {
            retval = new maxLength(field, rest_base_type, Integer.parseInt(val));

        } else if (rest_type.equals("minExclusive")) {
            retval = new minExclusive(field, rest_base_type, Double.parseDouble(val));

        } else if (rest_type.equals("minInclusive")) {
            retval = new minInclusive(field, rest_base_type, Double.parseDouble(val));

        } else if (rest_type.equals("minLength")) {
            retval = new minLength(field, rest_base_type, Integer.parseInt(val));

        } else if (rest_type.equals("pattern")) {
            retval = new pattern(field, rest_base_type, val);

        } else if (rest_type.equals("totalDigits")) {
            retval = new totalDigits(field, rest_base_type, Integer.parseInt(val));

        } else if (rest_type.equals("whiteSpace")) {
            retval = new whiteSpace(field, rest_base_type, val);
        }

        return retval;
    }

    /*
     * Collision detection is not implemented yet, since there's no need for it at the present
     */
    public boolean checkCollision() {
        throw new Error("Collision detection is not implemented yet, since there are validators with the same utility.");
    }

    ;

    public ArrayList<Collision> checkCollisionInField(String field) {
        throw new Error("Collision detection is not implemented yet, since there are validators with the same utility.");
    }

    public ArrayList<Collision> checkCollisionIfString(String field) {
        throw new Error("Collision detection is not implemented yet, since there are validators with the same utility.");
    }

    public ArrayList<Collision> checkCollisionIfDate(String field) {
        throw new Error("Collision detection is not implemented yet, since there are validators with the same utility.");
    }

    public ArrayList<Collision> checkCollisionIfNumeric(String field) {
        throw new Error("Collision detection is not implemented yet, since there are validators with the same utility.");
    }

    public ArrayList<Collision> checkCollisionIfOther(String field) {
        throw new Error("Collision detection is not implemented yet, since there are validators with the same utility.");
    }

    /*
     * Value generation based on restrictions
     */
    public String generateValue(String field, String value) {

        String retval = null;

        //check if there are any restrictions to the field
        if (restrictions.get(field) != null) {

            //check the type of the field (the base type of a restriction points it out)
            switch (getMajorDataTypeOfField(restrictions.get(field).get(0))) {
                case "string":
                    retval = generateValueIfString(field,value);
                    break;
                case "numeric":
                    retval = generateValueIfNumeric(field,value);
                    break;
                case "date":
                    retval = generateValueIfDate(field,value);
                    break;
                case "other":
                    retval = generateValueIfOther(field,value);
                    break;
                default:
                    retval = null;
                    break;
            }

            return retval;
        }

        return retval;
    }

    /*
     * Generating random characters doesn't help, because the black-box which calculates the prices
     * probably couldn't understand them --> we only have to generate strings which are defined by
     * enumerations
     */
    public String generateValueIfString(String field, String value) {

        String retval = null;

        //try to find an enumeration
        for (int i = 0; i < restrictions.get(field).size(); i++) {

            if (restrictions.get(field).get(i).getRestrictionName().equals("enumeration")) {
                //select a random value from the enumeration
                retval = restrictions.get(field).get(i).generateValue(null);

                //if there's an enumeration only items from that are allowed
                break;
            }

        }
        
        //only enumerations are handled in this version, feel free to upgrade it
        if(retval == null) retval = value;
        
        return retval;
    }

    public String generateValueIfDate(String field, String value) {
        String retval = null;

        //try to find an enumeration
        for (int i = 0; i < restrictions.get(field).size(); i++) {

            if (restrictions.get(field).get(i).getRestrictionName().equals("enumeration")) {
                //select a random value from the enumeration
                retval = restrictions.get(field).get(i).generateValue(null);

                //if there's an enumeration only items from that are allowed
                break;
            }

        }
        
        //only enumerations are handled in this version, feel free to upgrade it
        if(retval == null) retval = value;
        
        return retval;
        /*
        
         Random r = new Random();

         int year = 0, month = 1, day = 1, hrs = 0, mins = 0, secs = 0;
        
         Date min = null, max = null;
        
         if (restrictions.get(field) != null) {

         for (int i = 0; i < restrictions.get(field).size(); i++) {
         //create the appropriate date interval according to the restrictions
         if(restrictions.get(field).get(i).getRestrictionName().equals("maxExclusive")){
         String date = restrictions.get(field).get(i).getRestrictionValue();
         max = new Date(date);
                    
         }
         }
            
         }


        


         return null;
        
         */
        

    }

    public String generateValueIfNumeric(String field, String value) {
        String retval = null;

        //try to find an enumeration
        for (int i = 0; i < restrictions.get(field).size(); i++) {

            if (restrictions.get(field).get(i).getRestrictionName().equals("enumeration")) {
                //select a random value from the enumeration
                retval = restrictions.get(field).get(i).generateValue(null);

                //if there's an enumeration only items from that are allowed
                break;
            }

        }
        
        //only enumerations are handled in this version, feel free to upgrade it
        if(retval == null) retval = value;
        
        return retval;
        /*
        Random r = new Random();

        String retval = null;

        if (restrictions.get(field) != null) {

            Double min = 0.0, max = 0.0;

            for (int i = 0; i < restrictions.get(field).size(); i++) {

                if (restrictions.get(field).get(i).getRestrictionName().equals("enumeration")) {
                    //select a random value from the enumeration
                    retval = restrictions.get(field).get(i).generateValue(null);

                    //if there's an enumeration only items from that are allowed
                    break;
                }

                if (restrictions.get(field).get(i).getRestrictionName().equals("maxExclusive")) {
                    max = Double.parseDouble(restrictions.get(field).get(i).getRestrictionValue()) - 1;
                }
                if (restrictions.get(field).get(i).getRestrictionName().equals("maxInclusive")) {
                    max = Double.parseDouble(restrictions.get(field).get(i).getRestrictionValue());
                }
                if (restrictions.get(field).get(i).getRestrictionName().equals("minExclusive")) {
                    min = Double.parseDouble(restrictions.get(field).get(i).getRestrictionValue()) + 1;
                }
                if (restrictions.get(field).get(i).getRestrictionName().equals("minInclusive")) {
                    min = Double.parseDouble(restrictions.get(field).get(i).getRestrictionValue());
                }

                Integer interval = (int) (max - min);

                r.nextInt(interval);

                retval = Integer.toString(min.intValue() + interval);

            }

        }

        return retval;
        */
    }

    public String generateValueIfOther(String field, String value) {
        
        String retval = null;

        //try to find an enumeration
        for (int i = 0; i < restrictions.get(field).size(); i++) {

            if (restrictions.get(field).get(i).getRestrictionName().equals("enumeration")) {
                //select a random value from the enumeration
                retval = restrictions.get(field).get(i).generateValue(null);

                //if there's an enumeration only items from that are allowed
                break;
            }

        }
        
        //only enumerations are handled in this version, feel free to upgrade it
        if(retval == null) retval = value;
        
        return retval;
        
        /*
         *
        
        Random r = new Random();

        String retval = null;

        if (restrictions.get(field) != null) {

            for (int i = 0; i < restrictions.get(field).size(); i++) {

                String restrictionTypeWithoutNS = restrictions.get(field).get(i).getRestrictionType().replaceFirst(nameSpace, "");

                //check if there's an enumeration
                if (restrictions.get(field).get(i).getRestrictionName().equals("enumeration")) {
                    //select a random value from the enumeration
                    retval = restrictions.get(field).get(i).generateValue(null);

                    //if there's an enumeration only items from that are allowed
                    break;
                    
                } else {
                    
                    //generate a value based on the base of the restriction
                    switch (restrictionTypeWithoutNS) {

                        case "boolean":
                            retval = Boolean.toString(r.nextBoolean());
                            break;
                        case "double":
                            
                            
                            
                            break;
                        case "float":
                            break;
                        default:
                            throw new Error("Value generation not yet implemented for type " + restrictionTypeWithoutNS);

                    }
                }
            }

        }

        return retval;
        */
    }

    public String getMajorDataTypeOfField(restriction r) {

        //System.out.println(r);

        String retval = null;

        String restrictionTypeWithoutNS = r.getRestrictionType().replaceFirst(nameSpace, "");

        switch (restrictionTypeWithoutNS) {

            //string types
            case ":ENTITIES":
                retval = "string";
                break;
            case ":ENTITY":
                retval = "string";
                break;
            case ":ID":
                retval = "string";
                break;
            case ":IDREF":
                retval = "string";
                break;
            case ":IDREFS":
                retval = "string";
                break;
            case ":language":
                retval = "string";
                break;
            case ":Name":
                retval = "string";
                break;
            case ":NCName":
                retval = "string";
                break;
            case ":NMTOKEN":
                retval = "string";
                break;
            case ":NMTOKENS":
                retval = "string";
                break;
            case ":normalizedString":
                retval = "string";
                break;
            case ":QName":
                retval = "string";
                break;
            case ":string":
                retval = "string";
                break;
            case ":token":
                retval = "string";
                break;

            //numeric types
            case ":byte":
                retval = "numeric";
                break;
            case ":decimal":
                retval = "numeric";
                break;
            case ":int":
                retval = "numeric";
                break;
            case ":integer":
                retval = "numeric";
                break;
            case ":long":
                retval = "numeric";
                break;
            case ":negativeInteger":
                retval = "numeric";
                break;
            case ":nonNegativeInteger":
                retval = "numeric";
                break;
            case ":nonPositiveInteger":
                retval = "numeric";
                break;
            case ":positiveInteger":
                retval = "numeric";
                break;
            case ":short":
                retval = "numeric";
                break;
            case ":unsignedLong":
                retval = "numeric";
                break;
            case ":unsignedInt":
                retval = "numeric";
                break;
            case ":unsignedShort":
                retval = "numeric";
                break;
            case ":unsignedByte":
                retval = "numeric";
                break;

            //date types
            case ":date":
                retval = "date";
                break;
            case ":dateTime":
                retval = "date";
                break;
            case ":duration":
                retval = "date";
                break;
            case ":gDay":
                retval = "date";
                break;
            case ":gMonth":
                retval = "date";
                break;
            case ":gMonthDay":
                retval = "date";
                break;
            case ":gYear":
                retval = "date";
                break;
            case ":gYearMonth":
                retval = "date";
                break;
            case ":time":
                retval = "date";
                break;

            //other types
            default:
                retval = "other";
                break;
        }

        return retval;
    }
    
 

    public void printRestrictions(String field) {
        System.out.println(field);
        for (int i = 0; i < restrictions.get(field).size(); i++) {
            System.out.println(restrictions.get(field).get(i));
        }
    }
    
    public void printRestrictions(){
        for(Map.Entry<String, ArrayList<restriction>> entry : this.restrictions.entrySet()){
            System.out.println(entry.getKey());
        }
    }
}
