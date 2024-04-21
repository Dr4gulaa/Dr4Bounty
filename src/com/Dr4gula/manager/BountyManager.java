package com.Dr4gula.manager;

import com.Dr4gula.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BountyManager {

    private Main plugin;
    private HashMap<UUID, Bounty> bounties = new HashMap<>();
    private HashMap<UUID, Bounty> announcedBounties = new HashMap<>();

    public BountyManager(Main plugin) {
        this.plugin = plugin;
    }


    public void addBounty(UUID target, UUID issuer, double reward, boolean announced) {
        Bounty bounty = new Bounty(target, issuer, reward, announced);
        if (announced) {
            announcedBounties.put(target, bounty);
            saveHeadBounties();
        } else {
            bounties.put(target, bounty);
            saveBounties();
        }

    }

    public boolean removeBounty(UUID bountyId) {
        if (bounties.containsKey(bountyId)) {
            Bounty bounty = bounties.remove(bountyId);
            if (bounty != null) {
                OfflinePlayer issuer = Bukkit.getOfflinePlayer(bounty.getIssuer());
                saveBounties();
                return true;
            }
        }
        return false;
    }

    public boolean removeHeadBounty(UUID bountyId) {
        if (announcedBounties.containsKey(bountyId)) {
            announcedBounties.remove(bountyId);
            saveHeadBounties();
            return true;
        }
        return false;
    }

    public boolean announceBounty(Player issuer, UUID targetUUID, double reward) {

        if (announcedBounties.containsKey(targetUUID)) {
            issuer.sendMessage(ChatColor.RED + "Já existe uma bounty anunciada para este jogador. Conclua a atual antes de anunciar outra.");
            return false;
        }


        if (!Main.getEconomy().has(issuer, reward)) {
            issuer.sendMessage(ChatColor.RED + "Você não tem fundos suficientes para anunciar esta bounty.");
            return false;
        }


        Main.getEconomy().withdrawPlayer(issuer, reward);


        addBounty(targetUUID, issuer.getUniqueId(), reward, true);
        issuer.sendMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Altverse" + ChatColor.WHITE + "] " + ChatColor.GREEN + "Bounty de " + reward + " anunciada com sucesso em " + Bukkit.getOfflinePlayer(targetUUID).getName() + ".");


        if (reward >= 200000) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Altverse" + ChatColor.WHITE + "] " + ChatColor.GREEN + issuer.getName() + ChatColor.LIGHT_PURPLE + " anunciou uma recompensa de " + reward + " na cabeça " + Bukkit.getOfflinePlayer(targetUUID).getName() + "!");
        }

        return true;
    }


    public HashMap<UUID, Bounty> getBounties() {
        return bounties;
    }

    public void saveBounties() {
        File bountiesFile = new File(plugin.getDataFolder(), "bounties.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(bountiesFile);

        config.set("bounties", null);

        for (Map.Entry<UUID, Bounty> entry : bounties.entrySet()) {
            UUID key = entry.getKey();
            Bounty bounty = entry.getValue();
            config.set("bounties." + key + ".target", bounty.getTarget().toString());
            config.set("bounties." + key + ".issuer", bounty.getIssuer().toString());
            config.set("bounties." + key + ".reward", bounty.getReward());
        }

        try {
            config.save(bountiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveHeadBounties() {
        File headBountiesFile = new File(plugin.getDataFolder(), "headbounties.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(headBountiesFile);

        config.set("headBounties", null);

        for (Map.Entry<UUID, Bounty> entry : announcedBounties.entrySet()) {
            UUID key = entry.getKey();
            Bounty bounty = entry.getValue();
            String pathBase = "headBounties." + key; //
            config.set(pathBase + ".target", bounty.getTarget().toString());
            config.set(pathBase + ".issuer", bounty.getIssuer().toString());
            config.set(pathBase + ".reward", bounty.getReward());
        }

        try {
            config.save(headBountiesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBounties() {
        File bountiesFile = new File(plugin.getDataFolder(), "bounties.yml");
        if (!bountiesFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(bountiesFile);
        ConfigurationSection bountiesSection = config.getConfigurationSection("bounties");
        if (bountiesSection == null) {
            return;
        }

        for (String key : bountiesSection.getKeys(false)) {
            UUID targetUUID = UUID.fromString(bountiesSection.getString(key + ".target"));
            UUID issuerUUID = UUID.fromString(bountiesSection.getString(key + ".issuer"));
            double reward = bountiesSection.getDouble(key + ".reward");

            bounties.put(UUID.fromString(key), new Bounty(targetUUID, issuerUUID, reward, false));
        }
    }


    public void loadHeadBounties() {
        File headBountiesFile = new File(plugin.getDataFolder(), "headbounties.yml");
        if (!headBountiesFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(headBountiesFile);
        ConfigurationSection headBountiesSection = config.getConfigurationSection("headBounties");
        if (headBountiesSection == null) {
            return;
        }

        for (String key : headBountiesSection.getKeys(false)) {
            UUID targetUUID = UUID.fromString(headBountiesSection.getString(key + ".target"));
            UUID issuerUUID = UUID.fromString(headBountiesSection.getString(key + ".issuer"));
            double reward = headBountiesSection.getDouble(key + ".reward");

            announcedBounties.put(UUID.fromString(key), new Bounty(targetUUID, issuerUUID, reward, true));
        }
    }

    public Set<Bounty> getBountiesByPage(int page, int itemsPerPage) {
        List<Bounty> sortedBounties = new ArrayList<>(bounties.values())
                .stream()
                .sorted(Comparator.comparingDouble(Bounty::getReward).reversed())
                .collect(Collectors.toList());

        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, sortedBounties.size());


        Set<Bounty> pageBounties = new LinkedHashSet<>(sortedBounties.subList(start, end));

        return pageBounties;
    }


    //5 parâmetros
    public boolean updateOrAddBounty(UUID targetUUID, UUID issuerUUID, double reward, Player issuer, boolean additive, boolean isAnuncio) {
        Bounty existingBounty = this.bounties.get(targetUUID);
        double newReward = reward;

        if (existingBounty != null) {
            newReward = additive ? existingBounty.getReward() + reward : reward;
            if (newReward <= existingBounty.getReward()) {
                if (issuer != null) {
                    issuer.sendMessage(ChatColor.RED + "A bounty já tem um valor igual ou superior.");
                }
                return false;
            }
            existingBounty.setReward(newReward);
        } else {
            existingBounty = new Bounty(targetUUID, issuerUUID, newReward, isAnuncio);
            if (isAnuncio) {
                if (issuer == null || Main.getEconomy().has(issuer, reward)) {
                    if (issuer != null) {
                        Main.getEconomy().withdrawPlayer(issuer, reward);
                    }
                } else {
                    if (issuer != null) {
                        issuer.sendMessage(ChatColor.RED + "Fundos insuficientes para definir a bounty.");
                    }
                    return false;
                }
            }
            if (isAnuncio) {
                this.announcedBounties.put(targetUUID, existingBounty);
            } else {
                this.bounties.put(targetUUID, existingBounty);
            }
        }

        if (isAnuncio) {
            this.saveHeadBounties();
        } else {
            this.saveBounties();
        }

        return true;
    }



    public int getTotalPages(int itemsPerPage) {
        int totalItems = getBounties().size();
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    public boolean hasBounty(UUID uuid) {
        return bounties.containsKey(uuid);
    }

    public boolean hasHeadBounty(UUID uuid){return announcedBounties.containsKey(uuid);}

    public Bounty getBounty(UUID uuid) {
        return bounties.get(uuid);
    }

    public double getBountyValueByPlayer(UUID playerUUID) {
        Bounty bounty = bounties.get(playerUUID);
        return bounty != null ? bounty.getReward() : 0;
    }

    public HashMap<UUID, Bounty> getAnnouncedBounties() {
        return announcedBounties;
    }

    public boolean updateBountyReward(UUID uuid, double newReward) {
        if (hasBounty(uuid)) {
            Bounty bounty = getBounty(uuid);
            bounty.setReward(newReward);
            return true;
        }
        return false;
    }

}