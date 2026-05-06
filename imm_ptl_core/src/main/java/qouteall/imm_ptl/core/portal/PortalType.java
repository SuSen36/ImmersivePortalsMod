package qouteall.imm_ptl.core.portal;

import net.minecraft.util.StringRepresentable;

public enum PortalType implements StringRepresentable {
    nether,
    end,
    flat;
    
    @Override
    public String getSerializedName() {
        return name();
    }
}
