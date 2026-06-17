package com.darksoldier1404.dprdch.challenge;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengeDescriber {

    public static String objective(ChallengeDefinition def) {
        RandomChallenge plugin = RandomChallenge.plugin;
        switch (def.getType()) {
            case KILL_MOB:
                return plugin.lang("desc-kill_mob", s(def, "mob"), s(def, "amount"));
            case KILL_PLAYER:
                return plugin.lang("desc-kill_player", s(def, "amount"));
            case DAMAGE_DEALT:
                return plugin.lang("desc-damage_dealt", s(def, "amount"));
            case KILL_BOSS:
                return plugin.lang("desc-kill_boss", s(def, "boss"), s(def, "amount"));
            case KILL_WITH_BOW:
                return plugin.lang("desc-kill_with_bow", s(def, "amount"));
            case KILL_WHILE_BURNING:
                return plugin.lang("desc-kill_while_burning", s(def, "amount"));
            case DAMAGE_WITH_POTION:
                return plugin.lang("desc-damage_with_potion", s(def, "amount"));
            case COLLECT_ITEM:
                return plugin.lang("desc-collect_item", s(def, "item"), s(def, "amount"));
            case CRAFT_ITEM:
                return plugin.lang("desc-craft_item", s(def, "item"), s(def, "amount"));
            case FISH_ITEM:
                return plugin.lang("desc-fish_item", s(def, "item"), s(def, "amount"));
            case SMELT_ITEM:
                return plugin.lang("desc-smelt_item", s(def, "item"), s(def, "amount"));
            case BREW_POTION:
                return plugin.lang("desc-brew_potion", s(def, "potion_type"), s(def, "amount"));
            case ENCHANT_ITEM:
                return plugin.lang("desc-enchant_item", s(def, "amount"));
            case TRADE_VILLAGER:
                return plugin.lang("desc-trade_villager", s(def, "amount"));
            case SHEAR_SHEEP:
                return plugin.lang("desc-shear_sheep", s(def, "amount"));
            case TRAVEL_DISTANCE:
                return plugin.lang("desc-travel_distance", s(def, "distance"));
            case VISIT_BIOME:
                return plugin.lang("desc-visit_biome", s(def, "biome"));
            case ENTER_STRUCTURE:
                return plugin.lang("desc-enter_structure", s(def, "structure"));
            case REACH_Y_LEVEL: {
                String direction = s(def, "direction");
                String word = direction.equalsIgnoreCase("BELOW")
                        ? plugin.lang("word-below")
                        : plugin.lang("word-above");
                return plugin.lang("desc-reach_y_level", s(def, "y"), word);
            }
            case RIDE_ENTITY:
                return plugin.lang("desc-ride_entity", s(def, "entity"), s(def, "duration"));
            case ENTER_PORTAL:
                return plugin.lang("desc-enter_portal", s(def, "portal_type"), s(def, "amount"));
            case SURVIVE_TIME:
                return plugin.lang("desc-survive_time", s(def, "duration"));
            case KEEP_HUNGER:
                return plugin.lang("desc-keep_hunger", s(def, "level"), s(def, "duration"));
            case NO_DAMAGE_TIME:
                return plugin.lang("desc-no_damage_time", s(def, "duration"));
            case SURVIVE_LOW_HEALTH:
                return plugin.lang("desc-survive_low_health", s(def, "health"), s(def, "duration"));
            case SURVIVE_UNDERWATER:
                return plugin.lang("desc-survive_underwater", s(def, "duration"));
            case WEAR_FULL_ARMOR:
                return plugin.lang("desc-wear_full_armor", s(def, "duration"));
            case PLACE_BLOCKS:
                return plugin.lang("desc-place_blocks", s(def, "block"), s(def, "amount"));
            case BREAK_BLOCKS:
                return plugin.lang("desc-break_blocks", s(def, "block"), s(def, "amount"));
            case KILL_FROM_DISTANCE:
                return plugin.lang("desc-kill_from_distance", s(def, "amount"), s(def, "distance"));
            case EAT_FOOD:
                return plugin.lang("desc-eat_food", s(def, "amount"));
            case HARVEST_CROP:
                return plugin.lang("desc-harvest_crop", s(def, "amount"));
            case TAME_ANIMAL:
                return plugin.lang("desc-tame_animal", s(def, "amount"));
            case BREED_ANIMAL:
                return plugin.lang("desc-breed_animal", s(def, "amount"));
            case FILL_BUCKET:
                return plugin.lang("desc-fill_bucket", s(def, "amount"));
            case BONE_MEAL:
                return plugin.lang("desc-bone_meal", s(def, "amount"));
            case THROW_PROJECTILE:
                return plugin.lang("desc-throw_projectile", s(def, "amount"));
            case GLIDE_DISTANCE:
                return plugin.lang("desc-glide_distance", s(def, "distance"));
            case VISIT_WORLD:
                return plugin.lang("desc-visit_world", s(def, "world"));
            case SLEEP_IN_BED:
                return plugin.lang("desc-sleep_in_bed", s(def, "amount"));
            case TAKE_DAMAGE:
                return plugin.lang("desc-take_damage", s(def, "amount"));
            case GAIN_XP:
                return plugin.lang("desc-gain_xp", s(def, "amount"));
            case REACH_LEVEL:
                return plugin.lang("desc-reach_level", s(def, "level"));
            default:
                return def.getType().name();
        }
    }

    public static List<String> conditions(ChallengeDefinition def) {
        RandomChallenge plugin = RandomChallenge.plugin;
        List<String> out = new ArrayList<>();
        for (ChallengeParams.ParamSpec spec : ChallengeParams.of(def.getType())) {
            if (spec.required) continue;
            String value = def.getStringParam(spec.key);
            if (value == null) continue;
            out.add(plugin.lang("cond-line", plugin.lang("param-" + spec.key), value));
        }
        return out;
    }

    public static List<String> describe(ChallengeDefinition def) {
        RandomChallenge plugin = RandomChallenge.plugin;
        List<String> out = new ArrayList<>();
        out.add(plugin.lang("gui-lore-objective", objective(def)));
        out.addAll(conditions(def));
        return out;
    }

    public static String exampleObjective(ChallengeType type) {
        Map<String, Object> params = new HashMap<>();
        for (ChallengeParams.ParamSpec spec : ChallengeParams.of(type)) {
            if (spec.required) {
                params.put(spec.key, spec.def);
            }
        }
        return objective(new ChallengeDefinition("example", type, params, "", null));
    }

    private static String s(ChallengeDefinition def, String key) {
        String value = def.getStringParam(key);
        return value == null ? "?" : value;
    }
}
