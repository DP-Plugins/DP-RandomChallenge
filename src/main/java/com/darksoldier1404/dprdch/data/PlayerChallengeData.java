package com.darksoldier1404.dprdch.data;

import com.darksoldier1404.dppc.data.DataCargo;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerChallengeData implements DataCargo {
    private static final int HISTORY_LIMIT = 20;

    private UUID playerId;
    private String currentChallengeId;
    private int progress;
    private long assignedAt;
    private boolean completed;
    private int totalCompleted;
    private List<String> history = new ArrayList<>();

    public PlayerChallengeData() {
    }

    public PlayerChallengeData(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("playerId", playerId == null ? null : playerId.toString());
        data.set("currentChallengeId", currentChallengeId);
        data.set("progress", progress);
        data.set("assignedAt", assignedAt);
        data.set("completed", completed);
        data.set("totalCompleted", totalCompleted);
        data.set("history", history);
        return data;
    }

    @Override
    public PlayerChallengeData deserialize(YamlConfiguration data) {
        String uuid = data.getString("playerId");
        this.playerId = uuid == null ? null : UUID.fromString(uuid);
        this.currentChallengeId = data.getString("currentChallengeId");
        this.progress = data.getInt("progress", 0);
        this.assignedAt = data.getLong("assignedAt", 0L);
        this.completed = data.getBoolean("completed", false);
        this.totalCompleted = data.getInt("totalCompleted", 0);
        this.history = new ArrayList<>(data.getStringList("history"));
        return this;
    }

    public void addHistory(String challengeId) {
        history.add(challengeId + ";" + System.currentTimeMillis());
        while (history.size() > HISTORY_LIMIT) {
            history.remove(0);
        }
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public String getCurrentChallengeId() {
        return currentChallengeId;
    }

    public void setCurrentChallengeId(String currentChallengeId) {
        this.currentChallengeId = currentChallengeId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(long assignedAt) {
        this.assignedAt = assignedAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getTotalCompleted() {
        return totalCompleted;
    }

    public void setTotalCompleted(int totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

    public List<String> getHistory() {
        return history;
    }
}
