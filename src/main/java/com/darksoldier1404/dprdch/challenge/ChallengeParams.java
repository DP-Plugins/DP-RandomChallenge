package com.darksoldier1404.dprdch.challenge;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ChallengeParams {

    public enum ParamKind {
        INT, DOUBLE, STRING, MATERIAL, ENTITY
    }

    public static class ParamSpec {
        public final String key;
        public final ParamKind kind;
        public final boolean required;
        public final Object def;
        public final String hint;

        private ParamSpec(String key, ParamKind kind, boolean required, Object def, String hint) {
            this.key = key;
            this.kind = kind;
            this.required = required;
            this.def = def;
            this.hint = hint;
        }
    }

    private static ParamSpec req(String key, ParamKind kind, Object def, String hint) {
        return new ParamSpec(key, kind, true, def, hint);
    }

    private static ParamSpec opt(String key, ParamKind kind, String hint) {
        return new ParamSpec(key, kind, false, null, hint);
    }

    private static final Map<ChallengeType, List<ParamSpec>> SPECS = new EnumMap<>(ChallengeType.class);

    static {
        ParamSpec world = opt("world", ParamKind.STRING, "world");
        ParamSpec biome = opt("biome", ParamKind.STRING, "PLAINS, DESERT, ...");

        SPECS.put(ChallengeType.KILL_MOB, Arrays.asList(
                req("mob", ParamKind.ENTITY, "ZOMBIE", "ZOMBIE, SKELETON, ..."),
                req("amount", ParamKind.INT, 10, null),
                opt("weapon", ParamKind.STRING, "SWORD, AXE, BOW"),
                biome, world));
        SPECS.put(ChallengeType.KILL_PLAYER, Arrays.asList(
                req("amount", ParamKind.INT, 1, null), world, biome));
        SPECS.put(ChallengeType.DAMAGE_DEALT, Arrays.asList(
                req("amount", ParamKind.INT, 100, null), world));
        SPECS.put(ChallengeType.KILL_BOSS, Arrays.asList(
                req("boss", ParamKind.STRING, "WITHER", "WITHER, ENDER_DRAGON, ELDER_GUARDIAN"),
                req("amount", ParamKind.INT, 1, null)));
        SPECS.put(ChallengeType.KILL_WITH_BOW, Arrays.asList(
                req("amount", ParamKind.INT, 10, null),
                opt("mob", ParamKind.ENTITY, "ZOMBIE, SKELETON, ..."), world));
        SPECS.put(ChallengeType.KILL_WHILE_BURNING, Arrays.asList(
                req("amount", ParamKind.INT, 5, null),
                opt("mob", ParamKind.ENTITY, "ZOMBIE, SKELETON, ..."), world));
        SPECS.put(ChallengeType.DAMAGE_WITH_POTION, Arrays.asList(
                req("amount", ParamKind.INT, 50, null),
                opt("potion_type", ParamKind.STRING, "HARM, POISON, ..."), world));
        SPECS.put(ChallengeType.KILL_FROM_DISTANCE, Arrays.asList(
                req("amount", ParamKind.INT, 5, null),
                req("distance", ParamKind.INT, 30, null),
                opt("mob", ParamKind.ENTITY, "SKELETON, ZOMBIE, ..."), world));

        SPECS.put(ChallengeType.COLLECT_ITEM, Arrays.asList(
                req("item", ParamKind.MATERIAL, "COBBLESTONE", "COBBLESTONE, DIAMOND, ..."),
                req("amount", ParamKind.INT, 64, null),
                opt("source", ParamKind.STRING, "MINE, LOOT, ANY")));
        SPECS.put(ChallengeType.CRAFT_ITEM, Arrays.asList(
                req("item", ParamKind.MATERIAL, "BREAD", "BREAD, STICK, ..."),
                req("amount", ParamKind.INT, 16, null)));
        SPECS.put(ChallengeType.FISH_ITEM, Arrays.asList(
                req("item", ParamKind.MATERIAL, "COD", "COD, SALMON, ..."),
                req("amount", ParamKind.INT, 5, null)));
        SPECS.put(ChallengeType.SMELT_ITEM, Arrays.asList(
                req("item", ParamKind.MATERIAL, "IRON_INGOT", "IRON_INGOT, GOLD_INGOT, ..."),
                req("amount", ParamKind.INT, 16, null)));
        SPECS.put(ChallengeType.BREW_POTION, Arrays.asList(
                req("potion_type", ParamKind.STRING, "SPEED", "SPEED, STRENGTH, ..."),
                req("amount", ParamKind.INT, 3, null)));
        SPECS.put(ChallengeType.ENCHANT_ITEM, Arrays.asList(
                req("amount", ParamKind.INT, 3, null),
                opt("item", ParamKind.STRING, "SWORD, PICKAXE, BOOK"),
                opt("enchantment", ParamKind.STRING, "sharpness, efficiency, ...")));
        SPECS.put(ChallengeType.TRADE_VILLAGER, Arrays.asList(
                req("amount", ParamKind.INT, 5, null),
                opt("item", ParamKind.MATERIAL, "EMERALD, BREAD, ...")));
        SPECS.put(ChallengeType.SHEAR_SHEEP, Arrays.asList(
                req("amount", ParamKind.INT, 10, null),
                opt("color", ParamKind.STRING, "WHITE, RED, BLUE, ...")));
        SPECS.put(ChallengeType.EAT_FOOD, Arrays.asList(
                req("amount", ParamKind.INT, 10, null),
                opt("item", ParamKind.MATERIAL, "COOKED_BEEF, BREAD, ...")));
        SPECS.put(ChallengeType.HARVEST_CROP, Arrays.asList(
                req("amount", ParamKind.INT, 32, null),
                opt("block", ParamKind.MATERIAL, "WHEAT, CARROTS, POTATOES"), world));
        SPECS.put(ChallengeType.TAME_ANIMAL, Arrays.asList(
                req("amount", ParamKind.INT, 3, null),
                opt("entity", ParamKind.ENTITY, "WOLF, CAT, HORSE")));
        SPECS.put(ChallengeType.BREED_ANIMAL, Arrays.asList(
                req("amount", ParamKind.INT, 5, null),
                opt("entity", ParamKind.ENTITY, "COW, SHEEP, PIG")));
        SPECS.put(ChallengeType.FILL_BUCKET, Arrays.asList(
                req("amount", ParamKind.INT, 3, null),
                opt("liquid", ParamKind.STRING, "WATER / LAVA")));
        SPECS.put(ChallengeType.BONE_MEAL, Arrays.asList(
                req("amount", ParamKind.INT, 16, null),
                opt("block", ParamKind.MATERIAL, "WHEAT, CARROTS, ...")));
        SPECS.put(ChallengeType.THROW_PROJECTILE, Arrays.asList(
                req("amount", ParamKind.INT, 16, null),
                opt("projectile", ParamKind.ENTITY, "SNOWBALL, ENDER_PEARL, EGG, ARROW")));

        SPECS.put(ChallengeType.TRAVEL_DISTANCE, Arrays.asList(
                req("distance", ParamKind.INT, 1000, null), world,
                opt("mode", ParamKind.STRING, "WALK, SPRINT, ANY")));
        SPECS.put(ChallengeType.VISIT_BIOME, Arrays.asList(
                req("biome", ParamKind.STRING, "DESERT", "PLAINS, DESERT, ..."), world));
        SPECS.put(ChallengeType.ENTER_STRUCTURE, Arrays.asList(
                req("structure", ParamKind.STRING, "Village", "Village, Fortress, Mineshaft, ..."), world));
        SPECS.put(ChallengeType.REACH_Y_LEVEL, Arrays.asList(
                req("y", ParamKind.INT, 100, null),
                req("direction", ParamKind.STRING, "ABOVE", "ABOVE / BELOW"), world));
        SPECS.put(ChallengeType.RIDE_ENTITY, Arrays.asList(
                req("entity", ParamKind.ENTITY, "HORSE", "HORSE, PIG, BOAT, ..."),
                req("duration", ParamKind.INT, 60, null), world));
        SPECS.put(ChallengeType.ENTER_PORTAL, Arrays.asList(
                req("portal_type", ParamKind.STRING, "NETHER", "NETHER / END / ANY"),
                req("amount", ParamKind.INT, 1, null)));
        SPECS.put(ChallengeType.GLIDE_DISTANCE, Arrays.asList(
                req("distance", ParamKind.INT, 1000, null), world));
        SPECS.put(ChallengeType.VISIT_WORLD, Collections.singletonList(
                req("world", ParamKind.STRING, "world_nether", "world, world_nether, ...")));
        SPECS.put(ChallengeType.SLEEP_IN_BED, Collections.singletonList(
                req("amount", ParamKind.INT, 1, null)));

        SPECS.put(ChallengeType.SURVIVE_TIME, Arrays.asList(
                req("duration", ParamKind.INT, 600, null), world));
        SPECS.put(ChallengeType.KEEP_HUNGER, Arrays.asList(
                req("level", ParamKind.INT, 18, null),
                req("duration", ParamKind.INT, 300, null)));
        SPECS.put(ChallengeType.NO_DAMAGE_TIME, Collections.singletonList(
                req("duration", ParamKind.INT, 300, null)));
        SPECS.put(ChallengeType.SURVIVE_LOW_HEALTH, Arrays.asList(
                req("health", ParamKind.DOUBLE, 4.0, null),
                req("duration", ParamKind.INT, 60, null), world));
        SPECS.put(ChallengeType.SURVIVE_UNDERWATER, Arrays.asList(
                req("duration", ParamKind.INT, 60, null), world));
        SPECS.put(ChallengeType.WEAR_FULL_ARMOR, Arrays.asList(
                req("duration", ParamKind.INT, 300, null),
                opt("material", ParamKind.STRING, "DIAMOND, IRON, GOLDEN")));
        SPECS.put(ChallengeType.TAKE_DAMAGE, Arrays.asList(
                req("amount", ParamKind.INT, 50, null), world));
        SPECS.put(ChallengeType.GAIN_XP, Collections.singletonList(
                req("amount", ParamKind.INT, 100, null)));
        SPECS.put(ChallengeType.REACH_LEVEL, Collections.singletonList(
                req("level", ParamKind.INT, 30, null)));

        SPECS.put(ChallengeType.PLACE_BLOCKS, Arrays.asList(
                req("block", ParamKind.MATERIAL, "STONE", "STONE, DIRT, ..."),
                req("amount", ParamKind.INT, 64, null), world));
        SPECS.put(ChallengeType.BREAK_BLOCKS, Arrays.asList(
                req("block", ParamKind.MATERIAL, "STONE", "STONE, DIRT, ..."),
                req("amount", ParamKind.INT, 64, null), world));
    }

    public static List<ParamSpec> of(ChallengeType type) {
        List<ParamSpec> list = SPECS.get(type);
        return list == null ? Collections.<ParamSpec>emptyList() : list;
    }

    public static ParamSpec spec(ChallengeType type, String key) {
        for (ParamSpec spec : of(type)) {
            if (spec.key.equalsIgnoreCase(key)) return spec;
        }
        return null;
    }
}
