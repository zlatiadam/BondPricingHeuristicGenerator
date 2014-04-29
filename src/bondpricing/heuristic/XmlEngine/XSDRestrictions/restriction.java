/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public abstract class restriction {
    
    private String field = null;
    private String restrictionType = null;
    
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestriction_type(String restriction_type) {
        this.restrictionType = restriction_type;
    }
    
    public restriction() {
    }
    
    public restriction(String field, String restrictionType){
        this.field = field;
        this.restrictionType = restrictionType;
    }

    /*
     * Validates the value parameter agains the rules of the restriction
     * @value: the value to be validated
     * @return: the result of the validation
     */
    public abstract boolean validateValue(String value);

    /*
     * Generates a random value that is valid based in the restriction
     * @return: the generated value
     */
    public abstract String generateValue(String value);
    
    public abstract String getRestrictionName();
    
    public abstract String getRestrictionValue();
}
