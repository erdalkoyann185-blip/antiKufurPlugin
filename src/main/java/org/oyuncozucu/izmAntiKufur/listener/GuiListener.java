package org.oyuncozucu.izmAntiKufur.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.oyuncozucu.izmAntiKufur.IzmAntiKufur;
import org.oyuncozucu.izmAntiKufur.gui.AdminGui;

public final class GuiListener implements Listener {

    private final IzmAntiKufur plugin;

    public GuiListener(IzmAntiKufur plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.adminGui().isGui(event.getView().getTitle())) {
            return;
        }
        event.setCancelled(true);
        if (!event.getWhoClicked().hasPermission("izmantikufur.admin")) {
            event.getWhoClicked().closeInventory();
            return;
        }

        switch (event.getRawSlot()) {
            case AdminGui.RELOAD_SLOT -> {
                plugin.reloadServices();
                event.getWhoClicked().sendMessage(plugin.settings().message("reloaded"));
                if (event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
                    plugin.adminGui().open(player);
                }
            }
            case AdminGui.WORDS_SLOT -> event.getWhoClicked().sendMessage(plugin.settings().message("word-stats")
                .replace("%words%", String.valueOf(plugin.filter().badWords().size()))
                .replace("%whitelist%", String.valueOf(plugin.filter().whitelist().size())));
            case AdminGui.STATS_SLOT -> event.getWhoClicked().sendMessage(plugin.settings().message("stats")
                .replace("%tracked%", String.valueOf(plugin.punishments().totalTrackedPlayers())));
            case AdminGui.CLOSE_SLOT -> event.getWhoClicked().closeInventory();
            default -> {
            }
        }
    }
}
