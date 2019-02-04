package com.clussmanproductions.railstuff.event;

import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.clussmanproductions.railstuff.item.ItemRollingStockAssigner;
import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSetIdentifierForAssignGUI;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.SERVER)
public class PlayerInteractServerEventHandler {

	@SubscribeEvent
	public static void playerInteract(PlayerInteractEvent.EntityInteract e)
	{
		Class rollingStockClass = null;
		try {
			rollingStockClass = Class.forName("cam72cam.immersiverailroading.entity.EntityRollingStock");
		} catch (ClassNotFoundException e1) {
			return;
		}
		
		if (!rollingStockClass.isAssignableFrom(e.getTarget().getClass()))
		{
			return;
		}
		
		if (!(e.getEntityPlayer().inventory.getCurrentItem().getItem() instanceof ItemRollingStockAssigner))
		{
			return;
		}
		
		e.setCanceled(true);
		Entity entity = e.getTarget();
		int x = (int)Math.floor(entity.posX);
		int y = (int)Math.floor(entity.posY);
		int z = (int)Math.floor(entity.posZ);
		
		RollingStockIdentificationData data = RollingStockIdentificationData.get(e.getWorld());
		String name = data.getIdentifierByUUID(e.getTarget().getPersistentID());
		
		PacketSetIdentifierForAssignGUI packet = new PacketSetIdentifierForAssignGUI();
		packet.id = e.getTarget().getPersistentID();
		packet.name = name;
		packet.x = x;
		packet.y = y;
		packet.z = z;
		
		PacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP)e.getEntityPlayer());
	}
}
