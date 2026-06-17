package com.darksoldier1404.dprdch.api.events;

import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChallengeProgressEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ChallengeDefinition definition;
    private final int currentProgress;
    private int amount;
    private boolean cancelled;

    public ChallengeProgressEvent(Player player, ChallengeDefinition definition, int currentProgress, int amount) {
        this.player = player;
        this.definition = definition;
        this.currentProgress = currentProgress;
        this.amount = amount;
    }

    public Player getPlayer() {
        return player;
    }

    public ChallengeDefinition getDefinition() {
        return definition;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
