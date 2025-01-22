package top.mores.areaSetup;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConfigHandler {
    FileConfiguration config = AreaSetup.getPlugin().getConfig();

    //根据世界获取所有传送点
    public List<Location> getAllTeleportLocationsByWorldName(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        List<Location> locations = new ArrayList<>();
        for (String locKey : Objects.requireNonNull(config.getConfigurationSection("TeleportPoint." + worldName)).getKeys(false)) {
            double x = config.getDouble("TeleportPoint." + worldName + "." + locKey + ".x");
            double y = config.getDouble("TeleportPoint." + worldName + "." + locKey + ".y");
            double z = config.getDouble("TeleportPoint." + worldName + "." + locKey + ".z");
            locations.add(new Location(world, x, y, z));
        }
        return locations;
    }

    //写入传送点坐标
    public void addTeleportLocation(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        assert world != null;
        UUID uuid = UUID.randomUUID();
        config.set("TeleportPoint." + world.getName() + "." + uuid + ".x", loc.getX());
        config.set("TeleportPoint." + world.getName() + "." + uuid + ".y", loc.getY());
        config.set("TeleportPoint." + world.getName() + "." + uuid + ".z", loc.getZ());
        try{
            config.save(new File(AreaSetup.getPlugin().getDataFolder(),"config.yml"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //删除传送点
    public void removeTeleportLocation(Player player, String teleportName) {
        World world = player.getLocation().getWorld();
        if (world == null) {
            throw new IllegalStateException("Player is not in a valid world.");
        }
        String path = "TeleportPoint." + world.getName() + "." + teleportName;
        if (config.contains(path)) {
            config.set(path, null);
            player.sendMessage("Teleport location '" + teleportName + "' removed successfully!");
            try {
                config.save(new File(AreaSetup.getPlugin().getDataFolder(), "config.yml"));
            } catch (IOException e) {
                player.sendMessage("Failed to save configuration file!");
                e.printStackTrace();
            }
        } else {
            player.sendMessage("Teleport location '" + teleportName + "' does not exist.");
        }
    }

    //获取主城坐标
    public Location getSpawnLocation() {
        String WorldName = config.getString("SpawnLocation.world");
        double x = config.getDouble("SpawnLocation.x");
        double y = config.getDouble("SpawnLocation.y");
        double z = config.getDouble("SpawnLocation.z");
        if (WorldName == null) {
            return null;
        }
        return new Location(Bukkit.getWorld(WorldName), x, y, z);
    }
}
