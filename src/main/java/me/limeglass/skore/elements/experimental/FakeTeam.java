package me.limeglass.skore.elements.experimental;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class FakeTeam {

	private static final WrappedChatComponent emptyWrappedChatComponent = WrappedChatComponent.fromText("");
	private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

	private BaseComponent[] value;
	private final Player player;
	private int slot;

	FakeTeam(int slot, Player player) {
		this.player = player;
		this.setValue(TextComponent.fromLegacyText(ChatColor.values()[slot] + ""));
		this.setSlot(slot);
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM, true);
		packet.getStrings().write(0, player.getName());
		packet.getIntegers().write(0, 0);
		Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
		if (optional.isPresent()) { // Make sure the structure exists (it always does)
			InternalStructure structure = optional.get();
			structure.getIntegers().write(0, 1); // This new team has 1 member
			structure.getChatComponents().write(0, emptyWrappedChatComponent).write(1, ComponentConverter.fromBaseComponent(value)).write(2, emptyWrappedChatComponent);
			//structure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, color);
			packet.getOptionalStructures().write(0, Optional.of(structure)); // Set the changed structure as the one to use in the packet
		}
		packet.getModifier().write(2, Lists.newArrayList(player.getName(), UUID.randomUUID().toString())); // Team consists of the viewer by name, and the fake entity by its generated UUID
		protocolManager.sendServerPacket(player, packet);
	}

	public Player getPlayer() {
		return player;
	}

	public BaseComponent[] getValue() {
		return value;
	}

	public void setValue(BaseComponent[] value) {
		this.value = value;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

//	private PacketContainer createPacket(int mode) {
//		PacketContainer packet = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM, true);
//		packet.getIntegers().write(0, mode);
//		packet.getStrings().write(0, name);
//
//		Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
//		if (optional.isPresent()) { // Make sure the structure exists (it always does)
//			InternalStructure structure = optional.get();
//			structure.getIntegers().write(0, 1); // This new team has 1 member
//			structure.getChatComponents().write(0, emptyWrappedChatComponent).write(1, ComponentConverter.fromBaseComponent(prefix)).write(2, ComponentConverter.fromBaseComponent(suffix));
//			//structure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, color);
//			packet.getOptionalStructures().write(0, Optional.of(structure)); // Set the changed structure as the one to use in the packet
//		}
//		packet.getModifier().write(2, Lists.newArrayList(getName(), UUID.randomUUID().toString())); // Team consists of the viewer by name, and the fake entity by its generated UUID
//		//packet.getChatComponents().write(0, emptyWrappedChatComponent).write(1, WrappedChatComponent.fromText(prefix)).write(2, WrappedChatComponent.fromText(suffix));
//
//		return packet;
//	}
//
//		public Iterable<PacketContainer> sendLine() {
//			final List<PacketContainer> packets = new ArrayList<>();
//
//			if (first) {
//				first = false;
//				packets.add(createTeam());
//			} else if (prefixChanged || suffixChanged) {
//				packets.add(updateTeam());
//			}
//
//			if (first || playerChanged) {
//				if (oldPlayer != null)
//					packets.add(addOrRemovePlayer(4, oldPlayer));
//				packets.add(changePlayer());
//			}
//
//			return packets;
//		}
//
//		// Team
//
//		public PacketContainer createTeam() {
//			return createPacket(0);
//		}
//
//		public PacketContainer removeTeam() {
//			final PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
//
//			pc.getIntegers().write(0, 1);
//			pc.getStrings().write(0, name);
//
//			first = true;
//
//			return pc;
//		}
//
//		public PacketContainer updateTeam() {
//			return createPacket(2);
//		}
//
//		// Player
//
//		public PacketContainer addOrRemovePlayer(int mode, String playerName) {
//			final PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
//
//			pc.getIntegers().write(0, mode);
//			pc.getSpecificModifier(Collection.class).write(0, Lists.newArrayList(playerName));
//			pc.getStrings().write(0, name);
//
//			return pc;
//		}
//
//		public PacketContainer changePlayer() {
//			return addOrRemovePlayer(3, currentPlayer);
//		}
//
//		public String getCurrentPlayer() {
//			return currentPlayer;
//		}
//
//		public void setPlayer(String name) {
//			if (this.currentPlayer == null || !this.currentPlayer.equals(name))
//				this.playerChanged = true;
//			this.oldPlayer = this.currentPlayer;
//			this.currentPlayer = name;
//		}
//
//		// Value
//		public String getStringValue() {
//			TextComponent text = new TextComponent();
//			Stream.of(getPrefix(), getCurrentPlayer(), getSuffix()).flatMap(component -> Stream.of(component)).forEach(component -> {
//				if (component instanceof String)
//					text.addExtra((String)component);
//				else
//					text.addExtra((BaseComponent)component);
//			});
//			return TextComponent.toLegacyText(text);
//		}
//
//		@SuppressWarnings("deprecation")
//		public void setValue(BaseComponent... value) {
//			if (!limit) {
//				setPrefix(value);
//				setPlayer(ChatColor.values()[index] + "");
//				return;
//			}
//			String compiled = TextComponent.toLegacyText(value);
//			final int length = compiled.length();
//			if (length <= 64) {
//				setPrefix(TextComponent.fromLegacyText(""));
//				setPlayer(TextComponent.toLegacyText(value));
//				setSuffix(TextComponent.fromLegacyText(""));
//			} else if (length <= 80) {
//				setPrefix(TextComponent.fromLegacyText(compiled.substring(0, 64)));
//				setPlayer(compiled.substring(64));
//				setSuffix(TextComponent.fromLegacyText(""));
//			} else if (length <= 144) {
//				setPrefix(TextComponent.fromLegacyText(compiled.substring(0, 64)));
//				setPlayer(compiled.substring(64, 80));
//				setSuffix(TextComponent.fromLegacyText(compiled.substring(80)));
//			} else if (length > 144 && limit)
//				throw new IllegalArgumentException("Too long virtual team value (" + length + " > 48 characters)");
//		}

}
