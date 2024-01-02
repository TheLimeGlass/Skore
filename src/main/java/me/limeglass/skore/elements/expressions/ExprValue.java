package me.limeglass.skore.elements.expressions;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.limeglass.skore.utils.ScoreboardManager;
import me.limeglass.skore.utils.ScoreboardSign;
import net.md_5.bungee.api.chat.BaseComponent;

@Name("Skoreboard - Value")
@Description("Returns or changes the value of the Skoreboard(s).")
@Since("3.0.0")
public class ExprValue extends PropertyExpression<Player, String> {

	static {
		Skript.registerExpression(ExprTitle.class, String.class, ExpressionType.PROPERTY,
				"[(all [[of] the]|the)] ([custom|skore] score|skore)[[ ]board] (slot|value|line)[s] %numbers% of %players%",
				"%players%'[s] ([custom|skore] score|skore)[[ ]board] (slot|value|line)[s] %numbers%"
		);
	}

	private Expression<Number> slots;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<? extends Player>) exprs[matchedPattern ^ 1]);
		slots = (Expression<Number>) exprs[0];
		return true;
	}

	@Override
	protected String[] get(Event event, Player[] source) {
		Number[] slots = this.slots.getArray(event);
		String[] values = new String[slots.length];
		for (Player player : getExpr().getArray(event)) {
			Optional<ScoreboardSign> skoreboard = ScoreboardManager.getScoreboard(player);
			if (!skoreboard.isPresent())
				return new String[0];
			for (int i = 0; i < slots.length; i++)
				values[i] = skoreboard.get().getLine(slots[i].intValue());
		}
		return values;
	}

	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		switch (mode) {
		case DELETE:
		case RESET:
		case SET:
			return CollectionUtils.array(String.class);
		case ADD:
		case REMOVE:
		case REMOVE_ALL:
		default:
			return null;
		}
	}

	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		BaseComponent[] components = delta == null ? null : BungeeConverter.convert(ChatMessages.parseToArray((String) delta[0]));
		for (Player player : getExpr().getArray(event)) {
			for (Number value : slots.getArray(event)) {
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

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "skoreboard slots " + slots.toString(event, debug) + " of " + getExpr().toString(event, debug);
	}

}
