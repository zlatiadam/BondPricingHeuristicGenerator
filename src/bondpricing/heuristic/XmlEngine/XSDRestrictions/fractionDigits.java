/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public class fractionDigits extends restriction {

    private int maxDigits = 0;

    public fractionDigits() {
        super();
    }
    
    public fractionDigits(String field, String restrictionType, int maxDigits) {
        super(field, restrictionType);
        if (maxDigits < 0) {
            throw new Error("fractionDigits must be 0 or greater.");
        }
        this.maxDigits = maxDigits;
    }

    @Override
    public boolean validateValue(String value) {

        boolean valid = false;
        
        //check if the value is numeric
        if(value.matches("(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)")){
            
            //if there were no errors check if it was an int ot double
            String[] parts = value.split("\\.");
            
            if (parts.length == 1) {
                //it was an int, which automatically makes it valid
                valid = true;
            } else if (parts.length == 2) {
                //it was a double
                valid = parts[1].length() <= this.maxDigits;
                
            }
        } else {
            valid = false;
        }

        return valid;

    }
    
    /*
     * Formats the parameter to have maximum maxDigits digits
     */
    @Override
    public String generateValue(String value) {
        
        String retval = null;
        
        if(value.matches("(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)")) {
            //try to parse the string into a double number
            

            //if there were no errors check if it was an int ot double
            String[] parts = value.split("\\.");
            if (parts.length == 1) {
                //it was an int, which automatically makes it valid
                retval = value;
            } else if (parts.length == 2) {
                //it was a double
                
                int min = 0;
                if(maxDigits < parts[1].length()) 
                    min = maxDigits;
                else
                    min = parts[1].length();
                
                retval = parts[0]+"."+parts[1].substring(0, min);
            }
        } else {
            retval = null;
        }
        
        return retval;
    }

    @Override
    public String getRestrictionName() {
        return "fractionDigits";
    }

    @Override
    public String getRestrictionValue() {
        return Integer.toString(maxDigits);
    }
}
