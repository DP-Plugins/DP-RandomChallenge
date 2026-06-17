package com.darksoldier1404.dprdch.api;

public class ChallengeHistoryEntry {
    private final String challengeId;
    private final long completedAt;

    public ChallengeHistoryEntry(String challengeId, long completedAt) {
        this.challengeId = challengeId;
        this.completedAt = completedAt;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    @Override
    public String toString() {
        return challengeId + ";" + completedAt;
    }
}
