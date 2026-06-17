package com.darksoldier1404.dprdch.listener;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExploreListener implements Listener {
    private final RandomChallenge plugin = RandomChallenge.plugin;
    private final Map<UUID, Double> travelBuffer = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null || from.getWorld() == null || !from.getWorld().equals(to.getWorld())) return;

        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null) return;

        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        if (def.getType() == ChallengeType.TRAVEL_DISTANCE) {
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist <= 0 || dist > 8) return;
            if (!def.matchWorld(p)) return;
            String mode = def.getStringParam("mode");
            if (mode != null) {
                if (mode.equalsIgnoreCase("WALK") && (p.isSprinting() || p.isFlying() || p.isInsideVehicle())) return;
                if (mode.equalsIgnoreCase("SPRINT") && !p.isSprinting()) return;
            }
            accumulate(p, ChallengeType.TRAVEL_DISTANCE, dist);
            return;
        }

        if (def.getType() == ChallengeType.GLIDE_DISTANCE) {
            if (!p.isGliding()) return;
            if (!def.matchWorld(p)) return;
            double dy = to.getY() - from.getY();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist <= 0 || dist > 30) return;
            accumulate(p, ChallengeType.GLIDE_DISTANCE, dist);
        }
    }

    private void accumulate(Player p, ChallengeType type, double dist) {
        double buffer = travelBuffer.getOrDefault(p.getUniqueId(), 0D) + dist;
        int whole = (int) buffer;
        if (whole >= 1) {
            buffer -= whole;
            plugin.assigner.addProgress(p, type, whole);
        }
        travelBuffer.put(p.getUniqueId(), buffer);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent e) {
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.ENTER_PORTAL) return;
        String portalType = portalTypeOf(e.getCause());
        if (portalType == null) return;
        String param = def.getStringParam("portal_type");
        if (param != null && !param.equalsIgnoreCase("ANY") && !param.equalsIgnoreCase(portalType)) return;
        plugin.assigner.addProgress(p, ChallengeType.ENTER_PORTAL, 1);
    }

    private String portalTypeOf(PlayerTeleportEvent.TeleportCause cause) {
        switch (cause) {
            case NETHER_PORTAL:
                return "NETHER";
            case END_PORTAL:
            case END_GATEWAY:
                return "END";
            default:
                return null;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        travelBuffer.remove(e.getPlayer().getUniqueId());
    }
}
