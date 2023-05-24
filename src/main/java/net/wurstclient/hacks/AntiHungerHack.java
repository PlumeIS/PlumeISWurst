/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.PacketOutputListener;
import net.wurstclient.hack.Hack;

import java.lang.reflect.Field;

@SearchTags({"anti hunger"})
public final class AntiHungerHack extends Hack implements PacketOutputListener
{
	public AntiHungerHack()
	{
		super("AntiHunger");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void onEnable()
	{
		EVENTS.add(PacketOutputListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		EVENTS.remove(PacketOutputListener.class, this);
	}
	
	@Override
	public void onSentPacket(PacketOutputEvent event)
	{
		if(!(event.getPacket() instanceof PlayerMoveC2SPacket))
			return;
		
		PlayerMoveC2SPacket oldPacket = (PlayerMoveC2SPacket)event.getPacket();
		if(!MC.player.isOnGround() || MC.player.fallDistance > 0.5)
			return;
		
		if(MC.interactionManager.isBreakingBlock())
			return;
		
		double x = oldPacket.getX(-1);
		double y = oldPacket.getY(-1);
		double z = oldPacket.getZ(-1);
		float yaw = oldPacket.getYaw(-1);
		float pitch = oldPacket.getPitch(-1);
		
		Packet<?> newPacket;
		try {
			Class<? extends PlayerMoveC2SPacket> oldPacketClass = oldPacket.getClass();
			Field changePositionField = oldPacketClass.getDeclaredField("changePosition");
			Field changeLookField = oldPacketClass.getDeclaredField("changeLook");
			boolean changePosistion = (boolean)changePositionField.get(oldPacketClass);
			boolean changeLook = (boolean)changeLookField.get(oldPacketClass);
			if(changePosistion)
				if(changeLook)
					newPacket =
							new PlayerMoveC2SPacket.Both(x, y, z, yaw, pitch, false);
				else
					newPacket =
							new PlayerMoveC2SPacket.PositionOnly(x, y, z, false);
			else if(changeLook)
				newPacket = new PlayerMoveC2SPacket.LookOnly(yaw, pitch, false);
			else
				newPacket = new PlayerMoveC2SPacket(false);
			event.setPacket(newPacket);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}