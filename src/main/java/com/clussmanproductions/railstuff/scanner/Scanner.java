package com.clussmanproductions.railstuff.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.clussmanproductions.railstuff.Config;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.tile.SignalTileEntity.LastSwitchInfo;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;
import com.clussmanproductions.railstuff.util.Tuple;
import com.google.common.collect.ImmutableList;

import cam72cam.immersiverailroading.entity.EntityMoveableRollingStock;
import cam72cam.immersiverailroading.tile.TileRailBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import trackapi.lib.ITrack;
import trackapi.lib.Util;

public class Scanner
{	
	private World _world;
	private ScannerData _data;
	public static HashMap<World, Scanner> ScannersByWorld = new HashMap<World, Scanner>();
	
	private Vec3d lastPosition = null;
	private Vec3d lastMotion = null;
	private IScannerSubscriber lastSubscriber = null;
	private ScanRequest lastRequest = null;
	private int blocksScannedThisRequest = 0;
	private LastSwitchInfo lastSwitchInfoThisRequest = null;
	private int blocksScannedThisTick = 0;	
	
	public Scanner(World world)
	{
		super();
		_world = world;
		_data = (ScannerData)_world.loadData(ScannerData.class, "RS_scanner_data");
		if (_data == null)
		{
			_data = new ScannerData();
			_world.setData(_data.mapName, _data);
		}
	}
	
	public <T extends TileEntity & IScannerSubscriber> void subscribe(T subscriber)
	{
		_data.addSubscriber(subscriber.getPos());
	}
	
	public void tick() {
		try
		{
//			for(BlockPos pos : _data.getSubscribers())
//			{
//				if (!_world.isBlockLoaded(pos))
//				{
//					continue;
//				}
//				
//				TileEntity tileEntity = _world.getTileEntity(pos);
//				if (!(tileEntity instanceof IScannerSubscriber))
//				{
//					_data.removeSubscriber(pos);
//					continue;
//				}
//				
//				IScannerSubscriber subscriber = (IScannerSubscriber)tileEntity;
//				for(ScanRequest req : subscriber.getScanRequests())
//				{
//					
//					ScanCompleteData data = performScan(req);
//					
//					if (!_world.isBlockLoaded(pos))
//					{
//						break;
//					}
//					
//					subscriber.onScanComplete(data);
//				}
//			}
			
			blocksScannedThisTick = 0;
			
			int subscribersCheckedThisTick = 0;
			int subscriberTotal = _data.getSubscribers().size();
			
			while(blocksScannedThisTick < Config.signalDistanceTick && subscribersCheckedThisTick < subscriberTotal)
			{
				if (lastSubscriber == null)
				{
					// Get next first available subscriber
					lastSubscriber = getNextSubscriber(null);
				}
				
				if (lastSubscriber == null)
				{
					return;
				}
				
				List<ScanRequest> scannerRequests = lastSubscriber.getScanRequests();
				if (lastRequest == null && scannerRequests.size() > 0)
				{
					lastRequest = scannerRequests.get(0);
				}
				
				while(lastRequest != null)
				{
					ScanCompleteData data = performScan(lastRequest);
					
					if (data == null)
					{
						break;
					}
					
					lastPosition = null;
					lastMotion = null;
					lastSwitchInfoThisRequest = null;
					blocksScannedThisRequest = 0;
					lastSubscriber.onScanComplete(data);
					
					int nextRequestIndex = scannerRequests.indexOf(lastRequest) + 1;
					if (nextRequestIndex >= scannerRequests.size())
					{
						lastRequest = null;
					}
					else
					{
						lastRequest = scannerRequests.get(nextRequestIndex);
					}
				}
				
				if (lastRequest == null)
				{
					lastSubscriber = getNextSubscriber(lastSubscriber);
				}
				
				subscribersCheckedThisTick++;
			}
		}
		catch(Exception ex) // Something went wrong - report it and try again
		{
			ex.printStackTrace();
		} 
	}
	
	private IScannerSubscriber getNextSubscriber(IScannerSubscriber lastSubscriber)
	{
		ImmutableList<BlockPos> scannerPoses = _data.getSubscribers();
		if (scannerPoses.size() == 0)
		{
			return null;
		}
		
		BlockPos lastSubscriberPos = null;
		if (lastSubscriber != null)
		{
			lastSubscriberPos = ((TileEntity)lastSubscriber).getPos();
		}
		
		int lastIndex = scannerPoses.indexOf(lastSubscriberPos);		
		int nextIndex = lastIndex + 1;
		if (nextIndex >= scannerPoses.size())
		{
			nextIndex = 0;
		}
		
		IScannerSubscriber retVal = null;
		while(nextIndex != lastIndex)
		{
			BlockPos nextPos = scannerPoses.get(nextIndex);
			if (nextPos != null && _world.isBlockLoaded(nextPos))
			{
				TileEntity teAtPos = _world.getTileEntity(nextPos);
				if (teAtPos == null || teAtPos.isInvalid() || !(teAtPos instanceof IScannerSubscriber))
				{
					_data.removeSubscriber(nextPos);
				}
				else
				{
					retVal = (IScannerSubscriber)teAtPos;
					break;
				}
			}
			
			nextIndex++;
			if (nextIndex >= scannerPoses.size())
			{
				nextIndex = 0;
			}
			
			// Special check in case lastSubscriber passed in is null
			if (nextIndex == 0 && lastIndex == -1)
			{
				break;
			}
		}
		
		return retVal;
	}
	
	private ScanCompleteData performScan(ScanRequest req)
	{
		Vec3d currentPosition = lastPosition != null ? lastPosition : new Vec3d(req.getStartingPos().getX(), req.getStartingPos().getY(), req.getStartingPos().getZ());
		Vec3d motion = lastMotion != null ? lastMotion : new Vec3d(req.getStartDirection().getDirectionVec());
		LastSwitchInfo lastSwitchInfo = lastSwitchInfoThisRequest != null ? lastSwitchInfoThisRequest : new LastSwitchInfo();
		
		while(blocksScannedThisTick < Config.signalDistanceTick)
		{					
			Vec3d nextPosition = ImmersiveRailroadingHelper.getNextPosition(currentPosition, motion, _world, lastSwitchInfo);
			
			if (nextPosition.equals(currentPosition))
			{
				return new ScanCompleteData(req, true, false, false);
			}
			
			motion = new Vec3d(nextPosition.x - currentPosition.x,
								nextPosition.y - currentPosition.y,
								nextPosition.z - currentPosition.z);
			
			currentPosition = nextPosition;
			
			ScanCompleteData positionCheck = checkPosition(req, currentPosition, motion);
			if (positionCheck != null)
			{
				return positionCheck;
			}
			
			if (req.getEndingPos().equals(new BlockPos(currentPosition.x, currentPosition.y, currentPosition.z)))
			{
				return new ScanCompleteData(req, false, false, false);
			}
			
			blocksScannedThisTick++;
			blocksScannedThisRequest++;
			
			if (blocksScannedThisRequest >= Config.signalDistanceTimeout)
			{
				return new ScanCompleteData(req, true, false, false);
			}
		}
		
		lastPosition = currentPosition;
		lastMotion = motion;
		lastSwitchInfoThisRequest = lastSwitchInfo;
		return null;
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
