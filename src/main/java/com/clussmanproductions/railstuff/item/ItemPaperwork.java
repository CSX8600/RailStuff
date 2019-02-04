package com.clussmanproductions.railstuff.item;

import com.clussmanproductions.railstuff.ModRailStuff;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemPaperwork extends Item {
	public ItemPaperwork()
	{
		setRegistryName("paperwork");
		setUnlocalizedName(ModRailStuff.MODID + ".paperwork");
		setCreativeTab(ModRailStuff.CREATIVE_TAB);
	}
	
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
