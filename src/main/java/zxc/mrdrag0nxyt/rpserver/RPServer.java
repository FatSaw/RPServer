package zxc.mrdrag0nxyt.rpserver;

import java.io.File;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import zxc.mrdrag0nxyt.rpserver.command.PluginCommand;
import zxc.mrdrag0nxyt.rpserver.config.Config;
import zxc.mrdrag0nxyt.rpserver.server.HttpServerManager;
import zxc.mrdrag0nxyt.rpserver.util.Utilities;

public final class RPServer extends JavaPlugin {
    private Config config;
	private CachedResourcePack resourcepack;
    private HttpServerManager server;

    @Override
    public void onEnable() {
        config = new Config(this);
        resourcepack = new CachedResourcePack();
        resourcepack.setFile(new File(getDataFolder(), config.getResourcePackFileName()));
        getCommand("rpserver").setExecutor(new PluginCommand(this, config, resourcepack));

        server = new HttpServerManager(this.getLogger(), resourcepack, config.getPort());
        server.start();

        sendTitle(true);
    }

    @Override
    public void onDisable() {
        server.end();
        sendTitle(false);
    }

    public void reload() {
        config.reload();
        resourcepack.setFile(new File(getDataFolder(), config.getResourcePackFileName()));
        server.restartServer(config.getPort());
    }

    private void sendTitle(boolean isEnable) {
        String isEnableMessage = isEnable ? "&#ace1afPlugin successfully loaded!" : "&#d45079Plugin successfully unloaded!";

        ConsoleCommandSender consoleSender = getServer().getConsoleSender();

        consoleSender.sendMessage(Utilities.colorize(" "));
        consoleSender.sendMessage(Utilities.colorize(" &#745c97█▀█ █▀█ █▀ █▀▀ █▀█ █░█ █▀▀ █▀█    &#696969|    &#fcfcfcVersion: &#745c97" + this.getDescription().getVersion()));
        consoleSender.sendMessage(Utilities.colorize(" &#745c97█▀▄ █▀▀ ▄█ ██▄ █▀▄ ▀▄▀ ██▄ █▀▄    &#696969|    &#fcfcfcAuthor: &#745c97MrDrag0nXYT (https://drakoshaslv.ru)"));
        consoleSender.sendMessage(Utilities.colorize(" "));
        consoleSender.sendMessage(Utilities.colorize(" " + isEnableMessage));
        consoleSender.sendMessage(Utilities.colorize(" "));
    }

}
