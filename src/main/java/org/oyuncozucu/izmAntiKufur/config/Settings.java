package org.oyuncozucu.izmAntiKufur.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.oyuncozucu.izmAntiKufur.IzmAntiKufur;
import org.oyuncozucu.izmAntiKufur.punishment.PunishmentStep;

public final class Settings {

    private final IzmAntiKufur plugin;
    private final FileConfiguration config;

    public Settings(IzmAntiKufur plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public boolean enabled() {
        return config.getBoolean("filter.enabled", true);
    }

    public boolean logDetections() {
        return config.getBoolean("filter.log-detections", true);
    }

    public boolean blockCapsBypass() {
        return config.getBoolean("filter.block-caps-bypass", true);
    }

    public int maxRepeatedCharacters() {
        return Math.max(1, config.getInt("filter.max-repeated-characters", 2));
    }

    public boolean checkSpacedLetters() {
        return config.getBoolean("filter.check-spaced-letters", true);
    }

    public String replacement() {
        return config.getString("filter.replacement", "***");
    }

    public List<String> badWords() {
        return normalizeList(config.getStringList("filter.bad-words"));
    }

    public List<String> whitelist() {
        return normalizeList(config.getStringList("filter.whitelist"));
    }

    public int warningExpireMinutes() {
        return Math.max(1, config.getInt("punishments.warning-expire-minutes", 30));
    }

    public boolean broadcastPunishments() {
        return config.getBoolean("punishments.broadcast.enabled", true);
    }

    public String broadcastMessage() {
        return color(config.getString("punishments.broadcast.message", "&c%player% uygunsuz dil kullandigi icin cezalandirildi."));
    }

    public List<PunishmentStep> punishmentSteps() {
        List<PunishmentStep> steps = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("punishments.steps");
        if (section == null) {
            return steps;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection step = section.getConfigurationSection(key);
            if (step == null) {
                continue;
            }
            int violations = parsePositiveInt(key, step.getInt("violations", 1));
            steps.add(new PunishmentStep(
                violations,
                step.getStringList("actions"),
                Math.max(0, step.getInt("mute-seconds", 0)),
                step.getStringList("commands")
            ));
        }
        steps.sort((a, b) -> Integer.compare(a.violations(), b.violations()));
        return steps;
    }

    public String message(String path) {
        return color(config.getString("messages." + path, ""));
    }

    public String guiTitle() {
        return color(config.getString("gui.title", "&8Anti Kufur Paneli"));
    }

    public String guiItemName(String key) {
        return color(config.getString("gui.items." + key + ".name", key));
    }

    public List<String> guiItemLore(String key) {
        return colorList(config.getStringList("gui.items." + key + ".lore"));
    }

    public Sound sound(String path, Sound fallback) {
        String raw = config.getString(path, fallback.name());
        try {
            return Sound.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Gecersiz ses: " + raw + ", fallback kullaniliyor: " + fallback.name());
            return fallback;
        }
    }

    public String color(String input) {
        if (input == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public List<String> colorList(List<String> input) {
        List<String> colored = new ArrayList<>();
        for (String line : input) {
            colored.add(color(line));
        }
        return colored;
    }

    private List<String> normalizeList(List<String> input) {
        List<String> result = new ArrayList<>();
        for (String value : input) {
            if (value != null && !value.isBlank()) {
                result.add(value.toLowerCase(Locale.ROOT).trim());
            }
        }
        return result;
    }

    private int parsePositiveInt(String value, int fallback) {
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
            return Math.max(1, fallback);
        }
    }
}
