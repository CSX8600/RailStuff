package com.clussmanproductions.railstuff.tile;

import com.clussmanproductions.railstuff.Config;
import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.BlockEndABS;
import com.clussmanproductions.railstuff.blocks.BlockMast;
import com.clussmanproductions.railstuff.blocks.BlockSignalHead;
import com.clussmanproductions.railstuff.util.EnumAspect;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import scala.Tuple3;

public class SignalTileEntity extends TileEntitySyncable implements ITickable {

	private Mode mode = Mode.Manual;
	private Aspect unpoweredAspect = Aspect.Dark;
	private Aspect poweredAspect = Aspect.Dark;
	private String name = "";
	private boolean hasUpdatedBlockState = true;
	private int flashTimer = 0;
	
	// Occupation mode members
	private double occupationOriginX = 0;
	private double occupationOriginY = -1;
	private double occupationOriginZ = 0;
	private int blocksTraveled = 0;
	private Vec3d lastLocation;
	private double lastMotionX = 0;
	private double lastMotionY = 0;
	private double lastMotionZ = 0;
	private Tuple3</*RailPoint*/BlockPos, /*TE Point*/BlockPos, String> registeredEndPoint;
	private LastSwitchInfo lastSwitchInfo = new LastSwitchInfo();
	private Aspect occupationAspect = Aspect.Red;
	private Ticket lastTicket;
	private final String NO_ORIGIN = "No track found nearby";
	private final String NO_PAIR = "Not paired";
	private final String OK = "Normal";
	private final String TIME_OUT = "Could not find end point";
	private boolean lastTickTimedOut = false;
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("mode", mode.index);
		compound.setInteger("unpoweredAspect", unpoweredAspect.index);
		compound.setInteger("poweredAspect", poweredAspect.index);
		compound.setString("name", name);
		compound.setDouble("occupationOriginX", occupationOriginX);
		compound.setDouble("occupationOriginY", occupationOriginY);
		compound.setDouble("occupationOriginZ", occupationOriginZ);
		compound.setInteger("occupationAspect", occupationAspect.index);
		
		if (registeredEndPoint != null)
		{
			BlockPos endPointPos = registeredEndPoint._1();
			BlockPos tePos = registeredEndPoint._2();
			compound.setIntArray("registeredEndPointPos", new int[] { endPointPos.getX(), endPointPos.getY(), endPointPos.getZ() });
			compound.setIntArray("registeredTileEntityPos", new int[] { tePos.getX(), tePos.getY(), tePos.getZ() });
			compound.setString("registeredEndPointName", registeredEndPoint._3());
		}
		
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		mode = Mode.get(compound.getInteger("mode"));
		unpoweredAspect = Aspect.get(compound.getInteger("unpoweredAspect"));
		poweredAspect = Aspect.get(compound.getInteger("poweredAspect"));
		name = compound.getString("name");
		occupationOriginX = compound.getInteger("occupationOriginX");
		occupationOriginY = compound.getInteger("occupationOriginY");
		occupationOriginZ = compound.getInteger("occupationOriginZ");
		occupationAspect = Aspect.get(compound.getInteger("occupationAspect"));
		
		int[] endPointArray = null;
		int[] teArray = null;
		String endPointName = null;
		
		if (compound.hasKey("registeredEndPointPos"))
		{
			endPointArray = compound.getIntArray("registeredEndPointPos");
		}
		
		if (compound.hasKey("registeredTileEntityPos"))
		{
			teArray = compound.getIntArray("registeredTileEntityPos");
		}
		
		if (compound.hasKey("registeredEndPointName"))
		{
			endPointName = compound.getString("registeredEndPointName");
		}
		
