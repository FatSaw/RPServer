package zxc.mrdrag0nxyt.rpserver.config;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import zxc.mrdrag0nxyt.rpserver.RPServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {

    private final RPServer plugin;
    private final File file;
    private YamlConfiguration config;

    @Getter
    private List<String> usageMessage = Arrays.asList(
            " ",
            "&#745c97RPServer &#696969- &#fcfcfcПомощь",
            " &#696969- &#745c97/rpserver reload &#696969- &#fcfcfcперезагрузить плагин",
            " &#696969- &#745c97/rpserver link &#696969- &#fcfcfcполучить ссылку на скачивание РП",
            " "
    );
    @Getter
    private String noPermissionMessage = "&#d45079У вас недостаточно прав для выполнения этой команды!";
    @Getter
    private String reloadedMessage = "&#ace1afПлагин успешно перезагружен!";
    @Getter
    private String linkMessage = "&#745c97RPServer &#696969▸ &#fcfcfcСсылка на скачивание ресурспака: &#745c97http://%domain%:%port%/";

    @Getter
    private String domain = "example.com";
    @Getter
    private int port = 8081;

    @Getter
    private String resourcePackFileName = "Server Resource Pack.zip";

    public Config(RPServer plugin) {
        this.plugin = plugin;

        this.file = new File(plugin.getDataFolder(), "config.yml");
        init();
    }

    public void reload() {
        init();
    }


    private void init() {
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        updateConfig();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }

    private void updateConfig() {
        usageMessage = checkValue("messages.usage", usageMessage);
        noPermissionMessage = checkValue("messages.noPermission", noPermissionMessage);
        reloadedMessage = checkValue("messages.reloaded", reloadedMessage);
        linkMessage = checkValue("messages.link", linkMessage);

        resourcePackFileName = checkValue("file", resourcePackFileName);

        domain = checkValue("server.domain", domain);
        port = checkValue("server.port", port);

        save();
    }

    private <T> T checkValue(String key, T defaultValue) {
        if (!config.contains(key)) {
            config.set(key, defaultValue);
            return defaultValue;

        } else {
            return (T) config.get(key);
        }
    }

}
