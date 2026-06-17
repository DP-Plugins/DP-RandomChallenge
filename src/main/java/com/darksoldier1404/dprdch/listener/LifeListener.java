package com.darksoldier1404.dprdch.listener;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.projectiles.ProjectileSource;

public class LifeListener implements Listener {
    private final RandomChallenge plugin = RandomChallenge.plugin;

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.EAT_FOOD) return;
        if (!def.matchParam("item", e.getItem().getType().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.EAT_FOOD, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTame(EntityTameEvent e) {
        if (!(e.getOwner() instanceof Player)) return;
        Player p = (Player) e.getOwner();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.TAME_ANIMAL) return;
        if (!def.matchParam("entity", e.getEntity().getType().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.TAME_ANIMAL, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreed(EntityBreedEvent e) {
        if (!(e.getBreeder() instanceof Player)) return;
        Player p = (Player) e.getBreeder();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.BREED_ANIMAL) return;
        if (!def.matchParam("entity", e.getEntity().getType().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.BREED_ANIMAL, 1);
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent e) {
        if (e.getAmount() <= 0) return;
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.GAIN_XP) return;
        plugin.assigner.addProgress(p, ChallengeType.GAIN_XP, e.getAmount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedEnter(PlayerBedEnterEvent e) {
        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.SLEEP_IN_BED) return;
        plugin.assigner.addProgress(p, ChallengeType.SLEEP_IN_BED, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent e) {
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.FILL_BUCKET) return;
        String liquid = def.getStringParam("liquid");
        if (liquid != null && e.getItemStack() != null
                && !e.getItemStack().getType().name().startsWith(liquid.toUpperCase())) return;
        plugin.assigner.addProgress(p, ChallengeType.FILL_BUCKET, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFertilize(BlockFertilizeEvent e) {
        if (e.getPlayer() == null) return;
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.BONE_MEAL) return;
        if (!def.matchParam("block", e.getBlock().getType().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.BONE_MEAL, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLaunch(ProjectileLaunchEvent e) {
        ProjectileSource shooter = e.getEntity().getShooter();
        if (!(shooter instanceof Player)) return;
        Player p = (Player) shooter;
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.THROW_PROJECTILE) return;
        if (!def.matchParam("projectile", e.getEntityType().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.THROW_PROJECTILE, 1);
    }
}
