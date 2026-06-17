package com.darksoldier1404.dprdch;

import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dprdch.challenge.ChallengeAssigner;
import com.darksoldier1404.dprdch.challenge.ChallengeRegistry;
import com.darksoldier1404.dprdch.command.ChallengeCommand;
import com.darksoldier1404.dprdch.data.ChallengeDefinition;
import com.darksoldier1404.dprdch.data.PlayerChallengeData;
import com.darksoldier1404.dprdch.listener.BuildListener;
import com.darksoldier1404.dprdch.listener.CollectListener;
import com.darksoldier1404.dprdch.listener.CombatListener;
import com.darksoldier1404.dprdch.listener.ConnectionListener;
import com.darksoldier1404.dprdch.listener.ExploreListener;
import com.darksoldier1404.dprdch.listener.GUIListener;
import com.darksoldier1404.dprdch.listener.LifeListener;
import com.darksoldier1404.dprdch.listener.SurvivalListener;
import com.darksoldier1404.dprdch.placeholder.ChallengePlaceholder;
import com.darksoldier1404.dprdch.task.SurvivalTicker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.UUID;

public class RandomChallenge extends DPlugin {
    public static RandomChallenge plugin;
    public DataContainer<UUID, PlayerChallengeData> udata;
    public DataContainer<String, ChallengeDefinition> cdata;
    public ChallengeRegistry registry;
    public ChallengeAssigner assigner;
    private SurvivalTicker ticker;

    public RandomChallenge() {
        super(true);
        plugin = this;
        init();
    }

    @Override
    public void onLoad() {
        udata = loadDataContainer(new DataContainer<>(this, DataType.CUSTOM, "users"), PlayerChallengeData.class);
        cdata = loadDataContainer(new DataContainer<>(this, DataType.CUSTOM, "challenges"), ChallengeDefinition.class);
        registry = new ChallengeRegistry(this);
        registry.load();
        assigner = new ChallengeAssigner(this);
        PluginUtil.addPlugin(plugin, 32061);
        ChallengePlaceholder.register(this);
    }

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ConnectionListener(), this);
        pm.registerEvents(new CombatListener(), this);
        pm.registerEvents(new CollectListener(), this);
        pm.registerEvents(new ExploreListener(), this);
        pm.registerEvents(new BuildListener(), this);
        pm.registerEvents(new SurvivalListener(), this);
        pm.registerEvents(new LifeListener(), this);
        pm.registerEvents(new GUIListener(), this);
        ChallengeCommand.register(this);
        ticker = new SurvivalTicker(this);
        ticker.runTaskTimer(this, 20L, 20L);
        for (Player p : Bukkit.getOnlinePlayers()) {
            assigner.handleJoin(p);
        }
    }

    @Override
    public void onDisable() {
        if (ticker != null) {
            ticker.cancel();
        }
        saveAllData();
    }

    public void send(CommandSender sender, String key, String... args) {
        sender.sendMessage(getPrefix() + lang(key, args));
    }

    public String lang(String key, String... args) {
        return args.length == 0 ? getLang().get(key) : getLang().getWithArgs(key, args);
    }
}
