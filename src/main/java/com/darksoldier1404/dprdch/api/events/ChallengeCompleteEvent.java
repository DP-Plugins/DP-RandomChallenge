package com.darksoldier1404.dprdch.api.events;

import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChallengeCompleteEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ChallengeDefinition definition;
    private final int totalCompleted;

    public ChallengeCompleteEvent(Player player, ChallengeDefinition definition, int totalCompleted) {
        this.player = player;
        this.definition = definition;
        this.totalCompleted = totalCompleted;
    }

    public Player getPlayer() {
        return player;
    }

    public ChallengeDefinition getDefinition() {
        return definition;
    }

    public int getTotalCompleted() {
        return totalCompleted;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
