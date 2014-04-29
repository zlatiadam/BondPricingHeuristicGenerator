/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public class pattern extends restriction{
    
    private String pattern = null;
    
    public pattern(){
        super();
    }
    
    public pattern(String field, String restrictionType, String pattern){
        super(field, restrictionType);
        this.pattern = pattern;
    }
    
    @Override
    public boolean validateValue(String value) {
        boolean valid = false;
        
        if(value != null){
            valid = value.matches(pattern);
        }else{
            valid = false;
        }
        
        return valid;
    }

    @Override
    public String generateValue(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRestrictionName() {
        return "pattern";
    }

    @Override
    public String getRestrictionValue() {
        return pattern;
    }
    
}
