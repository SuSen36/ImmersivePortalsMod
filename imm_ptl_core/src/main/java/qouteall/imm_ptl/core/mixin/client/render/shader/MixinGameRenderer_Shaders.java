package qouteall.imm_ptl.core.mixin.client.render.shader;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.IPCGlobal;
import qouteall.imm_ptl.core.render.MyRenderHelper;

import java.util.Map;

@Mixin(GameRenderer.class)
public class MixinGameRenderer_Shaders {
    @Shadow
    @Final
    private Map<String, ShaderInstance> shaders;
    
    @Inject(
        method = "Lnet/minecraft/client/renderer/GameRenderer;reloadShaders(Lnet/minecraft/server/packs/resources/ResourceManager;)V", at = @At("RETURN")
    )
    private void onLoadShaders(ResourceManager manager, CallbackInfo ci) {
        MyRenderHelper.loadShaderSignal.emit(
            manager, (shader) -> {
                shaders.put(shader.getName(), shader);
            }
        );
    }

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V",
            shift = At.Shift.AFTER
        )
    )
    private void onAfterLevelRendering(float partialTick, long nanoTime, PoseStack matrixStack, CallbackInfo ci) {
        if (IPCGlobal.renderer != null) {
            IPCGlobal.renderer.onAfterLevelRendering(matrixStack);
        }
    }
}
