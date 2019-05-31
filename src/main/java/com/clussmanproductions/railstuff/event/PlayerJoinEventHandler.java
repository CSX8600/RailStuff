package com.clussmanproductions.railstuff.event;

import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSetAllIdentifiersForClient;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class PlayerJoinEventHandler {
	@SubscribeEvent
	public static void playerJoin(EntityJoinWorldEvent e)
	{
		if (e.getWorld().isRemote)
		{
			return;
		}
		
		Entity entity = e.getEntity();
		if (!(entity instanceof EntityPlayerMP))
		{
			return;
		}
		
		EntityPlayerMP player = (EntityPlayerMP)entity;
		World world = e.getWorld();
	}
}
