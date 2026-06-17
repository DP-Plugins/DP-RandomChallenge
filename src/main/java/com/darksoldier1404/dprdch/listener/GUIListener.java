package com.darksoldier1404.dprdch.listener;

import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryClickEvent;
import com.darksoldier1404.dppc.events.danvilinventory.DAnvilInventoryCloseEvent;
import com.darksoldier1404.dppc.events.dinventory.DInventoryClickEvent;
import com.darksoldier1404.dppc.events.dinventory.DInventoryCloseEvent;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeParams;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.inventory.AdminGUI;
import com.darksoldier1404.dprdch.inventory.ChallengeGUI;
import com.darksoldier1404.dprdch.inventory.RewardItemsGUI;
import com.darksoldier1404.dprdch.inventory.TypeSelectGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIListener implements Listener {
    private final RandomChallenge plugin = RandomChallenge.plugin;

    @EventHandler
    public void onClick(DInventoryClickEvent e) {
        DInventory inv = e.getDInventory();
        if (!inv.isValidHandler(plugin)) return;

        if (inv.isValidChannel(ChallengeGUI.CHANNEL)) {
            e.setCancelled(true);
            return;
        }

        if (inv.isValidChannel(RewardItemsGUI.CHANNEL)) {
            return;
        }

        if (inv.isValidChannel(AdminGUI.CHANNEL_LIST)) {
            handleListClick(e, inv);
            return;
        }
        if (inv.isValidChannel(AdminGUI.CHANNEL_EDIT)) {
            handleEditClick(e, inv);
            return;
        }
        if (inv.isValidChannel(TypeSelectGUI.CHANNEL)) {
            handleTypeSelectClick(e, inv);
        }
    }

    private void handleListClick(DInventoryClickEvent e, DInventory inv) {
        e.setCancelled(true);
        if (e.isPlayerInventory()) return;
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        final Player p = (Player) e.getWhoClicked();

        if (NBT.hasTagKey(item, AdminGUI.TAG_CREATE)) {
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openAnvilInput(p, null, AdminGUI.MODE_CREATE));
            return;
        }
        if (!NBT.hasTagKey(item, AdminGUI.TAG_ID)) return;
        final String id = NBT.getStringTag(item, AdminGUI.TAG_ID);
        if (e.getClick() == ClickType.SHIFT_RIGHT) {
            plugin.registry.delete(id);
            plugin.send(p, "challenge-deleted", id);
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openList(p));
        } else if (e.getClick() == ClickType.LEFT) {
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openEdit(p, id));
        }
    }

    private void handleEditClick(DInventoryClickEvent e, DInventory inv) {
        e.setCancelled(true);
        if (e.isPlayerInventory()) return;
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        final String id = (String) inv.getObj();
        final Player p = (Player) e.getWhoClicked();
        ChallengeDefinition def = plugin.registry.get(id);
        if (def == null) {
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openList(p));
            return;
        }

        if (NBT.hasTagKey(item, AdminGUI.TAG_EDIT_PARAM)) {
            final String key = NBT.getStringTag(item, AdminGUI.TAG_EDIT_PARAM);
            ChallengeParams.ParamSpec spec = ChallengeParams.spec(def.getType(), key);
            if (spec == null) return;
            if (e.getClick() == ClickType.SHIFT_RIGHT && !spec.required) {
                plugin.registry.setParam(id, key, null);
                plugin.send(p, "param-removed", key);
                Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openEdit(p, id));
            } else {
                Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openAnvilInput(p, id, AdminGUI.MODE_PARAM_PREFIX + key));
            }
            return;
        }
        if (NBT.hasTagKey(item, AdminGUI.TAG_EDIT_DISPLAY)) {
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openAnvilInput(p, id, AdminGUI.MODE_DISPLAY));
            return;
        }
        if (NBT.hasTagKey(item, AdminGUI.TAG_EDIT_MONEY)) {
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openAnvilInput(p, id, AdminGUI.MODE_MONEY));
            return;
        }
        if (NBT.hasTagKey(item, AdminGUI.TAG_EDIT_ITEMS)) {
            Bukkit.getScheduler().runTask(plugin, () -> RewardItemsGUI.open(p, id));
            return;
        }
        if (NBT.hasTagKey(item, AdminGUI.TAG_EDIT_COMMANDS)) {
            if (e.getClick() == ClickType.SHIFT_RIGHT) {
                plugin.registry.clearCommands(id);
                plugin.send(p, "commands-cleared", id);
                Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openEdit(p, id));
            } else {
                Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openAnvilInput(p, id, AdminGUI.MODE_COMMAND));
            }
            return;
        }
        if (NBT.hasTagKey(item, AdminGUI.TAG_BACK)) {
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openList(p));
            return;
        }
        if (NBT.hasTagKey(item, AdminGUI.TAG_DELETE)) {
            plugin.registry.delete(id);
            plugin.send(p, "challenge-deleted", id);
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openList(p));
        }
    }

    private void handleTypeSelectClick(DInventoryClickEvent e, DInventory inv) {
        e.setCancelled(true);
        if (e.isPlayerInventory()) return;
        ItemStack item = e.getCurrentItem();
        if (item == null || !NBT.hasTagKey(item, TypeSelectGUI.TAG_TYPE)) return;
        final Player p = (Player) e.getWhoClicked();
        final String id = (String) inv.getObj();
        if (id == null || id.isEmpty()) return;
        ChallengeType type;
        try {
            type = ChallengeType.valueOf(NBT.getStringTag(item, TypeSelectGUI.TAG_TYPE));
        } catch (IllegalArgumentException ex) {
            return;
        }
        if (plugin.registry.exists(id)) {
            plugin.send(p, "create-id-exists", id);
            Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openList(p));
            return;
        }
        plugin.registry.create(id, type);
        plugin.send(p, "challenge-created", id);
        Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openEdit(p, id));
    }

    @EventHandler
    public void onClose(DInventoryCloseEvent e) {
        DInventory inv = e.getDInventory();
        if (!inv.isValidHandler(plugin)) return;
        if (!inv.isValidChannel(RewardItemsGUI.CHANNEL)) return;
        final String id = (String) inv.getObj();
        if (id == null || plugin.registry.get(id) == null) return;
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                items.add(item.clone());
            }
        }
        plugin.registry.setRewardItems(id, items);
        final Player p = (Player) e.getPlayer();
        plugin.send(p, "reward-items-saved", id, String.valueOf(items.size()));
        Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openEdit(p, id));
    }

    @EventHandler
    public void onAnvilClick(DAnvilInventoryClickEvent e) {
        if (!e.getDAnvilInventory().isValidHandler(plugin)) return;
        e.setCancelled(true);
        if (e.isPlayerInventory()) return;
        if (e.getRawSlot() != 2) return;

        final Player p = (Player) e.getWhoClicked();
        String[] ctx = AdminGUI.pendingInput.remove(p.getUniqueId());
        String input = e.getRenameText();
        e.getDAnvilInventory().close();
        if (ctx == null) return;
        if (input == null) {
            plugin.send(p, "anvil-unsupported");
            return;
        }
        input = input.trim();

        final String id = ctx[0];
        final String mode = ctx[1];

        if (AdminGUI.MODE_CREATE.equals(mode)) {
            final String newId = input.replace(' ', '_');
            if (!newId.matches(AdminGUI.ID_PATTERN)) {
                plugin.send(p, "create-id-invalid", newId);
                return;
            }
            if (plugin.registry.exists(newId)) {
                plugin.send(p, "create-id-exists", newId);
                return;
            }
            Bukkit.getScheduler().runTask(plugin, () -> TypeSelectGUI.open(p, newId));
            return;
        }

        ChallengeDefinition def = plugin.registry.get(id);
        if (def == null) return;

        if (AdminGUI.MODE_DISPLAY.equals(mode)) {
            plugin.registry.setDisplay(id, input);
            plugin.send(p, "display-changed", id);
        } else if (AdminGUI.MODE_MONEY.equals(mode)) {
            double money;
            try {
                money = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                plugin.send(p, "invalid-number", input);
                Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openEdit(p, id));
                return;
            }
            plugin.registry.setMoney(id, money);
            plugin.send(p, "money-changed", id, String.valueOf(money));
        } else if (AdminGUI.MODE_COMMAND.equals(mode)) {
            String command = input.startsWith("/") ? input.substring(1) : input;
            plugin.registry.addCommand(id, command);
            plugin.send(p, "command-added", id);
        } else if (mode.startsWith(AdminGUI.MODE_PARAM_PREFIX)) {
            String key = mode.substring(AdminGUI.MODE_PARAM_PREFIX.length());
            ChallengeParams.ParamSpec spec = ChallengeParams.spec(def.getType(), key);
            if (spec == null) return;
            Object value = validateParam(spec, input);
            if (value == null) {
                plugin.send(p, "invalid-value", input);
                Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openEdit(p, id));
                return;
            }
            plugin.registry.setParam(id, key, value);
            plugin.send(p, "param-changed", key, String.valueOf(value));
        }
        Bukkit.getScheduler().runTask(plugin, () -> AdminGUI.openEdit(p, id));
    }

    private Object validateParam(ChallengeParams.ParamSpec spec, String input) {
        switch (spec.kind) {
            case INT:
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    return null;
                }
            case DOUBLE:
                try {
                    return Double.parseDouble(input);
                } catch (NumberFormatException ex) {
                    return null;
                }
            case MATERIAL: {
                Material mat = Material.matchMaterial(input);
                return mat == null ? null : mat.name();
            }
            case ENTITY:
                try {
                    return EntityType.valueOf(input.toUpperCase().replace(' ', '_')).name();
                } catch (IllegalArgumentException ex) {
                    return null;
                }
            default:
                return input;
        }
    }

    @EventHandler
    public void onAnvilClose(DAnvilInventoryCloseEvent e) {
        if (!e.getDAnvilInventory().isValidHandler(plugin)) return;
        e.getInventory().clear();
        Player viewer = e.getDAnvilInventory().getViewer();
        if (viewer != null) {
            AdminGUI.pendingInput.remove(viewer.getUniqueId());
        }
    }
}
