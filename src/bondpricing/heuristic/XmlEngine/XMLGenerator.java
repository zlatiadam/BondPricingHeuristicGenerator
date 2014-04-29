/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine;

import java.io.File;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;


/**
 *
 * @author Zlati
 */
public class XMLGenerator {

    private int counter = 0;
    private boolean writeGeneratedFilesToDisk = false;
    private String defaultFileDestinationPath = null;

    public XMLGenerator() {
    }

    public XMLGenerator(int counterStartPosition, boolean wtf, String defaultDestination) {

        if (counterStartPosition < 0) {
            counterStartPosition = 0;
        }
        if (defaultDestination == null || defaultDestination.equals("/")) {
            defaultDestination = "";
        }

        this.counter = counterStartPosition;
        this.writeGeneratedFilesToDisk = wtf;
        this.defaultFileDestinationPath = defaultDestination;
    }

    public String[] generate(Document xml, Boolean wtf, String dest) {

        String[] retval = new String[2];

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Source input = new DOMSource(xml);

            if (wtf == null) {
                wtf = writeGeneratedFilesToDisk;
            }
            
            if (wtf) {
                if (dest == null) {
                    dest = this.defaultFileDestinationPath;
                }
                if (!dest.equals("") && !dest.equals("/")) {
                    dest += "\\";
                }
                
                

                //generate an XML file
                Result output = new StreamResult(new File(dest + "modified_xml_" + counter + ".xml"));
                transformer.transform(input, output);

                retval[1] = dest + "modified_xml_" + counter + ".xml";
            }


            //generate a string
            StringWriter writer = new StringWriter();

            Result output = new StreamResult(writer);
            transformer.transform(input, output);

            retval[0] = writer.getBuffer().toString().replaceAll("\n|\r", "");
            
            counter++;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return retval;
    }
}
