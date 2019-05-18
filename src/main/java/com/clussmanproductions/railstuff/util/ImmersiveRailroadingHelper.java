package com.clussmanproductions.railstuff.util;

import java.util.List;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;

import cam72cam.immersiverailroading.blocks.BlockRailBase;
import cam72cam.immersiverailroading.entity.EntityMoveableRollingStock;
import cam72cam.immersiverailroading.library.SwitchState;
import cam72cam.immersiverailroading.library.TrackItems;
import cam72cam.immersiverailroading.tile.TileRail;
import cam72cam.immersiverailroading.tile.TileRailBase;
import cam72cam.immersiverailroading.tile.TileRailGag;
import cam72cam.immersiverailroading.track.BuilderSwitch;
import cam72cam.immersiverailroading.track.IIterableTrack;
import cam72cam.immersiverailroading.util.SwitchUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ImmersiveRailroadingHelper {
	public static Vec3d findOrigin(BlockPos currentPos, EnumFacing signalFacing, World world)
	{
		Vec3d retVal = new Vec3d(0, -1, 0);
		
		EnumFacing searchDirection = signalFacing.rotateY().rotateY().rotateY();
		
		BlockPos workingPos = new BlockPos(currentPos.getX(), currentPos.getY() - 3, currentPos.getZ());
		for(int y = 0; y < 6; y++)
		{
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
					
					retVal = new Vec3d(center.x, center.y, center.z);
					break;
				}
			}
			
			workingPos = new BlockPos(currentPos.getX(), workingPos.getY() + 1, currentPos.getZ());
		}
		
		return retVal;
	}
	
	public static Vec3d getNextPosition(Vec3d currentPosition, Vec3d motion, World world, SignalTileEntity.LastSwitchInfo lastSwitchInfo)
	{
		BlockPos currentBlockPos = new BlockPos(currentPosition.x, currentPosition.y, currentPosition.z);
		TileEntity te = world.getTileEntity(currentBlockPos);
		
		int attempt = 0;
		while(te == null && attempt < 8)
		{
			switch(attempt)
			{
				case 0:
					currentBlockPos = currentBlockPos.up();
					break;
				case 1:
					currentBlockPos = currentBlockPos.down(2);
					break;
				case 2:
					currentBlockPos = currentBlockPos.up();
					EnumFacing direction = EnumFacing.getFacingFromVector((float)motion.x, (float)motion.y, (float)motion.z).rotateY();
					currentBlockPos = currentBlockPos.offset(direction);
					break;
				case 3:
					direction = EnumFacing.getFacingFromVector((float)motion.x, (float)motion.y, (float)motion.z).rotateY().rotateY().rotateY();
					currentBlockPos = currentBlockPos.offset(direction, 2);
					break;
				case 4:
					currentBlockPos = currentBlockPos.up();
					break;
				case 5:
					currentBlockPos = currentBlockPos.down(2);
					break;
				case 6:
					direction = EnumFacing.getFacingFromVector((float)motion.x, (float)motion.y, (float)motion.z).rotateY();
					currentBlockPos = currentBlockPos.offset(direction, 2);
					break;
				case 7:
					currentBlockPos = currentBlockPos.up(2);
					break;
			}
			
			te = world.getTileEntity(currentBlockPos);
			attempt++;
		}
		
		if (te == null)
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
					IIterableTrack switchBuilder = (IIterableTrack)railParent.info.getBuilder();
					
					boolean isOnStraight = switchBuilder.isOnTrack(railParent.info, currentPosition);
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
//		TileEntity te = world.getTileEntity(currentBlockPos);
//		if (te == null || !(te instanceof TileRailBase))
//		{
//			ModRailStuff.logger.info("Could not find rail");
//			return false;
//		}
//		
//		TileRailBase rail = (TileRailBase)te;
//		return rail.getStockNearBy(null) != null;
		
		AxisAlignedBB bb = new AxisAlignedBB(currentBlockPos.south().west(), currentBlockPos.up(3).east().north());
		List<EntityMoveableRollingStock> stocks = world.getEntitiesWithinAABB(EntityMoveableRollingStock.class, bb);
		
		return !stocks.isEmpty();
	}

	public static boolean blockPosIsRail(BlockPos pos, World world)
	{
		return world.getBlockState(pos).getBlock() instanceof BlockRailBase;
	}
}
