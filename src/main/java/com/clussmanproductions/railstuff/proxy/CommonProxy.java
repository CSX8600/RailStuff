package com.clussmanproductions.railstuff.proxy;

import java.io.File;

import com.clussmanproductions.railstuff.Config;
import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.BlockBlueFlag;
import com.clussmanproductions.railstuff.blocks.BlockEndABS;
import com.clussmanproductions.railstuff.blocks.BlockGreenFlag;
import com.clussmanproductions.railstuff.blocks.BlockManualSwitchStand;
import com.clussmanproductions.railstuff.blocks.BlockMast;
import com.clussmanproductions.railstuff.blocks.BlockMilepost;
import com.clussmanproductions.railstuff.blocks.BlockRedFlag;
import com.clussmanproductions.railstuff.blocks.BlockSignalHead;
import com.clussmanproductions.railstuff.blocks.BlockWhistle;
import com.clussmanproductions.railstuff.blocks.BlockYellowFlag;
import com.clussmanproductions.railstuff.gui.GuiProxy;
import com.clussmanproductions.railstuff.item.ItemPaperwork;
import com.clussmanproductions.railstuff.item.ItemRollingStockAssigner;
import com.clussmanproductions.railstuff.item.ItemSignalSurveyor;
import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;
import com.clussmanproductions.railstuff.tile.TileEntityManualSwitchStand;
import com.clussmanproductions.railstuff.tile.TileEntityMilepost;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber
public class CommonProxy {
	public static Configuration config;
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e)
	{
		if (Loader.isModLoaded("immersiverailroading"))
		{
			e.getRegistry().register(new ItemRollingStockAssigner());
			e.getRegistry().register(new ItemPaperwork());
			e.getRegistry().register(new ItemSignalSurveyor());
		}
		
		e.getRegistry().register(new ItemBlock(ModBlocks.manual_switch_stand).setRegistryName(ModBlocks.manual_switch_stand.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.red_flag).setRegistryName(ModBlocks.red_flag.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.yellow_flag).setRegistryName(ModBlocks.yellow_flag.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.green_flag).setRegistryName(ModBlocks.green_flag.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.blue_flag).setRegistryName(ModBlocks.blue_flag.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.end_abs).setRegistryName(ModBlocks.end_abs.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.mast).setRegistryName(ModBlocks.mast.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.signal_head).setRegistryName(ModBlocks.signal_head.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.milepost).setRegistryName(ModBlocks.milepost.getRegistryName()));
		e.getRegistry().register(new ItemBlock(ModBlocks.whistle).setRegistryName(ModBlocks.whistle.getRegistryName()));
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e)
	{
		e.getRegistry().register(new BlockManualSwitchStand());
		e.getRegistry().register(new BlockRedFlag());
		e.getRegistry().register(new BlockYellowFlag());
		e.getRegistry().register(new BlockGreenFlag());
		e.getRegistry().register(new BlockBlueFlag());
		e.getRegistry().register(new BlockMast());
		e.getRegistry().register(new BlockSignalHead());
		e.getRegistry().register(new BlockEndABS());
		e.getRegistry().register(new BlockMilepost());
		e.getRegistry().register(new BlockWhistle());
		
		GameRegistry.registerTileEntity(TileEntityManualSwitchStand.class, ModRailStuff.MODID + "_manual_switch_stand");
		GameRegistry.registerTileEntity(SignalTileEntity.class, ModRailStuff.MODID + "_signal");
		GameRegistry.registerTileEntity(TileEntityMilepost.class, ModRailStuff.MODID + "_milepost");
	}
	
	public void preInit(FMLPreInitializationEvent event)
    {
        PacketHandler.registerMessages("railstuff");
        
        File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "railstuff.cfg"));
        Config.readConfig();
    }

    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(ModRailStuff.instance, new GuiProxy());
        if (ModRailStuff.IR_INSTALLED)
        {
        	MinecraftForge.EVENT_BUS.register(ImmersiveRailroadingHelper.class);
        }
    }
    
    public void postInit(FMLPostInitializationEvent event)
    {
    	if (config.hasChanged())
    	{
    		config.save();
    	}
    }
}
