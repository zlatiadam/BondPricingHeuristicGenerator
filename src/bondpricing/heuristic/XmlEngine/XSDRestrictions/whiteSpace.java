/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public class whiteSpace extends restriction {
    
    private String handle;
    
    public whiteSpace(){
        super();
    }
    
    public whiteSpace(String field, String restrictionType, String handle){
        super(field, restrictionType);
        if(handle==null || handle.isEmpty()) throw new Error("whiteSpace handler must be set.");
        this.handle = handle;
    }
    
    @Override
    public boolean validateValue(String value) {
        boolean valid = false;
        
        if(value != null){
            valid = value.equals(generateValue(value));
        }else{
            valid = false;
        }
        
        return valid;
    }
    
    /*
     * Formats the value according to the handle
     */
    @Override
    public String generateValue(String value) {
        String retval = null;
        if(value!=null && !value.isEmpty()){
            
            if(handle == "preserve"){
                //doesn't do anything
                retval = value;
            }else if(handle == "replace"){
                //replaces every whitespace char with space
                retval = value.replaceAll("\\s+", " ");
            }else if(handle == "collapse"){
                //removes all whitespace characters
                retval = value.replaceAll("\\s+", "");
            }
        }else{
            retval = null;
        }
        
        return retval;
    }

    @Override
    public String getRestrictionName() {
        return "whiteSpace";
    }

    @Override
    public String getRestrictionValue() {
        return handle;
    }
    
}
