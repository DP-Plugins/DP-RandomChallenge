package com.darksoldier1404.dprdch.api;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeDescriber;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.challenge.RenewalMode;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.data.PlayerChallengeData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class RandomChallengeAPI {

    private RandomChallengeAPI() {
    }

    private static RandomChallenge plugin() {
        return RandomChallenge.plugin;
    }

    public static boolean isAvailable() {
        return RandomChallenge.plugin != null && RandomChallenge.plugin.isEnabled();
    }

    public static ChallengeDefinition getChallenge(String id) {
        return plugin().registry.get(id);
    }

    public static Set<String> getChallengeIds() {
        return Collections.unmodifiableSet(new HashSet<>(plugin().registry.ids()));
    }

    public static Collection<ChallengeDefinition> getChallenges() {
        return Collections.unmodifiableList(new ArrayList<>(plugin().registry.getAll()));
    }

    public static boolean exists(String id) {
        return id != null && plugin().registry.exists(id);
    }

    public static String getObjective(ChallengeDefinition def) {
        return def == null ? null : ChallengeDescriber.objective(def);
    }

    public static ChallengeDefinition getActiveChallenge(Player p) {
        return plugin().assigner.getActive(p);
    }

    public static String getCurrentChallengeId(UUID playerId) {
        PlayerChallengeData data = plugin().udata.get(playerId);
        return data == null ? null : data.getCurrentChallengeId();
    }

    public static int getProgress(UUID playerId) {
        PlayerChallengeData data = plugin().udata.get(playerId);
        return data == null ? 0 : data.getProgress();
    }

    public static int getTarget(UUID playerId) {
        ChallengeDefinition def = getChallenge(getCurrentChallengeId(playerId));
        return def == null ? 0 : def.getTarget();
    }

    public static int getProgressPercent(UUID playerId) {
        int target = getTarget(playerId);
        if (target <= 0) return 0;
        return Math.min(100, getProgress(playerId) * 100 / target);
    }

    public static boolean isCompleted(UUID playerId) {
        PlayerChallengeData data = plugin().udata.get(playerId);
        return data != null && data.isCompleted();
    }

    public static int getTotalCompleted(UUID playerId) {
        PlayerChallengeData data = plugin().udata.get(playerId);
        return data == null ? 0 : data.getTotalCompleted();
    }

    public static List<ChallengeHistoryEntry> getHistory(UUID playerId) {
        PlayerChallengeData data = plugin().udata.get(playerId);
        if (data == null) return Collections.emptyList();
        List<ChallengeHistoryEntry> result = new ArrayList<>();
        for (int i = data.getHistory().size() - 1; i >= 0; i--) {
            String raw = data.getHistory().get(i);
            int idx = raw.lastIndexOf(';');
            if (idx < 0) continue;
            try {
                result.add(new ChallengeHistoryEntry(raw.substring(0, idx), Long.parseLong(raw.substring(idx + 1))));
            } catch (NumberFormatException ignored) {
            }
        }
        return Collections.unmodifiableList(result);
    }

    public static boolean assignRandom(Player p) {
        if (plugin().registry.isEmpty()) return false;
        plugin().assigner.assignRandom(p, plugin().assigner.getOrCreate(p), AssignCause.API);
        return getCurrentChallengeId(p.getUniqueId()) != null;
    }

    public static boolean assignChallenge(Player p, String challengeId) {
        ChallengeDefinition def = plugin().registry.get(challengeId);
        if (def == null) return false;
        plugin().assigner.assignSpecific(p, def, AssignCause.API);
        return def.getId().equals(getCurrentChallengeId(p.getUniqueId())) && !isCompleted(p.getUniqueId());
    }

    public static void clearChallenge(Player p) {
        plugin().assigner.clearChallenge(p);
    }

    public static void addProgress(Player p, int amount) {
        ChallengeDefinition def = getActiveChallenge(p);
        if (def == null || amount <= 0) return;
        plugin().assigner.addProgress(p, def.getType(), amount);
    }

    public static void setProgress(Player p, int value) {
        plugin().assigner.setProgress(p, value);
    }

    public static boolean completeChallenge(Player p) {
        ChallengeDefinition def = getActiveChallenge(p);
        if (def == null) return false;
        plugin().assigner.setProgress(p, def.getTarget());
        return true;
    }

    public static ChallengeDefinition createChallenge(String id, ChallengeType type) {
        if (id == null || type == null || plugin().registry.exists(id)) return null;
        return plugin().registry.create(id, type);
    }

    public static void saveChallenge(ChallengeDefinition def) {
        if (def == null || def.getId() == null) return;
        plugin().registry.saveDefinition(def);
    }

    public static boolean deleteChallenge(String id) {
        if (!exists(id)) return false;
        plugin().registry.delete(id);
        return true;
    }

    public static RenewalMode getRenewalMode() {
        return plugin().registry.getRenewalMode();
    }

    public static int getTimeInterval() {
        return plugin().registry.getTimeInterval();
    }
}
