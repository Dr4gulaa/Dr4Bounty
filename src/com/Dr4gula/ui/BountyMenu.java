package com.Dr4gula.ui;

import com.Dr4gula.Main;
import com.Dr4gula.manager.Bounty;
import com.Dr4gula.manager.BountyManager;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class BountyMenu {

    private final Main plugin;
    private BountyManager bountyManager;

    public BountyMenu(Main plugin, BountyManager bountyManager) {
        this.plugin = plugin;
        this.bountyManager = bountyManager;
    }

    public void openBountyMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Bounty");

        ItemStack listBounties = new ItemStack(Material.PAPER);
        ItemStack setBounty = new ItemStack(Material.SKULL_ITEM);
        ItemStack closeInventory = new ItemStack(Material.ARROW);
        ItemStack headRewards = new ItemStack(Material.NETHER_STAR);

        ItemMeta listMeta = listBounties.getItemMeta();
        listMeta.setDisplayName(ChatColor.GOLD + "Lista Global de Bounties");
        List<String> listLore = new ArrayList<>();
        listLore.add(ChatColor.YELLOW + " Nesta Lista você encontra todas as bounties fictícias bases dos players  ");
        listLore.add(ChatColor.YELLOW + " em ordem decrescente, Essa bounty pode ser adquirida completando Quests e");
        listLore.add(ChatColor.YELLOW + " matando certos Boss ou roubando de outros players matando uns aos outros. ");
        listLore.add( "  ");
        listLore.add(ChatColor.WHITE + "➜ Clique para abrir.");
        listMeta.setLore(listLore);
        listBounties.setItemMeta(listMeta);

        ItemMeta setMeta = setBounty.getItemMeta();
        setMeta.setDisplayName(ChatColor.GOLD + "Anunciar Bounty");
        List<String> setLore = new ArrayList<>();
        setLore.add(ChatColor.YELLOW + " Anunciar bounty é uma função para adicionar uma recompensa fixa ");
        setLore.add(ChatColor.YELLOW + " na cabeça de um player. ");
        setLore.add(ChatColor.YELLOW + " As cabeças anunciadas são mostradas na Lista de Recompensa por cabeça ");
        setLore.add("  ");
        setLore.add(ChatColor.WHITE + "➜ Clique para abrir.");
        setMeta.setLore(setLore);
        setBounty.setItemMeta(setMeta);


        ItemMeta headMeta = headRewards.getItemMeta();
        headMeta.setDisplayName(ChatColor.GOLD + "Recompensa por cabeça");
        List<String> headLore = new ArrayList<>();
        headLore.add(ChatColor.RED + " Veja as cabeças anunciadas ");
        headLore.add(ChatColor.RED + " Tome cuidado para a sua cabeça nao ser a próxima! ");
        headLore.add("  ");
        headLore.add(ChatColor.WHITE + "➜ Clique para abrir.");
        headMeta.setLore(headLore);
        headRewards.setItemMeta(headMeta);

        ItemMeta closeMeta = closeInventory.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Fechar Menu");
        closeInventory.setItemMeta(closeMeta);


        inventory.setItem(11, listBounties);
        inventory.setItem(13, headRewards);
        inventory.setItem(15, setBounty);
        inventory.setItem(26, closeInventory);


        player.openInventory(inventory);
    }


    public void openBountiesList(Player player, int page) {
        int itemsPerPage = 36;
        int totalPages = bountyManager.getTotalPages(itemsPerPage);
        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        Inventory bountiesList = Bukkit.createInventory(player, 54, ChatColor.DARK_GREEN + "Lista de Bounties - Página " + page);

        fillBordersWithGlassPanes(bountiesList);


        Set<Bounty> bounties = bountyManager.getBountiesByPage(page, itemsPerPage);

        for (Bounty bounty : bounties) {
            ItemStack item = createBountyItem(bounty);
            bountiesList.addItem(item);
        }

        addNavigationButtons(bountiesList, page, totalPages);



        player.openInventory(bountiesList);
    }

    private void addNavigationButtons(Inventory inventory, int page, int totalPages) {


        if (page > 1) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = previousPage.getItemMeta();
            prevMeta.setDisplayName(ChatColor.GREEN + "Página anterior");
            previousPage.setItemMeta(prevMeta);
            inventory.setItem(45, previousPage);
        }


        if (page < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(ChatColor.GREEN + "Próxima página");
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(53, nextPage);
        }

    }

    public void openHeadBountyList(Player player, int page) {
        int itemsPerPage = 9;
        int totalPages = (int) Math.ceil(bountyManager.getAnnouncedBounties().size() / (double) itemsPerPage);

        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        String title = ChatColor.DARK_RED + "Recompensa por Cabeça - " + page;
        if (title.length() > 32) {
            title = ChatColor.DARK_RED + "Recompensas - " + page;
        }
        Inventory headBountyList = Bukkit.createInventory(player, 54, title);

        fillBordersWithRedGlassPanes(headBountyList);

        List<Bounty> bounties = new ArrayList<>(bountyManager.getAnnouncedBounties().values());

        for (int i = (page - 1) * itemsPerPage; i < page * itemsPerPage && i < bounties.size(); i++) {
            Bounty bounty = bounties.get(i);
            ItemStack item = createHeadBountyItem(bounty);
            headBountyList.addItem(item);
        }

        addNavigationButtons(headBountyList, page, totalPages);

        player.openInventory(headBountyList);
    }

    private void fillBordersWithGlassPanes(Inventory inventory) {
        ItemStack greenGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
        ItemMeta meta = greenGlassPane.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "");
        greenGlassPane.setItemMeta(meta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, greenGlassPane);
            inventory.setItem(i + 45, greenGlassPane);
        }

        for (int i = 9; i <= 36; i += 9) {
            inventory.setItem(i, greenGlassPane);
            inventory.setItem(i + 8, greenGlassPane);
        }

        inventory.setItem(36, greenGlassPane);
        inventory.setItem(44, greenGlassPane);
    }

    private void fillBordersWithRedGlassPanes(Inventory inventory) {
        ItemStack redGlassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        ItemMeta meta = redGlassPane.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "");
        redGlassPane.setItemMeta(meta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, redGlassPane);
            inventory.setItem(i + 45, redGlassPane);
        }

        for (int i = 9; i <= 36; i += 9) {
            inventory.setItem(i, redGlassPane);
            inventory.setItem(i + 8, redGlassPane);
        }

        inventory.setItem(36, redGlassPane);
        inventory.setItem(44, redGlassPane);
    }



    private ItemStack createBountyItem(Bounty bounty) {
        String targetName = Bukkit.getOfflinePlayer(bounty.getTarget()).getName();
        String issuerName = Bukkit.getOfflinePlayer(bounty.getIssuer()).getName();
        ItemStack item = getPlayerHead(targetName);
        ItemMeta meta = item.getItemMeta();


        double bountyValue = parseBountyValue(String.format(Locale.FRANCE, "%.2f", bounty.getReward()));

        meta.setDisplayName(ChatColor.GOLD + "Bounty em: " + targetName);
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "Recompensa: " + bountyValue
        ));
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createHeadBountyItem(Bounty bounty) {
        String targetName = Bukkit.getOfflinePlayer(bounty.getTarget()).getName();
        String issuerName = Bukkit.getOfflinePlayer(bounty.getIssuer()).getName();
        ItemStack item = getPlayerHead(targetName);
        ItemMeta meta = item.getItemMeta();


        double bountyValue = parseBountyValue(String.format(Locale.FRANCE, "%.2f", bounty.getReward()));

        meta.setDisplayName(ChatColor.GOLD + "Bounty em: " + targetName);
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "Recompensa: " + bountyValue,
                ChatColor.AQUA + "Anunciado por: " + issuerName
        ));
        item.setItemMeta(meta);

        return item;
    }


    public ItemStack getPlayerHead(String playerName) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        net.minecraft.server.v1_7_R4.ItemStack stack = CraftItemStack.asNMSCopy(head);
        NBTTagCompound tag = stack.hasTag() ? stack.getTag() : new NBTTagCompound();
        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.setString("Name", playerName);
        tag.set("SkullOwner", skullOwner);
        stack.setTag(tag);
        return CraftItemStack.asCraftMirror(stack);
    }

    private double parseBountyValue(String value) {
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        try {
            Number number = format.parse(value);
            return number.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
