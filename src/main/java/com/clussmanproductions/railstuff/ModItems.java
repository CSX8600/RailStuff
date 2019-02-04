package com.clussmanproductions.railstuff;

import com.clussmanproductions.railstuff.item.ItemPaperwork;
import com.clussmanproductions.railstuff.item.ItemRollingStockAssigner;
import com.clussmanproductions.railstuff.item.ItemSignal;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("railstuff")
public class ModItems {
	@ObjectHolder("rolling_stock_assigner")
	public static ItemRollingStockAssigner rolling_stock_assigner;
	@ObjectHolder("paperwork")
	public static ItemPaperwork paperwork;
	@ObjectHolder("signal")
	public static ItemSignal signal;
	
	public static void initModels()
	{
		if (Loader.isModLoaded("immersiverailroading"))
		{
			rolling_stock_assigner.initModel();
			paperwork.initModel();
		}
		
		signal.initModel();
	}
}
