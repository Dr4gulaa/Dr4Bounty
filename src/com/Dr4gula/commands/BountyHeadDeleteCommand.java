package com.Dr4gula.commands;

import com.Dr4gula.manager.BountyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BountyHeadDeleteCommand implements CommandExecutor {

    private BountyManager bountyManager;

    public BountyHeadDeleteCommand(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("dr4bounty.admin")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para executar esse comando.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Uso correto: /bountyheaddel <nick>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Jogador não encontrado.");
            return true;
        }

        if (bountyManager.removeHeadBounty(target.getUniqueId())) {
            sender.sendMessage(ChatColor.GREEN + "Bounty removida com sucesso.");
        } else {
            sender.sendMessage(ChatColor.RED + "Não foi encontrada uma bounty para esse jogador.");
        }

        return true;
    }
}
