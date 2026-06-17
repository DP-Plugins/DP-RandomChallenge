package com.darksoldier1404.dprdch.listener;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildListener implements Listener {
    private final RandomChallenge plugin = RandomChallenge.plugin;

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.PLACE_BLOCKS) return;
        if (!def.matchParam("block", e.getBlockPlaced().getType().name())) return;
        if (!def.matchWorld(p)) return;
        plugin.assigner.addProgress(p, ChallengeType.PLACE_BLOCKS, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null) return;

        if (def.getType() == ChallengeType.BREAK_BLOCKS) {
            if (!def.matchParam("block", e.getBlock().getType().name())) return;
            if (!def.matchWorld(p)) return;
            plugin.assigner.addProgress(p, ChallengeType.BREAK_BLOCKS, 1);
            return;
        }

        if (def.getType() == ChallengeType.HARVEST_CROP) {
            BlockData blockData = e.getBlock().getBlockData();
            if (!(blockData instanceof Ageable)) return;
            Ageable age = (Ageable) blockData;
            if (age.getAge() < age.getMaximumAge()) return;
            if (!def.matchParam("block", e.getBlock().getType().name())) return;
            if (!def.matchWorld(p)) return;
            plugin.assigner.addProgress(p, ChallengeType.HARVEST_CROP, 1);
        }
    }
}
