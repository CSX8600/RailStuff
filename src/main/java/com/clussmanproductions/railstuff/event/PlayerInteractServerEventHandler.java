package com.clussmanproductions.railstuff.event;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.clussmanproductions.railstuff.item.ItemRollingStockAssigner;
import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSetIdentifierForAssignGUI;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;

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
		if (ModRailStuff.IR_INSTALLED)
		{
			ImmersiveRailroadingHelper.handlePlayerInteractServer(e);
		}
	}
}
