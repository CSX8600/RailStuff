package com.clussmanproductions.railstuff.tile;

import java.util.HashMap;
import java.util.Map;

import com.clussmanproductions.railstuff.Config;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.BlockMast;
import com.clussmanproductions.railstuff.blocks.BlockMastFake;
import com.clussmanproductions.railstuff.blocks.BlockSignalHead;
import com.clussmanproductions.railstuff.data.OccupationEndPointData;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SignalTileEntity extends TileEntitySyncable implements ITickable {

	private boolean isMaster;
	private int masterX;
	private int masterY;
	private int masterZ;
	private int signalHeadX;
	private int signalHeadY;
	private int signalHeadZ;
	private boolean isBreaking;
	private Mode mode = Mode.Manual;
	private Aspect unpoweredAspect = Aspect.Dark;
	private Aspect poweredAspect = Aspect.Dark;
	private String name = "";
	private boolean hasUpdatedBlockState = true;
	private int flashTimer = 0;
	
	// Occupation mode members
	OccupationEndPointData endPointData;
	private OccupationModeState occupationModeState = OccupationModeState.Initialization;
	private int occupationOriginX = 0;
	private int occupationOriginY = -1;
	private int occupationOriginZ = 0;
	private int blocksTraveled = 0;
	private Vec3d lastLocation;
	private double lastMotionX = 0;
	private double lastMotionY = 0;
	private double lastMotionZ = 0;
	private HashMap<BlockPos, String> registeredEndPoints = new HashMap<BlockPos, String>();
	private LastSwitchInfo lastSwitchInfo = new LastSwitchInfo();
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("isMaster", isMaster);
		compound.setInteger("masterX", masterX);
		compound.setInteger("masterY", masterY);
		compound.setInteger("masterZ", masterZ);
		compound.setInteger("signalHeadX", signalHeadX);
		compound.setInteger("signalHeadY", signalHeadY);
		compound.setInteger("signalHeadZ", signalHeadZ);
		compound.setInteger("mode", mode.index);
		compound.setInteger("unpoweredAspect", unpoweredAspect.index);
		compound.setInteger("poweredAspect", poweredAspect.index);
		compound.setString("name", name);
		compound.setInteger("occupationOriginX", occupationOriginX);
		compound.setInteger("occupationOriginY", occupationOriginY);
		compound.setInteger("occupationOriginZ", occupationOriginZ);
		compound.setInteger("occupationModeState", occupationModeState.index);
		
		int endPointIndex = 0;
		for(BlockPos endPoint : registeredEndPoints.keySet())
		{
			String key = "endpoint" + endPointIndex + "_key";
			compound.setIntArray(key, new int[] { endPoint.getX(), endPoint.getY(), endPoint.getZ() });
			
			String value = "endpoint" + endPointIndex + "_value";
			compound.setString(value, registeredEndPoints.get(endPoint));
			
			endPointIndex++;
		}
		
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		isMaster = compound.getBoolean("isMaster");
		masterX = compound.getInteger("masterX");
		masterY = compound.getInteger("masterY");
		masterZ = compound.getInteger("masterZ");
		signalHeadX = compound.getInteger("signalHeadX");
		signalHeadY = compound.getInteger("signalHeadY");
		signalHeadZ = compound.getInteger("signalHeadZ");
		mode = Mode.get(compound.getInteger("mode"));
		unpoweredAspect = Aspect.get(compound.getInteger("unpoweredAspect"));
		poweredAspect = Aspect.get(compound.getInteger("poweredAspect"));
		name = compound.getString("name");
		occupationOriginX = compound.getInteger("occupationOriginX");
		occupationOriginY = compound.getInteger("occupationOriginY");
		occupationOriginZ = compound.getInteger("occupationOriginZ");
		occupationModeState = OccupationModeState.get(compound.getInteger("occupationModeState"));
		
		for(String key : compound.getKeySet())
		{
			if (!key.startsWith("endpoint"))
			{
				continue;
			}
			
			if (!key.endsWith("_key"))
			{
				continue;
			}
			
			int[] endpoint = compound.getIntArray(key);
			BlockPos elementKey = new BlockPos(endpoint[0], endpoint[1], endpoint[2]);
			
			String valueKey = key.substring(0, key.indexOf("_key")) + "_value";
			String elementValue = compound.getString(valueKey);
			
			registeredEndPoints.put(elementKey, elementValue);
		}
	}
	
	public void setMaster()
	{
		isMaster = true;
		markDirty();
	}
	
	public void setMasterLocation(BlockPos pos)
	{
		masterX = pos.getX();
		masterY = pos.getY();
		masterZ = pos.getZ();
		markDirty();
	}
	
	public void setSignalHeadLocation(BlockPos pos)
	{
		signalHeadX = pos.getX();
		signalHeadY = pos.getY();
		signalHeadZ = pos.getZ();
		
		markDirty();
	}
	
	public SignalTileEntity getMaster(World world)
	{
		if (isMaster)
		{
			return this;
		}
		
		BlockPos pos = new BlockPos(masterX, masterY, masterZ);
		TileEntity te = world.getTileEntity(pos);
		
		if (te != null && te instanceof SignalTileEntity)
		{
			return (SignalTileEntity)te;
		}
		
		return null;
	}
	
	/**
	 * Only call on master TE...if this is not master, it will be ignored
	 */
	public void onBreak(World world)
	{
		if (!isMaster)
		{
			return;
		}
		
		if (isBreaking)
		{
			return;
		}
		
		if (mode == Mode.Occupation)
		{
			getEndPointData().removeEndPoint(getOccupationOriginBlockPos());
		}
		
		isBreaking = true;		
		BlockPos workingPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
		
		boolean workingIsMaster = true;
		for(int i = 0; i < 5; i++)
		{
			world.destroyBlock(workingPos, workingIsMaster);
			workingIsMaster = false;
			workingPos = workingPos.up();
		}
	}

	
	public enum Mode
	{
		Manual(0),
		Occupation(1),
		Diamond(2);
		
		private int index;
		Mode(int index)
		{
			this.index = index;
		}
		
		public static Mode get(int index)
		{
			for(Mode mode : Mode.values())
			{
				if (mode.index == index)
				{
					return mode;
				}
			}
			
			return null;
		}
	}

	public Mode getMode()
	{
		return mode;
	}
	
	public enum Aspect
	{
		Dark(0),
		Red(1),
		RedFlashing(2),
		Yellow(3),
		YellowFlashing(4),
		Green(5);
		
		private int index;
		Aspect(int index)
		{
			this.index = index;
		}
		
		public static Aspect get(int index)
		{
			for (Aspect aspect : Aspect.values())
			{
				if (aspect.index == index)
				{
					return aspect;
				}
			}
			
			return null;
		}
	}
	
	public enum OccupationModeState
	{
		Initialization(0),
		Normal(1);
		
		private int index;
		OccupationModeState(int index)
		{
			this.index = index;
		}
		
		public static OccupationModeState get(int index)
		{
			for(OccupationModeState state : OccupationModeState.values())
			{
				if (state.index == index)
				{
					return state;
				}
			}
			
			return null;
		}
	}
	
	public Aspect getUnpoweredAspect()
	{
		return unpoweredAspect;
	}
	
	public Aspect getPoweredAspect()
	{
		return poweredAspect;
	}
	
	public void setUnpoweredAspect(Aspect aspect)
	{
		unpoweredAspect = aspect;
		markDirty();
	}
	
	public void setPoweredAspect(Aspect aspect)
	{
		poweredAspect = aspect;
		markDirty();
	}
	
	public String getName()
	{
		if (name != null)
		{
			return name;
		}
		
		return "";
	}
	
	public void setName(String name)
	{
		this.name = name;
		markDirty();
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag.setBoolean("isMaster", isMaster);
		tag.setInteger("masterX", masterX);
		tag.setInteger("masterY", masterY);
		tag.setInteger("masterZ", masterZ);
		tag.setInteger("signalHeadX", signalHeadX);
		tag.setInteger("signalHeadY", signalHeadY);
		tag.setInteger("signalHeadZ", signalHeadZ);
		tag.setInteger("mode", mode.index);
		tag.setInteger("unpoweredAspect", unpoweredAspect.index);
		tag.setInteger("poweredAspect", poweredAspect.index);
		tag.setString("name", name);
		tag.setInteger("occupationModeState", occupationModeState.index);
		
		int endpointIndex = 0;
		for(Map.Entry<BlockPos, String> kvp : registeredEndPoints.entrySet())
		{
			BlockPos key = kvp.getKey();
			tag.setIntArray("endpoint" + endpointIndex + "_key", new int[] { key.getX(), key.getY(), key.getZ() });
			tag.setString("endpoint" + endpointIndex + "_value", kvp.getValue());
			
			endpointIndex++;
		}
		
		return tag;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		isMaster = tag.getBoolean("isMaster");
		masterX = tag.getInteger("masterX");
		masterY = tag.getInteger("masterY");
		masterZ = tag.getInteger("masterZ");
		signalHeadX = tag.getInteger("signalHeadX");
		signalHeadY = tag.getInteger("signalHeadY");
		signalHeadZ = tag.getInteger("signalHeadZ");
		name = tag.getString("name");
		mode = Mode.get(tag.getInteger("mode"));
		unpoweredAspect = Aspect.get(tag.getInteger("unpoweredAspect"));
		poweredAspect = Aspect.get(tag.getInteger("poweredAspect"));
		occupationModeState = OccupationModeState.get(tag.getInteger("occupationModeState"));
		
		registeredEndPoints = new HashMap<BlockPos, String>();
		for(String key : tag.getKeySet())
		{
			if (!(key.startsWith("endpoint") && key.endsWith("_key")))
			{
				continue;
			}
			
			int[] endPointArray = tag.getIntArray(key);
			BlockPos endPoint = new BlockPos(endPointArray[0], endPointArray[1], endPointArray[2]);
			
			String valueKey = key.substring(0, key.indexOf("_key")) + "_value";
			String value = tag.getString(valueKey);
			
			registeredEndPoints.put(endPoint, value);
		}
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound getSyncPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("mode", mode.index);
		tag.setInteger("occupationModeState", occupationModeState.index);
		tag.setInteger("unpoweredAspect", unpoweredAspect.index);
		tag.setInteger("poweredAspect", poweredAspect.index);
		tag.setString("name", name);
		return tag;
	}

	@Override
	public void readSyncPacket(NBTTagCompound tag) {
		name = tag.getString("name");
		mode = Mode.get(tag.getInteger("mode"));
		occupationModeState = OccupationModeState.get(tag.getInteger("occupationModeState"));
		unpoweredAspect = Aspect.get(tag.getInteger("unpoweredAspect"));
		poweredAspect = Aspect.get(tag.getInteger("poweredAspect"));

		getEndPointData();
		
		if (occupationOriginY == -1)
		{
			setOccupationOrigin();
		}
		
		if (mode == Mode.Occupation)
		{
			getEndPointData().addEndPoint(getOccupationOriginBlockPos(), getPos());
		}
		else
		{
			IBlockState state = world.getBlockState(getPos());
			getEndPointData().removeEndPoint(getOccupationOriginBlockPos());
			registeredEndPoints.clear();
			
			notifyUpdate();
		}
		
		hasUpdatedBlockState = false;
	}
	
	private void setOccupationOrigin()
	{
		EnumFacing signalFacing = world.getBlockState(getSignalHeadPos()).getValue(BlockSignalHead.FACING);
		BlockPos origin = ImmersiveRailroadingHelper.findOrigin(getPos(), signalFacing, world);
		
		occupationOriginX = origin.getX();
		occupationOriginY = origin.getY();
		occupationOriginZ = origin.getZ();
		markDirty();
	}
	
	private BlockPos getOccupationOriginBlockPos()
	{
		return new BlockPos(occupationOriginX, occupationOriginY, occupationOriginZ);
	}

	@Override
	public void update() {
		if (world.isRemote || !isMaster)
		{
			return;
		}
		
		if (!hasUpdatedBlockState && mode == Mode.Manual)
		{
			boolean powered = world.isBlockPowered(getPos());
			BlockSignalHead.Aspect blockAspect = BlockSignalHead.Aspect.Dark;
			if (powered)
			{
				switch(poweredAspect)
				{
					case Green:
						blockAspect = BlockSignalHead.Aspect.Green;
						break;
					case Yellow:
					case YellowFlashing:
						blockAspect = BlockSignalHead.Aspect.Yellow;
						break;
					case Red:
					case RedFlashing:
						blockAspect = BlockSignalHead.Aspect.Red;
						break;
				}
			}
			else
			{
				switch(unpoweredAspect)
				{
					case Green:
						blockAspect = BlockSignalHead.Aspect.Green;
						break;
					case Yellow:
					case YellowFlashing:
						blockAspect = BlockSignalHead.Aspect.Yellow;
						break;
					case Red:
					case RedFlashing:
						blockAspect = BlockSignalHead.Aspect.Red;
						break;
				}
			}
			
			IBlockState currentState = world.getBlockState(getSignalHeadPos());
			
			if (!(currentState.getBlock() instanceof BlockSignalHead))
			{
				return;
			}
			
			world.setBlockState(getSignalHeadPos(), currentState.withProperty(BlockSignalHead.ASPECT, blockAspect));
			
			hasUpdatedBlockState = true;
		}
		
		switch(mode)
		{
			case Manual:
				boolean isPowered = world.isBlockPowered(getPos());
				if (isPowered && 
						(poweredAspect == Aspect.RedFlashing ||
						poweredAspect == Aspect.YellowFlashing))
				{
					if (flashTimer <= 20)
					{
						flashTimer++;
						return;
					}
					
					IBlockState state = world.getBlockState(getSignalHeadPos());
					
					if (!(state.getBlock() instanceof BlockSignalHead))
					{
						return;
					}
					
					if (state.getValue(BlockSignalHead.ASPECT) == BlockSignalHead.Aspect.Dark)
					{
						BlockSignalHead.Aspect newAspect;
						if (poweredAspect == Aspect.RedFlashing)
						{
							newAspect = BlockSignalHead.Aspect.Red;
						}
						else
						{
							newAspect = BlockSignalHead.Aspect.Yellow;
						}
						
						state = state.withProperty(BlockSignalHead.ASPECT, newAspect);
					}
					else
					{
						state = state.withProperty(BlockSignalHead.ASPECT, BlockSignalHead.Aspect.Dark);
					}

					world.setBlockState(getSignalHeadPos(), state);
					
					flashTimer = 0;
				}
				else if (!isPowered &&
							(unpoweredAspect == Aspect.RedFlashing ||
							unpoweredAspect == Aspect.YellowFlashing))
				{
					if (flashTimer <= 20)
					{
						flashTimer++;
						return;
					}
					
					IBlockState state = world.getBlockState(getSignalHeadPos());
					
					if (!(state.getBlock() instanceof BlockSignalHead))
					{
						return;
					}
					
					if (state.getValue(BlockSignalHead.ASPECT) == BlockSignalHead.Aspect.Dark)
					{
						BlockSignalHead.Aspect newAspect;
						if (unpoweredAspect == Aspect.RedFlashing)
						{
							newAspect = BlockSignalHead.Aspect.Red;
						}
						else
						{
							newAspect = BlockSignalHead.Aspect.Yellow;
						}
						
						state = state.withProperty(BlockSignalHead.ASPECT, newAspect);
					}
					else
					{
						state = state.withProperty(BlockSignalHead.ASPECT, BlockSignalHead.Aspect.Dark);
					}
					
					world.setBlockState(getSignalHeadPos(), state);
					
					flashTimer = 0;
				}
				break;
			case Occupation:
				if (ModRailStuff.IR_INSTALLED)
				{
					getEndPointData();
					
					if (occupationOriginY == -1)
					{
						setOccupationOrigin();
						
						if (occupationOriginY == -1)
						{
							registeredEndPoints.clear();
							return;
						}
					}
										
					if (occupationModeState == OccupationModeState.Initialization)
					{
						doInitializationTick();
					}
					else if (occupationModeState == OccupationModeState.Normal)
					{
						doNormalTick();
					}
				}
				break;
		}
	}
	
	private OccupationEndPointData getEndPointData()
	{
		if (endPointData == null)
		{
			endPointData = OccupationEndPointData.get(world);
		}
		
		return endPointData;
	}
	
	private void doInitializationTick() {
		if (blocksTraveled == 0)
		{
			EnumFacing signalFacing = world.getBlockState(getSignalHeadPos()).getValue(BlockSignalHead.FACING);
			BlockPos current = getOccupationOriginBlockPos();
			BlockPos motionBP = current.offset(signalFacing);
			
			lastMotionX = motionBP.getX() - current.getX();
			lastMotionY = motionBP.getY() - current.getY();
			lastMotionZ = motionBP.getZ() - current.getZ();
		}
		
		for(int i = 0; i < Config.signalDistanceTick; i++)
		{
			if (lastLocation == null)
			{
				lastLocation = new Vec3d(occupationOriginX, occupationOriginY, occupationOriginZ);
			}
			
			Vec3d motion = new Vec3d(lastMotionX, lastMotionY, lastMotionZ);
			
			Vec3d nextLocation = ImmersiveRailroadingHelper.getNextPosition(lastLocation, motion, world, lastSwitchInfo);
			
			BlockPos nextPos = new BlockPos(nextLocation.x, nextLocation.y, nextLocation.z);
			
			// Check to see if the endpoint has been removed
			if (endpointsContains(nextPos) && !getEndPointData().hasEndPoint(nextPos))
			{
				registeredEndPoints.remove(nextPos);
				
				notifyUpdate();
			}
			
			// Check to see if this is an endpoint and add it if applicable
			if (!nextPos.equals(getOccupationOriginBlockPos()) && !endpointsContains(nextPos) && getEndPointData().hasEndPoint(nextPos))
			{
				// Is this signal facing the right way?
				IBlockState nextSignal = world.getBlockState(getEndPointData().getEndPointMasterPos(nextPos));
				EnumFacing signalFacing = nextSignal.getValue(BlockMast.FACING);
				EnumFacing motionFacing = EnumFacing.getFacingFromVector((float)motion.x, (float)motion.y, (float)motion.z);
				
				if (signalFacing.equals(motionFacing))
				{
					SignalTileEntity nextPosMaster = (SignalTileEntity)world.getTileEntity(getEndPointData().getEndPointMasterPos(nextPos));
					registeredEndPoints.put(nextPos, nextPosMaster.getName());
					
					notifyUpdate();
				}
			}
			
			lastMotionX = nextLocation.x - lastLocation.x;
			lastMotionY = nextLocation.y - lastLocation.y;
			lastMotionZ = nextLocation.z - lastLocation.z;
			
			blocksTraveled++;
			if (blocksTraveled >= Config.signalDistanceTimeout || nextLocation == lastLocation)
			{				
				lastLocation = null;
				blocksTraveled = 0;
				break;
			}
			
			lastLocation = nextLocation;
		}
	}
	
	private boolean endpointsContains(BlockPos pos)
	{
		for(BlockPos endpoint : registeredEndPoints.keySet())
		{
			if (pos.equals(endpoint))
			{
				return true;
			}
		}
		
		return false;
	}

	private void doNormalTick()
	{
		if (blocksTraveled == 0)
		{
			EnumFacing signalFacing = world.getBlockState(getSignalHeadPos()).getValue(BlockSignalHead.FACING);
			BlockPos current = new BlockPos(occupationOriginX, occupationOriginY, occupationOriginZ);
			BlockPos motionBP = current.offset(signalFacing);
			
			lastMotionX = motionBP.getX() - current.getX();
			lastMotionY = motionBP.getY() - current.getY();
			lastMotionZ = motionBP.getZ() - current.getZ();
		}
		
		for(int i = 0; i < Config.signalDistanceTick; i++)
		{
			if (lastLocation == null)
			{
				lastLocation = new Vec3d(occupationOriginX, occupationOriginY, occupationOriginZ);
			}
			
			Vec3d motion = new Vec3d(lastMotionX, lastMotionY, lastMotionZ);
			
			Vec3d nextLocation = ImmersiveRailroadingHelper.getNextPosition(lastLocation, motion, world, lastSwitchInfo);
			
			if (ImmersiveRailroadingHelper.hasStockNearby(nextLocation, world))
			{
				IBlockState signalHeadBlockState = world.getBlockState(getSignalHeadPos());
				if (signalHeadBlockState != null && signalHeadBlockState.getBlock() instanceof BlockSignalHead)
				{
					world.setBlockState(getSignalHeadPos(), signalHeadBlockState.withProperty(BlockSignalHead.ASPECT, BlockSignalHead.Aspect.Red));
				}
				
				lastLocation = null;
				blocksTraveled = 0;
				break;
			}
			
			lastMotionX = nextLocation.x - lastLocation.x;
			lastMotionY = nextLocation.y - lastLocation.y;
			lastMotionZ = nextLocation.z - lastLocation.z;
			
			blocksTraveled++;
			if (blocksTraveled >= Config.signalDistanceTimeout || nextLocation == lastLocation)
			{
				IBlockState signalHeadBlockState = world.getBlockState(getSignalHeadPos());
				if (signalHeadBlockState != null && 
						signalHeadBlockState.getBlock() instanceof BlockSignalHead &&
						signalHeadBlockState.getValue(BlockSignalHead.ASPECT) != BlockSignalHead.Aspect.Red)
				{
					world.setBlockState(getSignalHeadPos(), signalHeadBlockState.withProperty(BlockSignalHead.ASPECT, BlockSignalHead.Aspect.Red));
				}
				
				lastLocation = null;
				blocksTraveled = 0;
				break;
			}
			
			lastLocation = nextLocation;
		}
	}
	
	private BlockPos getSignalHeadPos()
	{
		return new BlockPos(signalHeadX, signalHeadY, signalHeadZ);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		Block newBlock = newSate.getBlock();
		
		if (newBlock instanceof BlockMast || newBlock instanceof BlockMastFake || newBlock instanceof BlockSignalHead)
		{
			return false;
		}
		
		return true;
	}

	public void onNeighborChanged(World world)
	{
		boolean powered = world.isBlockPowered(getPos());
		
		if ((powered &&
				(poweredAspect == Aspect.RedFlashing ||
				poweredAspect == Aspect.YellowFlashing))
			|| (!powered &&
				(unpoweredAspect == Aspect.RedFlashing ||
				unpoweredAspect == Aspect.YellowFlashing)))
		{
			// Will be picked up by tick
			return;
		}
		
		BlockSignalHead.Aspect aspect = BlockSignalHead.Aspect.Dark;
		if (powered)
		{
			switch(poweredAspect)
			{
				case Red:
					aspect = BlockSignalHead.Aspect.Red;
					break;
				case Yellow:
					aspect = BlockSignalHead.Aspect.Yellow;
					break;
				case Green:
					aspect = BlockSignalHead.Aspect.Green;
					break;
			}
		}
		else
		{
			switch(unpoweredAspect)
			{
				case Red:
					aspect = BlockSignalHead.Aspect.Red;
					break;
				case Yellow:
					aspect = BlockSignalHead.Aspect.Yellow;
					break;
				case Green:
					aspect = BlockSignalHead.Aspect.Green;
					break;
			}
		}
		
		IBlockState signalState = world.getBlockState(getSignalHeadPos());
		
		if (!(signalState.getBlock() instanceof BlockSignalHead))
		{
			return;
		}
		
		signalState = signalState.withProperty(BlockSignalHead.ASPECT, aspect);
		
		world.setBlockState(getSignalHeadPos(), signalState);
	}

	public void setMode(Mode mode)
	{
		this.mode = mode;
		markDirty();
	}

	public void setOccupationModeState(OccupationModeState state)
	{
		occupationModeState = state;
		markDirty();
	}

	public ImmutableMap<BlockPos, String> getRegisteredEndPoints()
	{
		return ImmutableMap.copyOf(registeredEndPoints);
	}

	public void setRegisteredEndPoints(HashMap<BlockPos, String> endPoints)
	{
		registeredEndPoints = endPoints;
	}

	private void notifyUpdate()
	{
		IBlockState state = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), state, state, 3);
	}

	public static class LastSwitchInfo
	{
		public Vec3d lastSwitchPlacementPosition = null;
	}
}
