package com.clussmanproductions.railstuff.util;

import com.clussmanproductions.railstuff.tile.SignalTileEntity;

import cam72cam.immersiverailroading.blocks.BlockRailBase;
import cam72cam.immersiverailroading.library.SwitchState;
import cam72cam.immersiverailroading.library.TrackItems;
import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.tile.TileRailBase;
import cam72cam.immersiverailroading.tile.TileRailGag;
import cam72cam.immersiverailroading.track.BuilderSwitch;
import cam72cam.immersiverailroading.util.SwitchUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ImmersiveRailroadingHelper {
	public static BlockPos findOrigin(BlockPos currentPos, EnumFacing signalFacing, World world)
	{
		BlockPos retVal = new BlockPos(0, -1, 0);
		
		EnumFacing searchDirection = signalFacing.rotateY().rotateY().rotateY();
		
		BlockPos workingPos = new BlockPos(currentPos.getX(), currentPos.getY(), currentPos.getZ());
		for(int i = 0; i <= 10; i++)
		{
			workingPos = workingPos.offset(searchDirection);
			
			if (world.getBlockState(workingPos).getBlock() instanceof BlockRailBase)
			{
				TileRailBase tile = (TileRailBase)world.getTileEntity(workingPos);
				if (tile == null)
				{
					continue;
				}
				
				Vec3d current = new Vec3d(workingPos.getX(), workingPos.getY(), workingPos.getZ());
				
				Vec3d center = tile.getNextPosition(current, new Vec3d(0, 0, 0));
				
				retVal = new BlockPos(center.x, center.y, center.z);
				break;
			}
		}
		
		return retVal;
	}
	
	public static Vec3d getNextPosition(Vec3d currentPosition, Vec3d motion, World world, SignalTileEntity.LastSwitchInfo lastSwitchInfo)
	{
		BlockPos currentBlockPos = new BlockPos(currentPosition.x, currentPosition.y, currentPosition.z);
		TileEntity te = world.getTileEntity(currentBlockPos);
		if (te == null || !(te instanceof TileRailBase))
		{
			return currentPosition;
		}
		
		TileRailBase railBase = (TileRailBase)te;
		
		TileRail railParent = null;
		TileRail rail = null;
		
		if (railBase instanceof TileRail)
		{
			railParent = railBase.getParentTile();
			rail = (TileRail)railBase;
		}
		
		if (rail != null)
		{
			if (railParent.info.settings.type != TrackItems.SWITCH)
			{
				lastSwitchInfo.lastSwitchPlacementPosition = null;
			}
			else if (lastSwitchInfo.lastSwitchPlacementPosition == null || !lastSwitchInfo.lastSwitchPlacementPosition.equals(railParent.info.placementInfo.placementPosition))
			{
				// This is a new switch we are encountering - check to make sure
				// the route is lined in a valid fashion
				
				if (currentPosition.distanceTo(railParent.info.placementInfo.placementPosition) <= 0.5)
				{
					// We are on the facing point of the switch...we're good to go
					lastSwitchInfo.lastSwitchPlacementPosition = rail.info.placementInfo.placementPosition;
				}
				else
				{
					// We are on the trailing point of the switch...we need to make sure we're lined
					BuilderSwitch builder = (BuilderSwitch)railParent.info.getBuilder();
					boolean isOnStraight = builder.isOnStraight(currentPosition);
					SwitchState switchState = SwitchUtil.getSwitchState(rail);
					
					if ((isOnStraight && switchState == SwitchState.TURN) ||
							(!isOnStraight && switchState == SwitchState.STRAIGHT))
					{
						// We're incorrectly lined, stop here
						return currentPosition;
					}
					
					if (switchState != SwitchState.NONE)
					{
						// We're correctly lined, ignore the rest of this switch
						lastSwitchInfo.lastSwitchPlacementPosition = railParent.info.placementInfo.placementPosition;
					}
				}
			}
		}
		
		return railBase.getNextPosition(currentPosition, motion);
	}
	
	public static boolean hasStockNearby(Vec3d currentPosition, World world)
	{
		BlockPos currentBlockPos = new BlockPos(currentPosition.x, currentPosition.y, currentPosition.z);
		TileEntity te = world.getTileEntity(currentBlockPos);
		if (te == null || !(te instanceof TileRailBase))
		{
			return false;
		}
		
		TileRailBase rail = (TileRailBase)te;
		return rail.getStockNearBy(null) != null;
	}

	public static boolean blockPosIsRail(BlockPos pos, World world)
	{
		return world.getBlockState(pos).getBlock() instanceof BlockRailBase;
	}
}