		if (endPointArray == null || teArray == null)
		{
			registeredEndPoint = null;
		}
		else
		{
			registeredEndPoint = new Tuple3<BlockPos, BlockPos, String>(new BlockPos(endPointArray[0], endPointArray[1], endPointArray[2]), new BlockPos(teArray[0], teArray[1], teArray[2]), endPointName);
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
		tag.setInteger("mode", mode.index);
		tag.setInteger("unpoweredAspect", unpoweredAspect.index);
		tag.setInteger("poweredAspect", poweredAspect.index);
		tag.setString("name", name);
		tag.setDouble("occupationOriginY", occupationOriginY);
		tag.setBoolean("timedOut", lastTickTimedOut);
		
		if (registeredEndPoint != null)
		{
			BlockPos endPointPos = registeredEndPoint._1();
			BlockPos tePos = registeredEndPoint._2();
			tag.setIntArray("registeredEndPointPos", new int[] { endPointPos.getX(), endPointPos.getY(), endPointPos.getZ() });
			tag.setIntArray("registeredTileEntityPos", new int[] { tePos.getX(), tePos.getY(), tePos.getZ() });
			tag.setString("registeredEndPointName", registeredEndPoint._3());
		}
		
		return tag;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		name = tag.getString("name");
		mode = Mode.get(tag.getInteger("mode"));
		unpoweredAspect = Aspect.get(tag.getInteger("unpoweredAspect"));
		poweredAspect = Aspect.get(tag.getInteger("poweredAspect"));
		occupationOriginY = tag.getDouble("occupationOriginY");
		lastTickTimedOut = tag.getBoolean("timedOut");
		int[] endPointPos = null;
		int[] tePos = null;
		String endPointName = null;
		
		if (tag.hasKey("registeredEndPointPos"))
		{
			endPointPos = tag.getIntArray("registeredEndPointPos");
		}
		
		if (tag.hasKey("registeredTileEntityPos"))
		{
			tePos = tag.getIntArray("registeredTileEntityPos");
		}
		
		if (tag.hasKey("registeredEndPointName"))
		{
			endPointName = tag.getString("registeredendPointName");
		}
		
		if (endPointPos == null || tePos == null)
		{
			registeredEndPoint = null;
		}
		else
		{
			registeredEndPoint = new Tuple3<BlockPos, BlockPos, String>(new BlockPos(endPointPos[0], endPointPos[1], endPointPos[2]), new BlockPos(tePos[0], tePos[1], tePos[2]), endPointName);
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
		tag.setInteger("unpoweredAspect", unpoweredAspect.index);
		tag.setInteger("poweredAspect", poweredAspect.index);
		tag.setString("name", name);
		tag.setInteger("occupationAspect", occupationAspect.index);
		return tag;
	}

	@Override
	public void readSyncPacket(NBTTagCompound tag) {
		name = tag.getString("name");
		mode = Mode.get(tag.getInteger("mode"));
		unpoweredAspect = Aspect.get(tag.getInteger("unpoweredAspect"));
		poweredAspect = Aspect.get(tag.getInteger("poweredAspect"));
		occupationAspect = Aspect.get(tag.getInteger("occupationAspect"));
		
		if (occupationOriginY == -1)
		{
			setOccupationOrigin();
		}
		
		if (mode == Mode.Manual)
		{
			IBlockState state = world.getBlockState(getPos());
			registeredEndPoint = null;
			
			notifyUpdate();
		}
		
		hasUpdatedBlockState = false;
	}
	
	private void setOccupationOrigin()
	{
		EnumFacing signalFacing = world.getBlockState(getPos()).getValue(BlockSignalHead.FACING);
		
		BlockPos workingPos = getPos();
		while((world.getBlockState(workingPos).getBlock() instanceof BlockSignalHead) || (world.getBlockState(workingPos).getBlock() instanceof BlockMast))
		{
			workingPos = workingPos.down();
		}
		workingPos = workingPos.up();
		Vec3d origin = ImmersiveRailroadingHelper.findOrigin(workingPos, signalFacing, world);
		
		boolean willNotify = occupationOriginY != origin.y;
		occupationOriginX = origin.x;
		occupationOriginY = origin.y;
		occupationOriginZ = origin.z;
		markDirty();
		
		if (willNotify)
		{
			notifyUpdate();
		}
	}
	
	public BlockPos getOccupationOriginBlockPos()
	{
		return new BlockPos(occupationOriginX, occupationOriginY, occupationOriginZ);
	}

	@Override
	public void update() {
		if (world.isRemote)
		{
			return;
		}
		
		if (!hasUpdatedBlockState && mode == Mode.Manual)
		{
			boolean powered = world.isBlockPowered(getPos());
			EnumAspect blockAspect = EnumAspect.Dark;
			if (powered)
			{
				switch(poweredAspect)
				{
					case Green:
						blockAspect = EnumAspect.Green;
						break;
					case Yellow:
					case YellowFlashing:
						blockAspect = EnumAspect.Yellow;
						break;
					case Red:
					case RedFlashing:
						blockAspect = EnumAspect.Red;
						break;
				}
			}
			else
			{
				switch(unpoweredAspect)
				{
					case Green:
						blockAspect = EnumAspect.Green;
						break;
					case Yellow:
					case YellowFlashing:
						blockAspect = EnumAspect.Yellow;
						break;
					case Red:
					case RedFlashing:
						blockAspect = EnumAspect.Red;
						break;
				}
			}
			
			IBlockState currentState = world.getBlockState(getPos());
			
			world.setBlockState(getPos(), currentState.withProperty(BlockSignalHead.ASPECT, blockAspect));
			
			hasUpdatedBlockState = true;
		}
		
		performFlash();
		
		if (mode == Mode.Occupation && ModRailStuff.IR_INSTALLED)
		{			
			if (occupationOriginY == -1)
			{
				setOccupationOrigin();
				
				if (occupationOriginY == -1)
				{
					registeredEndPoint = null;
					return;
				}
			}
			
			doNormalTick();
		}
	}

	private void performFlash()
	{
		if (mode == Mode.Manual)
		{
			BlockPos workingPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
			IBlockState workingState = world.getBlockState(workingPos);
			
			while(workingState.getBlock() == ModBlocks.mast || workingState.getBlock() == ModBlocks.signal_head)
			{
				workingPos = workingPos.down();
				
				workingState = world.getBlockState(workingPos);
			}

			workingPos = workingPos.up();

			boolean isPowered = world.isBlockPowered(workingPos);
			
			if (isPowered && 
					(poweredAspect == Aspect.RedFlashing ||
					poweredAspect == Aspect.YellowFlashing))
			{
				if (flashTimer <= 20)
				{
					flashTimer++;
					return;
				}
				
				IBlockState state = world.getBlockState(getPos());
				
				if (!(state.getBlock() instanceof BlockSignalHead))
				{
					return;
				}
				
				if (state.getValue(BlockSignalHead.ASPECT) == EnumAspect.Dark)
				{
					EnumAspect newAspect;
					if (poweredAspect == Aspect.RedFlashing)
					{
						newAspect = EnumAspect.Red;
					}
					else
					{
						newAspect = EnumAspect.Yellow;
					}
					
					state = state.withProperty(BlockSignalHead.ASPECT, newAspect);
				}
				else
				{
					state = state.withProperty(BlockSignalHead.ASPECT, EnumAspect.Dark);
				}

				world.setBlockState(getPos(), state);
				
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
				
				IBlockState state = world.getBlockState(getPos());
				
				if (!(state.getBlock() instanceof BlockSignalHead))
				{
					return;
				}
				
				if (state.getValue(BlockSignalHead.ASPECT) == EnumAspect.Dark)
				{
					EnumAspect newAspect;
					if (unpoweredAspect == Aspect.RedFlashing)
					{
						newAspect = EnumAspect.Red;
					}
					else
					{
						newAspect = EnumAspect.Yellow;
					}
					
					state = state.withProperty(BlockSignalHead.ASPECT, newAspect);
				}
				else
				{
					state = state.withProperty(BlockSignalHead.ASPECT, EnumAspect.Dark);
				}
				
				world.setBlockState(getPos(), state);
				
				flashTimer = 0;
			}
		}
		else if (mode == Mode.Occupation)
		{
			IBlockState state = world.getBlockState(getPos());
			
			if (!(state.getBlock() instanceof BlockSignalHead))
			{
				return;
			}
			
			if (occupationAspect == Aspect.YellowFlashing ||
					occupationAspect == Aspect.RedFlashing)
			{
				if (flashTimer <= 20)
				{
					flashTimer++;
					return;
				}
				
				if (state.getValue(BlockSignalHead.ASPECT) == EnumAspect.Dark)
				{
					EnumAspect newAspect;
					if (occupationAspect == Aspect.RedFlashing)
					{
						newAspect = EnumAspect.Red;
					}
					else
					{
						newAspect = EnumAspect.Yellow;
					}
					
					state = state.withProperty(BlockSignalHead.ASPECT, newAspect);
				}
				else
				{
					state = state.withProperty(BlockSignalHead.ASPECT, EnumAspect.Dark);
				}
				
				world.setBlockState(getPos(), state);
				
				flashTimer = 0;
			}
			else
			{
				EnumAspect newAspect = EnumAspect.Dark;
				
				switch(occupationAspect)
				{
					case Red:
						newAspect = EnumAspect.Red;
						break;
					case Yellow:
						newAspect = EnumAspect.Yellow;
						break;
					case Green:
						newAspect = EnumAspect.Green;
						break;
				}
				
				if (state.getValue(BlockSignalHead.ASPECT) != newAspect)
				{
					state = state.withProperty(BlockSignalHead.ASPECT, newAspect);
					world.setBlockState(getPos(), state);
				}
			}
		}
	}
	
	private boolean doChunkLoad(BlockPos nextPos)
	{
		Chunk chunk = world.getChunkFromBlockCoords(nextPos);
		if (!chunk.isLoaded())
		{
			if (lastTicket != null)
			{
				ChunkPos lastChunkPos = null;
				for(ChunkPos pos : lastTicket.getChunkList())
				{
					lastChunkPos = pos;
					break;
				}
				
				ForgeChunkManager.unforceChunk(lastTicket, lastChunkPos);
				ForgeChunkManager.releaseTicket(lastTicket);
			}
			
			lastTicket = ForgeChunkManager.requestTicket(ModRailStuff.instance, world, Type.NORMAL);
			if (lastTicket == null)
			{
				ModRailStuff.logger.error("Signal failed to load chunk during tick - maybe there are too many signals?");
				return false;
			}
			
			ForgeChunkManager.forceChunk(lastTicket, new ChunkPos(chunk.x, chunk.z));
		}
		else if (lastTicket != null && !lastTicket.getChunkList().contains(new ChunkPos(chunk.x, chunk.z)))
		{
			ChunkPos lastChunkPos = null;
			for(ChunkPos pos : lastTicket.getChunkList())
			{
				lastChunkPos = pos;
				break;
			}
			
			ForgeChunkManager.unforceChunk(lastTicket, lastChunkPos);
			ForgeChunkManager.releaseTicket(lastTicket);
		}
		
		return true;
	}
	
	private void doNormalTick()
	{		
		if (registeredEndPoint == null)
		{
			if (occupationAspect != occupationAspect.Dark)
			{
				occupationAspect = occupationAspect.Dark;
				markDirty();
				notifyUpdate();
			}
			return;
		}
		
		if (blocksTraveled == 0)
		{
			EnumFacing signalFacing = world.getBlockState(getPos()).getValue(BlockSignalHead.FACING);
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
			
			if (!doChunkLoad(new BlockPos(nextLocation.x, nextLocation.y, nextLocation.z)))
			{
				lastLocation = null;
				blocksTraveled = 0;
				break;
			}
			
			if (ImmersiveRailroadingHelper.hasStockNearby(new Vec3d(occupationOriginX, occupationOriginY, occupationOriginZ), world) || ImmersiveRailroadingHelper.hasStockNearby(nextLocation, world))
			{
				occupationAspect = Aspect.Red;
				lastTickTimedOut = false;
				markDirty();
				notifyUpdate();
				
				lastLocation = null;
				blocksTraveled = 0;
				break;
			}
			else if (registeredEndPoint._1().equals(new BlockPos(nextLocation.x, nextLocation.y, nextLocation.z)))
			{				
				TileEntity masterTE = (TileEntity)world.getTileEntity(registeredEndPoint._2());
				if (masterTE instanceof SignalTileEntity)
				{
					SignalTileEntity masterTESignal = (SignalTileEntity)masterTE;
					
					switch(masterTESignal.getOccupationAspect())
					{
						case Dark:
						case Red:
							occupationAspect = Aspect.Yellow;
							break;
						case Yellow:
							occupationAspect = Aspect.YellowFlashing;
							break;
						case YellowFlashing:
						case Green:
							occupationAspect = Aspect.Green;
							break;
						default:
							occupationAspect = Aspect.Red;
							break;
					}

					lastTickTimedOut = false;
					markDirty();
					lastLocation = null;
					blocksTraveled = 0;
					break;
				}
				else if (world.getBlockState(registeredEndPoint._2()).getBlock() instanceof BlockEndABS)
				{
					occupationAspect = Aspect.Yellow;
					lastTickTimedOut = false;
					markDirty();
					notifyUpdate();
					lastLocation = null;
					blocksTraveled = 0;
					break;
				}
			}
			
			lastMotionX = nextLocation.x - lastLocation.x;
			lastMotionY = nextLocation.y - lastLocation.y;
			lastMotionZ = nextLocation.z - lastLocation.z;
			
			blocksTraveled++;
			if (blocksTraveled >= Config.signalDistanceTimeout || nextLocation == lastLocation)
			{
				occupationAspect = Aspect.Red;
				lastTickTimedOut = true;
				markDirty();
				notifyUpdate();
				lastLocation = null;
				blocksTraveled = 0;
				break;
			}
			
			lastLocation = nextLocation;
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		Block newBlock = newSate.getBlock();
		
		if (newBlock instanceof BlockSignalHead)
		{
			return false;
		}
		
		return true;
	}

	public void onNeighborChanged(World world, boolean isPowered)
	{		
		if ((isPowered &&
				(poweredAspect == Aspect.RedFlashing ||
				poweredAspect == Aspect.YellowFlashing))
			|| (!isPowered &&
				(unpoweredAspect == Aspect.RedFlashing ||
				unpoweredAspect == Aspect.YellowFlashing))
			|| mode == Mode.Occupation)
		{
			// Will be picked up by tick
			return;
		}
		
		EnumAspect aspect = EnumAspect.Dark;
		if (isPowered)
		{
			switch(poweredAspect)
			{
				case Red:
					aspect = EnumAspect.Red;
					break;
				case Yellow:
					aspect = EnumAspect.Yellow;
					break;
				case Green:
					aspect = EnumAspect.Green;
					break;
			}
		}
		else
		{
			switch(unpoweredAspect)
			{
				case Red:
					aspect = EnumAspect.Red;
					break;
				case Yellow:
					aspect = EnumAspect.Yellow;
					break;
				case Green:
					aspect = EnumAspect.Green;
					break;
			}
		}
		
		IBlockState signalState = world.getBlockState(getPos());
		
		if (!(signalState.getBlock() instanceof BlockSignalHead))
		{
			return;
		}
		
		signalState = signalState.withProperty(BlockSignalHead.ASPECT, aspect);
		
		world.setBlockState(getPos(), signalState);
	}

	public void setMode(Mode mode)
	{
		this.mode = mode;
		markDirty();
	}

	public void setRegisteredEndPoint(Tuple3<BlockPos, BlockPos, String> endPoint)
	{
		registeredEndPoint = endPoint;
		markDirty();
	}

	private void notifyUpdate()
	{
		IBlockState state = world.getBlockState(getPos());
		world.notifyBlockUpdate(getPos(), state, state, 3);
	}

	public Aspect getOccupationAspect()
	{
		return occupationAspect;
	}
	
	public static class LastSwitchInfo
	{
		public Vec3d lastSwitchPlacementPosition = null;
	}

	public String getSignalStatus()
	{
		if (mode != Mode.Occupation)
		{
			return "";
		}
		
		if (occupationOriginY == -1)
		{
			return NO_ORIGIN;
		}
		
		if (registeredEndPoint == null)
		{
			return NO_PAIR;
		}
		
		if (lastTickTimedOut)
		{
			return TIME_OUT;
		}
		
		return OK + " (Pair: " + registeredEndPoint._3() + ")";
	}
	
	public int getSignalStatusColor()
	{
		String OKString = OK;
		
		if (registeredEndPoint != null)
		{
			OKString = OK + " (Pair: " + registeredEndPoint._3() + ")";
		}
		
		switch(getSignalStatus())
		{
			case TIME_OUT:
			case NO_ORIGIN:
				return 0xFF0000;
			case NO_PAIR:
				return 0xFFFF00;
		}
		
		if (getSignalStatus().equals(OKString))
		{
			return 0x00AA00;
		}
		
		return 0xFFFFFF;
	}

	public void setOccupationOrigin(BlockPos newPos)
	{
		occupationOriginX = newPos.getX();
		occupationOriginY = newPos.getY();
		occupationOriginZ = newPos.getZ();
		
		markDirty();
		notifyUpdate();
	}
}
