package com.Dr4gula.commands;

import com.Dr4gula.manager.BountyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BountyDeleteCommand implements CommandExecutor {

    private BountyManager bountyManager;

    public BountyDeleteCommand(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("dr4bounty.admin")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Uso correto: /" + label + " <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!bountyManager.hasBounty(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Este jogador não tem uma bounty.");
            return true;
        }

        if (bountyManager.removeBounty(target.getUniqueId())) {
            sender.sendMessage(ChatColor.GREEN + "A bounty de " + target.getName() + " foi removida com sucesso.");
        } else {
            sender.sendMessage(ChatColor.RED + "Falha ao remover a bounty.");
        }

        return true;
    }
}
