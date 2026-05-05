package qouteall.imm_ptl.peripheral;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import qouteall.imm_ptl.peripheral.portal_generation.IntrinsicPortalGeneration;

public class PeripheralModMain {
    
    public static Block portalHelperBlock;
    public static BlockItem portalHelperBlockItem;

    public static void init() {
        IntrinsicPortalGeneration.init();
    }
}