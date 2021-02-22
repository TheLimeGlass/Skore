package me.limeglass.skore.elements.conditions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skore.lang.SkoreCondition;
import me.limeglass.skore.utils.ScoreboardManager;
import me.limeglass.skore.utils.annotations.Patterns;

@Name("Skoreboard - Player has Skoreboard")
@Description("Check if the player has a skoreboard initialized.")
@Patterns("%player% (1¦has|2¦does not have) [a] skoreboard")
public class CondPlayerSkoreboard extends SkoreCondition {

	public boolean check(Event event) {
		if (areNull(event))
			return !isNegated();
		Player player = expressions.getSingle(event, Player.class);
		return ScoreboardManager.getScoreboard(player).isPresent() ? isNegated() : !isNegated();
	}

}