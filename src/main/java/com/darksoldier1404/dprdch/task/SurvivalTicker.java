package com.darksoldier1404.dprdch.task;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.data.PlayerChallengeData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class SurvivalTicker extends BukkitRunnable {
    private final RandomChallenge plugin;
    private int tickCount = 0;

    public SurvivalTicker(RandomChallenge plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        tickCount++;
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerChallengeData data = plugin.udata.get(p.getUniqueId());
            if (data == null) continue;
            plugin.assigner.checkRenewal(p, data);
            ChallengeDefinition def = plugin.registry.get(data.getCurrentChallengeId());
            if (def == null) {
                if (!plugin.registry.isEmpty()
                        && plugin.registry.getRenewalMode() != com.darksoldier1404.dprdch.challenge.RenewalMode.FIXED) {
                    plugin.assigner.assignRandom(p, data);
                }
                continue;
            }
            if (data.isCompleted()) continue;

            switch (def.getType()) {
                case SURVIVE_TIME:
                    if (def.matchWorld(p)) {
                        plugin.assigner.addProgress(p, ChallengeType.SURVIVE_TIME, 1);
                    }
                    break;
                case KEEP_HUNGER:
                    if (p.getFoodLevel() >= def.getIntParam("level", 20)) {
                        plugin.assigner.addProgress(p, ChallengeType.KEEP_HUNGER, 1);
                    } else {
                        plugin.assigner.resetProgress(p, ChallengeType.KEEP_HUNGER);
                    }
                    break;
                case NO_DAMAGE_TIME:
                    plugin.assigner.addProgress(p, ChallengeType.NO_DAMAGE_TIME, 1);
                    break;
                case SURVIVE_LOW_HEALTH:
                    if (p.getHealth() <= def.getDoubleParam("health", 4) && def.matchWorld(p)) {
                        plugin.assigner.addProgress(p, ChallengeType.SURVIVE_LOW_HEALTH, 1);
                    } else {
                        plugin.assigner.resetProgress(p, ChallengeType.SURVIVE_LOW_HEALTH);
                    }
                    break;
                case SURVIVE_UNDERWATER:
                    if (p.getEyeLocation().getBlock().getType() == Material.WATER && def.matchWorld(p)) {
                        plugin.assigner.addProgress(p, ChallengeType.SURVIVE_UNDERWATER, 1);
                    } else {
                        plugin.assigner.resetProgress(p, ChallengeType.SURVIVE_UNDERWATER);
                    }
                    break;
                case WEAR_FULL_ARMOR:
                    if (isWearingFullArmor(p, def.getStringParam("material"))) {
                        plugin.assigner.addProgress(p, ChallengeType.WEAR_FULL_ARMOR, 1);
                    } else {
                        plugin.assigner.resetProgress(p, ChallengeType.WEAR_FULL_ARMOR);
                    }
                    break;
                case RIDE_ENTITY:
                    if (p.getVehicle() != null
                            && def.matchParam("entity", p.getVehicle().getType().name())
                            && def.matchWorld(p)) {
                        plugin.assigner.addProgress(p, ChallengeType.RIDE_ENTITY, 1);
                    }
                    break;
                case REACH_Y_LEVEL:
                    if (matchYLevel(p, def) && def.matchWorld(p)) {
                        plugin.assigner.addProgress(p, ChallengeType.REACH_Y_LEVEL, 1);
                    }
                    break;
                case VISIT_WORLD: {
                    String worldName = def.getStringParam("world");
                    if (worldName != null && p.getWorld().getName().equalsIgnoreCase(worldName)) {
                        plugin.assigner.addProgress(p, ChallengeType.VISIT_WORLD, 1);
                    }
                    break;
                }
                case REACH_LEVEL:
                    if (p.getLevel() >= def.getIntParam("level", 30)) {
                        plugin.assigner.addProgress(p, ChallengeType.REACH_LEVEL, 1);
                    }
                    break;
                case VISIT_BIOME:
                    if (def.getStringParam("biome") != null
                            && p.getLocation().getBlock().getBiome().name()
                            .equalsIgnoreCase(def.getStringParam("biome"))
                            && def.matchWorld(p)) {
                        plugin.assigner.addProgress(p, ChallengeType.VISIT_BIOME, 1);
                    }
                    break;
                case ENTER_STRUCTURE:
                    if (tickCount % 10 == 0) {
                        checkStructure(p, def);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isWearingFullArmor(Player p, String material) {
        PlayerInventory inv = p.getInventory();
        ItemStack[] armor = {inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots()};
        for (ItemStack piece : armor) {
            if (piece == null || piece.getType() == Material.AIR) return false;
            if (material != null && !piece.getType().name().startsWith(material.toUpperCase())) return false;
        }
        return true;
    }

    private boolean matchYLevel(Player p, ChallengeDefinition def) {
        int y = def.getIntParam("y", 0);
        String direction = def.getStringParam("direction");
        double py = p.getLocation().getY();
        if (direction != null && direction.equalsIgnoreCase("BELOW")) {
            return py <= y;
        }
        return py >= y;
    }

    private void checkStructure(Player p, ChallengeDefinition def) {
        String structName = def.getStringParam("structure");
        if (structName == null || !def.matchWorld(p)) return;
        StructureType st = StructureType.getStructureTypes().get(structName.toLowerCase());
        if (st == null) return;
        try {
            Location loc = p.getWorld().locateNearestStructure(p.getLocation(), st, 3, false);
            if (loc == null) return;
            double dx = loc.getX() - p.getLocation().getX();
            double dz = loc.getZ() - p.getLocation().getZ();
            if (dx * dx + dz * dz <= 64 * 64) {
                plugin.assigner.addProgress(p, ChallengeType.ENTER_STRUCTURE, 1);
            }
        } catch (Throwable ignored) {
        }
    }
}
