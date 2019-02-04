package com.clussmanproductions.railstuff.item;

import com.clussmanproductions.railstuff.ModRailStuff;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ItemRollingStockAssigner extends Item {
	public ItemRollingStockAssigner()
	{
		setRegistryName("rolling_stock_assigner");
		setUnlocalizedName(ModRailStuff.MODID + ".rolling_stock_assigner");
		setCreativeTab(ModRailStuff.CREATIVE_TAB);
	}
	
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
