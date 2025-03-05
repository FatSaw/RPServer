package zxc.mrdrag0nxyt.rpserver.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import zxc.mrdrag0nxyt.rpserver.CachedResourcePack;
import zxc.mrdrag0nxyt.rpserver.RPServer;
import zxc.mrdrag0nxyt.rpserver.config.Config;
import zxc.mrdrag0nxyt.rpserver.util.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginCommand implements CommandExecutor, TabCompleter {

    private final RPServer plugin;
    private final Config config;
    private final CachedResourcePack resourcepack;

    public PluginCommand(RPServer plugin, Config config, CachedResourcePack resourcepack) {
        this.plugin = plugin;
        this.config = config;
        this.resourcepack = resourcepack;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) return usage(commandSender);

        switch (strings[0].toLowerCase()) {
            case "reload": {
                return reloadSubcommand(commandSender);
            }
            case "link": {
                return linkSubcommand(commandSender);
            }
            case "hash": {
                return hashSubcommand(commandSender);
            }
            default: {
                return usage(commandSender);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            List<String> list = new ArrayList<>(3);
            String arg0 = strings[0].toLowerCase();
            if ("reload".startsWith(arg0)) {
                list.add("reload");
            }
            if ("link".startsWith(arg0)) {
                list.add("link");
            }
            if ("hash".startsWith(arg0)) {
                list.add("hash");
            }
            return list;
        }
        return Collections.emptyList();
    }


    private boolean noPermission(CommandSender commandSender) {
        commandSender.sendMessage(config.getNoPermissionMessage());
        return false;
    }


    private boolean usage(CommandSender commandSender) {
        for (String str : config.getUsageMessage()) {
            commandSender.sendMessage(str);
        }
        return false;
    }

    private boolean reloadSubcommand(CommandSender commandSender) {
        if (!commandSender.hasPermission("rpserver.reload")) return noPermission(commandSender);

        plugin.reload();
        commandSender.sendMessage(config.getReloadedMessage());
        return true;
    }

    private boolean linkSubcommand(CommandSender commandSender) {
        if (!commandSender.hasPermission("rpserver.link")) return noPermission(commandSender);

        commandSender.sendMessage(config.getLinkMessage());
        return true;
    }

    private boolean hashSubcommand(CommandSender commandSender) {
        if (!commandSender.hasPermission("rpserver.hash")) return noPermission(commandSender);
        String hash = resourcepack.getHash();
        commandSender.sendMessage(Utilities.colorize(config.getHashMessage().replace("%hash%", hash)));
        return true;
    }

}
