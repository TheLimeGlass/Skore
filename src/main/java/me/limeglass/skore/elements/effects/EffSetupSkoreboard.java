package me.limeglass.skore.elements.effects;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.limeglass.skore.utils.ScoreboardManager;

@Name("Skoreboard - Setup")
@Description("Setup Skoreboards for players, each player must have a skoreboard initalized first.")
@Examples({
	"on player join:",
		"\tsetup skoreboard of player"
})
@Since("3.0.0")
public class EffSetupSkoreboard extends Effect {

	static {
		Skript.registerEffect(EffSetupSkoreboard.class,
				"(create|set[up]) [a] ([custom|skore] score|skore)[ ]board (for|to|of) %players%",
				"(remove|reset|delete|destroy) [the] ([custom|skore] score|skore)[ ]board (for|to|of) %players%"
		);
		
	}

	private Expression<Player> players;
	private boolean remove;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		players = (Expression<Player>) exprs[0];
		remove = matchedPattern == 1;
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Player player : players.getArray(event)) {
			if (remove) {
				ScoreboardManager.destoryScoreboard(player);
			} else {
				ScoreboardManager.setupScoreboard(player);
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (remove ? "remove " : "setup ") + "skoreboard for " + players.toString(event, debug);
	}

}
