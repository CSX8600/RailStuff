package com.clussmanproductions.railstuff.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cam72cam.mod.block.tile.TileEntity;
import cam72cam.mod.entity.ModdedEntity;
import cam72cam.mod.math.Vec3i;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import trackapi.lib.ITrack;
import trackapi.lib.Util;

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

				ITrack tile = Util.getTileEntity(world, new Vec3d(workingPos), false);;
				if (tile == null)
				{
					continue;
				}

				Vec3d current = new Vec3d(workingPos.getX(), workingPos.getY(), workingPos.getZ());

				Vec3d center = tile.getNextPosition(current, new Vec3d(0, 0, 0));

				retVal = new Vec3d(center.x, center.y, center.z);
				break;
			}
			
			workingPos = new BlockPos(currentPos.getX(), workingPos.getY() + 1, currentPos.getZ());
		}
		
		return retVal;
	}
	
	public static Vec3d getNextPosition(Vec3d currentPosition, Vec3d motion, World world, SignalTileEntity.LastSwitchInfo lastSwitchInfo)
	{
		BlockPos currentBlockPos = new BlockPos(currentPosition.x, currentPosition.y, currentPosition.z);
		ITrack te = Util.getTileEntity(world, new Vec3d(currentBlockPos), false);
		
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

			te = Util.getTileEntity(world, new Vec3d(currentBlockPos), false);
			attempt++;
		}
		
		if (te == null)
		{
			return currentPosition;
		}


		if (te instanceof TileEntity && ((TileEntity) te).instance() instanceof TileRailBase) {
			
			TileRailBase currentRailBase = (TileRailBase)((TileEntity)te).instance();
			
			TileRail switchTile = currentRailBase.findSwitchParent(currentRailBase);
			
			if (switchTile != null)
			{
				// Skip if we came from facing point - no need to check it since we're moving in the facing-point direction
				if (lastSwitchInfo.lastSwitchPlacementPosition == null || !lastSwitchInfo.lastSwitchPlacementPosition.equals(switchTile.info.placementInfo.placementPosition.internal))
				{
					// We're in here because this is either the first time we've encountered a switch
					// or we encountered a new chained-switch.
					// First, we need to see if we're at the facing point of the switch
					if (currentPosition.distanceTo(switchTile.info.placementInfo.placementPosition.internal) <= 0.5)
					{
						// We are at the facing point.  Set last placement info
						// to indicate we've reached it
						lastSwitchInfo.lastSwitchPlacementPosition = switchTile.info.placementInfo.placementPosition.internal;
					}
					else
					{
						// We are at the trailing point.  Now we need to see if
						// the switch is properly lined for our movement.
						
						IIterableTrack builder = (IIterableTrack)switchTile.info.getBuilder();
						
						boolean isOnStraight = builder.isOnTrack(switchTile.info, new cam72cam.mod.math.Vec3d(currentPosition));
						
						Vec3d placementPosition = switchTile.info.placementInfo.placementPosition.internal;
						ITrack placementITrack = Util.getTileEntity(world, placementPosition, false);
						
						if (placementITrack == null || !(placementITrack instanceof TileEntity) || !(((TileEntity)placementITrack).instance() instanceof TileRailBase))
						{
							// Can't determine switch state
							// Assume wrong
							return currentPosition;
						}
						
						TileRail placementTileEntity = ((TileRailBase)((TileEntity)placementITrack).instance()).getParentTile();
						
						if((placementTileEntity.info.switchState == SwitchState.STRAIGHT && !isOnStraight) ||
							(placementTileEntity.info.switchState == SwitchState.TURN && isOnStraight))
						{
							// This switch is not correctly lined for movement
							// Stopping here
							return currentPosition;
						}
						else
						{
							// This switch IS correctly lined
							// Mark this switch okay for next time
							lastSwitchInfo.lastSwitchPlacementPosition = placementPosition;
						}
					}
				}
			}
			else
			{
				lastSwitchInfo.lastSwitchPlacementPosition = null;
			}
		}
		
		return te.getNextPosition(currentPosition, motion);
	}
	
	public static boolean hasStockNearby(Vec3d currentPosition, World world)
	{
		BlockPos currentBlockPos = new BlockPos(currentPosition.x, currentPosition.y, currentPosition.z);

		AxisAlignedBB bb = new AxisAlignedBB(currentBlockPos.south().west(), currentBlockPos.up(3).east().north());
		List<EntityMoveableRollingStock> stocks = world.getEntitiesWithinAABB(ModdedEntity.class, bb)
				.stream()
				.map(x -> x.getSelf() instanceof EntityMoveableRollingStock ? (EntityMoveableRollingStock)x.getSelf() : null)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		return !stocks.isEmpty();
	}
}
