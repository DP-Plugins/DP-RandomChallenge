package com.darksoldier1404.dprdch.listener;

import com.darksoldier1404.dprdch.RandomChallenge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    private final RandomChallenge plugin = RandomChallenge.plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.assigner.handleJoin(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (plugin.udata.containsKey(e.getPlayer().getUniqueId())) {
            plugin.udata.save(e.getPlayer().getUniqueId());
        }
        plugin.assigner.clearCache(e.getPlayer().getUniqueId());
    }
}
