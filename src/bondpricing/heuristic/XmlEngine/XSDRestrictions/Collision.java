/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bondpricing.heuristic.XmlEngine.XSDRestrictions;

/**
 *
 * @author Zlati
 */
public class Collision {
    private String field = null;
    private restriction restriction1 = null;
    private restriction restriction2 = null;
    private int collisionLevel = 0;
    
    public Collision(){};
    
    public Collision(String field, restriction restriction1, restriction restriction2, int collisionLevel){
        this.field = field;
        this.restriction1 = restriction1;
        this.restriction2 = restriction2;
        this.collisionLevel = collisionLevel;
    }
    
    public int getCollisionLevel() {
        return collisionLevel;
    }

    public void setCollisionLevel(int collisionLevel) {
        this.collisionLevel = collisionLevel;
    }

    public restriction getRestriction1() {
        return restriction1;
    }

    public void setRestriction1(restriction restriction1) {
        this.restriction1 = restriction1;
    }

    public restriction getRestriction2() {
        return restriction2;
    }

    public void setRestriction2(restriction restriction2) {
        this.restriction2 = restriction2;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    
    
    
    public String getCollisionWarning(){
        String[] collisionEnum = new String[]{
            "partial collision",
            "mutual exclusion"
        };
        
        return "Warning! There's "+collisionEnum[collisionLevel]+" in the restrictions of the XML element "+field+" between "+
                restriction1.getRestrictionName()+" and "+restriction2.getRestrictionName()+"!";
    }
}
