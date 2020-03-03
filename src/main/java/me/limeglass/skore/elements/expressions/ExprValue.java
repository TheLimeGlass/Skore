package me.limeglass.skore.elements.expressions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import me.limeglass.skore.Skore;
import me.limeglass.skore.lang.SkorePropertyExpression;
import me.limeglass.skore.utils.annotations.Changers;
import me.limeglass.skore.utils.annotations.Properties;
import me.limeglass.skore.utils.annotations.PropertiesAddition;
import me.limeglass.skore.utils.annotations.Settable;

@Name("Skoreboard - Value")
@Description("Returns or changes the value of the Skoreboard(s).")
@Properties({"players", "(slot|value|line)[s] %numbers%", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("skoreboard[s]")
@Changers({ChangeMode.SET, ChangeMode.DELETE, ChangeMode.RESET})
@Settable(String.class)
public class ExprValue extends SkorePropertyExpression<Player, String> {

	private TitleManagerAPI api = Skore.getTitleManagerAPI();
	
	@Override
	protected String[] get(Event event, Player[] players) {
		if (isNull(event)) return null;
		for (Player player : players) {
			for (Number slot : expressions.getAll(event, Number.class)) {
				collection.add(api.getScoreboardValue(player, slot.intValue()));
			}
		}
		return collection.toArray(new String[collection.size()]);
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (isNull(event) || delta == null) return;
		for (Player player : expressions.getAll(event, Player.class)) {
			for (Number slot : expressions.getAll(event, Number.class)) {
				if (mode == ChangeMode.SET) {
					api.setScoreboardValueWithPlaceholders(player, slot.intValue(), (String)delta[0]);
				} else {
					api.removeScoreboardValue(player, slot.intValue());
				}
			}
		}
	}

}
