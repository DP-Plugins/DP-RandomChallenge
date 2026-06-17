package com.darksoldier1404.dprdch.data;

import com.darksoldier1404.dppc.data.DataCargo;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChallengeDefinition implements DataCargo {
    private String id;
    private ChallengeType type;
    private Map<String, Object> params = new HashMap<>();
    private String display;
    private RewardDefinition reward = new RewardDefinition();

    public ChallengeDefinition() {
    }

    public ChallengeDefinition(String id, ChallengeType type, Map<String, Object> params, String display, RewardDefinition reward) {
        this.id = id;
        this.type = type;
        this.params = params == null ? new HashMap<>() : params;
        this.display = display;
        this.reward = reward == null ? new RewardDefinition() : reward;
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("id", id);
        data.set("type", type == null ? null : type.name());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            data.set("params." + entry.getKey(), entry.getValue());
        }
        data.set("display", display);
        data.set("reward.money", reward.getMoney());
        data.set("reward.serialized-items", reward.serializeItemStacks());
        data.set("reward.commands", reward.getCommands());
        return data;
    }

    @Override
    public ChallengeDefinition deserialize(YamlConfiguration data) {
        this.id = data.getString("id");
        try {
            this.type = ChallengeType.valueOf(data.getString("type", "").toUpperCase());
        } catch (IllegalArgumentException e) {
            this.type = null;
        }
        this.params = new HashMap<>();
        ConfigurationSection ps = data.getConfigurationSection("params");
        if (ps != null) {
            for (String key : ps.getKeys(false)) {
                params.put(key.toLowerCase(), ps.get(key));
            }
        }
        this.display = data.getString("display", id);
        this.reward = new RewardDefinition(
                data.getDouble("reward.money", 0),
                data.getStringList("reward.serialized-items"),
                data.getStringList("reward.commands"));
        return this;
    }

    public int getTarget() {
        switch (type) {
            case TRAVEL_DISTANCE:
            case GLIDE_DISTANCE:
                return getIntParam("distance", 1);
            case VISIT_BIOME:
            case ENTER_STRUCTURE:
            case REACH_Y_LEVEL:
            case VISIT_WORLD:
            case REACH_LEVEL:
                return 1;
            case RIDE_ENTITY:
            case SURVIVE_TIME:
            case KEEP_HUNGER:
            case NO_DAMAGE_TIME:
            case SURVIVE_LOW_HEALTH:
            case SURVIVE_UNDERWATER:
            case WEAR_FULL_ARMOR:
                return getIntParam("duration", 60);
            default:
                return getIntParam("amount", 1);
        }
    }

    public String getStringParam(String key) {
        Object o = params.get(key.toLowerCase());
        return o == null ? null : String.valueOf(o);
    }

    public int getIntParam(String key, int def) {
        Object o = params.get(key.toLowerCase());
        if (o instanceof Number) return ((Number) o).intValue();
        if (o != null) {
            try {
                return Integer.parseInt(String.valueOf(o).trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return def;
    }

    public double getDoubleParam(String key, double def) {
        Object o = params.get(key.toLowerCase());
        if (o instanceof Number) return ((Number) o).doubleValue();
        if (o != null) {
            try {
                return Double.parseDouble(String.valueOf(o).trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return def;
    }

    public boolean matchParam(String key, String value) {
        String pv = getStringParam(key);
        return pv == null || pv.equalsIgnoreCase(value);
    }

    public boolean matchWorld(Player p) {
        return matchParam("world", p.getWorld().getName());
    }

    public boolean matchBiome(Player p) {
        return matchParam("biome", p.getLocation().getBlock().getBiome().name());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChallengeType getType() {
        return type;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public RewardDefinition getReward() {
        return reward;
    }

    public void setReward(RewardDefinition reward) {
        this.reward = reward;
    }
}
