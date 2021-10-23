package me.limeglass.skore.elements.sections;

import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import me.limeglass.skore.utils.ScoreboardManager;
import me.limeglass.skore.utils.ScoreboardSign;

public class SecUpdate extends EffectSection {

	static {
//		Skript.registerSection(SecUpdate.class, "(1¦remove|2¦(set|update)) [the] (slot|value|line)[s] %numbers% of (%players%'[s] skoreboard[s]|skoreboard[s] [of] %players%) [to %-string%] when <.+>");
//		EventValues.registerEventValue(UpdateInfo.class, String.class, new Getter<>() {
//			@Override
//			public @Nullable String get(UpdateInfo info) {
//				return info.getLine();
//			}
//		}, 0);
//		EventValues.registerEventValue(UpdateInfo.class, Player[].class, new Getter<>() {
//			@Override
//			public @Nullable Player[] get(UpdateInfo info) {
//				return info.getPlayers();
//			}
//		}, 0);
//		EventValues.registerEventValue(UpdateInfo.class, Number[].class, new Getter<>() {
//			@Override
//			public @Nullable Number[] get(UpdateInfo info) {
//				return info.getSlots();
//			}
//		}, 0);
	}

	public static class UpdateInfo extends Event {

		private static final HandlerList handlers = new HandlerList();
		private final Player[] players;
		private final Number[] slots;
		private final String line;
		private String condition;

		public UpdateInfo(Player[] players, Number[] slots, String line) {
			this.players = players;
			this.slots = slots;
			this.line = line;
		}

		public void setCondition(String condition) {
			this.condition = condition;
		}

		@Nullable
		public String getCondition() {
			return condition;
		}

		public Player[] getPlayers() {
			return players;
		}

		public Number[] getSlots() {
			return slots;
		}

		@Nullable
		public String getLine() {
			return line;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}

		public static HandlerList getHandlerList() {
			return handlers;
		}

	}

	@Nullable
	private TriggerItem actualNext;
	@Nullable
	private Expression<Number> slots;
	@Nullable
	private Expression<Player> players;
	@Nullable
	private Expression<String> line;
	private boolean remove;

	private Condition condition;
	private Trigger trigger;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
		trigger = loadCode(sectionNode, "update slot", UpdateInfo.class);
		slots = (Expression<Number>) exprs[0];
		players = (Expression<Player>) exprs[1];
		line = (Expression<String>) exprs[2];
		String condition = parseResult.regexes.get(0).group();
		this.condition = Condition.parse(condition, "Can't understand this condition: '" + condition + "'");
		remove = matchedPattern == 1;
		if (line == null && !remove)
			return false;
		if (this.condition == null)
			return false;
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		if (!condition.check(event))
			return walk(event, true);
		UpdateInfo info = new UpdateInfo(players.getArray(event), slots.getArray(event), line == null ? null : line.getSingle(event));
		for (Player player : info.getPlayers()) {
			for (Number value : info.getSlots()) {
				int slot = value.intValue();
				if (slot > 15 || slot < 1) {
					Skript.error("Index needs to be in the range of 1 to 15 (1 and 15 inclusive) in the skoreboard value/slot syntax. Index provided: " + slot, ErrorQuality.SEMANTIC_ERROR);
					continue;
				}
				slot--;
				if (!remove) {
					Optional<ScoreboardSign> scoreboard = ScoreboardManager.getScoreboard(player);
					if (scoreboard.isPresent())
						scoreboard.get().setLine(slot, info.getLine());
				} else {
					Optional<ScoreboardSign> scoreboard = ScoreboardManager.getScoreboard(player);
					if (scoreboard.isPresent())
						scoreboard.get().removeLine(slot);
				}
			}
		}
		Object localVariables = Variables.copyLocalVariables(event);
		Variables.setLocalVariables(info, localVariables);
		trigger.execute(info);
		// The user doesn't want to repeat the update.
		if (info.getCondition() == null) {
			debug(event, true);
			return actualNext;
		}
		this.condition = Condition.parse(info.getCondition(), "Can't understand this condition: '" + info.getCondition() + "'");
		return walk(event, true);
	}

	@Override
	public SecUpdate setNext(@Nullable TriggerItem next) {
		actualNext = next;
		return this;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (debug)
			return "update slot section";
		return "update for " + players.toString(event, debug) + " slot " + slots.toString(event, debug);
	}

}
