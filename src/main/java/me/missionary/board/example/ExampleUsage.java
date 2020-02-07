package me.missionary.board.example;

import me.missionary.board.BoardManager;
import me.missionary.board.provider.BoardProvider;
import me.missionary.board.settings.BoardSettings;
import me.missionary.board.settings.ScoreDirection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Missionary (missionarymc@gmail.com)
 * @since 3/29/2018
 */
public class ExampleUsage extends JavaPlugin {

    private BoardManager manager;

    @Override
    public void onEnable() {
        manager = new BoardManager(this, BoardSettings.builder().boardProvider(new ExampleProviderImplementation()).scoreDirection(ScoreDirection.UP).build());
    }

    @Override
    public void onDisable() {
        manager.onDisable();
    }

    private class ExampleProviderImplementation implements BoardProvider {

        @Override
        public String getTitle(Player player) {
            return ChatColor.LIGHT_PURPLE + "Board";
        }

        @Override
        public List<String> getLines(Player player) {
            List<String> lines = new ArrayList<>();
            lines.add("&7&m-----------------");
            lines.add(ChatColor.LIGHT_PURPLE + "Name" + ChatColor.GRAY + ": " + ChatColor.YELLOW + player.getName());
            lines.add(ChatColor.LIGHT_PURPLE + "Health" + ChatColor.GRAY + ": " + ChatColor.YELLOW + String.format("%.1f\u2764", Math.ceil(player.getHealth()) / 2.0));
            lines.add(ChatColor.LIGHT_PURPLE + "Hunger" + ChatColor.GRAY + ": " + ChatColor.YELLOW + player.getFoodLevel() / 2);
            lines.add("&7&m-----------------");
            return lines;
        }
    }
}
