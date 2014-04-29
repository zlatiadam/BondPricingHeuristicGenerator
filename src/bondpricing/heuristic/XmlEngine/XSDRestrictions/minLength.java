/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public class minLength extends restriction{
    
    private int minLength = 0;
    
    public minLength(){
        super();
    }
    
    public minLength(String field, String restrictionType, int minLength){
        super(field, restrictionType);
        if(minLength < 0) throw new Error("minLength must be 0 or more.");
        this.minLength = minLength;
    }
    
    @Override
    public boolean validateValue(String value) {
        return value.length() >= minLength;
    }
    
    /*
     * Formats the parameter to be at least minLength characters long
     */
    @Override
    public String generateValue(String value) {
        
        String retval = null;

        if (value != null) {
            if (value.length() >= minLength) {
                retval = value;
            } else {
                int missing_chars = minLength - value.length();                
                
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
        return "minLength";
    }

    @Override
    public String getRestrictionValue() {
        return Integer.toString(minLength);
    }
    
}
