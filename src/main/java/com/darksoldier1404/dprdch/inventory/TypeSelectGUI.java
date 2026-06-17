package com.darksoldier1404.dprdch.inventory;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.builder.itemstack.ItemStackBuilder;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeDescriber;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TypeSelectGUI {
    public static final int CHANNEL = 4;
    public static final String TAG_TYPE = "dprdch_type";

    public static void open(Player p, String pendingId) {
        RandomChallenge plugin = RandomChallenge.plugin;
        DInventory inv = new DInventory(plugin.lang("type-select-title"), 54, true, true, plugin);
        inv.setChannel(CHANNEL);
        inv.setObj(pendingId);

        List<ItemStack> items = new ArrayList<>();
        for (ChallengeType type : ChallengeType.values()) {
            ItemStack item = ItemStackBuilder.of(iconOf(type))
                    .name("&e" + type.name())
                    .lore(
                            plugin.lang("type-example", ChallengeDescriber.exampleObjective(type)),
                            "",
                            plugin.lang("type-lore-click"))
                    .build();
            item = NBT.setStringTag(item, TAG_TYPE, type.name());
            items.add(item);
        }
        inv.addPageItems(items);
        inv.update();
        inv.openInventory(p);
    }

    public static Material iconOf(ChallengeType type) {
        switch (type) {
            case KILL_MOB:
                return Material.IRON_SWORD;
            case KILL_PLAYER:
                return Material.DIAMOND_SWORD;
            case DAMAGE_DEALT:
                return Material.BLAZE_POWDER;
            case KILL_BOSS:
                return Material.WITHER_SKELETON_SKULL;
            case KILL_WITH_BOW:
                return Material.BOW;
            case KILL_WHILE_BURNING:
                return Material.FIRE_CHARGE;
            case DAMAGE_WITH_POTION:
                return Material.SPLASH_POTION;
            case COLLECT_ITEM:
                return Material.CHEST;
            case CRAFT_ITEM:
                return Material.CRAFTING_TABLE;
            case FISH_ITEM:
                return Material.FISHING_ROD;
            case SMELT_ITEM:
                return Material.FURNACE;
            case BREW_POTION:
                return Material.BREWING_STAND;
            case ENCHANT_ITEM:
                return Material.ENCHANTING_TABLE;
            case TRADE_VILLAGER:
                return Material.EMERALD;
            case SHEAR_SHEEP:
                return Material.SHEARS;
            case TRAVEL_DISTANCE:
                return Material.LEATHER_BOOTS;
            case VISIT_BIOME:
                return Material.GRASS_BLOCK;
            case ENTER_STRUCTURE:
                return Material.STONE_BRICKS;
            case REACH_Y_LEVEL:
                return Material.LADDER;
            case RIDE_ENTITY:
                return Material.SADDLE;
            case ENTER_PORTAL:
                return Material.OBSIDIAN;
            case SURVIVE_TIME:
                return Material.CLOCK;
            case KEEP_HUNGER:
                return Material.COOKED_BEEF;
            case NO_DAMAGE_TIME:
                return Material.SHIELD;
            case SURVIVE_LOW_HEALTH:
                return Material.GLISTERING_MELON_SLICE;
            case SURVIVE_UNDERWATER:
                return Material.WATER_BUCKET;
            case WEAR_FULL_ARMOR:
                return Material.IRON_CHESTPLATE;
            case PLACE_BLOCKS:
                return Material.SCAFFOLDING;
            case BREAK_BLOCKS:
                return Material.IRON_PICKAXE;
            case KILL_FROM_DISTANCE:
                return Material.SPYGLASS;
            case EAT_FOOD:
                return Material.BREAD;
            case HARVEST_CROP:
                return Material.WHEAT;
            case TAME_ANIMAL:
                return Material.BONE;
            case BREED_ANIMAL:
                return Material.GOLDEN_CARROT;
            case FILL_BUCKET:
                return Material.BUCKET;
            case BONE_MEAL:
                return Material.BONE_MEAL;
            case THROW_PROJECTILE:
                return Material.SNOWBALL;
            case GLIDE_DISTANCE:
                return Material.ELYTRA;
            case VISIT_WORLD:
                return Material.COMPASS;
            case SLEEP_IN_BED:
                return Material.RED_BED;
            case TAKE_DAMAGE:
                return Material.CACTUS;
            case GAIN_XP:
                return Material.EXPERIENCE_BOTTLE;
            case REACH_LEVEL:
                return Material.LAPIS_LAZULI;
            default:
                return Material.BOOK;
        }
    }
}
