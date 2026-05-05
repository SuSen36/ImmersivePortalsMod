package qouteall.imm_ptl.core.portal;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.McHelper;
import qouteall.imm_ptl.core.portal.global_portals.GlobalPortalStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PortalUtils {

    public static Optional<Pair<Portal, Vec3>> raytracePortals(
        Level world, Vec3 from, Vec3 to,
        boolean includeGlobalPortal,
        Predicate<Portal> predicate
    ) {
        Stream<Portal> portalStream = McHelper.getEntitiesNearby(
            world,
            from,
            Portal.class,
            from.distanceTo(to)
        ).stream();
        if (includeGlobalPortal) {
            List<Portal> globalPortals = GlobalPortalStorage.getGlobalPortals(world);
            portalStream = Streams.concat(
                portalStream,
                globalPortals.stream()
            );
        }
        return portalStream.map(
            portal -> new Pair<Portal, Vec3>(
                portal, portal.rayTrace(from, to)
            )
        ).filter(
            portalAndHitPos -> portalAndHitPos.getSecond() != null
                && predicate.test(portalAndHitPos.getFirst())
        ).min(
            Comparator.comparingDouble(
                portalAndHitPos -> portalAndHitPos.getSecond().distanceToSqr(from)
            )
        );
    }

    public static Optional<Pair<Portal, Vec3>> raytracePortalFromEntityView(
        Entity player, float tickDelta, double maxDistance, boolean includeGlobalPortal,
        Predicate<Portal> predicate
    ) {
        Vec3 from = player.getEyePosition(tickDelta);
        Vec3 to = from.add(player.getViewVector(tickDelta).scale(maxDistance));
        return raytracePortals(player.level, from, to, includeGlobalPortal, predicate);
    }
}