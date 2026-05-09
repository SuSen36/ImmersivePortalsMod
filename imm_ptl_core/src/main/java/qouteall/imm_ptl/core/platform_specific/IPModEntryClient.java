package qouteall.imm_ptl.core.platform_specific;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import org.apache.commons.lang3.Validate;
import qouteall.imm_ptl.core.IPModMainClient;
import qouteall.imm_ptl.core.portal.*;
import qouteall.imm_ptl.core.portal.global_portals.GlobalTrackedPortal;
import qouteall.imm_ptl.core.portal.global_portals.VerticalConnectingPortal;
import qouteall.imm_ptl.core.portal.global_portals.WorldWrappingPortal;
import qouteall.imm_ptl.core.portal.nether_portal.GeneralBreakablePortal;
import qouteall.imm_ptl.core.portal.nether_portal.NetherPortalEntity;
import qouteall.imm_ptl.core.render.LoadingIndicatorRenderer;
import qouteall.imm_ptl.core.render.PortalEntityRenderer;

import java.util.Arrays;

public class IPModEntryClient implements ClientModInitializer {
    
    public static void initPortalRenderers() {
        
        Arrays.stream(new EntityType<?>[]{
            Portal.entityType,
            NetherPortalEntity.entityType,
            EndPortalEntity.entityType,
            Mirror.entityType,
            BreakableMirror.entityType,
            GlobalTrackedPortal.entityType,
            WorldWrappingPortal.entityType,
            VerticalConnectingPortal.entityType,
            GeneralBreakablePortal.entityType
        }).peek(
            Validate::notNull
        ).forEach(
            entityType -> EntityRendererRegistry.register(
                entityType,
                (EntityRendererProvider) PortalEntityRenderer::new
            )
        );
        
        EntityRendererRegistry.register(
            LoadingIndicatorEntity.entityType,
            LoadingIndicatorRenderer::new
        );
        
    }
    
    @Override
    public void onInitializeClient() {
        IPModMainClient.init();
        
        initPortalRenderers();
        
        BlockRenderLayerMap.INSTANCE.putBlock(
            PortalPlaceholderBlock.instance, RenderType.translucent()
        );
    }
    
}
