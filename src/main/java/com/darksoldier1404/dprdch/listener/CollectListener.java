package com.darksoldier1404.dprdch.listener;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class CollectListener implements Listener {
    private final RandomChallenge plugin = RandomChallenge.plugin;

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.COLLECT_ITEM) return;
        String source = def.getStringParam("source");
        if (source != null && source.equalsIgnoreCase("MINE")) return;
        ItemStack stack = e.getItem().getItemStack();
        if (!def.matchParam("item", stack.getType().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.COLLECT_ITEM, stack.getAmount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent e) {
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.COLLECT_ITEM) return;
        String source = def.getStringParam("source");
        if (source == null || !source.equalsIgnoreCase("MINE")) return;
        int total = 0;
        for (Item item : e.getItems()) {
            ItemStack stack = item.getItemStack();
            if (def.matchParam("item", stack.getType().name())) {
                total += stack.getAmount();
            }
        }
        if (total > 0) {
            plugin.assigner.addProgress(p, ChallengeType.COLLECT_ITEM, total);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        final Player p = (Player) e.getWhoClicked();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.CRAFT_ITEM) return;
        ItemStack result = e.getRecipe().getResult();
        if (!def.matchParam("item", result.getType().name())) return;

        if (e.isShiftClick()) {
            final Material type = result.getType();
            final int before = countByType(p, type);
            Bukkit.getScheduler().runTask(plugin, () -> {
                int crafted = countByType(p, type) - before;
                if (crafted > 0) {
                    plugin.assigner.addProgress(p, ChallengeType.CRAFT_ITEM, crafted);
                }
            });
        } else {
            plugin.assigner.addProgress(p, ChallengeType.CRAFT_ITEM, result.getAmount());
        }
    }

    private int countByType(Player p, Material type) {
        int total = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == type) {
                total += item.getAmount();
            }
        }
        return total;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(e.getCaught() instanceof Item)) return;
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.FISH_ITEM) return;
        ItemStack stack = ((Item) e.getCaught()).getItemStack();
        if (!def.matchParam("item", stack.getType().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.FISH_ITEM, stack.getAmount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmelt(FurnaceExtractEvent e) {
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.SMELT_ITEM) return;
        if (!def.matchParam("item", e.getItemType().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.SMELT_ITEM, e.getItemAmount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent e) {
        Player p = e.getEnchanter();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.ENCHANT_ITEM) return;
        String item = def.getStringParam("item");
        if (item != null && !e.getItem().getType().name().contains(item.toUpperCase())) return;
        String enchant = def.getStringParam("enchantment");
        if (enchant != null && !hasEnchant(e, enchant)) return;
        plugin.assigner.addProgress(p, ChallengeType.ENCHANT_ITEM, 1);
    }

    private boolean hasEnchant(EnchantItemEvent e, String name) {
        for (Enchantment ench : e.getEnchantsToAdd().keySet()) {
            if (ench.getKey().getKey().equalsIgnoreCase(name) || ench.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent e) {
        if (!(e.getEntity() instanceof Sheep)) return;
        Player p = e.getPlayer();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null || def.getType() != ChallengeType.SHEAR_SHEEP) return;
        if (!def.matchParam("color", ((Sheep) e.getEntity()).getColor().name())) return;
        plugin.assigner.addProgress(p, ChallengeType.SHEAR_SHEEP, 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onResultTake(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getClickedInventory() == null) return;
        Player p = (Player) e.getWhoClicked();
        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def == null) return;
        ItemStack current = e.getCurrentItem();
        if (current == null || current.getType() == Material.AIR) return;

        if (def.getType() == ChallengeType.BREW_POTION
                && e.getClickedInventory().getType() == InventoryType.BREWING
                && e.getRawSlot() >= 0 && e.getRawSlot() <= 2
                && isPotion(current)) {
            String potionType = def.getStringParam("potion_type");
            if (potionType != null && !matchPotionType(current, potionType)) return;
            plugin.assigner.addProgress(p, ChallengeType.BREW_POTION, current.getAmount());
            return;
        }

        if (def.getType() == ChallengeType.TRADE_VILLAGER
                && e.getClickedInventory().getType() == InventoryType.MERCHANT
                && e.getRawSlot() == 2) {
            if (!def.matchParam("item", current.getType().name())) return;
            plugin.assigner.addProgress(p, ChallengeType.TRADE_VILLAGER, current.getAmount());
        }
    }

    private boolean isPotion(ItemStack item) {
        Material type = item.getType();
        return type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION;
    }

    private boolean matchPotionType(ItemStack item, String typeName) {
        if (!(item.getItemMeta() instanceof PotionMeta)) return false;
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        return meta.getBasePotionData().getType().name().equalsIgnoreCase(typeName);
    }
}
