package me.missionary.board;

import me.missionary.board.provider.BoardProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 3/23/2018
 */
public class BoardManager implements Listener {

    private static final Predicate<UUID> PLAYER_IS_ONLINE = uuid -> Bukkit.getPlayer(uuid) != null;
    private final JavaPlugin plugin;
    private BoardProvider provider;
    private Map<UUID, Board> scoreboards;
    private BukkitTask updateTask;

    public BoardManager(JavaPlugin plugin, BoardProvider provider) {
        this.plugin = plugin;
        this.provider = provider;
        this.scoreboards = new ConcurrentHashMap<>();
        this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateAll, 2, 2); // Updates every 2 ticks.
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getOnlinePlayers().forEach(this::setup);
    }

    public boolean hasBoard(Player player) {
        return scoreboards.containsKey(player.getUniqueId());
    }

    public Optional<Board> getBoard(Player player) {
        return Optional.ofNullable(scoreboards.get(player.getUniqueId()));
    }

    public void setProvider(BoardProvider provider) {
        this.provider = provider;
        scoreboards.values().forEach(board -> board.setProvider(provider));
    }

    private void setup(Player player) {
        Optional.ofNullable(scoreboards.remove(player.getUniqueId())).ifPresent(Board::reset);
        if (player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        scoreboards.put(player.getUniqueId(), new Board(player, provider));
    }

    private void remove(Player player) {
        Optional.ofNullable(scoreboards.remove(player.getUniqueId())).ifPresent(Board::remove);
    }

    private void updateAll() {
        scoreboards.keySet().stream().filter(PLAYER_IS_ONLINE).map(id -> scoreboards.get(id)).forEach(Board::update);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (e.getPlayer().isOnline()) { // Set this up 2 ticks later.
                setup(e.getPlayer());
            }
        }, 2L);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        this.remove(e.getPlayer());
    }

    public void onDisable() {
        updateTask.cancel();
        Bukkit.getOnlinePlayers().forEach(this::remove);
        HandlerList.unregisterAll(this);
        scoreboards.clear();
    }
}
