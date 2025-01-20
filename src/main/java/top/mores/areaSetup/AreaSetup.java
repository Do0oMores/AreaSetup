package top.mores.areaSetup;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class AreaSetup extends JavaPlugin {
    public static FileConfiguration config;
    private static AreaSetup plugin;

    @Override
    public void onEnable() {
        plugin = this;
        loadConfig();
        Objects.requireNonNull(this.getCommand("areatp")).setExecutor(new AreaCommands());
        getLogger().info("Area Setup Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Area Setup Disabled");
    }

    //加载配置文件
    private void loadConfig() {
        File cconfigFile = new File(getDataFolder(), "config.yml");
        if (!cconfigFile.exists()) {
            saveResource("config.yml", false);
        }
        config = getConfig();
    }

    //获取插件实例
    public static AreaSetup getPlugin() {
        return plugin;
    }
}
