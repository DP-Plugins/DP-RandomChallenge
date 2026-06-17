package com.darksoldier1404.dprdch.inventory;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.builder.itemstack.ItemStackBuilder;
import com.darksoldier1404.dppc.utils.TimeUtils;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeDescriber;
import com.darksoldier1404.dprdch.challenge.RenewalMode;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.data.PlayerChallengeData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChallengeGUI {
    public static final int CHANNEL = 1;

    public static void open(Player p) {
        RandomChallenge plugin = RandomChallenge.plugin;
        DInventory inv = new DInventory(plugin.lang("gui-title"), 27, plugin);
        inv.setChannel(CHANNEL);

        ItemStack pane = ItemStackBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, pane);
        }

        PlayerChallengeData data = plugin.udata.get(p.getUniqueId());
        ChallengeDefinition def = data == null ? null : plugin.registry.get(data.getCurrentChallengeId());

        if (def == null) {
            inv.setItem(13, ItemStackBuilder.of(Material.BARRIER).name(plugin.lang("no-challenge")).build());
        } else {
            List<String> lore = new ArrayList<>();
            lore.add(plugin.lang("gui-lore-type", def.getType().name()));
            lore.addAll(ChallengeDescriber.describe(def));
            lore.add("");
            int target = def.getTarget();
            int progress = Math.min(data.getProgress(), target);
            int percent = target <= 0 ? 100 : progress * 100 / target;
            lore.add(plugin.lang("progress", String.valueOf(progress), String.valueOf(target), String.valueOf(percent)));
            if (plugin.registry.getRenewalMode() != RenewalMode.COMPLETE && plugin.registry.getTimeInterval() > 0) {
                lore.add(plugin.lang("time-left", plugin.assigner.formatTimeLeft(data)));
            }
            lore.add("");
            if (def.getReward().getMoney() > 0) {
                lore.add(plugin.lang("gui-lore-reward-money", String.valueOf(def.getReward().getMoney())));
            }
            for (ItemStack item : def.getReward().getItemStacks()) {
                lore.add(plugin.lang("gui-lore-reward-item", item.getType().name(), String.valueOf(item.getAmount())));
            }
            if (!def.getReward().getCommands().isEmpty()) {
                lore.add(plugin.lang("gui-lore-reward-command", String.valueOf(def.getReward().getCommands().size())));
            }
            if (data.isCompleted()) {
                lore.add("");
                lore.add(plugin.lang("gui-completed-state"));
            }
            inv.setItem(13, ItemStackBuilder.of(Material.BOOK).name(def.getDisplay()).lore(lore).build());
        }

        if (data != null && !data.getHistory().isEmpty()) {
            List<String> history = data.getHistory();
            int slot = 18;
            for (int i = history.size() - 1; i >= 0 && slot <= 22; i--, slot++) {
                String[] parts = history.get(i).split(";", 2);
                ChallengeDefinition done = plugin.registry.get(parts[0]);
                String name = done == null ? parts[0] : done.getDisplay();
                String date = "";
                if (parts.length > 1) {
                    try {
                        date = TimeUtils.formatDate(new Date(Long.parseLong(parts[1])));
                    } catch (NumberFormatException ignored) {
                    }
                }
                inv.setItem(slot, ItemStackBuilder.of(Material.PAPER)
                        .name(name)
                        .lore(plugin.lang("gui-history-entry", date))
                        .build());
            }
        } else {
            inv.setItem(18, ItemStackBuilder.of(Material.GRAY_DYE).name(plugin.lang("gui-history-empty")).build());
        }

        inv.openInventory(p);
    }
}
