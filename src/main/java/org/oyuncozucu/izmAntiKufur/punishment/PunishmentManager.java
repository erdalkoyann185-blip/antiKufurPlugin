package org.oyuncozucu.izmAntiKufur.punishment;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.oyuncozucu.izmAntiKufur.IzmAntiKufur;
import org.oyuncozucu.izmAntiKufur.config.Settings;

public final class PunishmentManager {

    private final IzmAntiKufur plugin;
    private final Settings settings;
    private final Map<UUID, ViolationState> violations = new HashMap<>();
    private final Map<UUID, Long> mutedUntil = new HashMap<>();

    public PunishmentManager(IzmAntiKufur plugin, Settings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void handleViolation(Player player, String originalMessage, List<String> matches) {
        ViolationState state = violations.computeIfAbsent(player.getUniqueId(), ignored -> new ViolationState());
        state.addViolation(settings.warningExpireMinutes());

        player.sendMessage(replace(settings.message("blocked"), player, originalMessage, matches, state.count()));
        player.playSound(player.getLocation(), settings.sound("sounds.blocked", Sound.BLOCK_NOTE_BLOCK_BASS), 1.0f, 0.7f);

        PunishmentStep step = matchingStep(state.count());
        if (step == null) {
            return;
        }

        for (String action : step.actions()) {
            executeAction(action, player, originalMessage, matches, state.count(), step);
        }

        for (String command : step.commands()) {
            String parsed = replace(command, player, originalMessage, matches, state.count());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed.startsWith("/") ? parsed.substring(1) : parsed);
        }

        if (settings.broadcastPunishments()) {
            Bukkit.broadcastMessage(replace(settings.broadcastMessage(), player, originalMessage, matches, state.count()));
        }
    }

    public boolean isMuted(Player player) {
        Long until = mutedUntil.get(player.getUniqueId());
        if (until == null) {
            return false;
        }
        if (System.currentTimeMillis() >= until) {
            mutedUntil.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public long mutedSecondsLeft(Player player) {
        Long until = mutedUntil.get(player.getUniqueId());
        if (until == null) {
            return 0L;
        }
        return Math.max(0L, (until - System.currentTimeMillis()) / 1000L);
    }

    public int violationCount(Player player) {
        ViolationState state = violations.get(player.getUniqueId());
        if (state == null) {
            return 0;
        }
        state.expireOld(settings.warningExpireMinutes());
        return state.count();
    }

    public int totalTrackedPlayers() {
        return violations.size();
    }

    public void clear(Player player) {
        violations.remove(player.getUniqueId());
        mutedUntil.remove(player.getUniqueId());
    }

    private PunishmentStep matchingStep(int count) {
        PunishmentStep selected = null;
        for (PunishmentStep step : settings.punishmentSteps()) {
            if (count >= step.violations()) {
                selected = step;
            }
        }
        return selected;
    }

    private void executeAction(String rawAction, Player player, String originalMessage, List<String> matches, int count, PunishmentStep step) {
        String action = rawAction.toLowerCase(Locale.ROOT).trim();
        switch (action) {
            case "message" -> player.sendMessage(replace(settings.message("punishment"), player, originalMessage, matches, count));
            case "title" -> player.sendTitle(
                replace(settings.message("title"), player, originalMessage, matches, count),
                replace(settings.message("subtitle"), player, originalMessage, matches, count),
                10, 45, 15
            );
            case "sound" -> player.playSound(player.getLocation(), settings.sound("sounds.punishment", Sound.ENTITY_VILLAGER_NO), 1.0f, 1.0f);
            case "mute" -> {
                if (step.muteSeconds() > 0) {
                    mutedUntil.put(player.getUniqueId(), System.currentTimeMillis() + (step.muteSeconds() * 1000L));
                    player.sendMessage(replace(settings.message("muted"), player, originalMessage, matches, count)
                        .replace("%seconds%", String.valueOf(step.muteSeconds())));
                }
            }
            case "kick" -> player.kickPlayer(replace(settings.message("kick"), player, originalMessage, matches, count));
            default -> plugin.getLogger().warning("Bilinmeyen ceza aksiyonu: " + rawAction);
        }
    }

    private String replace(String input, Player player, String originalMessage, List<String> matches, int count) {
        return input
            .replace("%player%", player.getName())
            .replace("%message%", originalMessage)
            .replace("%match%", String.join(", ", matches))
            .replace("%violations%", String.valueOf(count));
    }

    private static final class ViolationState {
        private final List<Long> timestamps = new java.util.ArrayList<>();

        void addViolation(int expireMinutes) {
            expireOld(expireMinutes);
            timestamps.add(Instant.now().toEpochMilli());
        }

        int count() {
            return timestamps.size();
        }

        void expireOld(int expireMinutes) {
            long threshold = System.currentTimeMillis() - (expireMinutes * 60_000L);
            timestamps.removeIf(timestamp -> timestamp < threshold);
        }
    }
}
