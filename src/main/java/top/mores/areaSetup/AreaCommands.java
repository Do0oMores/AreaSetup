package top.mores.areaSetup;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class AreaCommands implements CommandExecutor {
    private final ConfigHandler configHandler = new ConfigHandler();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 控制台命令处理
        if (!(sender instanceof Player)) {
            if (label.equalsIgnoreCase("areatp") && args.length == 2) {
                String worldName = args[0];
                String playerName = args[1];
                Player target = Bukkit.getPlayer(playerName);

                if (target == null) {
                    sender.sendMessage("Player '" + playerName + "' is not online!");
                    return true;
                }

                List<Location> locations = configHandler.getAllTeleportLocationsByWorldName(worldName);
                if (locations == null || locations.isEmpty()) {
                    sender.sendMessage("No teleport locations found for world '" + worldName + "'.");
                    return true;
                }

                Location randomLocation = locations.get(new Random().nextInt(locations.size()));
                target.teleport(randomLocation);
                sender.sendMessage("Player '" + playerName + "' has been teleported to a random location in world '" + worldName + "'.");
            } else {
                sender.sendMessage("Usage: /areatp <worldName> <PlayerName>");
            }
            return true;
        }

        // 玩家命令处理
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("spawn")) {
            // 玩家使用 /spawn 指令回城
            configHandler.useSpawn(player);
            return true;
        }

        if (!player.hasPermission("areatp.admin")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Usage: /areatp <add|del|worldName>");
            return true;
        }

        String subCommand = args[0];

        if (subCommand.equalsIgnoreCase("add")) {
            configHandler.addTeleportLocation(player);
            player.sendMessage("Teleport location added at your current position.");
            return true;

        } else if (subCommand.equalsIgnoreCase("del")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /areatp del <TeleportLocationName>");
                return true;
            }

            String teleportName = args[1];
            configHandler.removeTeleportLocation(player, teleportName);
            return true;

        } else {
            player.sendMessage("Unknown command. Use /areatp <add|del|worldName>.");
            return true;
        }
    }
}
