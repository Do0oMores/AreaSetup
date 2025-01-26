package top.mores.areaSetup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class AreaListener implements Listener {
    private final ConfigHandler configHandler = new ConfigHandler();
    private final Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
    private final String TEAM_NAME = "HiddenID";

    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        String worldName = p.getWorld().getName();
        if (configHandler.getRespawnWorlds().contains(worldName)) {
            e.setRespawnLocation(configHandler.getSpawnLocation());
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
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        String worldName = e.getFrom().getName();
        Player player = e.getPlayer();
        if (configHandler.getAreaWorlds().contains(worldName)) {
            Team team = scoreboard.getTeam(TEAM_NAME);
            if (team != null && team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        String toWorldName = Objects.requireNonNull(Objects.requireNonNull(e.getTo()).getWorld()).getName();
        if (configHandler.getAreaWorlds().contains(toWorldName)) {
            Team team = scoreboard.getTeam(TEAM_NAME);
            if (team != null) {
                team.addEntry(player.getName());
            }
        }
    }

    //僵尸血量修复
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            double maxHealth = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            double currentHealth = livingEntity.getHealth();
            if (currentHealth != maxHealth) {
                livingEntity.setHealth(maxHealth);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (ConfigHandler.teleportingPlayers.contains(player)) {
            ConfigHandler.teleportingPlayers.remove(player);
            player.sendMessage(ChatColor.RED +"传送已取消");
        }
    }
}
