/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

import java.util.Random;

/**
 *
 * @author Zlati
 */
public class minInclusive extends restriction {
    
    private double minval = 0;
    
    public minInclusive(){
        super();
    }
    
    public minInclusive(String field, String restrictionType, double minval){
        super(field, restrictionType);
        this.minval = minval;
    }

    
    @Override
    public boolean validateValue(String value) {
        
        boolean valid = false;
        
        if(value.matches("(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)")){
            double num = Double.parseDouble(value);
            
            valid = num >= minval;
        }else{
            valid = false;
        }
        
        return valid;
    }

    /*
     * Generates a value that is at least minval
     */
    @Override
    public String generateValue(String value) {
        
        //value provides the magnitude of the expected random number
        
        Random r = new Random();
        
        String retval = null;
        
        value = Double.toString(minval);
        
        if(value.matches("(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)")){
            String[] parts = value.split("\\.");
            
            int power = parts[0].replace("-", "").length();
            
            double e_power = Math.pow(10, power);
            
            double rand = r.nextDouble()*e_power;
            
            if(r.nextBoolean()){
                rand *= -1.0;
            }
            
            while(!(rand >= minval)){
                rand = r.nextDouble()*e_power;
                if(r.nextBoolean()) rand *= -1.0;
            }
            
            retval = Double.toString(rand);
        }else{
            retval = null;
        }
        
        return retval;
    }

    @Override
    public String getRestrictionName() {
        return "mindInclusive";
    }

    @Override
    public String getRestrictionValue() {
        return Double.toString(minval);
    }
    
}
