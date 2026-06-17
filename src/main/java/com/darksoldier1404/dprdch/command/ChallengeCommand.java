package com.darksoldier1404.dprdch.command;

import com.darksoldier1404.dppc.builder.command.ArgumentIndex;
import com.darksoldier1404.dppc.builder.command.ArgumentType;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeDescriber;
import com.darksoldier1404.dprdch.challenge.RenewalMode;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.data.PlayerChallengeData;
import com.darksoldier1404.dprdch.inventory.AdminGUI;
import com.darksoldier1404.dprdch.inventory.ChallengeGUI;
import com.darksoldier1404.dprdch.inventory.TypeSelectGUI;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ChallengeCommand {

    public static void register(final RandomChallenge plugin) {
        CommandBuilder builder = new CommandBuilder(plugin);

        builder.setDefaultAction((sender, args) -> {
            if (sender instanceof Player) {
                showStatus(plugin, (Player) sender);
            }
        });

        builder.beginSubCommand("info", "/dprandomchallenge info")
                .executesPlayer((p, args) -> {
                    showStatus(plugin, p);
                    return true;
                });

        builder.beginSubCommand("gui", "/dprandomchallenge gui")
                .executesPlayer((p, args) -> {
                    ChallengeGUI.open(p);
                    return true;
                });

        builder.beginSubCommand("create", "/dprandomchallenge create <id>")
                .withPermission("dprdch.admin")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING)
                .executesPlayer((p, args) -> {
                    String id = args.getString(ArgumentIndex.ARG_0);
                    if (id == null) return false;
                    id = id.replace(' ', '_');
                    if (!id.matches(AdminGUI.ID_PATTERN)) {
                        plugin.send(p, "create-id-invalid", id);
                        return true;
                    }
                    if (plugin.registry.exists(id)) {
                        plugin.send(p, "create-id-exists", id);
                        return true;
                    }
                    TypeSelectGUI.open(p, id);
                    return true;
                });

        builder.beginSubCommand("reload", "/dprandomchallenge reload")
                .withPermission("dprdch.admin")
                .executes((sender, args) -> {
                    plugin.reload();
                    plugin.registry.load();
                    plugin.send(sender, "reloaded");
                    return true;
                });

        builder.beginSubCommand("admin", "/dprandomchallenge admin [give|reset|clear] [player] [challengeId]")
                .withPermission("dprdch.admin")
                .withOptionalArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, Arrays.asList("give", "reset", "clear"))
                .withOptionalArgument(ArgumentIndex.ARG_1, ArgumentType.PLAYER)
                .withOptionalArgument(ArgumentIndex.ARG_2, ArgumentType.STRING,
                        (player, args) -> new java.util.ArrayList<>(plugin.registry.ids()))
                .executes((sender, args) -> {
                    String action = args.getString(ArgumentIndex.ARG_0);
                    if (action == null) {
                        if (!(sender instanceof Player)) return false;
                        AdminGUI.openList((Player) sender);
                        return true;
                    }
                    Player target = args.getPlayer(ArgumentIndex.ARG_1);
                    if (target == null) return false;
                    if (action.equalsIgnoreCase("give")) {
                        String challengeId = args.getString(ArgumentIndex.ARG_2);
                        if (challengeId != null) {
                            ChallengeDefinition def = plugin.registry.get(challengeId);
                            if (def == null) {
                                plugin.send(sender, "unknown-challenge", challengeId);
                                return true;
                            }
                            plugin.assigner.assignSpecific(target, def);
                        } else {
                            plugin.assigner.assignRandom(target, plugin.assigner.getOrCreate(target),
                                    com.darksoldier1404.dprdch.api.AssignCause.COMMAND);
                        }
                        plugin.send(sender, "admin-given", target.getName());
                        return true;
                    }
                    if (action.equalsIgnoreCase("reset")) {
                        PlayerChallengeData data = new PlayerChallengeData(target.getUniqueId());
                        plugin.udata.put(target.getUniqueId(), data);
                        if (plugin.registry.getRenewalMode() != RenewalMode.FIXED) {
                            plugin.assigner.assignRandom(target, data,
                                    com.darksoldier1404.dprdch.api.AssignCause.COMMAND);
                        } else {
                            plugin.udata.save(target.getUniqueId());
                        }
                        plugin.send(sender, "admin-reset", target.getName());
                        return true;
                    }
                    if (action.equalsIgnoreCase("clear")) {
                        plugin.assigner.clearChallenge(target);
                        plugin.send(sender, "admin-cleared", target.getName());
                        return true;
                    }
                    return false;
                });

        builder.build("dprandomchallenge");
    }

    public static void showStatus(RandomChallenge plugin, Player p) {
        PlayerChallengeData data = plugin.udata.get(p.getUniqueId());
        ChallengeDefinition def = data == null ? null : plugin.registry.get(data.getCurrentChallengeId());
        if (def == null) {
            plugin.send(p, "no-challenge");
            return;
        }
        plugin.send(p, "current-challenge", ColorUtils.applyColor(def.getDisplay()));
        plugin.send(p, "objective-chat", ChallengeDescriber.objective(def));
        for (String condition : ChallengeDescriber.conditions(def)) {
            p.sendMessage(plugin.getPrefix() + condition);
        }
        int target = def.getTarget();
        int progress = Math.min(data.getProgress(), target);
        int percent = target <= 0 ? 100 : progress * 100 / target;
        plugin.send(p, "progress", String.valueOf(progress), String.valueOf(target), String.valueOf(percent));
        if (plugin.registry.getRenewalMode() != RenewalMode.COMPLETE && plugin.registry.getTimeInterval() > 0) {
            plugin.send(p, "time-left", plugin.assigner.formatTimeLeft(data));
        }
        if (data.isCompleted()) {
            plugin.send(p, "challenge-completed-waiting");
        }
    }
}
