package com.Dr4gula.commands;

import com.Dr4gula.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Dr4BountyCommand implements CommandExecutor {

    private Main plugin;

    public Dr4BountyCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dr4bounty.reload")) {
                sender.sendMessage(ChatColor.RED + "Você não tem permissão para fazer isso.");
                return true;
            }

            plugin.reloadConfig();
            plugin.reloadBounties();
            plugin.reloadEconomy();

            sender.sendMessage(ChatColor.GREEN + "Configurações do plugin recarregadas!");
            return true;
        }

        return false;
    }
}
