package org.oyuncozucu.izmAntiKufur.listener;

import java.util.ArrayList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.oyuncozucu.izmAntiKufur.IzmAntiKufur;
import org.oyuncozucu.izmAntiKufur.filter.FilterResult;

public final class ChatListener implements Listener {

    private final IzmAntiKufur plugin;

    public ChatListener(IzmAntiKufur plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().hasPermission("izmantikufur.bypass")) {
            if (plugin.settings().logDetections()) {
                plugin.getLogger().fine(event.getPlayer().getName() + " bypass yetkisi nedeniyle filtrelenmedi.");
            }
            return;
        }

        String plainMessage = event.getMessage();
        if (plugin.punishments().isMuted(event.getPlayer())) {
            event.setCancelled(true);
            long left = plugin.punishments().mutedSecondsLeft(event.getPlayer());
            plugin.getServer().getScheduler().runTask(plugin, () -> event.getPlayer().sendMessage(
                plugin.settings().message("still-muted").replace("%seconds%", String.valueOf(left))
            ));
            return;
        }

        FilterResult result = plugin.filter().check(plainMessage);
        if (!result.blocked()) {
            return;
        }

        event.setCancelled(true);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.punishments().handleViolation(event.getPlayer(), plainMessage, new ArrayList<>(result.matches()));
            if (plugin.settings().logDetections()) {
                plugin.getLogger().info(event.getPlayer().getName() + " kufur engellendi: " + plainMessage + " | matches=" + result.matches());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.punishments().isMuted(event.getPlayer())) {
            return;
        }
        String command = event.getMessage().toLowerCase();
        if (command.startsWith("/msg ") || command.startsWith("/tell ") || command.startsWith("/w ") || command.startsWith("/r ")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.settings().message("still-muted")
                .replace("%seconds%", String.valueOf(plugin.punishments().mutedSecondsLeft(event.getPlayer()))));
        }
    }
}
