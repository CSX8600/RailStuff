package com.clussmanproductions.railstuff.scanner;

import java.util.HashMap;
import java.util.List;

import com.clussmanproductions.railstuff.Config;
import com.clussmanproductions.railstuff.tile.SignalTileEntity.LastSwitchInfo;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;

import cam72cam.immersiverailroading.entity.EntityMoveableRollingStock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ScannerThread extends Thread
{
	public static HashMap<World, ScannerThread> ThreadsByWorld = new HashMap<World, ScannerThread>();
	
	private World _world;
	private boolean _stop = false;
	private ScannerData _data;
	public ScannerThread(World world)
	{
		super();
		_world = world;
	}
	
	public void requestStop()
	{
		_stop = true;
	}
	
	public <T extends TileEntity & IScannerSubscriber> void subscribe(T subscriber)
	{
		_data.addSubscriber(subscriber.getPos());
	}
	
	@Override
	public void run() {
		_data = (ScannerData)_world.loadData(ScannerData.class, "RS_scanner_data");
		if (_data == null)
		{
			_data = new ScannerData();
			_world.setData(_data.mapName, _data);
		}
		
		while(true)
		{
			if (_stop)
			{
				return;
			}
			
			for(BlockPos pos : _data.getSubscribers())
			{
				if (!_world.isBlockLoaded(pos))
				{
					continue;
				}
				
				TileEntity tileEntity = _world.getTileEntity(pos);
				if (!(tileEntity instanceof IScannerSubscriber))
				{
					_data.removeSubscriber(pos);
					continue;
				}
				
				IScannerSubscriber subscriber = (IScannerSubscriber)tileEntity;
				for(ScanRequest req : subscriber.getScanRequests())
				{
					if (_stop)
					{
						return;
					}
					
					ScanCompleteData data = performScan(req);
					
					if (_stop)
					{
						return;
					}
					
					if (!_world.isBlockLoaded(pos))
					{
						break;
					}
					
					subscriber.onScanComplete(data);
				}
			}
		}
	}
	
	private ScanCompleteData performScan(ScanRequest req)
	{
		boolean foundTrain;
		boolean trainMovingTowardsDestination;
		
		Vec3d currentPosition = new Vec3d(req.getStartingPos().getX(), req.getStartingPos().getY(), req.getStartingPos().getZ());
		Vec3d motion = new Vec3d(req.getStartDirection().getDirectionVec());
		
		ScanCompleteData earlyData = checkPosition(req, currentPosition, motion);
		if (earlyData != null)
		{
			return earlyData;
		}

		LastSwitchInfo lastSwitchInfo = new LastSwitchInfo();
		BlockPos endingPosition = req.getEndingPos();
		Vec3d endingVec = new Vec3d(endingPosition.getX(), endingPosition.getY(), endingPosition.getZ());
		for(int i = 0; i < Config.signalDistanceTimeout; i++)
		{
			Vec3d nextPosition = ImmersiveRailroadingHelper.getNextPosition(currentPosition, motion, _world, lastSwitchInfo);
			
			if (nextPosition.equals(currentPosition) || endingPosition.equals(new BlockPos(nextPosition.x, nextPosition.y, nextPosition.z)))
			{
				return new ScanCompleteData(req, nextPosition.equals(currentPosition), false, false);
			}
			
			motion = new Vec3d(nextPosition.x - currentPosition.x,
								nextPosition.y - currentPosition.y,
								nextPosition.z - currentPosition.z);
			
			currentPosition = nextPosition;
			ScanCompleteData data = checkPosition(req, currentPosition, motion);
			if (data != null)
			{
				return data;
			}
		}
		
		return new ScanCompleteData(req, true, false, false);
	}
	
	private ScanCompleteData checkPosition(ScanRequest req, Vec3d position, Vec3d motion)
	{
		List<EntityMoveableRollingStock> moveableRollingStockNearby = ImmersiveRailroadingHelper.hasStockNearby(position, _world);
		if (!moveableRollingStockNearby.isEmpty())
		{
			EntityMoveableRollingStock stock = moveableRollingStockNearby.get(0);
			Vec3d stockVelocity = stock.getVelocity().internal;
			EnumFacing stockMovementFacing = EnumFacing.getFacingFromVector((float)stockVelocity.x, (float)stockVelocity.y, (float)stockVelocity.z);
			EnumFacing motionFacing = EnumFacing.getFacingFromVector((float)motion.x, (float)motion.y, (float)motion.z);
			
			boolean trainMovingTowardsDestination = motionFacing.equals(stockMovementFacing);
			return new ScanCompleteData(req, false, true, trainMovingTowardsDestination);
		}
		
		return null;
	}
}
