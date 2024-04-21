package com.Dr4gula.events;

import com.Dr4gula.Main;
import com.Dr4gula.manager.BountyManager;
import com.Dr4gula.ui.BountyMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MenuListeners implements Listener {

    private final Main plugin;
    private final BountyManager bountyManager;
    private final Set<UUID> playersSettingBounty = new HashSet<>();

    public MenuListeners(Main plugin, BountyManager bountyManager) {
        this.plugin = plugin;
        this.bountyManager = bountyManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getView().getTopInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String title = inv.getTitle();

        if (inv != null && inv.getTitle().equals(ChatColor.GOLD + "Bounty") || inv.getTitle().contains(ChatColor.DARK_GREEN + "Lista de Bounties - Página ") || inv.getTitle().contains(ChatColor.DARK_RED + "Recompensa por Cabeça - ")) {
            event.setCancelled(true);
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            String displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.equals(ChatColor.GOLD + "Lista Global de Bounties")) {
                new BountyMenu(plugin, bountyManager).openBountiesList(player, plugin.getPlayerPage(player));
            } else if (displayName.equals(ChatColor.GOLD + "Anunciar Bounty")) {
                if (player.hasPermission("dr4bounty.manager")) {
                    player.closeInventory();
                    playersSettingBounty.add(player.getUniqueId());
                    player.sendMessage(ChatColor.AQUA + "Por favor, insira o nome do jogador e a recompensa para a bounty no chat.\n" + ChatColor.RED + "Digite cancelar, para cancelar a ação.");
                } else {
                    player.sendMessage(ChatColor.RED + "Você precisa ser Nukenin ou negocie com um Nukenin para fazer isso.");
                }
            } else if (displayName.equals(ChatColor.GOLD + "Recompensa por cabeça")) {
                new BountyMenu(plugin, bountyManager).openHeadBountyList(player, plugin.getPlayerPage(player));
            } else if (displayName.equals(ChatColor.RED + "Fechar Menu")) {
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (playersSettingBounty.contains(playerUUID)) {
            event.setCancelled(true);
            String message = event.getMessage().trim();

            if (message.equalsIgnoreCase("cancelar")) {
                player.sendMessage(ChatColor.YELLOW + "Ação cancelada.");
                playersSettingBounty.remove(playerUUID);
                return;
            }

            String[] parts = message.split("\\s+");
            if (parts.length != 2) {
                player.sendMessage(ChatColor.RED + "Formato incorreto. Uso: <nomeDoJogador> <recompensa>");
                return;
            }

            try {
                String targetPlayerName = parts[0];
                double rewardValue = Double.parseDouble(parts[1]);
                OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(targetPlayerName);

                if (targetOfflinePlayer == null || !targetOfflinePlayer.hasPlayedBefore()) {
                    player.sendMessage(ChatColor.RED + "Jogador não encontrado.");
                    return;
                }

                UUID targetUUID = targetOfflinePlayer.getUniqueId();

                if (!bountyManager.announceBounty(player, targetUUID, rewardValue)) {
                    Main.getEconomy().depositPlayer(player, rewardValue);
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Valor da recompensa inválido. Por favor, insira um número.");
            } finally {
                playersSettingBounty.remove(playerUUID);
            }
        }
    }


}