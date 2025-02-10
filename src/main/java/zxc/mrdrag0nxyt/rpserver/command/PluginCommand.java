package zxc.mrdrag0nxyt.rpserver.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import zxc.mrdrag0nxyt.rpserver.RPServer;
import zxc.mrdrag0nxyt.rpserver.config.Config;
import zxc.mrdrag0nxyt.rpserver.util.Utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PluginCommand implements CommandExecutor, TabCompleter {

    private final RPServer plugin;
    private final Config config;

    public PluginCommand(RPServer plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
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
            default: {
                return usage(commandSender);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) return Arrays.asList("reload", "link");
        return Collections.emptyList();
    }


    private boolean noPermission(CommandSender commandSender) {
        commandSender.sendMessage(
                Utilities.colorize(config.getNoPermissionMessage())
        );
        return false;
    }


    private boolean usage(CommandSender commandSender) {
        for (String str : config.getUsageMessage()) {
            commandSender.sendMessage(
                    Utilities.colorize(str)
            );
        }
        return false;
    }

    private boolean reloadSubcommand(CommandSender commandSender) {
        if (!commandSender.hasPermission("rpserver.reload")) return noPermission(commandSender);

        plugin.reload();
        commandSender.sendMessage(
                Utilities.colorize(config.getReloadedMessage())
        );
        return true;
    }

    private boolean linkSubcommand(CommandSender commandSender) {
        if (!commandSender.hasPermission("rpserver.link")) return noPermission(commandSender);

        commandSender.sendMessage(
                Utilities.colorize(
                        config.getLinkMessage()
                                .replace("%domain%", config.getDomain())
                                .replace("%port%", String.valueOf(config.getPort()))
                )
        );
        return true;
    }

}
