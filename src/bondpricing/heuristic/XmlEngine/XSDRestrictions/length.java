/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public class length extends restriction {

    private int exactLength = 0;

    public length() {
        super();
    }

    public length(String field, String restrictionType, int exactLength) {
        super(field, restrictionType);
        if (exactLength < 0) {
            throw new Error("exactLength must be 0 or greater.");
        }
        this.exactLength = exactLength;
    }

    @Override
    public boolean validateValue(String value) {
        boolean valid = false;
        
        if(value != null){
            valid = value.length() == exactLength;
        }else{
            valid = false;
        }
        
        return valid;
    }

    /*
     * Formats the parameter to an exact length
     */
    @Override
    public String generateValue(String value) {

        String retval = null;

        if (value != null) {
            if (value.length() >= exactLength) {
                retval = value.substring(0, exactLength);
            } else {
                int missing_chars = exactLength - value.length();                
                
                for(int i=0; i<missing_chars; i++){
                    value += " ";
                }
                
                retval = value;
            }
        }else{
            retval = null;
        }
        
        return retval;
    }

    @Override
    public String getRestrictionName() {
        return "length";
    }

    @Override
    public String getRestrictionValue() {
        return Integer.toString(exactLength);
    }
}
