/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public class maxLength extends restriction {

    private int maxLength = 0;

    public maxLength() {
        super();
    }

    public maxLength(String field, String restrictionType, int maxLength) {
        super(field, restrictionType);
        if (maxLength < 0) {
            throw new Error("maxLength must be 0 or greater.");
        }
        this.maxLength = maxLength;
    }

    @Override
    public boolean validateValue(String value) {
        return value.length() <= maxLength;
    }
    
    /*
     * Formats the parameter to be maximum maxLength characters long
     */
    @Override
    public String generateValue(String value) {
        String retval = null;

        if (value != null) {
            if (maxLength > value.length()) {
                retval = value;
            } else {
                retval = value.substring(0, maxLength);
            }
        } else {
            retval = null;
        }

        return retval;
    }

    @Override
    public String getRestrictionName() {
        return "maxLength";
    }

    @Override
    public String getRestrictionValue() {
        return Double.toString(maxLength);
    }
}
