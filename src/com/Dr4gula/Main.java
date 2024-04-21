package com.Dr4gula;

import com.Dr4gula.commands.*;
import com.Dr4gula.events.MenuListeners;
import com.Dr4gula.events.PlayerKillListener;
import com.Dr4gula.manager.BountyManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {

        private BountyManager bountyManager;
        private static Economy econ = null;
        private final HashMap<UUID, Integer> playerPages = new HashMap<>();


        @Override
        public void onEnable(){
            //Instancias
            bountyManager = new BountyManager(this);
            bountyManager.loadBounties();
            bountyManager.loadHeadBounties();

            //Registro de eventos
            getServer().getPluginManager().registerEvents(new MenuListeners(this, bountyManager), this);
            getServer().getPluginManager().registerEvents(new PlayerKillListener(bountyManager), this);

            //Comandos
            getCommand("bounty").setExecutor(new BountyCommand(this));
            this.getCommand("bountyinfo").setExecutor(new BountyInfoCommand(bountyManager));
            this.getCommand("bountygive").setExecutor(new BountyGiveCommand(bountyManager));
            this.getCommand("bountydel").setExecutor(new BountyDeleteCommand(bountyManager));
            this.getCommand("bountyheaddel").setExecutor(new BountyHeadDeleteCommand(bountyManager));
            this.getCommand("dr4bounty").setExecutor(new Dr4BountyCommand(this));


            if (!setupEconomy() ) {
                getLogger().severe(String.format("[Dr4Bounty] - Desativado porque nenhuma dependência do Vault foi encontrada!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            Bukkit.getConsoleSender().sendMessage("[Dr4Bounty] Plugin ligado.");
        }

        @Override
        public void onDisable(){
            bountyManager.saveBounties();
            bountyManager.saveHeadBounties();
            Bukkit.getConsoleSender().sendMessage("[Dr4Bounty] Plugin desligado.");
        }

        public BountyManager getBountyManager() {
            return bountyManager;
        }

    public int getPlayerPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId(), 1);
    }

    public void setPlayerPage(Player player, int page) {
        playerPages.put(player.getUniqueId(), page);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public void reloadBounties() {
        bountyManager.loadBounties();
    }

    public void reloadEconomy() {
        setupEconomy();
    }



}


