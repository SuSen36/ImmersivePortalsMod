package qouteall.imm_ptl.core.mixin.client.render.shader;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import qouteall.imm_ptl.core.ducks.IEShader;

import javax.annotation.Nullable;

@Mixin(ShaderInstance.class)
public abstract class MixinShaderInstance implements IEShader {
    @Shadow
    @Nullable
    public abstract Uniform getUniform(String name);

    @Nullable
    @Override
    public Uniform ip_getClippingEquationUniform() {
        return null;
    }
}
