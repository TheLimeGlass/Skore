package me.limeglass.skore.elements.effects;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.limeglass.skore.elements.sections.SecUpdate.UpdateInfo;
@Name("Skoreboard - Update Line Section Repeat")
@Description("Set the condition to repeat the section and update the skoreboard line again.")
public class EffRepeat extends Effect {

	static {
		Skript.registerEffect(EffRepeat.class, "repeat [update] [again] [(when|with [new] condition) <.+>]");
	}

	private String condition;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!getParser().isCurrentEvent(UpdateInfo.class))
			return false;
		condition = parseResult.regexes.get(0).group();
		return true;
	}

	@Override
	protected void execute(Event event) {
		if (condition == null)
			return;
		((UpdateInfo) event).setCondition(condition);
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (debug)
			return "repeat line update";
		return "repeat line update with condition " + condition;
	}

}
