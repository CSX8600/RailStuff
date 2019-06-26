package com.clussmanproductions.railstuff.proxy;

import java.util.List;

import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.ModItems;
import com.clussmanproductions.railstuff.blocks.model.ModelLoader;
import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.clussmanproductions.railstuff.gui.GuiAssignRollingStock;
import com.clussmanproductions.railstuff.network.PacketSetAllIdentifiersForClient;
import com.clussmanproductions.railstuff.network.PacketSetIdentifierForAssignGUI;
import com.clussmanproductions.railstuff.network.PacketSetIdentifierForClient;
import com.clussmanproductions.railstuff.tile.TileEntityManualSwitchStand;
import com.clussmanproductions.railstuff.tile.render.ManualSwitchStandRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ModelLoaderRegistry.registerLoader(new ModelLoader());
	}
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent e)
	{
		ModBlocks.initModels();
		ModItems.initModels();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityManualSwitchStand.class, new ManualSwitchStandRenderer());
	}
	
	public static void handleSetIdentifierForAssignGUI(PacketSetIdentifierForAssignGUI message)
	{
		World world = Minecraft.getMinecraft().world;
		RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
		data.setIdentifierGivenUUID(message.id, message.name);
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(message.x-1, message.y-1, message.z-1, message.x+1, message.y+1, message.z+1));
		if (entities.size() > 0)
		{
			GuiAssignRollingStock gui = new GuiAssignRollingStock(message.id);
			Minecraft.getMinecraft().displayGuiScreen(gui);
			gui.setText(message.name);
			gui.setOverwrite(message.overwrite);
		}
	}
	
	public static void handleSetAllIdentifiersForClient(PacketSetAllIdentifiersForClient message)
	{
		World world = Minecraft.getMinecraft().world;
		RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
		data.setData(message.values, message.overwrites);
	}
	
	public static void handleSetIdentifierForClient(PacketSetIdentifierForClient message)
	{
		World world = Minecraft.getMinecraft().world;
		RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
		
		data.setIdentifierGivenUUID(message.id, message.name);
		data.setOverwriteOcTagsGivenUUID(message.id, message.overwrite);
	}

}
