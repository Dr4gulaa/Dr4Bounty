package com.Dr4gula.commands;

import com.Dr4gula.Main;
import com.Dr4gula.manager.BountyManager;
import com.Dr4gula.ui.BountyMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class BountyCommand implements CommandExecutor {

    private final Main plugin;

    public BountyCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("bounty").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            BountyManager bountyManager = plugin.getBountyManager();
            new BountyMenu(plugin, bountyManager).openBountyMenu(player);
        } else {
            sender.sendMessage(ChatColor.RED + "Somente players podem executar esse comando.");
        }
        return true;
    }

}
