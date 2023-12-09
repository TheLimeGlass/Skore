package me.limeglass.skore.elements.conditions;

import org.bukkit.entity.Player;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import me.limeglass.skore.utils.ScoreboardManager;

@Name("Skoreboard - Player has Skoreboard")
@Description("Check if players have a skoreboard initialized.")
@Examples({
	"on player join:",
		"\tplayer does not have a skoreboard",
		"\tsetup skoreboard for player"
})
@Since("3.0.0")
public class CondPlayerSkoreboard extends PropertyCondition<Player> {

	static {
		register(CondPlayerSkoreboard.class, PropertyType.HAVE, "[a] ([custom|skore] score|skore)[ ]board", "players");
	}

	@Override
	public boolean check(Player player) {
		return ScoreboardManager.getScoreboard(player).isPresent();
	}

	@Override
	protected String getPropertyName() {
		return "skoreboard";
	}

}
