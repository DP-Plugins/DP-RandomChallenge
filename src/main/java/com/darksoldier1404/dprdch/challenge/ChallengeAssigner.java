package com.darksoldier1404.dprdch.challenge;

import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.api.AssignCause;
import com.darksoldier1404.dprdch.api.events.ChallengeAssignEvent;
import com.darksoldier1404.dprdch.api.events.ChallengeCompleteEvent;
import com.darksoldier1404.dprdch.api.events.ChallengeProgressEvent;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.data.PlayerChallengeData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChallengeAssigner {
    private final RandomChallenge plugin;
    private final Map<UUID, Long> lastActionBar = new HashMap<>();

    public ChallengeAssigner(RandomChallenge plugin) {
        this.plugin = plugin;
    }

    public PlayerChallengeData getData(Player p) {
        return plugin.udata.get(p.getUniqueId());
    }

    public PlayerChallengeData getOrCreate(Player p) {
        PlayerChallengeData data = plugin.udata.get(p.getUniqueId());
        if (data == null) {
            data = new PlayerChallengeData(p.getUniqueId());
            plugin.udata.put(p.getUniqueId(), data);
        }
        return data;
    }

    public void handleJoin(Player p) {
        PlayerChallengeData data = getOrCreate(p);
        ChallengeDefinition def = plugin.registry.get(data.getCurrentChallengeId());
        if (def == null) {
            if (plugin.registry.getRenewalMode() != RenewalMode.FIXED) {
                assignRandom(p, data, AssignCause.JOIN);
            }
            return;
        }
        checkRenewal(p, data);
        def = plugin.registry.get(data.getCurrentChallengeId());
        if (def != null && !data.isCompleted()) {
            plugin.send(p, "current-challenge", ColorUtils.applyColor(def.getDisplay()));
            plugin.send(p, "objective-chat", ChallengeDescriber.objective(def));
        }
    }

    public void assignRandom(Player p, PlayerChallengeData data) {
        assignRandom(p, data, AssignCause.RENEWAL);
    }

    public void assignRandom(Player p, PlayerChallengeData data, AssignCause cause) {
        ChallengeDefinition def = plugin.registry.random(data.getCurrentChallengeId());
        if (def == null) {
            data.setProgress(0);
            data.setCompleted(false);
            data.setAssignedAt(System.currentTimeMillis());
            data.setCurrentChallengeId(null);
            return;
        }
        assign(p, data, def, cause);
    }

    public void assignSpecific(Player p, ChallengeDefinition def) {
        assignSpecific(p, def, AssignCause.COMMAND);
    }

    public void assignSpecific(Player p, ChallengeDefinition def, AssignCause cause) {
        assign(p, getOrCreate(p), def, cause);
    }

    private void assign(Player p, PlayerChallengeData data, ChallengeDefinition def, AssignCause cause) {
        ChallengeAssignEvent event = new ChallengeAssignEvent(p, def, cause);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        def = event.getDefinition();
        data.setProgress(0);
        data.setCompleted(false);
        data.setAssignedAt(System.currentTimeMillis());
        data.setCurrentChallengeId(def.getId());
        plugin.udata.save(p.getUniqueId());
        plugin.send(p, "challenge-assigned", ColorUtils.applyColor(def.getDisplay()));
        plugin.send(p, "objective-chat", ChallengeDescriber.objective(def));
    }

    public void clearChallenge(Player p) {
        PlayerChallengeData data = getOrCreate(p);
        data.setCurrentChallengeId(null);
        data.setProgress(0);
        data.setCompleted(false);
        plugin.udata.save(p.getUniqueId());
    }

    public ChallengeDefinition getActive(Player p) {
        PlayerChallengeData data = plugin.udata.get(p.getUniqueId());
        if (data == null || data.isCompleted()) return null;
        return plugin.registry.get(data.getCurrentChallengeId());
    }

    public void checkRenewal(Player p, PlayerChallengeData data) {
        RenewalMode mode = plugin.registry.getRenewalMode();
        if (mode == RenewalMode.COMPLETE || mode == RenewalMode.FIXED) return;
        long interval = plugin.registry.getTimeInterval() * 1000L;
        if (interval <= 0) return;
        if (System.currentTimeMillis() - data.getAssignedAt() >= interval) {
            assignRandom(p, data);
        }
    }

    public void addProgress(Player p, ChallengeType type, int amount) {
        PlayerChallengeData data = plugin.udata.get(p.getUniqueId());
        if (data == null || data.isCompleted()) return;
        ChallengeDefinition def = plugin.registry.get(data.getCurrentChallengeId());
        if (def == null || def.getType() != type) return;
        ChallengeProgressEvent event = new ChallengeProgressEvent(p, def, data.getProgress(), amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getAmount() <= 0) return;
        data.setProgress(data.getProgress() + event.getAmount());
        if (data.getProgress() >= def.getTarget()) {
            complete(p, data, def);
        } else {
            sendProgressActionBar(p, data, def);
        }
    }

    public void setProgress(Player p, int value) {
        PlayerChallengeData data = plugin.udata.get(p.getUniqueId());
        if (data == null || data.isCompleted()) return;
        ChallengeDefinition def = plugin.registry.get(data.getCurrentChallengeId());
        if (def == null) return;
        data.setProgress(Math.max(0, value));
        if (data.getProgress() >= def.getTarget()) {
            complete(p, data, def);
        }
    }

    public void resetProgress(Player p, ChallengeType type) {
        PlayerChallengeData data = plugin.udata.get(p.getUniqueId());
        if (data == null || data.isCompleted()) return;
        ChallengeDefinition def = plugin.registry.get(data.getCurrentChallengeId());
        if (def == null || def.getType() != type) return;
        data.setProgress(0);
    }

    private void complete(Player p, PlayerChallengeData data, ChallengeDefinition def) {
        data.setProgress(def.getTarget());
        data.setCompleted(true);
        data.setTotalCompleted(data.getTotalCompleted() + 1);
        data.addHistory(def.getId());
        plugin.send(p, "challenge-completed", ColorUtils.applyColor(def.getDisplay()));
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        def.getReward().give(p);
        Bukkit.getPluginManager().callEvent(new ChallengeCompleteEvent(p, def, data.getTotalCompleted()));
        RenewalMode mode = plugin.registry.getRenewalMode();
        if (mode == RenewalMode.COMPLETE || mode == RenewalMode.MIXED) {
            assignRandom(p, data, AssignCause.COMPLETE);
        } else if (mode == RenewalMode.FIXED) {
            plugin.send(p, "challenge-completed-fixed");
            plugin.udata.save(p.getUniqueId());
        } else {
            plugin.send(p, "challenge-completed-waiting");
            plugin.udata.save(p.getUniqueId());
        }
    }

    private void sendProgressActionBar(Player p, PlayerChallengeData data, ChallengeDefinition def) {
        if (!plugin.getConfig().getBoolean("Settings.progress-actionbar", true)) return;
        long now = System.currentTimeMillis();
        Long last = lastActionBar.get(p.getUniqueId());
        if (last != null && now - last < 1000L) return;
        lastActionBar.put(p.getUniqueId(), now);
        String text = plugin.lang("progress-actionbar",
                ColorUtils.applyColor(def.getDisplay()),
                String.valueOf(Math.min(data.getProgress(), def.getTarget())),
                String.valueOf(def.getTarget()));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public void clearCache(UUID uuid) {
        lastActionBar.remove(uuid);
    }

    public String formatTimeLeft(PlayerChallengeData data) {
        long interval = plugin.registry.getTimeInterval() * 1000L;
        long remain = data.getAssignedAt() + interval - System.currentTimeMillis();
        if (remain < 0) remain = 0;
        long totalSec = remain / 1000L;
        long h = totalSec / 3600;
        long m = (totalSec % 3600) / 60;
        long s = totalSec % 60;
        if (h > 0) {
            return String.format("%d:%02d:%02d", h, m, s);
        }
        return String.format("%02d:%02d", m, s);
    }
}
