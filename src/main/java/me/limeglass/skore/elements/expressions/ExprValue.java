package me.limeglass.skore.elements.expressions;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import me.limeglass.skore.lang.SkorePropertyExpression;
import me.limeglass.skore.utils.ScoreboardManager;
import me.limeglass.skore.utils.ScoreboardSign;
import me.limeglass.skore.utils.annotations.Changers;
import me.limeglass.skore.utils.annotations.Properties;
import me.limeglass.skore.utils.annotations.PropertiesAddition;
import me.limeglass.skore.utils.annotations.Settable;
import net.md_5.bungee.api.chat.BaseComponent;

@Name("Skoreboard - Value")
@Description("Returns or changes the value of the Skoreboard(s).")
@Properties({"players", "(slot|value|line)[s] %numbers%", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("skoreboard[s]")
@Changers({ChangeMode.SET, ChangeMode.DELETE, ChangeMode.RESET})
@Settable(String.class)
public class ExprValue extends SkorePropertyExpression<Player, String> {
	
	@Override
	protected String[] get(Event event, Player[] players) {
		if (isNull(event))
			return null;
		for (Player player : players) {
			for (Number slot : expressions.getAll(event, Number.class)) {
				collection.add(ScoreboardManager.getScoreboard(player).get().getLine(slot.intValue()));
			}
		}
		return collection.toArray(new String[collection.size()]);
	}

	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (isNull(event) || delta == null)
			return;
		BaseComponent[] components = BungeeConverter.convert(ChatMessages.parseToArray((String) delta[0]));
		for (Player player : expressions.getAll(event, Player.class)) {
			for (Number value : expressions.getAll(event, Number.class)) {
				int slot = value.intValue();
				if (slot > 15 || slot < 1) {
					Skript.error("Index needs to be in the range of 1 to 15 (1 and 15 inclusive) in the skoreboard value/slot syntax. Index provided: " + slot, ErrorQuality.SEMANTIC_ERROR);
					continue;
				}
				slot--;
				if (mode == ChangeMode.SET) {
					Optional<ScoreboardSign> scoreboard = ScoreboardManager.getScoreboard(player);
					if (scoreboard.isPresent())
						scoreboard.get().setLine(slot, components);
				} else {
					Optional<ScoreboardSign> scoreboard = ScoreboardManager.getScoreboard(player);
					if (scoreboard.isPresent())
						scoreboard.get().removeLine(slot);
				}
			}
		}
	}

}
