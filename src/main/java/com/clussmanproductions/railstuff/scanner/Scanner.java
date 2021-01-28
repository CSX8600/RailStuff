package com.clussmanproductions.railstuff.scanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.clussmanproductions.railstuff.Config;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.tile.SignalTileEntity.LastSwitchInfo;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;
import com.clussmanproductions.railstuff.util.Tuple;

import cam72cam.immersiverailroading.entity.EntityMoveableRollingStock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Scanner
{
	private ScannerData _data;
	public static HashMap<Integer, Scanner> ScannersByWorld = new HashMap<Integer, Scanner>();
	
	private ScanSession[] scansInProgress;
	private HashSet<IScannerSubscriber> requestsHandledThisTick = new HashSet<>();
	private int lastIndex = 0;
	
	public Scanner(World world)
	{
		super();
		_data = (ScannerData)world.loadData(ScannerData.class, "RS_scanner_data");
		if (_data == null)
		{
			_data = new ScannerData();
			world.setData(_data.mapName, _data);
		}
		
		scansInProgress = new ScanSession[Config.parallelScans];
		for(int i = 0; i < Config.parallelScans; i++)
		{
			scansInProgress[i] = new ScanSession();
		}
	}
	
	public <T extends TileEntity & IScannerSubscriber> void subscribe(T subscriber)
	{
		_data.addSubscriber(subscriber.getPos());
	}
	
	public void tick(World world) {
		try
		{			
			if (_data.getSubscribers().size() == 0)
			{
				for(ScanSession scanSession : scansInProgress)
				{
					scanSession.setScanSubscriber(null);
				}
				return;
			}
			
			for(ScanSession scanSession : scansInProgress)
			{
				if (scanSession.getScanSubscriber() == null)
				{
					tryFindNextSubscriber(scanSession, world);
				}
				
				if (scanSession.getScanRequest() == null)
				{
					scanSession.setScanSubscriber(null);
					continue;
				}
				
				ScanRequest request = scanSession.getScanRequest();
				while(scanSession.getBlocksScannedThisSession() < Config.signalDistanceTimeout && scanSession.getBlocksScannedThisTick() < Config.signalDistanceTick && request != null)
				{
					if (!scanSession.isScanSubscriberLoaded(world))
					{
						tryFindNextSubscriber(scanSession, world);
						request = scanSession.getScanRequest();
						
						continue;
					}
					
					Vec3d lastPosition = scanSession.getLastPosition();
					Vec3d motion = scanSession.getMotion();
					if (lastPosition == null)
					{
						lastPosition = new Vec3d(request.getStartingPos());
						motion = new Vec3d(request.getStartDirection().getDirectionVec());
					}
					
					Vec3d nextPosition = ImmersiveRailroadingHelper.getNextPosition(lastPosition, motion, world, scanSession.lastSwitchInfo);
					scanSession.addBlockScannedThisTick();
					if (nextPosition.equals(lastPosition))
					{
						ScanCompleteData completeData = new ScanCompleteData(request, true, false, false, "Bad switch or end of track at " + vec3dToString(nextPosition));
						scanSession.getScanSubscriber().onScanComplete(completeData);
						scanSession.popRequest();
						
						if (scanSession.getScanRequest() == null)
						{
							tryFindNextSubscriber(scanSession, world);
						}

						request = scanSession.getScanRequest();
						continue;
					}
					
					motion = new Vec3d(nextPosition.x - lastPosition.x,
									   nextPosition.y - lastPosition.y,
									   nextPosition.z - lastPosition.z);
					
					Tuple<Boolean, Boolean> trainResultHere = checkPosition(nextPosition, motion, world);
					if (trainResultHere.getFirst())
					{
						ScanCompleteData completeData = new ScanCompleteData(request, false, trainResultHere.getFirst(), trainResultHere.getSecond(), "Found a train at " + vec3dToString(nextPosition));
						scanSession.getScanSubscriber().onScanComplete(completeData);
						scanSession.popRequest();
						
						if (scanSession.getScanRequest() == null)
						{
							tryFindNextSubscriber(scanSession, world);
						}
						
						request = scanSession.getScanRequest();
						continue;
					}
					
					AxisAlignedBB endingBB = new AxisAlignedBB(request.getEndingPos());
					endingBB = endingBB.expand(-1, -1, -1).expand(1, 1, 1);
					
					if (endingBB.contains(nextPosition))
					{
						ScanCompleteData data = new ScanCompleteData(request, false, false, false, "Found end point at " + vec3dToString(nextPosition));
						scanSession.getScanSubscriber().onScanComplete(data);
						scanSession.popRequest();
						
						if (data.getScanRequest() == null)
						{
							tryFindNextSubscriber(scanSession, world);
						}
						
						request = scanSession.getScanRequest();
						continue;
					}
					
					scanSession.setLastPosition(nextPosition);
					scanSession.setMotion(motion);
				}
				
				if (scanSession.getBlocksScannedThisSession() >= Config.signalDistanceTimeout)
				{
					if (!scanSession.isScanSubscriberLoaded(world))
					{
						tryFindNextSubscriber(scanSession, world);
						continue;
					}
					
					ScanCompleteData timeout = new ScanCompleteData(request, true, false, false, "Signal timed out at " + vec3dToString(scanSession.lastPosition));
					scanSession.getScanSubscriber().onScanComplete(timeout);
					scanSession.popRequest();
					
					if (scanSession.getScanRequest() == null)
					{
						tryFindNextSubscriber(scanSession, world);
					}
				}
			}
			
			// Cleanup tick-based variables
			requestsHandledThisTick.clear();
			for(ScanSession scanSession : scansInProgress)
			{
				scanSession.resetBlocksScannedThisTick();
			}
		}
		catch(Exception ex) // Something went wrong - report it and try again
		{
			ModRailStuff.logger.error(ex);
		} 
	}
	
	private void tryFindNextSubscriber(ScanSession scan, World world)
	{
		lastIndex++;
		if (lastIndex >= _data.getSubscribers().size())
		{
			lastIndex = 0;
		}
		
		IScannerSubscriber thisScanSubscriber = null;
		do
		{
			BlockPos subscriberPos = _data.getSubscribers().get(lastIndex);
			if (world.isBlockLoaded(subscriberPos, false))
			{
				TileEntity te = world.getTileEntity(subscriberPos);
				if (IScannerSubscriber.class.isAssignableFrom(te.getClass()))
				{
					thisScanSubscriber = (IScannerSubscriber)te;
					
					if (thisScanSubscriber.getScanRequests().size() != 0)
					{
						final IScannerSubscriber finalThisScanSubscriber = thisScanSubscriber;
						if (!requestsHandledThisTick.add(thisScanSubscriber) || Arrays.stream(scansInProgress).anyMatch(ss -> ss.getScanSubscriber() == finalThisScanSubscriber))
						{
							thisScanSubscriber = null;
						}
					}
				}
			}
			
			if (thisScanSubscriber == null)
			{
				lastIndex++;
			}
		}
		while(lastIndex < _data.getSubscribers().size() && thisScanSubscriber == null);
		
		scan.setScanSubscriber(thisScanSubscriber);
	}
	
	private Tuple<Boolean, Boolean> checkPosition(Vec3d position, Vec3d motion, World world)
	{
		List<EntityMoveableRollingStock> moveableRollingStockNearby = ImmersiveRailroadingHelper.hasStockNearby(position, world);
		if (!moveableRollingStockNearby.isEmpty())
		{
			EntityMoveableRollingStock stock = moveableRollingStockNearby.get(0);
			Vec3d stockVelocity = stock.getVelocity().internal();
			EnumFacing stockMovementFacing = EnumFacing.getFacingFromVector((float)stockVelocity.x, (float)stockVelocity.y, (float)stockVelocity.z);
			EnumFacing motionFacing = EnumFacing.getFacingFromVector((float)motion.x, (float)motion.y, (float)motion.z);
			
			boolean trainMovingTowardsDestination = motionFacing.equals(stockMovementFacing);
			return new Tuple<Boolean, Boolean>(true, trainMovingTowardsDestination);
		}
		
		return new Tuple<Boolean, Boolean>(false, false);
	}

	private static class ScanSession
	{
		private IScannerSubscriber scanSubscriber = null;
		private Queue<ScanRequest> scanRequestsToDo;
		private int blocksScannedThisTick = 0;
		private int blocksScannedThisSession = 0;
		private Vec3d lastPosition = null;
		private Vec3d motion = null;
		private LastSwitchInfo lastSwitchInfo = new LastSwitchInfo();
		
		public IScannerSubscriber getScanSubscriber() {
			return scanSubscriber;
		}
		
		public void setScanSubscriber(IScannerSubscriber scanSubscriber) {
			this.scanSubscriber = scanSubscriber;
			
			if (this.scanSubscriber != null)
			{
				scanRequestsToDo = new LinkedList<>(scanSubscriber.getScanRequests());
			}
			else
			{
				scanRequestsToDo = new LinkedList<>();
			}
			
			lastPosition = null;
			motion = null;
			blocksScannedThisSession = 0;
		}
		
		public boolean isScanSubscriberLoaded(World world)
		{
			if (scanSubscriber == null)
			{
				return false;
			}
			
			TileEntity subscriberTE = (TileEntity)scanSubscriber;
			return world.isBlockLoaded(subscriberTE.getPos(), false);
		}
		
		public ScanRequest getScanRequest()
		{
			return scanRequestsToDo.peek();
		}
		
		public void popRequest()
		{
			scanRequestsToDo.poll();
			lastPosition = null;
			motion = null;
			blocksScannedThisSession = 0;
		}
		
		public int getBlocksScannedThisTick()
		{
			return blocksScannedThisTick;
		}
		
		public void addBlockScannedThisTick()
		{
			blocksScannedThisTick++;
			blocksScannedThisSession++;
		}
		
		public void resetBlocksScannedThisTick()
		{
			blocksScannedThisTick = 0;
		}
		
		public int getBlocksScannedThisSession()
		{
			return blocksScannedThisSession;
		}
		
		public Vec3d getLastPosition() {
			return lastPosition;
		}

		public void setLastPosition(Vec3d lastPosition) {
			this.lastPosition = lastPosition;
		}
		
		public Vec3d getMotion() {
			return motion;
		}

		public void setMotion(Vec3d motion) {
			this.motion = motion;
		}

		public LastSwitchInfo getLastSwitchInfo() {
			return lastSwitchInfo;
		}

		public void setLastSwitchInfo(LastSwitchInfo lastSwitchInfo) {
			this.lastSwitchInfo = lastSwitchInfo;
		}
	}

	private String vec3dToString(Vec3d vec)
	{
		StringBuilder builder = new StringBuilder("[");
		
		String formattedString = Double.toString(vec.x);
		if (formattedString.contains(".") && formattedString.substring(formattedString.lastIndexOf(".")).length() > 3)
		{
			formattedString = formattedString.substring(0, formattedString.lastIndexOf(".") + 3);
		}
		builder.append(formattedString + ", ");
		
		formattedString = Double.toString(vec.y);
		if (formattedString.contains(".") && formattedString.substring(formattedString.lastIndexOf(".")).length() > 3)
		{
			formattedString = formattedString.substring(0, formattedString.lastIndexOf(".") + 3);
		}
		builder.append(formattedString + ", ");
		
		formattedString = Double.toString(vec.z);
		if (formattedString.contains(".") && formattedString.substring(formattedString.lastIndexOf(".")).length() > 3)
		{
			formattedString = formattedString.substring(0, formattedString.lastIndexOf(".") + 3);
		}
		builder.append(formattedString + "]");
		
		return builder.toString();
	}
}
