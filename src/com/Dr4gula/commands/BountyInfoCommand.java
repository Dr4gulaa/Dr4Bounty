package com.Dr4gula.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import com.Dr4gula.manager.BountyManager;
import org.bukkit.ChatColor;

public class BountyInfoCommand implements CommandExecutor {

    private BountyManager bountyManager;

    public BountyInfoCommand(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            double bountyValue = bountyManager.getBountyValueByPlayer(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Sua bounty atual ☠ é: " + (bountyValue > 0 ? bountyValue : "0"));
        } else {
            sender.sendMessage(ChatColor.RED + "Este comando só pode ser executado por um jogador.");
        }
        return true;
    }
}
