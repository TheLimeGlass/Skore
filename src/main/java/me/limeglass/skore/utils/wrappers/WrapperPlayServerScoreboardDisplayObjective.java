/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.limeglass.skore.utils.wrappers;

import org.bukkit.scoreboard.DisplaySlot;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerScoreboardDisplayObjective extends AbstractPacket {
	public static final PacketType TYPE =
			PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;

	private static Class<?> displaySlotClass;

	public WrapperPlayServerScoreboardDisplayObjective() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
		try {
			displaySlotClass = Class.forName("net.minecraft.world.scores.DisplaySlot");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public WrapperPlayServerScoreboardDisplayObjective(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve DisplaySlot.
	 * <p>
	 * Notes: the DisplaySlot of the scoreboard. 0 = list, 1 = sidebar, 2 =
	 * belowName. etc
	 * 
	 * @return The current DisplaySlot
	 */
	public DisplaySlot getDisplaySlot() {
		return handle.getEnumModifier(DisplaySlot.class, displaySlotClass).read(0);
	}

	/**
	 * Set DisplaySlot.
	 * 
	 * @param value - new value.
	 */
	public void setDisplaySlot(DisplaySlot displaySlot) {
		// Utilize Spigot's enum which should match Minecraft.
		handle.getEnumModifier(DisplaySlot.class, displaySlotClass).write(0, displaySlot);
	}

	/**
	 * Retrieve Score Name.
	 * <p>
	 * Notes: the unique name for the scoreboard to be displayed.
	 * 
	 * @return The current Score Name
	 */
	public String getScoreName() {
		return handle.getStrings().read(0);
	}

	/**
	 * Set Score Name.
	 * 
	 * @param value - new value.
	 */
	public void setScoreName(String value) {
		handle.getStrings().write(0, value);
	}

}
