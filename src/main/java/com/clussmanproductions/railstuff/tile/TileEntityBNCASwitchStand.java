package com.clussmanproductions.railstuff.tile;

import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.blocks.BlockBNCASwitchStand;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityBNCASwitchStand extends TileEntity implements ITickable {
	private SwitchStandState state = SwitchStandState.Normal;
	private int rotateAmount = 0;
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return super.writeToNBT(compound);
	}
	
	public SwitchStandState getState() {
		return state;
	}
	
	public int getRotationAmount()
	{
		return rotateAmount;
	}
	
	@Override
	public void update() {
		if (!world.isRemote)
		{
			return;
		}
		
		IBlockState blockState = world.getBlockState(getPos());
		if (blockState.getBlock() != ModBlocks.bnca_switch_stand)
		{
			return;
		}
		
		boolean isSwitched = blockState.getValue(BlockBNCASwitchStand.STATE);
		if ((isSwitched && state == SwitchStandState.Reverse) ||
				(!isSwitched && state == SwitchStandState.Normal))
		{
			return;
		}
		
		if (isSwitched && 
				(state == SwitchStandState.Normal || state == SwitchStandState.ReverseToNormal))
		{
			state = SwitchStandState.NormalToReverse;
		}
		
		if (!isSwitched && 
				(state == SwitchStandState.Reverse || state == SwitchStandState.NormalToReverse))
		{
			state = SwitchStandState.ReverseToNormal;
		}
		
		if (state == SwitchStandState.ReverseToNormal && rotateAmount <= 0)
		{
			state = SwitchStandState.Normal;
		}
		
		if (state == SwitchStandState.NormalToReverse && rotateAmount >= 180)
		{
			state = SwitchStandState.Reverse;
		}
		
		if (state == SwitchStandState.ReverseToNormal)
		{
			rotateAmount -= 3;
		}
		
		if (state == SwitchStandState.NormalToReverse)
		{
			rotateAmount += 3;
		}
	}
		
	public enum SwitchStandState
	{
		Normal(0),
		NormalToReverse(1),
		Reverse(2),
		ReverseToNormal(3);
		
		private int index;
		SwitchStandState(int index)
		{
			this.index = index;
		}
		
		public static SwitchStandState get(int index)
		{
			for(SwitchStandState state : SwitchStandState.values())
			{
				if (state.index == index)
				{
					return state;
				}
			}
			
			return null;
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return newSate.getBlock() != ModBlocks.bnca_switch_stand;
	}
}
