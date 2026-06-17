package com.darksoldier1404.dprdch.challenge;

import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.data.RewardDefinition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ChallengeRegistry {
    private final RandomChallenge plugin;
    private final Random random = new Random();
    private RenewalMode renewalMode = RenewalMode.MIXED;
    private int timeInterval = 1800;

    public ChallengeRegistry(RandomChallenge plugin) {
        this.plugin = plugin;
    }

    private DataContainer<String, ChallengeDefinition> data() {
        return plugin.cdata;
    }

    public void load() {
        try {
            renewalMode = RenewalMode.valueOf(plugin.getConfig().getString("Settings.renewal-mode", "MIXED").toUpperCase());
        } catch (IllegalArgumentException e) {
            renewalMode = RenewalMode.MIXED;
            plugin.log.warning("Invalid Settings.renewal-mode, falling back to MIXED.", true);
        }
        timeInterval = plugin.getConfig().getInt("Settings.time-interval", 1800);

        migrateFromConfig();

        for (String id : new ArrayList<>(data().keySet())) {
            ChallengeDefinition def = data().get(id);
            if (def == null || def.getType() == null) {
                plugin.log.warning("Removing invalid challenge definition: " + id, true);
                data().remove(id);
                continue;
            }
            if (def.getId() == null) {
                def.setId(id);
            }
        }

        plugin.log.info("Loaded " + data().size() + " challenges. (renewal-mode: " + renewalMode + ")", true);
        if (data().isEmpty()) {
            plugin.log.info("No challenges defined yet. Use /rc admin to create challenges in-game.", true);
        }
    }

    private void migrateFromConfig() {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("Challenges");
        if (sec == null) return;
        int migrated = 0;
        for (String id : sec.getKeys(false)) {
            ConfigurationSection cs = sec.getConfigurationSection(id);
            if (cs == null || data().containsKey(id)) continue;
            ChallengeType type;
            try {
                type = ChallengeType.valueOf(cs.getString("type", "").toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.log.warning("Skipping config challenge '" + id + "' with unknown type: " + cs.getString("type"), true);
                continue;
            }
            Map<String, Object> params = new HashMap<>();
            ConfigurationSection ps = cs.getConfigurationSection("params");
            if (ps != null) {
                for (String key : ps.getKeys(false)) {
                    params.put(key.toLowerCase(), ps.get(key));
                }
            }
            RewardDefinition reward = new RewardDefinition(
                    cs.getDouble("reward.money", 0),
                    cs.getStringList("reward.serialized-items"),
                    cs.getStringList("reward.commands"));
            for (String entry : cs.getStringList("reward.items")) {
                String[] parts = entry.split(":");
                Material mat = Material.matchMaterial(parts[0].trim());
                if (mat == null) continue;
                int amount = 1;
                if (parts.length > 1) {
                    try {
                        amount = Math.max(1, Integer.parseInt(parts[1].trim()));
                    } catch (NumberFormatException ignored) {
                    }
                }
                reward.getItemStacks().add(new ItemStack(mat, amount));
            }
            ChallengeDefinition def = new ChallengeDefinition(id, type, params, cs.getString("display", id), reward);
            data().put(id, def);
            data().save(id);
            migrated++;
        }
        plugin.getConfig().set("Challenges", null);
        plugin.saveConfig();
        if (migrated > 0) {
            plugin.log.info("Migrated " + migrated + " challenges from config.yml to data/challenges/.", true);
        }
    }

    public ChallengeDefinition get(String id) {
        return id == null ? null : data().get(id);
    }

    public Collection<ChallengeDefinition> getAll() {
        return data().values();
    }

    public Set<String> ids() {
        return data().keySet();
    }

    public boolean isEmpty() {
        return data().isEmpty();
    }

    public boolean exists(String id) {
        return data().containsKey(id);
    }

    public ChallengeDefinition random(String excludeId) {
        if (data().isEmpty()) return null;
        List<ChallengeDefinition> pool = new ArrayList<>(data().values());
        if (pool.size() > 1 && excludeId != null) {
            pool.removeIf(def -> def.getId().equals(excludeId));
        }
        return pool.get(random.nextInt(pool.size()));
    }

    public void delete(String id) {
        if (!data().delete(id)) {
            data().remove(id);
        }
    }

    public ChallengeDefinition create(String id, ChallengeType type) {
        Map<String, Object> params = new HashMap<>();
        for (ChallengeParams.ParamSpec spec : ChallengeParams.of(type)) {
            if (spec.required) {
                params.put(spec.key, spec.def);
            }
        }
        ChallengeDefinition def = new ChallengeDefinition(id, type, params, "&f" + id, new RewardDefinition());
        saveDefinition(def);
        return def;
    }

    public void saveDefinition(ChallengeDefinition def) {
        data().put(def.getId(), def);
        data().save(def.getId());
    }

    public void setDisplay(String id, String display) {
        ChallengeDefinition def = get(id);
        if (def == null) return;
        def.setDisplay(display);
        saveDefinition(def);
    }

    public void setMoney(String id, double money) {
        ChallengeDefinition def = get(id);
        if (def == null) return;
        def.getReward().setMoney(money);
        saveDefinition(def);
    }

    public void setParam(String id, String key, Object value) {
        ChallengeDefinition def = get(id);
        if (def == null) return;
        if (value == null) {
            def.getParams().remove(key.toLowerCase());
        } else {
            def.getParams().put(key.toLowerCase(), value);
        }
        saveDefinition(def);
    }

    public void setRewardItems(String id, List<ItemStack> items) {
        ChallengeDefinition def = get(id);
        if (def == null) return;
        def.getReward().setItemStacks(items);
        saveDefinition(def);
    }

    public void addCommand(String id, String command) {
        ChallengeDefinition def = get(id);
        if (def == null) return;
        def.getReward().getCommands().add(command);
        saveDefinition(def);
    }

    public void clearCommands(String id) {
        ChallengeDefinition def = get(id);
        if (def == null) return;
        def.getReward().getCommands().clear();
        saveDefinition(def);
    }

    public RenewalMode getRenewalMode() {
        return renewalMode;
    }

    public int getTimeInterval() {
        return timeInterval;
    }
}
