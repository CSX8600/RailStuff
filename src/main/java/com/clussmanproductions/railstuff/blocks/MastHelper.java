package com.clussmanproductions.railstuff.blocks;

import com.clussmanproductions.railstuff.ModRailStuff;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class MastHelper {
	public static AxisAlignedBB getBoundingBox(EnumFacing facing)
	{
		switch(facing)
		{
			case NORTH:
				return new AxisAlignedBB(0.375, 0, 0.25, 0.625, 1.0, 0.0);	
			case SOUTH:
				return new AxisAlignedBB(0.375, 0, 0.75, 0.625, 1.0, 1.0);
			case EAST:
				return new AxisAlignedBB(0.75, 0, 0.375, 1.0, 1.0, 0.625);
			case WEST:
				return new AxisAlignedBB(0.25, 0, 0.375, 0.0, 1.0, 0.625);
			default:
				return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		}
	}
}
