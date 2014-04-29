/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

import java.util.ArrayList;
import java.util.Random;
import org.w3c.dom.Document;

/**
 *
 * @author Zlati
 */
public class enumeration extends restriction{
    
    ArrayList<String> enumeration = null;
    
    /*
     * Default constructor
     */
    public enumeration(){
        super();
        enumeration = new ArrayList<String>();
    }
    
    public enumeration(String field, String restrictionType){
        super(field, restrictionType);
        enumeration = new ArrayList<String>();
        //System.out.println(restrictionType);
    }
    
    public enumeration(String field, String restrictionTpye, ArrayList<String> enumeration){
        super(field, restrictionTpye);
        this.enumeration = enumeration;
    }
    
    
    @Override
    public boolean validateValue(String value) {
        
        boolean valid = false;
        
        if(this.enumeration != null){
            
            valid = enumeration.contains(value);
            
        }else{
            valid = false;
        }
        
        return valid;
    }

    @Override
    public String generateValue(String value) {
        //value doesn't matter
        String rndval = null;
        
        Random rgen = new Random();
       
        int rndnum = rgen.nextInt(enumeration.size()-1);
        
        rndval = enumeration.get(rndnum);
        
        return rndval;
    }

    @Override
    public String getRestrictionName() {
        return "enumeration";
    }

    public void insertValue(String value){
        enumeration.add(value);
    }
    
    public void printValues(){
        for(int i=0; i<enumeration.size(); i++){
            System.out.println(enumeration.get(i));
        }
    }

    @Override
    public String getRestrictionValue() {
        //enumerations are not strings, but arrays of strings...
        return null;
    }
}
