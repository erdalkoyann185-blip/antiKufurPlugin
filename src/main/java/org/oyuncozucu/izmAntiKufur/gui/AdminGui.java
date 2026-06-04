package org.oyuncozucu.izmAntiKufur.gui;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.oyuncozucu.izmAntiKufur.IzmAntiKufur;

public final class AdminGui {

    public static final int SIZE = 27;
    public static final int STATUS_SLOT = 10;
    public static final int WORDS_SLOT = 12;
    public static final int STATS_SLOT = 14;
    public static final int RELOAD_SLOT = 16;
    public static final int CLOSE_SLOT = 22;

    private final IzmAntiKufur plugin;

    public AdminGui(IzmAntiKufur plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, SIZE, plugin.settings().guiTitle());
        inventory.setItem(STATUS_SLOT, item(plugin.settings().enabled() ? Material.LIME_DYE : Material.GRAY_DYE, "status"));
        inventory.setItem(WORDS_SLOT, item(Material.BOOK, "words"));
        inventory.setItem(STATS_SLOT, item(Material.PAPER, "stats"));
        inventory.setItem(RELOAD_SLOT, item(Material.REPEATER, "reload"));
        inventory.setItem(CLOSE_SLOT, item(Material.BARRIER, "close"));

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        player.openInventory(inventory);
    }

    public boolean isGui(String title) {
        return plugin.settings().guiTitle().equals(title);
    }

    private ItemStack item(Material material, String key) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(plugin.settings().guiItemName(key));
        List<String> lore = plugin.settings().guiItemLore(key).stream()
            .map(this::replacePlaceholders)
            .toList();
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private String replacePlaceholders(String line) {
        return line
            .replace("%enabled%", plugin.settings().enabled() ? "Aktif" : "Kapali")
            .replace("%words%", String.valueOf(plugin.filter().badWords().size()))
            .replace("%whitelist%", String.valueOf(plugin.filter().whitelist().size()))
            .replace("%tracked%", String.valueOf(plugin.punishments().totalTrackedPlayers()));
    }
}
