package qouteall.imm_ptl.core;

import com.mojang.blaze3d.platform.GlUtil;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import qouteall.imm_ptl.core.miscellaneous.GcMonitor;
import qouteall.imm_ptl.core.platform_specific.IPNetworkingClient;
import qouteall.imm_ptl.core.portal.PortalRenderInfo;
import qouteall.imm_ptl.core.portal.animation.ClientPortalAnimationManagement;
import qouteall.imm_ptl.core.portal.animation.StableClientTimer;
import qouteall.imm_ptl.core.render.*;
import qouteall.imm_ptl.core.render.context_management.CloudContext;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;
import qouteall.imm_ptl.core.render.optimization.GLResourceCache;
import qouteall.imm_ptl.core.render.optimization.SharedBlockMeshBuffers;
import qouteall.imm_ptl.core.teleportation.ClientTeleportationManager;
import qouteall.imm_ptl.core.teleportation.CollisionHelper;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.my_util.MyTaskList;

public class IPModMainClient {
    
    private static boolean fabulousWarned = false;
    
    public static void switchToCorrectRenderer() {
        if (PortalRendering.isRendering()) {
            return;
        }
        
        if (Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FABULOUS) {
            if (!fabulousWarned) {
                fabulousWarned = true;
                CHelper.printChat(Component.translatable("imm_ptl.fabulous_warning"));
            }
        }
        
        switch (IPGlobal.renderMode) {
            case normal -> switchRenderer(IPCGlobal.rendererUsingFrameBufferComposite);
            case compatibility -> switchRenderer(IPCGlobal.rendererUsingFrameBuffer);
            case debug -> switchRenderer(IPCGlobal.rendererDebug);
            case none -> switchRenderer(IPCGlobal.rendererDummy);
        }
    }
    
    private static void switchRenderer(PortalRenderer renderer) {
        if (IPCGlobal.renderer != renderer) {
            Helper.log("switched to renderer " + renderer.getClass());
            IPCGlobal.renderer = renderer;
        }
    }

    private static void showIntelVideoCardWarning() {
        IPGlobal.clientTaskList.addTask(MyTaskList.withDelayCondition(
            () -> Minecraft.getInstance().level == null,
            MyTaskList.oneShotTask(() -> {
                if (GlUtil.getVendor().toLowerCase().contains("intel")) {
                    CHelper.printChat(Component.translatable("imm_ptl.intel_warning"));
                }
            })
        ));
    }
    
    public static void init() {
        IPNetworkingClient.init();
        
        ClientWorldLoader.init();
        
        Minecraft.getInstance().execute(() -> {
            MyRenderHelper.init();
            
            IPCGlobal.rendererUsingStencil = new RendererUsingStencil();
            IPCGlobal.rendererUsingFrameBuffer = new RendererUsingFrameBuffer();
            IPCGlobal.rendererUsingFrameBufferComposite = new RendererUsingFrameBufferComposite();
            
            IPCGlobal.renderer = IPCGlobal.rendererUsingFrameBufferComposite;
            IPCGlobal.clientTeleportationManager = new ClientTeleportationManager();
        });

        CrossPortalEntityRenderer.init();
        
        GLResourceCache.init();
        
        CollisionHelper.initClient();
        
        PortalRenderInfo.init();
        
        CloudContext.init();
        
        SharedBlockMeshBuffers.init();
        
        GcMonitor.initClient();
        
        showIntelVideoCardWarning();
        
        StableClientTimer.init();
        
        ClientPortalAnimationManagement.init();
        
        VisibleSectionDiscovery.init();
        
        MyBuiltChunkStorage.init();

    }
}
