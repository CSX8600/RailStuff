package com.clussmanproductions.railstuff.util;

import net.minecraft.util.math.BlockPos;

public class BlockPosUtils {
	public static int[] asIntArray(BlockPos blockPos)
	{
		if (blockPos != null)
		{
			return new int[] { blockPos.getX(), blockPos.getY(), blockPos.getZ() };
		}
		
		return new int[] { 0, -1, 0 };
	}
	
	public static BlockPos asBlockPos(int[] array)
	{
		if (array.length != 3)
		{
			return new BlockPos(0, -1, 0);
		}
		
		return new BlockPos(array[0], array[1], array[2]);
	}
}
