package com.darksoldier1404.dprdch.listener;

import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.challenge.ChallengeType;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class SurvivalListener implements Listener {
    private final RandomChallenge plugin = RandomChallenge.plugin;

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getFinalDamage() <= 0) return;
        Player p = (Player) e.getEntity();
        plugin.assigner.resetProgress(p, ChallengeType.NO_DAMAGE_TIME);

        ChallengeDefinition def = plugin.assigner.getActive(p);
        if (def != null && def.getType() == ChallengeType.TAKE_DAMAGE && def.matchWorld(p)) {
            int dmg = (int) Math.round(e.getFinalDamage());
            if (dmg > 0) {
                plugin.assigner.addProgress(p, ChallengeType.TAKE_DAMAGE, dmg);
            }
        }
    }
}
