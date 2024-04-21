package com.Dr4gula.commands;

import com.Dr4gula.manager.BountyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BountyGiveCommand implements CommandExecutor {

    private final BountyManager bountyManager;

    public BountyGiveCommand(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("dr4bounty.give")) {
            sender.sendMessage("§cVocê não tem permissão para usar este comando.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUso correto: /bountygive <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cJogador não encontrado.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cValor inválido para a bounty.");
            return true;
        }


        UUID issuerUUID = (sender instanceof Player) ? ((Player) sender).getUniqueId() : new UUID(0,0);

        if (bountyManager.updateOrAddBounty(target.getUniqueId(), issuerUUID, amount, null, true, false)) {
            sender.sendMessage("§aBounty de $" + amount + " adicionada/atualizada com sucesso para " + target.getName() + ".");
        } else {
            sender.sendMessage("§cNão foi possível atualizar a bounty.");
        }

        return true;
    }
}