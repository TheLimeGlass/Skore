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
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.util.coll.CollectionUtils;
import me.limeglass.skore.utils.ScoreboardManager;
import me.limeglass.skore.utils.ScoreboardSign;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Name("Skoreboard - Title")
@Description("Returns or changes the title of the Skoreboard(s).")
@Since("3.0.0")
public class ExprTitle extends SimplePropertyExpression<Player, String> {

	static {
		Skript.registerExpression(ExprTitle.class, String.class, ExpressionType.PROPERTY,
				"[(all [[of] the]|the)] [([custom|skore] score|skore)[ ]board] title[s] of %players%",
				"%players%'[s] [([custom|skore] score|skore)[ ]board] title[s]"
		);
	}

	@Override
	@Nullable
	public String convert(Player player) {
		return TextComponent.toLegacyText(ScoreboardManager.getScoreboard(player).get().getObjectiveName());
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
			if (mode == ChangeMode.SET) {
				Optional<ScoreboardSign> scoreboard = ScoreboardManager.getScoreboard(player);
				if (scoreboard.isPresent())
					scoreboard.get().setObjectiveName(components);
			} else {
				Optional<ScoreboardSign> scoreboard = ScoreboardManager.getScoreboard(player);
				if (scoreboard.isPresent()) {
					BaseComponent[] playerName = BungeeConverter.convert(ChatMessages.parseToArray(player.getDisplayName()));
					scoreboard.get().setObjectiveName(playerName);
				}
			}
		}
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "custom scoreboard title";
	}

}
