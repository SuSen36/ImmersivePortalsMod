package qouteall.imm_ptl.core.portal;

public enum IntraClusterRelation {
    SAME(false, false),
    REVERSE(false, true);
    
    public final boolean isFlipped;
    public final boolean isReverse;
    
    IntraClusterRelation(boolean isFlipped, boolean isReverse) {
        this.isFlipped = isFlipped;
        this.isReverse = isReverse;
    }
}
