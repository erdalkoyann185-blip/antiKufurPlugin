package org.oyuncozucu.izmAntiKufur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.oyuncozucu.izmAntiKufur.filter.FilterResult;

public final class AntiKufurCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUB_COMMANDS = List.of("gui", "reload", "test", "list", "stats", "clear", "add", "remove");

    private final IzmAntiKufur plugin;

    public AntiKufurCommand(IzmAntiKufur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("izmantikufur.admin")) {
            sender.sendMessage(plugin.settings().message("no-permission"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(plugin.settings().message("player-only"));
                return true;
            }
            plugin.adminGui().open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadServices();
                sender.sendMessage(plugin.settings().message("reloaded"));
            }
            case "test" -> test(sender, args);
            case "list" -> sender.sendMessage(plugin.settings().message("word-list")
                .replace("%words%", String.join(", ", plugin.filter().badWords()))
                .replace("%whitelist%", String.join(", ", plugin.filter().whitelist())));
            case "stats" -> sender.sendMessage(plugin.settings().message("stats")
                .replace("%tracked%", String.valueOf(plugin.punishments().totalTrackedPlayers())));
            case "clear" -> clear(sender, args);
            case "add" -> updateWordList(sender, args, true);
            case "remove" -> updateWordList(sender, args, false);
            default -> sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("izmantikufur.admin")) {
            return List.of();
        }
        if (args.length == 1) {
            return SUB_COMMANDS.stream().filter(sub -> sub.startsWith(args[0].toLowerCase())).toList();
        }
        return List.of();
    }

    private void test(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.settings().message("usage-test"));
            return;
        }
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        FilterResult result = plugin.filter().check(message);
        sender.sendMessage(plugin.settings().message("test-result")
            .replace("%blocked%", String.valueOf(result.blocked()))
            .replace("%matches%", result.matches().isEmpty() ? "-" : String.join(", ", result.matches()))
            .replace("%sanitized%", result.sanitizedMessage()));
    }

    private void clear(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.settings().message("usage-clear"));
            return;
        }
        Player target = plugin.getServer().getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.settings().message("player-not-found"));
            return;
        }
        plugin.punishments().clear(target);
        sender.sendMessage(plugin.settings().message("cleared").replace("%player%", target.getName()));
    }

    private void updateWordList(CommandSender sender, String[] args, boolean add) {
        if (args.length < 2) {
            sender.sendMessage(plugin.settings().message(add ? "usage-add" : "usage-remove"));
            return;
        }

        String word = args[1].toLowerCase(java.util.Locale.ROOT).trim();
        List<String> words = new ArrayList<>(plugin.getConfig().getStringList("filter.bad-words"));
        boolean exists = words.stream().anyMatch(existing -> existing.equalsIgnoreCase(word));
        boolean changed;
        if (add) {
            changed = !exists && words.add(word);
        } else {
            changed = words.removeIf(existing -> existing.equalsIgnoreCase(word));
        }
        if (!changed) {
            sender.sendMessage(plugin.settings().message("word-not-changed").replace("%word%", word));
            return;
        }

        plugin.getConfig().set("filter.bad-words", words.stream().distinct().toList());
        plugin.saveConfig();
        plugin.reloadServices();
        sender.sendMessage(plugin.settings().message(add ? "word-added" : "word-removed").replace("%word%", word));
    }

    private void sendHelp(CommandSender sender) {
        List<String> help = new ArrayList<>();
        help.add("&8&m----------------------------");
        help.add("&c/antikufur gui &7- Paneli acar");
        help.add("&c/antikufur reload &7- Config yeniler");
        help.add("&c/antikufur test <mesaj> &7- Filtre testi yapar");
        help.add("&c/antikufur list &7- Kelime listelerini gosterir");
        help.add("&c/antikufur add <kelime> &7- Yasak kelime ekler");
        help.add("&c/antikufur remove <kelime> &7- Yasak kelime siler");
        help.add("&c/antikufur stats &7- Ihlal istatistikleri");
        help.add("&c/antikufur clear <oyuncu> &7- Ceza gecmisini temizler");
        help.add("&8&m----------------------------");
        help.forEach(line -> sender.sendMessage(plugin.settings().color(line)));
    }
}
