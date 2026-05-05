package qouteall.imm_ptl.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import qouteall.imm_ptl.core.ducks.IEClientWorld;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.q_misc_util.Helper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;

@Environment(EnvType.CLIENT)
public class CHelper {
    
    private static int reportedErrorNum = 0;
    
    public static PlayerInfo getClientPlayerListEntry() {
        return Minecraft.getInstance().getConnection().getPlayerInfo(
            Minecraft.getInstance().player.getGameProfile().getId()
        );
    }
    
    public static Level getClientWorld(ResourceKey<Level> dimension) {
        return ClientWorldLoader.getWorld(dimension);
    }
    
    public static List<Portal> getClientGlobalPortal(Level world) {
        if (world instanceof ClientLevel) {
            return ((IEClientWorld) world).getGlobalPortals();
        }
        else {
            return null;
        }
    }
    
    public static Stream<Portal> getClientNearbyPortals(double range) {
        return IPMcHelper.getNearbyPortals(Minecraft.getInstance().player, range);
    }
    
    public static void checkGlError() {
        if (!IPGlobal.doCheckGlError) {
            return;
        }
        if (reportedErrorNum > 100) {
            return;
        }
        doCheckGlError();
    }
    
    public static void doCheckGlError() {
        int errorCode = GL11.glGetError();
        if (errorCode != GL_NO_ERROR) {
            Helper.err("OpenGL Error" + errorCode);
            new Throwable().printStackTrace();
            reportedErrorNum++;
        }
    }
    
    public static void printChat(String str) {
        Helper.log(str);
        printChat(Component.literal(str));
    }
    
    public static void printChat(Component text) {
        Minecraft.getInstance().gui.getChat().addMessage(text);
    }

    public static Vec3 getCurrentCameraPos() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }
    
    public static Iterable<Entity> getWorldEntityList(Level world) {
        if (!(world instanceof ClientLevel clientWorld)) {
            return (Iterable<Entity>) Collections.emptyIterator();
        }

        return clientWorld.entitiesForRendering();
    }
    
    public static void disableDepthClamp() {
        if (IPGlobal.enableClippingMechanism) {
            GL11.glDisable(GL32.GL_DEPTH_CLAMP);
        }
    }
    
    public static void enableDepthClamp() {
        if (IPGlobal.enableClippingMechanism) {
            GL11.glEnable(GL32.GL_DEPTH_CLAMP);
        }
    }
    
}
