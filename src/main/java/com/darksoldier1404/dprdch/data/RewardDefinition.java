package com.darksoldier1404.dprdch.data;

import com.darksoldier1404.dppc.api.essentials.MoneyAPI;
import com.darksoldier1404.dppc.utils.ItemStackSerializer;
import com.darksoldier1404.dprdch.RandomChallenge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RewardDefinition {
    private double money;
    private List<ItemStack> itemStacks;
    private List<String> commands;

    public RewardDefinition() {
        this.money = 0;
        this.itemStacks = new ArrayList<>();
        this.commands = new ArrayList<>();
    }

    public RewardDefinition(double money, List<String> serializedItems, List<String> commands) {
        this.money = money;
        this.commands = commands == null ? new ArrayList<>() : commands;
        this.itemStacks = new ArrayList<>();
        if (serializedItems != null) {
            for (String s : serializedItems) {
                try {
                    ItemStack item = ItemStackSerializer.deserialize(s);
                    if (item != null) itemStacks.add(item);
                } catch (Exception e) {
                    RandomChallenge.plugin.log.warning("Failed to deserialize reward item: " + e.getMessage(), true);
                }
            }
        }
    }

    public List<String> serializeItemStacks() {
        List<String> out = new ArrayList<>();
        for (ItemStack item : itemStacks) {
            out.add(ItemStackSerializer.serialize(item));
        }
        return out;
    }

    public void give(Player p) {
        RandomChallenge plugin = RandomChallenge.plugin;
        if (money > 0 && MoneyAPI.isEnabled()) {
            MoneyAPI.addMoney(p, money);
            plugin.send(p, "reward-money", String.valueOf(money));
        }
        for (ItemStack item : itemStacks) {
            Map<Integer, ItemStack> left = p.getInventory().addItem(item.clone());
            for (ItemStack rest : left.values()) {
                p.getWorld().dropItemNaturally(p.getLocation(), rest);
            }
        }
        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", p.getName()));
        }
    }

    public int getItemRewardCount() {
        return itemStacks.size();
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }

    public void setItemStacks(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks == null ? new ArrayList<ItemStack>() : itemStacks;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
