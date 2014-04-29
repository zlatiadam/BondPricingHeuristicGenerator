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
public class maxInclusive extends restriction{
    
    private double maxval = 0;
    
    public maxInclusive(){
        super();
    }
    
    public maxInclusive(String field, String restrictionType, double maxval){
        super(field, restrictionType);
        this.maxval = maxval;
    }

    @Override
    public boolean validateValue(String value) {
        boolean valid = false;
        
        if(value.matches("(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)")){
            double num = Double.parseDouble(value);
            
            valid = num <= maxval;
        }else{
            valid = false;
        }
        
        return valid;
    }
    
    /*
     * Generates a value that is maximum maxval
     */
    @Override
    public String generateValue(String value) {
        //value provides the magnitude of the expected random number
        
        Random r = new Random();
        
        String retval = null;
        
        value = Double.toString(maxval);
        
        if(value.matches("(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)")){
            String[] parts = value.split("\\.");
            
            int power = parts[0].replace("-", "").length();
            
            double e_power = Math.pow(10, power);
            
            double rand = r.nextDouble()*e_power;
            
            if(r.nextBoolean()){
                rand *= -1.0;
            }
            
            while(!(rand <= maxval)){
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
        return "maxInclusive";
    }

    @Override
    public String getRestrictionValue() {
        return Double.toString(maxval);
    }
    
}
