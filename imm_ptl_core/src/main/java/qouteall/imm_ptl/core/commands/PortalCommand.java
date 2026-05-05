package qouteall.imm_ptl.core.commands;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalUtils;

import java.util.Optional;

public class PortalCommand {

    @Deprecated
    public static Optional<Pair<Portal, Vec3>> getPlayerPointingPortalRaw(
        Player player, float tickDelta, double maxDistance, boolean includeGlobalPortal
    ) {
        return PortalUtils.raytracePortalFromEntityView(player, tickDelta, maxDistance, includeGlobalPortal, p -> true);
    }

    @Deprecated
    public static Portal getPlayerPointingPortal(
        ServerPlayer player, boolean includeGlobalPortal
    ) {
        return getPlayerPointingPortalRaw(player, 1, 100, includeGlobalPortal)
            .map(Pair::getFirst).orElse(null);
    }

    public static Optional<Pair<Portal, Vec3>> raytracePortals(
        Level world, Vec3 from, Vec3 to, boolean includeGlobalPortal
    ) {
        return PortalUtils.raytracePortals(world, from, to, includeGlobalPortal, p -> true);
    }
}