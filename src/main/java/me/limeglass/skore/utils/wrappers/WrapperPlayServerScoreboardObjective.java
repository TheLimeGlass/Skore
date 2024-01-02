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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerScoreboardObjective extends AbstractPacket {

	public static final PacketType TYPE =
			PacketType.Play.Server.SCOREBOARD_OBJECTIVE;

	private static Class<?> renderTypeClass;
	
	public WrapperPlayServerScoreboardObjective() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
		try {
			renderTypeClass = Class.forName("net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public WrapperPlayServerScoreboardObjective(PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve Objective name.
	 * <p>
	 * Notes: an unique name for the objective
	 * 
	 * @return The current Objective name
	 */
	public String getName() {
		return handle.getStrings().read(0);
	}

	/**
	 * Set Objective name.
	 * 
	 * @param value - new value.
	 */
	public void setName(String value) {
		handle.getStrings().write(0, value);
	}

	/**
	 * Retrieve Objective DisplayName.
	 * <p>
	 * Notes: only if mode is 0 or 2. The text to be displayed for the score.
	 * 
	 * @return The current Objective value
	 */
	public WrappedChatComponent getDisplayName() {
		return handle.getChatComponents().read(0);
	}

	/**
	 * Set Objective DisplayName.
	 * 
	 * @param value - new value.
	 */
	public void setDisplayName(WrappedChatComponent value) {
		handle.getChatComponents().write(0, value);
	}

	/**
	 * Retrieve health display.
	 * <p>
	 * Notes: Can be either INTEGER or HEARTS
	 * 
	 * @return the current health display value
	 */
	public RenderType getHealthDisplay() {
		return handle.getEnumModifier(RenderType.class, renderTypeClass).read(0);
	}

	/**
	 * Set health display.
	 * 
	 * @param value - value
	 * @see #getHealthDisplay()
	 */
	public void setHealthDisplay(RenderType value) {
		handle.getEnumModifier(RenderType.class, renderTypeClass).write(0, value);
	}

	/**
	 * Retrieve Mode.
	 * <p>
	 * Notes: 0 to create the scoreboard. 1 to remove the scoreboard. 2 to
	 * update the display text.
	 * 
	 * @return The current Mode
	 */
	public ObjectiveMode getMode() {
		return ObjectiveMode.values()[handle.getIntegers().read(0)];
	}

	/**
	 * Set Mode.
	 * 
	 * @param value - new value.
	 */
	public void setMode(ObjectiveMode mode) {
		handle.getIntegers().write(0, mode.ordinal());
	}

	public enum ObjectiveMode {
		CREATE, REMOVE, UPDATE
	}

	public enum RenderType {
		INTEGER, HEARTS
	}

}
