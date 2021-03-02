package com.clussmanproductions.railstuff;

import org.apache.logging.log4j.Logger;

import com.clussmanproductions.railstuff.proxy.CommonProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModRailStuff.MODID, name = ModRailStuff.NAME, version = ModRailStuff.VERSION)
public class ModRailStuff
{
    public static final String MODID = "railstuff";
    public static final String NAME = "RailStuff";
    public static final String VERSION = "0.2.6c";
    public static boolean IR_INSTALLED = false;
    public static CreativeTabs CREATIVE_TAB = new CreativeTabs("RailStuff") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Item.getItemFromBlock(ModBlocks.signal_head), 1, 0);
		}
	};

	@Instance
	public static ModRailStuff instance;

	@SidedProxy(clientSide = "com.clussmanproductions.railstuff.proxy.ClientProxy", serverSide = "com.clussmanproductions.railstuff.proxy.ServerProxy")
	public static CommonProxy proxy;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit(event);

    	IR_INSTALLED = Loader.isModLoaded("immersiverailroading");
    }
}
