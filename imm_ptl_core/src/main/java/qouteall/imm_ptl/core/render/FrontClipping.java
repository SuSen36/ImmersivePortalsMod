package qouteall.imm_ptl.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.CHelper;
import qouteall.imm_ptl.core.IPCGlobal;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalLike;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;
import qouteall.q_misc_util.my_util.Plane;

public class FrontClipping {
    private static final Minecraft client = Minecraft.getInstance();
    private static double[] activeClipPlaneEquation;

    private static double[] activeClipPlaneForEntities;

    public static boolean isClippingEnabled = false;

    public static void disableClipping() {
        isClippingEnabled = false;
        activeClipPlaneEquation = null;
    }

    private static void enableClipping() {
        isClippingEnabled = true;
    }

    public static void updateInnerClipping(PoseStack matrixStack) {
        if (PortalRendering.isRendering()) {
            setupInnerClipping(PortalRendering.getRenderingPortal(), false, matrixStack);
        }
        else {
            disableClipping();
        }
    }

    public static void setupInnerClipping(
        PortalLike portalLike, boolean doCompensate, PoseStack matrixStack
    ) {
        if (!IPCGlobal.useFrontClipping) {
            return;
        }

        final Plane clipping = portalLike.getInnerClipping();

        if (clipping != null) {
            activeClipPlaneEquation = getClipEquationInner(doCompensate, clipping.pos, clipping.normal);
            activeClipPlaneForEntities = transformClipEquation(activeClipPlaneEquation, matrixStack);

            enableClipping();
        }
        else {
            activeClipPlaneEquation = null;
            disableClipping();
        }
    }

    private static double[] transformClipEquation(
        double[] equation, PoseStack matrixStack
    ) {
        Vector4f eq =
            new Vector4f((float) equation[0], (float) equation[1], (float) equation[2], (float) equation[3]);
        Matrix4f m = matrixStack.last().pose().copy();
        m.invert();
        m.transpose();
        eq.transform(m);
        return new double[]{eq.x(), eq.y(), eq.z(), eq.w()};
    }

    private static double[] getClipEquationInner(
        boolean doCompensate, Vec3 clippingPoint, Vec3 clippingDirection
    ) {

        Vec3 cameraPos = CHelper.getCurrentCameraPos();

        Vec3 planeNormal = clippingDirection;

        double correction;

        if (doCompensate) {
            correction = clippingPoint.subtract(cameraPos)
                .dot(clippingDirection) / 150.0;
        }
        else {
            correction = 0;
        }

        Vec3 portalPos = clippingPoint
            .subtract(planeNormal.scale(correction))
            .subtract(cameraPos);

        double c = planeNormal.scale(-1).dot(portalPos);

        return new double[]{
            planeNormal.x, planeNormal.y, planeNormal.z, c
        };
    }

    public static void setupOuterClipping(PoseStack matrixStack, PortalLike portalLike) {
        if (!IPCGlobal.useFrontClipping) {
            return;
        }

        if (portalLike instanceof Portal) {
            activeClipPlaneEquation = getClipEquationOuter(((Portal) portalLike));
            activeClipPlaneForEntities = transformClipEquation(activeClipPlaneEquation, matrixStack);
            enableClipping();
        }
        else {
            activeClipPlaneEquation = null;
            disableClipping();
        }
    }

    private static double[] getClipEquationOuter(Portal portal) {
        Vec3 planeNormal = portal.getNormal();

        Vec3 portalPos = portal.getOriginPos()
            .subtract(client.gameRenderer.getMainCamera().getPosition());

        double c = planeNormal.scale(-1).dot(portalPos);

        return new double[]{
            planeNormal.x, planeNormal.y, planeNormal.z, c
        };
    }

    public static double[] getActiveClipPlaneEquation() {
        return activeClipPlaneEquation;
    }

    public static double[] getActiveClipPlaneEquationForEntities() {
        return activeClipPlaneForEntities;
    }
}
