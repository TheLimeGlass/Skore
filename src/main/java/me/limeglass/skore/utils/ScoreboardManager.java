package me.limeglass.skore.utils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardManager implements Listener {

	private final static Set<ScoreboardSign> scoreboards = new HashSet<>();

	public static Optional<ScoreboardSign> getScoreboard(Player player) {
		return scoreboards.stream().filter(scoreboard -> scoreboard.getPlayer().equals(player)).findFirst();
	}

	public static void setupScoreboard(Player player) {
		getScoreboard(player).orElseGet(() -> {
			ScoreboardSign scoreboard = new ScoreboardSign(player, "§aSkoreBoard");
			scoreboard.create();
			scoreboards.add(scoreboard);
			return scoreboard;
		});
	}

	public static void destoryScoreboard(Player player) {
		Optional<ScoreboardSign> scoreboard = getScoreboard(player);
		if (!scoreboard.isPresent())
			return;
		scoreboard.get().destroy();
		scoreboards.remove(scoreboard.get());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		destoryScoreboard(event.getPlayer());
	}

}
