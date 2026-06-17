package com.darksoldier1404.dprdch.api.events;

import com.darksoldier1404.dprdch.api.AssignCause;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChallengeAssignEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final AssignCause cause;
    private ChallengeDefinition definition;
    private boolean cancelled;

    public ChallengeAssignEvent(Player player, ChallengeDefinition definition, AssignCause cause) {
        this.player = player;
        this.definition = definition;
        this.cause = cause;
    }

    public Player getPlayer() {
        return player;
    }

    public ChallengeDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ChallengeDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("definition cannot be null. Use setCancelled(true) to block the assignment.");
        }
        this.definition = definition;
    }

    public AssignCause getCause() {
        return cause;
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
