package me.limeglass.skore.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;

import me.limeglass.skore.Skore;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScoreboardSign {

	private static final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

	private final static boolean limit = Skore.getInstance().getConfig().getBoolean("Limit", false);
	private final VirtualTeam[] lines = new VirtualTeam[15];
	private final Player player;

	private String objectiveName;
	private boolean created;

	public ScoreboardSign(Player player, String objectiveName) {
		this.player = player;
		this.objectiveName = objectiveName;
	}

	public Player getPlayer() {
		return player;
	}

	public String getObjectiveName() {
		return objectiveName;
	}

	private void sendPacket(PacketContainer container) {
		try {
			pm.sendServerPacket(player, container);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send the initial creation packets for this scoreboard sign. Must be called at least once.
	 */
	public void create() {
		if (created)
			return;

		sendPacket(createObjectivePacket(0, objectiveName));
		sendPacket(setObjectiveSlot());
		for (int i = 0; i < lines.length; i++)
			sendLine(i);
		created = true;
	}

	/**
	 * Send the packets to remove this scoreboard sign. A destroyed scoreboard sign must
	 * be recreated using {@link ScoreboardSign#create()} in order to be used again.
	 */
	public void destroy() {
		if (!created)
			return;

		sendPacket(createObjectivePacket(1, null));
		for (VirtualTeam team : lines)
			if (team != null)
				sendPacket(team.removeTeam());
		created = false;
	}

	/**
	 * Change the name of the objective. The name is displayed at the top of the scoreboard.
	 *
	 * @param name the name of the objective - max 32 characters
	 */
	public void setObjectiveName(String name) {
		this.objectiveName = name;
		if (created)
			sendPacket(createObjectivePacket(2, name));
	}

	/**
	 * Change a scoreboard line and send the packets to the player. Can be called asynchronously.
	 *
	 * @param line  the number of the line - between 0 and 14
	 * @param value the new value for the scoreboard line
	 */
	public void setLine(int line, String value) {
		VirtualTeam team = getOrCreateTeam(line);
		String old = team.getCurrentPlayer();

		if (value.equals(old))
			return;

		if (old != null && created)
			sendPacket(removeLine(old));

		team.setValue(value);
		sendLine(line);
	}

	/**
	 * Set all scoreboard lines to the list and send these to the player.
	 *
	 * @param list the list of the new scoreboard lines
	 */
	public void setLines(Iterable<String> list) {
		int i = 0;
		for (String s : list) {
			setLine(i, s);
			i++;
		}
	}

	/**
	 * Remove a given scoreboard line.
	 *
	 * @param line the line to remove
	 */
	public void removeLine(int line) {
		final VirtualTeam team = getOrCreateTeam(line);
		final String old = team.getCurrentPlayer();

		if (old != null && created) {
			sendPacket(removeLine(old));
			sendPacket(team.removeTeam());
		}

		lines[line] = null;
	}

	/**
	 * Get the current value for a line.
	 *
	 * @param line the line
	 * @return its content
	 */
	public String getLine(int line) {
		return line < 0 || line > 14 ? null : getOrCreateTeam(line).getValue();
	}

	/**
	 * Get the team assigned to a line.
	 *
	 * @return the {@link VirtualTeam} used to display this line
	 */
	public VirtualTeam getTeam(final int line) {
		return line < 0 || line > 14 ? null : getOrCreateTeam(line);
	}

	private void sendLine(final int line) {
		if (line < 0 || line > 14 || !created)
			return;

		VirtualTeam team = getOrCreateTeam(line);
		for (PacketContainer pc : team.sendLine())
			sendPacket(pc);

		sendPacket(sendScore(team.getCurrentPlayer(), line));
		team.reset();
	}

	private VirtualTeam getOrCreateTeam(int line) {
		if (lines[line] == null)
			lines[line] = new VirtualTeam(line, "__fakeScore" + line, "", "");
		return lines[line];
	}

	// 0 : Create
	// 1 : Delete
	// 2 : Update
	private PacketContainer createObjectivePacket(int mode, String displayName) {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE, true);

		pc.getIntegers().write(0, mode);
		pc.getStrings().write(0, player.getName());

		if (mode == 0 || mode == 2)
			pc.getChatComponents().write(0, WrappedChatComponent.fromText(displayName));

		return pc;
	}

	private PacketContainer setObjectiveSlot() {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);

		pc.getIntegers().write(0, 1);
		pc.getStrings().write(0, player.getName());

		return pc;
	}

	private PacketContainer sendScore(String line, int score) {
		final PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);

		pc.getIntegers().write(0, score);
		pc.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);
		pc.getStrings().write(0, line).write(1, player.getName());

		return pc;
	}

	private PacketContainer removeLine(String line) {
		final PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);

		pc.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE);
		pc.getStrings().write(0, line);

		return pc;
	}

	/**
	 * This class is used to manage the content of a line. Advanced users can use it as they want, but they are
	 * encouraged to read and understand the code before doing so. Thus, use these methods at your own risk.
	 */
	public static class VirtualTeam {

		private final String name;

		private String currentPlayer, oldPlayer;
		private String prefix, suffix;

		private boolean playerChanged, prefixChanged, suffixChanged;
		private boolean first = true;

		private final int index;

		// Virtual team

		private VirtualTeam(int index, final String name, final String prefix, final String suffix) {
			this.prefix = prefix;
			this.suffix = suffix;
			this.index = index;
			this.name = name;
		}

		public void reset() {
			prefixChanged = false;
			suffixChanged = false;
			playerChanged = false;
			oldPlayer = null;
		}

		public String getName() {
			return name;
		}

		// Prefix

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(final String prefix) {
			if (this.prefix == null || !this.prefix.equals(prefix))
				this.prefixChanged = true;
			this.prefix = prefix;
		}

		// Suffix

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(final String suffix) {
			if (this.suffix == null || !this.suffix.equals(prefix))
				this.suffixChanged = true;
			this.suffix = suffix;
		}

		// Packets
		private static final WrappedChatComponent emptyWrappedChatComponent = WrappedChatComponent.fromText("");

		private PacketContainer createPacket(int mode) {
			PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM, true);
			packet.getIntegers().write(0, mode);
			packet.getStrings().write(0, name);

			Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
			if (optional.isPresent()) { // Make sure the structure exists (it always does)
				InternalStructure structure = optional.get();
				structure.getIntegers().write(0, 1); // This new team has 1 member
				structure.getChatComponents().write(0, emptyWrappedChatComponent).write(1, WrappedChatComponent.fromText(prefix)).write(2, WrappedChatComponent.fromText(suffix));
				//structure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, color);
				packet.getOptionalStructures().write(0, Optional.of(structure)); // Set the changed structure as the one to use in the packet
			}
			packet.getModifier().write(2, Lists.newArrayList(getName(), UUID.randomUUID().toString())); // Team consists of the viewer by name, and the fake entity by its generated UUID
			//packet.getChatComponents().write(0, emptyWrappedChatComponent).write(1, WrappedChatComponent.fromText(prefix)).write(2, WrappedChatComponent.fromText(suffix));

			return packet;
		}

		public Iterable<PacketContainer> sendLine() {
			final List<PacketContainer> packets = new ArrayList<>();

			if (first) {
				first = false;
				packets.add(createTeam());
			} else if (prefixChanged || suffixChanged) {
				packets.add(updateTeam());
			}

			if (first || playerChanged) {
				if (oldPlayer != null)
					packets.add(addOrRemovePlayer(4, oldPlayer));
				packets.add(changePlayer());
			}

			return packets;
		}

		// Team

		public PacketContainer createTeam() {
			return createPacket(0);
		}

		public PacketContainer removeTeam() {
			final PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);

			pc.getIntegers().write(0, 1);
			pc.getStrings().write(0, name);

			first = true;

			return pc;
		}

		public PacketContainer updateTeam() {
			return createPacket(2);
		}

		// Player

		public PacketContainer addOrRemovePlayer(int mode, String playerName) {
			final PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);

			pc.getIntegers().write(0, mode);
			pc.getSpecificModifier(Collection.class).write(0, Lists.newArrayList(playerName));
			pc.getStrings().write(0, name);

			return pc;
		}

		public PacketContainer changePlayer() {
			return addOrRemovePlayer(3, currentPlayer);
		}

		public String getCurrentPlayer() {
			return currentPlayer;
		}

		public void setPlayer(String name) {
			if (this.currentPlayer == null || !this.currentPlayer.equals(name))
				this.playerChanged = true;
			this.oldPlayer = this.currentPlayer;
			this.currentPlayer = name;
		}

		// Value

		public String getValue() {
			return getPrefix() + getCurrentPlayer() + getSuffix();
		}

		public void setValue(String value) {
			if (!limit) {
				setPrefix(value);
				setPlayer(ChatColor.values()[index] + "");
				return;
			}
			final int length = value.length();
			if (length <= 64) {
				setPrefix("");
				setPlayer(value);
				setSuffix("");
			} else if (length <= 80) {
				setPrefix(value.substring(0, 64));
				setPlayer(value.substring(64));
				setSuffix("");
			} else if (length <= 144) {
				setPrefix(value.substring(0, 64));
				setPlayer(value.substring(64, 80));
				setSuffix(value.substring(80));
			} else if (length > 144 && limit)
				throw new IllegalArgumentException("Too long virtual team value (" + length + " > 48 characters)");
		}
	}

}
