/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public class totalDigits extends restriction {

    private int exactDigits = 0;

    public totalDigits() {
        super();
    }

    public totalDigits(String field, String restrictionType, int exactDigits) {
        super(field, restrictionType);
        if (exactDigits < 0) {
            throw new Error("exactDigits must be 0 or more.");
        }
        this.exactDigits = exactDigits;
    }
    
    
    @Override
    public boolean validateValue(String value) {
        boolean valid = false;

        if (value != null) {
            if (value.matches("(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)")) {

                //if there were no errors check if it was an int ot double
                String[] parts = value.split("\\.");

                if (parts.length == 1) {
                    //it was an int, which automatically makes it valid
                    valid = false;
                } else if (parts.length == 2) {
                    //it was a double
                    valid = parts[1].length() == this.exactDigits;
                }
            }
        } else {
            valid = false;
        }

        return valid;
    }
    
    /*
     * Formats the parameter to have exactly exactDigits digits
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
                retval = value+".";
                
                for(int i=0; i<exactDigits; i++) retval += "0";
                
            } else if (parts.length == 2) {
                //it was a double
                
                if(parts[1].length() >= exactDigits){
                    //snap off the end of the digits
                    retval = parts[0]+"."+parts[1].substring(0, exactDigits);
                }else{
                    retval = value;
                    
                    for(int i=0; i<exactDigits - parts[1].length(); i++) retval += "0";
                }
            }
        } else {
            retval = null;
        }
        
        return retval;
    }

    @Override
    public String getRestrictionName() {
        return "totalDigits";
    }

    @Override
    public String getRestrictionValue() {
        return Double.toString(exactDigits);
    }
}
