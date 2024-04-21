package com.Dr4gula.events;

import com.Dr4gula.Main;
import com.Dr4gula.manager.Bounty;
import com.Dr4gula.manager.BountyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class PlayerKillListener implements Listener {

    private BountyManager bountyManager;

    public PlayerKillListener(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        Player killer = deceased.getKiller();

        if (killer != null && bountyManager.hasBounty(deceased.getUniqueId()) || bountyManager.hasHeadBounty(deceased.getUniqueId())) {
            Bounty bounty;
            if (bountyManager.hasHeadBounty(deceased.getUniqueId())) {
                bounty = bountyManager.getAnnouncedBounties().get(deceased.getUniqueId());
                Main.getEconomy().depositPlayer(killer, bounty.getReward());
                killer.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Altverse" + ChatColor.WHITE + "] " +
                        ChatColor.GREEN + "Você coletou uma recompensa total de " + bounty.getReward() +
                        " por matar " + deceased.getDisplayName() + ".");
                bountyManager.removeHeadBounty(deceased.getUniqueId());

            } else {
                bounty = bountyManager.getBounties().get(deceased.getUniqueId());
                double reward = bounty.getReward();
                if (reward > 500) {
                    double theftAmount = reward * 0.15;
                    theftAmount = Double.parseDouble(String.format("%.2f", theftAmount));

                    Main.getEconomy().withdrawPlayer(deceased, theftAmount);
                    bountyManager.updateBountyReward(deceased.getUniqueId(), reward - theftAmount);
                    bountyManager.updateOrAddBounty(killer.getUniqueId(), killer.getUniqueId(), theftAmount, killer, true, false);

                    killer.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Altverse" + ChatColor.WHITE + "] " + ChatColor.RED + "Você roubou " + String.format("%.2f", theftAmount) + " da bounty de " + deceased.getDisplayName() + "!");
                    deceased.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Altverse" + ChatColor.WHITE + "] " + ChatColor.RED + "Você perdeu " + String.format("%.2f", theftAmount) + " da sua bounty para " + killer.getDisplayName() + ".");
                } else {
                    killer.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Altverse" + ChatColor.WHITE + "] " + ChatColor.YELLOW + "A bounty de " + deceased.getDisplayName() + " é muito baixa para ser roubada.");
                    deceased.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Altverse" + ChatColor.WHITE + "] " + ChatColor.YELLOW + "Sua bounty é muito baixa para ser roubada, com isso vocês não foi roubado.");
                }
            }

            if (bounty.isAnnounced() && bounty.getReward() >= 200000) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Altverse" + ChatColor.WHITE + "] " +
                        ChatColor.LIGHT_PURPLE + killer.getName() + " coletou uma recompensa de " +
                        bounty.getReward() + " na cabeça " + deceased.getName() + "!");
            }
        }
    }

}