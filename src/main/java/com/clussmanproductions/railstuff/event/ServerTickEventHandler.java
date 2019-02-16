package com.clussmanproductions.railstuff.event;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.proxy.CommonProxy;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public class ServerTickEventHandler {
	@SubscribeEvent
	public static void tick(TickEvent.ServerTickEvent e)
	{		
		if (e.side == Side.CLIENT || e.phase == Phase.END || !ModRailStuff.IR_INSTALLED)
		{
			return;
		}
		
		World world = DimensionManager.getWorld(0);
		
		CommonProxy.ENDPOINT_DATA_CLEANUP_TASK.tick(world);
	}
}
