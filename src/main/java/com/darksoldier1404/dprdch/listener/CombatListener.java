package com.darksoldier1404.dprdch.listener;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

public class CombatListener implements Listener {
    private final RandomChallenge plugin = RandomChallenge.plugin;

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) return;
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        ChallengeDefinition def = plugin.assigner.getActive(killer);
        if (def == null) return;
        LivingEntity victim = e.getEntity();

        switch (def.getType()) {
            case KILL_MOB:
                if (!def.matchParam("mob", victim.getType().name())) return;
                if (!def.matchWorld(killer) || !def.matchBiome(killer)) return;
                String weapon = def.getStringParam("weapon");
                if (weapon != null) {
                    ItemStack hand = killer.getInventory().getItemInMainHand();
                    if (!hand.getType().name().contains(weapon.toUpperCase())) return;
                }
                plugin.assigner.addProgress(killer, ChallengeType.KILL_MOB, 1);
                break;
            case KILL_BOSS:
                if (!def.matchParam("boss", victim.getType().name())) return;
                plugin.assigner.addProgress(killer, ChallengeType.KILL_BOSS, 1);
                break;
            case KILL_WITH_BOW:
                if (!isKilledByArrow(victim)) return;
                if (!def.matchParam("mob", victim.getType().name())) return;
                if (!def.matchWorld(killer)) return;
                plugin.assigner.addProgress(killer, ChallengeType.KILL_WITH_BOW, 1);
                break;
            case KILL_WHILE_BURNING:
                if (killer.getFireTicks() <= 0) return;
                if (!def.matchParam("mob", victim.getType().name())) return;
                if (!def.matchWorld(killer)) return;
                plugin.assigner.addProgress(killer, ChallengeType.KILL_WHILE_BURNING, 1);
                break;
            case KILL_FROM_DISTANCE:
                if (!def.matchParam("mob", victim.getType().name())) return;
                if (!def.matchWorld(killer)) return;
                if (!killer.getWorld().equals(victim.getWorld())) return;
                if (killer.getLocation().distance(victim.getLocation()) < def.getIntParam("distance", 30)) return;
                plugin.assigner.addProgress(killer, ChallengeType.KILL_FROM_DISTANCE, 1);
                break;
            default:
                break;
        }
    }

    private boolean isKilledByArrow(LivingEntity victim) {
        EntityDamageEvent last = victim.getLastDamageCause();
        if (!(last instanceof EntityDamageByEntityEvent)) return false;
        return ((EntityDamageByEntityEvent) last).getDamager() instanceof AbstractArrow;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null || killer.getUniqueId().equals(e.getEntity().getUniqueId())) return;
        ChallengeDefinition def = plugin.assigner.getActive(killer);
        if (def == null || def.getType() != ChallengeType.KILL_PLAYER) return;
        if (!def.matchWorld(killer) || !def.matchBiome(killer)) return;
        plugin.assigner.addProgress(killer, ChallengeType.KILL_PLAYER, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        Player damager = resolveDamager(e.getDamager());
        if (damager == null) return;
        ChallengeDefinition def = plugin.assigner.getActive(damager);
        if (def == null) return;
        int dmg = (int) Math.round(e.getFinalDamage());
        if (dmg <= 0) return;

        switch (def.getType()) {
            case DAMAGE_DEALT:
                if (!def.matchWorld(damager)) return;
                plugin.assigner.addProgress(damager, ChallengeType.DAMAGE_DEALT, dmg);
                break;
            case DAMAGE_WITH_POTION:
                if (!(e.getDamager() instanceof ThrownPotion)) return;
                if (!def.matchWorld(damager)) return;
                String potionType = def.getStringParam("potion_type");
                if (potionType != null && !hasEffect((ThrownPotion) e.getDamager(), potionType)) return;
                plugin.assigner.addProgress(damager, ChallengeType.DAMAGE_WITH_POTION, dmg);
                break;
            default:
                break;
        }
    }

    private boolean hasEffect(ThrownPotion potion, String typeName) {
        for (PotionEffect effect : potion.getEffects()) {
            if (effect.getType().getName().equalsIgnoreCase(typeName)) return true;
        }
        return false;
    }

    private Player resolveDamager(Entity damager) {
        if (damager instanceof Player) return (Player) damager;
        if (damager instanceof AbstractArrow) {
            ProjectileSource shooter = ((AbstractArrow) damager).getShooter();
            if (shooter instanceof Player) return (Player) shooter;
        }
        if (damager instanceof ThrownPotion) {
            ProjectileSource shooter = ((ThrownPotion) damager).getShooter();
            if (shooter instanceof Player) return (Player) shooter;
        }
        return null;
    }
}
