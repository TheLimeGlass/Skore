package me.limeglass.skore.elements.experimental;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.limeglass.skore.utils.wrappers.WrapperPlayServerScoreboardDisplayObjective;
import me.limeglass.skore.utils.wrappers.WrapperPlayServerScoreboardObjective;
import me.limeglass.skore.utils.wrappers.WrapperPlayServerScoreboardObjective.ObjectiveMode;
import me.limeglass.skore.utils.wrappers.WrapperPlayServerScoreboardObjective.RenderType;

public class Skoreboard {

	//private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	private final FakeTeam[] lines = new FakeTeam[15];
	private final Player player;

	public Skoreboard(Player player) {
		this.player = player;
		sendObjectivePacket(ObjectiveMode.CREATE, WrappedChatComponent.fromText("SkoreBoard"));
		sendDisplayObjectivePacket();

		for (int i = 0; i < lines.length; i++)
			lines[i] = new FakeTeam(i + 1, player);
	}

	public Player getPlayer() {
		return player;
	}

	private void sendObjectivePacket(ObjectiveMode mode, WrappedChatComponent component) {
		WrapperPlayServerScoreboardObjective objectivePacket = new WrapperPlayServerScoreboardObjective();
		objectivePacket.setName(player.getName());
		objectivePacket.setMode(mode);
		if (mode != ObjectiveMode.REMOVE) {
			objectivePacket.setHealthDisplay(RenderType.INTEGER);
			objectivePacket.setDisplayName(component);
		}
		objectivePacket.sendPacket(player);
	}

	private void sendDisplayObjectivePacket() {
		WrapperPlayServerScoreboardDisplayObjective displayPacket = new WrapperPlayServerScoreboardDisplayObjective();
		displayPacket.setDisplaySlot(DisplaySlot.SIDEBAR);
		displayPacket.setScoreName(player.getName());
		displayPacket.sendPacket(player);
	}

//	private void sendLine(final int line) {
//		if (line < 0 || line > 14 || !created)
//			return;
//
//		VirtualTeam team = getOrCreateTeam(line);
//		for (PacketContainer pc : team.sendLine())
//			sendPacket(pc);
//
//		sendPacket(sendScore(team.getCurrentPlayer(), line));
//		team.reset();
//	}

}
