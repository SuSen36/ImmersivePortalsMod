package qouteall.imm_ptl.core.portal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import qouteall.imm_ptl.core.ducks.IEWorld;
import qouteall.imm_ptl.core.teleportation.ServerTeleportationManager;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public class PortalExtension {
    
    public static PortalExtension get(Portal portal) {
        if (portal.extension == null) {
            portal.extension = new PortalExtension();
        }
        return portal.extension;
    }
    
    public static void init() {
        Portal.clientPortalTickSignal.connect(portal -> {
            get(portal).tick(portal);
            
        });
        
        Portal.serverPortalTickSignal.connect(portal -> {
            get(portal).tick(portal);
            
        });
        
        Portal.readPortalDataSignal.connect((portal, tag) -> {
            get(portal).readFromNbt(tag);
        });
        
        Portal.writePortalDataSignal.connect((portal, tag) -> {
            get(portal).writeToNbt(tag);
        });
    }
    
    public double motionAffinity = 0;
    
    public boolean adjustPositionAfterTeleport = true;
    
    public boolean bindCluster = true;
    
    @Nullable
    public UUID reversePortalId;
    
    @Nullable
    public Portal reversePortal;
    
    public PortalExtension() {
    
    }
    
    private void readFromNbt(CompoundTag compoundTag) {
        if (compoundTag.contains("motionAffinity")) {
            motionAffinity = compoundTag.getDouble("motionAffinity");
        }
        else {
            motionAffinity = 0;
        }
        if (compoundTag.contains("adjustPositionAfterTeleport")) {
            adjustPositionAfterTeleport = compoundTag.getBoolean("adjustPositionAfterTeleport");
        }
        else {
            adjustPositionAfterTeleport = true;
        }
        
        if (compoundTag.contains("bindCluster")) {
            bindCluster = compoundTag.getBoolean("bindCluster");
        }
        else {
            bindCluster = true;
        }
        
        if (compoundTag.contains("reversePortalId")) {
            reversePortalId = compoundTag.getUUID("reversePortalId");
        }
        else {
            reversePortalId = null;
        }
    }
    
    private void writeToNbt(CompoundTag compoundTag) {
        if (motionAffinity != 0) {
            compoundTag.putDouble("motionAffinity", motionAffinity);
        }
        compoundTag.putBoolean("adjustPositionAfterTeleport", adjustPositionAfterTeleport);
        compoundTag.putBoolean("bindCluster", bindCluster);
        if (reversePortalId != null) {
            compoundTag.putUUID("reversePortalId", reversePortalId);
        }
    }
    
    private void tick(Portal portal) {
        if (portal.level.isClientSide()) {
            updateClusterStatusClient(portal);
        }
        else {
            updateClusterStatusServer(portal);
        }
    }
    
    private void updateClusterStatusServer(Portal portal) {
        boolean needsUpdate = false;
        
        if (bindCluster) {
            if (reversePortal != null) {
                if (reversePortal.isRemoved()) {
                    reversePortal = null;
                }
            }
            
            if (reversePortalId != null) {
                if (reversePortal == null) {
                    Entity e = ((IEWorld) portal.getDestWorld()).portal_getEntityLookup().get(reversePortalId);
                    if (e instanceof Portal p) {
                        reversePortal = p;
                    }
                    else {
                        if (portal.isOtherSideChunkLoaded()) {
                            reversePortalId = null;
                            needsUpdate = true;
                        }
                    }
                }
            }
            if (reversePortalId == null) {
                reversePortal = PortalManipulation.findReversePortal(portal);
                if (reversePortal != null) {
                    reversePortalId = reversePortal.getUUID();
                    needsUpdate = true;
                }
            }
        }
        else {
            reversePortal = null;
            reversePortalId = null;
        }
        
        if (reversePortal != null) {
            PortalExtension.get(reversePortal).bindCluster = true;
        }
        
        if (needsUpdate) {
            portal.reloadAndSyncToClient();
        }
    }
    
    
    private void updateClusterStatusClient(Portal portal) {
        if (bindCluster) {
            if (reversePortalId != null) {
                Entity e = ((IEWorld) portal.getDestWorld()).portal_getEntityLookup().get(reversePortalId);
                if (e instanceof Portal p) {
                    reversePortal = p;
                }
            }
            else {
                reversePortal = null;
            }
        }
        else {
            reversePortal = null;
        }
    }
    
    public void rectifyClusterPortals(Portal portal, boolean sync) {
        
        portal.animation.defaultAnimation.inverseScale = false;
        
        if (reversePortal != null) {
            reversePortal = ServerTeleportationManager.teleportRegularEntityTo(
                reversePortal,
                portal.getDestDim(),
                portal.getDestPos()
            );
            reversePortalId = reversePortal.getUUID();
            
            reversePortal.dimensionTo = portal.getOriginDim();
            reversePortal.setOriginPos(portal.getDestPos());
            reversePortal.setDestination(portal.getOriginPos());
            
            reversePortal.axisW = portal.transformLocalVecNonScale(portal.axisW);
            reversePortal.axisH = portal.transformLocalVecNonScale(portal.axisH.scale(-1));
            reversePortal.scaling = 1.0 / portal.scaling;
            if (portal.rotation != null) {
                reversePortal.rotation = portal.rotation.copy();
                reversePortal.rotation.conj();
            }
            else {
                reversePortal.rotation = null;
            }
            
            if (reversePortal.specialShape == null) {
                reversePortal.width = portal.width * portal.getScale();
                reversePortal.height = portal.height * portal.getScale();
            }
            
            PortalManipulation.copyAdditionalProperties(reversePortal, portal, false);
            
            reversePortal.isBifaced = portal.isBifaced;
            reversePortal.isBidirectional = portal.isBidirectional;
            
            reversePortal.animation.defaultAnimation.inverseScale = true;
            
            if (sync) {
                reversePortal.reloadAndSyncToClient();
            }
        }
    }
    
    public static void initializeClusterBind(
        Portal portalA, Portal portalB
    ) {
        get(portalA).bindCluster = true;
        get(portalB).bindCluster = true;
        
        get(portalA).reversePortalId = portalB.getUUID();
        get(portalB).reversePortalId = portalA.getUUID();
    }
    
    public static void forClusterPortals(Portal portal, Consumer<Portal> func) {
        func.accept(portal);
        
        forConnectedPortals(portal, func);
    }
    
    public static void forConnectedPortals(Portal portal, Consumer<Portal> func) {
        PortalExtension extension = PortalExtension.get(portal);
        if (extension.reversePortal != null) {
            func.accept(extension.reversePortal);
        }
    }
    
}
