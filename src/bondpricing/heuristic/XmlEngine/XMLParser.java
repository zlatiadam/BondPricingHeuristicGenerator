/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine;

import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Zlati
 */
public class XMLParser {
    /*
     * Default constructor
     */

    class FieldValuePair {

        public String field;
        public String value;

        public FieldValuePair(String field, String value) {
            this.field = field;
            this.value = value;
        }
    }

    public XMLParser() {
    }

    /*
     * Parses a provided XML source into an org.w3c.dom.Document object
     * @isFile: the provided source is a file (or it's a string containing an XML structure)
     * @source: if the isFile is true it's a filepath, else it's a string containing an XML structure
     * @return: the parsed Document
     */
    public Document parse(boolean isFile, String source) {

        Document xml_structure = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            //check whether the source is a file
            if (isFile) {

                //check whether the source file exists
                File f = new File(source);

                if (f.exists()) {
                    //read the file content
                    xml_structure = db.parse(f);
                } else {
                    System.out.println("XMLParser: the file '" + f.getPath() + "' does not exist.");
                }
            } else {
                //if the source is not a file set the content to that
                InputStream is = new StringBufferInputStream(source);

                xml_structure = db.parse(is);
            }

        } catch (Exception e) {
            //set the xml structure to null
            xml_structure = null;
            System.out.println(e.getMessage());
        }

        return xml_structure;
    }

    public HashMap<String, String> parseIntoHashMap(Document xml) {
        HashMap<String, String> retval = new HashMap<String, String>();

        ArrayList<FieldValuePair> nodes = processSubtree(xml.getDocumentElement());

        for (int i = 0; i < nodes.size(); i++) {
            retval.put(nodes.get(i).field, nodes.get(i).value);
            //System.out.println(nodes.get(i).field+"="+nodes.get(i).value);
        }

        return retval;
    }

    public ArrayList<FieldValuePair> processSubtree(Node node) {
        ArrayList<FieldValuePair> retval = new ArrayList<FieldValuePair>();



        if (node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.DOCUMENT_NODE) {



            if (node.getChildNodes().getLength() == 1) {

                retval.add(new FieldValuePair(node.getNodeName(), node.getTextContent()));

            } else {
                //for each child element get their subtree and put the current node's name before their's in the xpath
                for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                    ArrayList<FieldValuePair> subtree = processSubtree(node.getChildNodes().item(i));

                    for (int j = 0; j < subtree.size(); j++) {
                        retval.add(new FieldValuePair(node.getNodeName() + "/" + subtree.get(j).field, subtree.get(j).value));
                    }
                }

            }
        }

        return retval;
    }

    //merges xsds which point to another via the include tag
    public Document parseFileWithIncludes(String xsdSource) {

        ArrayList<String> alreadyIncluded = new ArrayList<String>();

        String XSD = "";

        try {

            String includeXSD = null;

            String fileToInclude = null;

            String filePath = new File(xsdSource).getParent();

            //read in the base file
            XSD = new Scanner(new File(xsdSource)).useDelimiter("\\Z").next();

            Pattern p = Pattern.compile("<.*?:include schemaLocation=\"(.*?)\"");
            Matcher m = p.matcher(XSD);


            //while there's something to include
            while (m.find()) {

                //get the file to include
                fileToInclude = m.group(1);

                //the xsd hasn't already been included, include it now

                //get the directory of the file if it doesn't have one specified in the filename
                if (!fileToInclude.contains("\\")) {
                    fileToInclude = filePath + "\\" + fileToInclude;
                }
                

                //check if it already has been included
                if (!alreadyIncluded.contains(fileToInclude)) {

                    //read the file
                    if (new File(fileToInclude).exists()) {
                        includeXSD = new Scanner(new File(fileToInclude)).useDelimiter("\\Z").next();

                        //strip the new content from tags
                        includeXSD = includeXSD.replaceAll("<\\?xml.*?\\?>", "");
                        includeXSD = includeXSD.replaceAll("<.*?:schema.*?>", "");

                        //somehow the regex above doesn't handle multilined text well with .*?
                        int p1 = includeXSD.indexOf("<xsd:schema");
                        int p2 = includeXSD.indexOf('>');
                        includeXSD = includeXSD.replace(includeXSD.substring(p1, p2 + 1), "");

                        //escape the $ character with a \ (java regex group char)
                        includeXSD = includeXSD.replaceAll("\\$", "\\\\\\$");

                        //replace the include tag with the filtered content of the file
                        XSD = XSD.replaceFirst("<.*?:include schemaLocation=\"(.*?)\" />", includeXSD);

                        //reset the matcher
                        m = p.matcher(XSD);

                        //add the XSD to the list of already included files
                        alreadyIncluded.add(fileToInclude);
                    } else {
                        System.out.println(fileToInclude + " is missing!");
                    }
                } else {
                    //the xsd is already included, remove the include tag and reset the matcher

                    //replace the include tag with the filtered content of the file
                    XSD = XSD.replaceFirst("<.*?:include schemaLocation=\"(.*?)\" />", "");

                    //reset the matcher
                    m = p.matcher(XSD);

                }
            }

            PrintWriter pw = new PrintWriter("joinoltXSD.xsd");
            pw.println(XSD);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return parse(false, XSD);

    }
}