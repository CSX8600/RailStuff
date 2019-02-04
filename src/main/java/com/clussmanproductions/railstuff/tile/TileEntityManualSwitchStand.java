package com.clussmanproductions.railstuff.tile;

import com.clussmanproductions.railstuff.blocks.BlockManualSwitchStand;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import scala.Tuple3;

public class TileEntityManualSwitchStand extends TileEntity implements ITickable {

	private AnimationStates _animationState = AnimationStates.Unknown;
	private int leverRotation = 0;
	private int poleRotation = 0;
	private int yRotation = -1;
	
	private int getYRotation(EnumFacing facing)
	{
		if (yRotation == -1)
		{			
			switch(facing)
			{
				case NORTH:
					yRotation = 90;
					break;
				case EAST:
					yRotation = 180;
					break;
				case SOUTH:
					yRotation = 270;
					break;
				default:
					yRotation = 0;
			}
		}
		
		return yRotation;
	}
	
	public int getPoleYRotation(EnumFacing facing)
	{
		return getYRotation(facing) + poleRotation;
	}
	
	public int getLeverRotation(EnumFacing facing)
	{
		int modifier = 1;
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST)
		{
			modifier = -1;
		}
		
		return leverRotation * modifier;
	}
	
	public Tuple3<Double, Double, Double> getPlateTranslation(EnumFacing facing)
	{
		switch(facing)
		{
			case NORTH:
				return new Tuple3<Double, Double, Double>(0.5, 1.1875, 0.34375);
			case SOUTH:
				return new Tuple3<Double, Double, Double>(0.5625, 1.1875, 0.28125);
			case WEST:
				return new Tuple3<Double, Double, Double>(0.5625, 1.1875, 0.34375);
			case EAST:
				return new Tuple3<Double, Double, Double>(0.5, 1.1875, 0.28125);
		}
		
		return new Tuple3<Double, Double, Double>(0.0, 0.0, 0.0);
	}
	
	public Tuple3<Double, Double, Double> getPoleTranslation(EnumFacing facing)
	{
		switch(facing)
		{
			case NORTH:
				return new Tuple3<Double, Double, Double>( 0.46875, 0.625, 0.53125);
			case WEST:
				return new Tuple3<Double, Double, Double>( 0.53125, 0.625, 0.53125);
			case SOUTH:
				return new Tuple3<Double, Double, Double>(0.53125, 0.625, 0.46875);
			case EAST:
				return new Tuple3<Double, Double, Double>( 0.46875, 0.625, 0.46875);
		}
		
		return new Tuple3<Double, Double, Double>(0.0, 0.0, 0.0);
	}
	
	public Tuple3<Double, Double, Double> getLeverHorizontalTranslation(EnumFacing facing)
	{
		switch(facing)
		{
			case NORTH:
				return new Tuple3<Double, Double, Double>(0.25, 0.5625, 0.5);
			case WEST:
				return new Tuple3<Double, Double, Double>(0.5, 0.5625, 0.625);
			case SOUTH:
				return new Tuple3<Double, Double, Double>(0.3125, 0.5625, 0.4375);
			case EAST:
				return new Tuple3<Double, Double, Double>(0.4375, 0.5625, 0.5625);
			default:
				return new Tuple3<Double, Double, Double>(0.0, 0.0, 0.0);
		}
	}
	
	public Tuple3<Double, Double, Double> getLeverTranslation(EnumFacing facing)
	{
		switch(facing)
		{
			case NORTH:
				return new Tuple3<Double, Double, Double>(0.25, 0.5625, 0.5);
			case WEST:
				return new Tuple3<Double, Double, Double>(0.75, 0.5625, 0.625);
			case SOUTH:
				return new Tuple3<Double, Double, Double>(0.3125, 0.5625, 0.4375);
			case EAST:
				return new Tuple3<Double, Double, Double>(0.6825, 0.5625, 0.5625);
			default:
				return new Tuple3<Double, Double, Double>(0.0, 0.0, 0.0);
		}
	}
	
	@Override
	public void update() {
		if (!world.isRemote)
		{
			return;
		}
		
		boolean state = world.getBlockState(getPos()).getValue(BlockManualSwitchStand.STATE);
		
		if (_animationState == AnimationStates.Unknown)
		{
			poleRotation = (state) ? 90 : 0;
			_animationState = (state) ? AnimationStates.Reverse : AnimationStates.Normal;
			return;
		}
		
		// No animation steps needed
		if ((state && _animationState == AnimationStates.Reverse) || (!state && _animationState == AnimationStates.Normal))
		{
			return;
		}
		
		// Reversing
		if (state)
		{
			switch(_animationState)
			{
				case Normal:
					_animationState = AnimationStates.LeverNormal;
					break;
				case LeverNormal:
					if (leverRotation >= 90)
					{
						_animationState = AnimationStates.Swinging;
					}
					else
					{
						leverRotation+=3;
					}
					break;
				case Swinging:
					if (poleRotation >= 90)
					{
						_animationState = AnimationStates.LeverReverse;
					}
					else
					{
						poleRotation+=3;
					}
					break;
				case LeverReverse:
					if (leverRotation <= 0)
					{
						_animationState = AnimationStates.Reverse;
					}
					else
					{
						leverRotation-=3;
					}
			}
		}
		else // Normalizing
		{
			switch(_animationState)
			{
				case Reverse:
					_animationState = AnimationStates.LeverReverse;
					break;
				case LeverReverse:
					if (leverRotation >= 90)
					{
						_animationState = AnimationStates.Swinging;
					}
					else
					{
						leverRotation+=3;
					}
					break;
				case Swinging:
					if (poleRotation <= 0)
					{
						_animationState = AnimationStates.LeverNormal;
					}
					else
					{
						poleRotation-=3;
					}
					break;
				case LeverNormal:
					if (leverRotation <= 0)
					{
						_animationState = AnimationStates.Normal;
					}
					else
					{
						leverRotation-=3;
					}
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		if (newSate.getBlock() instanceof BlockManualSwitchStand)
		{
			return false;
		}
		
		return true;
	}
	
	private enum AnimationStates
	{
		Unknown(0),
		Normal(1),
		Reverse(2),
		Swinging(3),
		LeverReverse(4),
		LeverNormal(5);
		
		private int index;
		AnimationStates(int index)
		{
			this.index = index;
		}
	}


}
