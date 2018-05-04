package me.missionary.board;

import lombok.NonNull;
import me.missionary.board.provider.BoardProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 3/23/2018
 */
public class Board {

    private static final String[] CACHED_ENTRIES = new String[ChatColor.values().length];

    static {
        for (int i = 0; i < 15; i++) {
            CACHED_ENTRIES[i] = ChatColor.values()[i].toString() + ChatColor.RESET;
        }
    }

    private final Player player;
    private final Objective objective;
    private final Team team;
    private BoardProvider provider;
    private boolean ready;

    public Board(@NonNull final Player player, final BoardProvider provider) {
        this.player = player;
        this.provider = provider;
        Objective objective = (this.getScoreboard().getObjective("board") == null)
                ? this.getScoreboard().registerNewObjective("board", "dummy")
                : this.getScoreboard().getObjective("board");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective = objective;
        Team team = (this.getScoreboard().getTeam("board") == null)
                ? this.getScoreboard().registerNewTeam("board")
                : this.getScoreboard().getTeam("board");
        team.setAllowFriendlyFire(true);
        team.setCanSeeFriendlyInvisibles(false);
        team.setPrefix("");
        team.setSuffix("");
        this.team = team;
        this.ready = true;
    }

    public Scoreboard getScoreboard() {
        return (player != null) ? player.getScoreboard() : null;
    }

    public void setProvider(@NonNull BoardProvider provider) {
        this.provider = provider;
    }

    public void remove() {
        this.reset();
    }

    public void update() {
        // Checking if we are ready to start updating the Scoreboard.
        if (!ready) {
            return;
        }

        // Making sure the player is connected.
        if (!player.isOnline()) {
            remove();
            return;
        }

        // Making sure the Scoreboard Provider is set.
        if (provider == null) {
            return;
        }

        // Getting their Scoreboard display from the Scoreboard Provider.
        final List<String> entries = provider.getLines(player).stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());

        // Setting the Scoreboard title
        String title = provider.getTitle(player);
        if (title.length() > 32) {
            Bukkit.getLogger().warning("The title " + title + " is over 32 characters in length, substringing to prevent errors.");
            title = title.substring(0, 32);
        }
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));

        // Clearing previous Scoreboard values if entry sizes don't match.
        if (this.getScoreboard().getEntries().size() != entries.size())
            this.getScoreboard().getEntries().forEach(this::removeEntry);

        // Setting Scoreboard lines.
        for (int i = 0; i < entries.size(); i++) {
            String str = entries.get(i);
            BoardEntry entry = BoardEntry.translateToEntry(str);
            Team team = getScoreboard().getTeam(CACHED_ENTRIES[i]);

            if (team == null) {
                team = this.getScoreboard().registerNewTeam(CACHED_ENTRIES[i]);
                team.addEntry(team.getName());
            }

            team.setPrefix(entry.getPrefix());
            team.setSuffix(entry.getSuffix());

            objective.getScore(team.getName()).setScore(15 - i);
        }
    }

    public void removeEntry(@NonNull final String id) {
        this.getScoreboard().resetScores(id);
    }

    public void reset() {
        ready = false;
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
