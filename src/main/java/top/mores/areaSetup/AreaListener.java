package top.mores.areaSetup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class AreaListener implements Listener {
    private final ConfigHandler configHandler=new ConfigHandler();
    private final Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
    private final String TEAM_NAME = "HiddenID";

    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        String worldName = p.getWorld().getName();
        if (configHandler.getAreaWorlds().contains(worldName)) {
            Bukkit.getScheduler().runTaskLater(
                    AreaSetup.getPlugin(),
                    () -> p.teleport(configHandler.getSpawnLocation()),
                    20L
            );
        }
    }

    public AreaListener() {
        // 初始化团队
        if (scoreboard.getTeam(TEAM_NAME) == null) {
            Team team = scoreboard.registerNewTeam(TEAM_NAME);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        handlePlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        String toWorldName = Objects.requireNonNull(Objects.requireNonNull(e.getTo()).getWorld()).getName();
        if (configHandler.getAreaWorlds().contains(toWorldName)) {
            handlePlayer(player);
        }
    }

    private void handlePlayer(Player player) {
        String worldName = player.getWorld().getName();
        if (configHandler.getAreaWorlds().contains(worldName)) {
            Team team = scoreboard.getTeam(TEAM_NAME);
            if (team != null) {
                team.addEntry(player.getName());
            }
        } else {
            Team team = scoreboard.getTeam(TEAM_NAME);
            if (team != null && team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }
    }
}
