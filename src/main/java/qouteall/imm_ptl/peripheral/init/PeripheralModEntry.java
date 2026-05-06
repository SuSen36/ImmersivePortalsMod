package qouteall.imm_ptl.peripheral.init;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import qouteall.imm_ptl.peripheral.PeripheralModMain;

public class PeripheralModEntry implements ModInitializer {

    private static void registerBlockItems() {
        PeripheralModMain.portalHelperBlock = new Block(FabricBlockSettings.of(Material.METAL).noOcclusion()
            .isRedstoneConductor((a, b, c) -> false));

        Registry.register(
            Registry.BLOCK,
            new ResourceLocation("immersive_portals", "portal_helper"),
            PeripheralModMain.portalHelperBlock
        );

        PeripheralModMain.portalHelperBlockItem = new BlockItem(
            PeripheralModMain.portalHelperBlock,
            new Item.Properties()
        );

        Registry.register(
            Registry.ITEM,
            new ResourceLocation("immersive_portals", "portal_helper"),
            PeripheralModMain.portalHelperBlockItem
        );
    }
    
    @Override
    public void onInitialize() {
        PeripheralModEntry.registerBlockItems();
        
        PeripheralModMain.init();
    }
}
