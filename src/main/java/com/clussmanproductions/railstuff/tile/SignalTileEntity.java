package com.clussmanproductions.railstuff.tile;

import com.clussmanproductions.railstuff.blocks.BlockMast;
import com.clussmanproductions.railstuff.blocks.BlockMastFake;
import com.clussmanproductions.railstuff.blocks.BlockSignalHead;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
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
		return tag;
	}

	@Override
	public void readSyncPacket(NBTTagCompound tag) {
		name = tag.getString("name");
		mode = Mode.get(tag.getInteger("mode"));
		unpoweredAspect = Aspect.get(tag.getInteger("unpoweredAspect"));
		poweredAspect = Aspect.get(tag.getInteger("poweredAspect"));
		
		hasUpdatedBlockState = false;
	}

	@Override
	public void update() {
		if (world.isRemote || !isMaster)
		{
			return;
		}
		
		if (!hasUpdatedBlockState)
		{
			switch(mode)
			{
				case Manual:
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
					
					break;
			}
			
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
							break;
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
					break;
				}
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
}
