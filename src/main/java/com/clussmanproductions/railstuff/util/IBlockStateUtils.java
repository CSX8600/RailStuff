package com.clussmanproductions.railstuff.util;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class IBlockStateUtils {
	public static IBlockState clone(IBlockState originalBlockState)
	{
		Block block = originalBlockState.getBlock();
		IBlockState newState = block.getDefaultState();
		for(IProperty property : originalBlockState.getPropertyKeys())
		{
			newState = newState.withProperty(property, originalBlockState.getValue(property));
		}
		
		return newState;
	}
}
