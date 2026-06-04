package org.oyuncozucu.izmAntiKufur;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.oyuncozucu.izmAntiKufur.config.Settings;
import org.oyuncozucu.izmAntiKufur.filter.ProfanityFilter;
import org.oyuncozucu.izmAntiKufur.gui.AdminGui;
import org.oyuncozucu.izmAntiKufur.listener.ChatListener;
import org.oyuncozucu.izmAntiKufur.listener.GuiListener;
import org.oyuncozucu.izmAntiKufur.punishment.PunishmentManager;

public final class IzmAntiKufur extends JavaPlugin {

    private Settings settings;
    private ProfanityFilter profanityFilter;
    private PunishmentManager punishmentManager;
    private AdminGui adminGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadServices();

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        AntiKufurCommand command = new AntiKufurCommand(this);
        PluginCommand pluginCommand = getCommand("antikufur");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        getLogger().info("IzmAntiKufur aktif. Filtered words: " + profanityFilter.badWords().size());
    }

    @Override
    public void onDisable() {
        getLogger().info("IzmAntiKufur kapatildi.");
    }

    public void reloadServices() {
        reloadConfig();
        this.settings = new Settings(this);
        this.profanityFilter = new ProfanityFilter(settings);
        this.punishmentManager = new PunishmentManager(this, settings);
        this.adminGui = new AdminGui(this);
    }

    public Settings settings() {
        return settings;
    }

    public ProfanityFilter filter() {
        return profanityFilter;
    }

    public PunishmentManager punishments() {
        return punishmentManager;
    }

    public AdminGui adminGui() {
        return adminGui;
    }
}
