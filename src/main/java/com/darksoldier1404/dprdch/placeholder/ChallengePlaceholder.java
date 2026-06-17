package com.darksoldier1404.dprdch.placeholder;

import com.darksoldier1404.dppc.api.placeholder.PlaceholderBuilder;
import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dprdch.RandomChallenge;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.data.PlayerChallengeData;

public class ChallengePlaceholder {

    public static void register(final RandomChallenge plugin) {
        new PlaceholderBuilder.Builder(plugin)
                .identifier("dpc")
                .onRequest((player, context) -> {
                    if (player == null) return "";
                    PlayerChallengeData data = plugin.udata.get(player.getUniqueId());
                    if (data == null) return "";
                    ChallengeDefinition def = plugin.registry.get(data.getCurrentChallengeId());
                    switch (context.toLowerCase()) {
                        case "challenge_name":
                            return def == null ? "" : ColorUtils.applyColor(def.getDisplay());
                        case "challenge_objective":
                            return def == null ? "" : com.darksoldier1404.dprdch.challenge.ChallengeDescriber.objective(def);
                        case "challenge_progress":
                            return def == null ? "0" : String.valueOf(Math.min(data.getProgress(), def.getTarget()));
                        case "challenge_target":
                            return def == null ? "0" : String.valueOf(def.getTarget());
                        case "challenge_percent": {
                            if (def == null) return "0%";
                            int target = def.getTarget();
                            int progress = Math.min(data.getProgress(), target);
                            return (target <= 0 ? 100 : progress * 100 / target) + "%";
                        }
                        case "challenge_timeleft":
                            return def == null ? "" : plugin.assigner.formatTimeLeft(data);
                        case "challenge_completed":
                            return String.valueOf(data.getTotalCompleted());
                        default:
                            return "";
                    }
                })
                .build();
    }
}
