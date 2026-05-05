package qouteall.imm_ptl.core.api;

import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.my_util.DQuaternion;

public class PortalAPI {

    public static void setPortalOrthodoxShape(Portal portal, Direction facing, AABB portalArea) {
        Tuple<Direction, Direction> directions = Helper.getPerpendicularDirections(facing);
        
        Vec3 areaSize = Helper.getBoxSize(portalArea);
        
        AABB boxSurface = Helper.getBoxSurface(portalArea, facing);
        Vec3 center = boxSurface.getCenter();
        portal.setPos(center.x, center.y, center.z);
        
        portal.axisW = Vec3.atLowerCornerOf(directions.getA().getNormal());
        portal.axisH = Vec3.atLowerCornerOf(directions.getB().getNormal());
        portal.width = Helper.getCoordinate(areaSize, directions.getA().getAxis());
        portal.height = Helper.getCoordinate(areaSize, directions.getB().getAxis());
    }
    
    public static DQuaternion getPortalOrientationQuaternion(Portal portal) {
        return PortalManipulation.getPortalOrientationQuaternion(portal.axisW, portal.axisH);
    }
    
    public static <T extends Portal> T createFlippedPortal(T portal) {
        portal.isBifaced = true;
        return portal;
    }
    
    
}
