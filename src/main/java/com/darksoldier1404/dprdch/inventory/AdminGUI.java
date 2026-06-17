package com.darksoldier1404.dprdch.inventory;

import com.darksoldier1404.dppc.api.inventory.DAnvilInventory;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.builder.itemstack.ItemStackBuilder;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeDescriber;
import com.darksoldier1404.dprdch.challenge.ChallengeParams;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminGUI {
    public static final int CHANNEL_LIST = 2;
    public static final int CHANNEL_EDIT = 3;

    public static final String TAG_ID = "dprdch_id";
    public static final String TAG_CREATE = "dprdch_create";
    public static final String TAG_EDIT_DISPLAY = "dprdch_edit_display";
    public static final String TAG_EDIT_MONEY = "dprdch_edit_money";
    public static final String TAG_EDIT_PARAM = "dprdch_edit_param";
    public static final String TAG_EDIT_ITEMS = "dprdch_edit_items";
    public static final String TAG_EDIT_COMMANDS = "dprdch_edit_cmds";
    public static final String TAG_BACK = "dprdch_back";
    public static final String TAG_DELETE = "dprdch_delete";

    public static final String ID_PATTERN = "[a-zA-Z0-9_-]{1,32}";

    public static final String MODE_DISPLAY = "display";
    public static final String MODE_MONEY = "money";
    public static final String MODE_CREATE = "create";
    public static final String MODE_COMMAND = "cmd";
    public static final String MODE_PARAM_PREFIX = "param:";

    public static final Map<UUID, String[]> pendingInput = new HashMap<>();

    public static void openList(Player p) {
        RandomChallenge plugin = RandomChallenge.plugin;
        DInventory inv = new DInventory(plugin.lang("admin-gui-title"), 54, true, true, plugin);
        inv.setChannel(CHANNEL_LIST);

        List<ItemStack> items = new ArrayList<>();
        for (ChallengeDefinition def : plugin.registry.getAll()) {
            List<String> lore = new ArrayList<>();
            lore.add(plugin.lang("gui-lore-type", def.getType().name()));
            lore.addAll(ChallengeDescriber.describe(def));
            lore.add("");
            lore.add(plugin.lang("gui-lore-display", def.getDisplay()));
            lore.add(plugin.lang("gui-lore-reward-money", String.valueOf(def.getReward().getMoney())));
            lore.add(plugin.lang("edit-lore-items-count", String.valueOf(def.getReward().getItemRewardCount())));
            lore.add(plugin.lang("edit-lore-commands-count", String.valueOf(def.getReward().getCommands().size())));
            lore.add("");
            lore.add(plugin.lang("admin-lore-left"));
            lore.add(plugin.lang("admin-lore-shift-right"));
            ItemStack item = ItemStackBuilder.of(TypeSelectGUI.iconOf(def.getType()))
                    .name("&e" + def.getId())
                    .lore(lore)
                    .build();
            item = NBT.setStringTag(item, TAG_ID, def.getId());
            items.add(item);
        }
        inv.addPageItems(items);

        ItemStack createBtn = ItemStackBuilder.of(Material.NETHER_STAR)
                .name(plugin.lang("create-btn"))
                .lore(plugin.lang("create-lore"))
                .build();
        inv.setPageTool(0, NBT.setStringTag(createBtn, TAG_CREATE, "true"));
        inv.update();
        inv.openInventory(p);
    }

    public static void openEdit(Player p, String id) {
        RandomChallenge plugin = RandomChallenge.plugin;
        ChallengeDefinition def = plugin.registry.get(id);
        if (def == null) {
            openList(p);
            return;
        }
        DInventory inv = new DInventory(plugin.lang("edit-gui-title"), 54, plugin);
        inv.setChannel(CHANNEL_EDIT);
        inv.setObj(id);

        ItemStack pane = ItemStackBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, pane);
        }

        List<String> infoLore = new ArrayList<>();
        infoLore.add(plugin.lang("gui-lore-type", def.getType().name()));
        infoLore.addAll(ChallengeDescriber.describe(def));
        infoLore.add("");
        infoLore.add(plugin.lang("gui-lore-display", def.getDisplay()));
        inv.setItem(4, ItemStackBuilder.of(TypeSelectGUI.iconOf(def.getType()))
                .name("&e" + def.getId())
                .lore(infoLore)
                .build());

        int slot = 19;
        for (ChallengeParams.ParamSpec spec : ChallengeParams.of(def.getType())) {
            if (slot > 25) break;
            List<String> lore = new ArrayList<>();
            lore.add(spec.required ? plugin.lang("edit-lore-param-required") : plugin.lang("edit-lore-param-optional"));
            lore.add(plugin.lang("edit-lore-param-kind", spec.kind.name()));
            String current = def.getStringParam(spec.key);
            lore.add(plugin.lang("edit-lore-current", current == null ? "-" : current));
            if (spec.hint != null) {
                lore.add(plugin.lang("edit-lore-param-hint", spec.hint));
            }
            lore.add("");
            lore.add(plugin.lang("edit-lore-click"));
            if (!spec.required) {
                lore.add(plugin.lang("edit-lore-param-remove"));
            }
            ItemStack btn = ItemStackBuilder.of(spec.required ? Material.OAK_SIGN : Material.BIRCH_SIGN)
                    .name("&b" + spec.key)
                    .lore(lore)
                    .build();
            inv.setItem(slot++, NBT.setStringTag(btn, TAG_EDIT_PARAM, spec.key));
        }

        ItemStack displayBtn = ItemStackBuilder.of(Material.NAME_TAG)
                .name(plugin.lang("edit-btn-display"))
                .lore(plugin.lang("edit-lore-current", def.getDisplay()), plugin.lang("edit-lore-click"))
                .build();
        inv.setItem(37, NBT.setStringTag(displayBtn, TAG_EDIT_DISPLAY, "true"));

        ItemStack moneyBtn = ItemStackBuilder.of(Material.GOLD_INGOT)
                .name(plugin.lang("edit-btn-money"))
                .lore(plugin.lang("edit-lore-current", String.valueOf(def.getReward().getMoney())), plugin.lang("edit-lore-click"))
                .build();
        inv.setItem(39, NBT.setStringTag(moneyBtn, TAG_EDIT_MONEY, "true"));

        ItemStack itemsBtn = ItemStackBuilder.of(Material.CHEST)
                .name(plugin.lang("edit-btn-items"))
                .lore(plugin.lang("edit-lore-items-count", String.valueOf(def.getReward().getItemRewardCount())),
                        plugin.lang("edit-lore-click"))
                .build();
        inv.setItem(41, NBT.setStringTag(itemsBtn, TAG_EDIT_ITEMS, "true"));

        List<String> cmdLore = new ArrayList<>();
        cmdLore.add(plugin.lang("edit-lore-commands-count", String.valueOf(def.getReward().getCommands().size())));
        List<String> commands = def.getReward().getCommands();
        for (int i = 0; i < commands.size() && i < 5; i++) {
            cmdLore.add("&8- &7" + commands.get(i));
        }
        cmdLore.add("");
        cmdLore.add(plugin.lang("edit-lore-cmd-add"));
        cmdLore.add(plugin.lang("edit-lore-cmd-clear"));
        ItemStack cmdBtn = ItemStackBuilder.of(Material.REPEATING_COMMAND_BLOCK)
                .name(plugin.lang("edit-btn-commands"))
                .lore(cmdLore)
                .build();
        inv.setItem(43, NBT.setStringTag(cmdBtn, TAG_EDIT_COMMANDS, "true"));

        ItemStack backBtn = ItemStackBuilder.of(Material.ARROW).name(plugin.lang("edit-btn-back")).build();
        inv.setItem(45, NBT.setStringTag(backBtn, TAG_BACK, "true"));
        ItemStack deleteBtn = ItemStackBuilder.of(Material.BARRIER).name(plugin.lang("edit-btn-delete")).build();
        inv.setItem(53, NBT.setStringTag(deleteBtn, TAG_DELETE, "true"));

        inv.openInventory(p);
    }

    public static void openAnvilInput(Player p, String id, String mode) {
        RandomChallenge plugin = RandomChallenge.plugin;
        String title;
        String initial;
        if (MODE_CREATE.equals(mode)) {
            title = plugin.lang("anvil-create-title");
            initial = "new_challenge";
        } else {
            ChallengeDefinition def = plugin.registry.get(id);
            if (def == null) return;
            if (MODE_DISPLAY.equals(mode)) {
                title = plugin.lang("anvil-display-title");
                initial = def.getDisplay();
            } else if (MODE_MONEY.equals(mode)) {
                title = plugin.lang("anvil-money-title");
                initial = String.valueOf(def.getReward().getMoney());
            } else if (MODE_COMMAND.equals(mode)) {
                title = plugin.lang("anvil-command-title");
                initial = "give %player% diamond 1";
            } else if (mode.startsWith(MODE_PARAM_PREFIX)) {
                String key = mode.substring(MODE_PARAM_PREFIX.length());
                title = plugin.lang("anvil-param-title", key);
                String current = def.getStringParam(key);
                if (current == null) {
                    ChallengeParams.ParamSpec spec = ChallengeParams.spec(def.getType(), key);
                    current = spec != null && spec.def != null ? String.valueOf(spec.def) : key;
                }
                initial = current;
            } else {
                return;
            }
        }
        pendingInput.put(p.getUniqueId(), new String[]{id == null ? "" : id, mode});
        DAnvilInventory anvil = new DAnvilInventory(title, plugin)
                .text(initial)
                .setItem(2, ItemStackBuilder.of(Material.GREEN_WOOL).name(plugin.lang("anvil-confirm")).build());
        anvil.open(p);
    }
}
