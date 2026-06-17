package com.darksoldier1404.dprdch.inventory;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardItemsGUI {
    public static final int CHANNEL = 5;

    public static void open(Player p, String id) {
        RandomChallenge plugin = RandomChallenge.plugin;
        ChallengeDefinition def = plugin.registry.get(id);
        if (def == null) {
            AdminGUI.openList(p);
            return;
        }
        DInventory inv = new DInventory(plugin.lang("reward-items-title"), 27, plugin);
        inv.setChannel(CHANNEL);
        inv.setObj(id);

        for (ItemStack item : def.getReward().getItemStacks()) {
            inv.addItem(item.clone());
        }
        inv.openInventory(p);
    }
}
