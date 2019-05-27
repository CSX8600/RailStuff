package com.clussmanproductions.railstuff.tile;

import com.clussmanproductions.railstuff.blocks.BlockSignalHead;
import com.clussmanproductions.railstuff.util.BlockPosUtils;
import com.clussmanproductions.railstuff.util.EnumAspect;
import com.clussmanproductions.railstuff.util.IBlockStateUtils;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

public class DoubleSignalBaseTileEntity extends TileEntity implements ITickable {

	// Data
	BlockPos signalHeadMainPos = new BlockPos(0, -1, 0);
	BlockPos signalHeadDivergePos = new BlockPos(0, -1, 0);
	BlockPos mainEndPoint = new BlockPos(0, -1, 0);
	BlockPos divergeEndPoint = new BlockPos(0, -1, 0);
	EnumAspect signalHeadMainAspect = EnumAspect.Dark;
	EnumAspect signalHeadDivergeAspect = EnumAspect.Dark;
	boolean mainFlashMode = false;
	boolean divergeFlashMode = false;
	
	// Operational
	boolean signalHeadMainOn = false;
	boolean signalHeadDivergeOn = false;
	int signalHeadMainFlashDelay = 0;
	int signalHeadDivergeFlashDelay = 0;
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		signalHeadMainPos = BlockPosUtils.asBlockPos(compound.getIntArray("signalHeadMainPos"));
		signalHeadDivergePos = BlockPosUtils.asBlockPos(compound.getIntArray("signalHeadDivergePos"));
		mainEndPoint = BlockPosUtils.asBlockPos(compound.getIntArray("mainEndPoint"));
		divergeEndPoint = BlockPosUtils.asBlockPos(compound.getIntArray("divergeEndPoint"));
		signalHeadMainAspect = EnumAspect.getAspect(compound.getInteger("signalHeadMainAspect"));
		signalHeadDivergeAspect = EnumAspect.getAspect(compound.getInteger("signalHeadDivergeAspect"));
		mainFlashMode = compound.getBoolean("mainFlashMode");
		divergeFlashMode = compound.getBoolean("divergeFlashMode");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setIntArray("signalHeadMainPos", BlockPosUtils.asIntArray(signalHeadMainPos));
		compound.setIntArray("signalHeadDivergePos", BlockPosUtils.asIntArray(signalHeadDivergePos));
		compound.setIntArray("mainEndPoint", BlockPosUtils.asIntArray(mainEndPoint));
		compound.setIntArray("divergeEndPoint", BlockPosUtils.asIntArray(divergeEndPoint));
		compound.setInteger("signalHeadMainAspect", signalHeadMainAspect.index);
		compound.setInteger("signalHeadDivergeAspect", signalHeadDivergeAspect.index);
		compound.setBoolean("mainFlashMode", mainFlashMode);
		compound.setBoolean("divergeFlashMode", divergeFlashMode);
		return super.writeToNBT(compound);
	}

	@Override
	public void update() {
		updateSignals();
	}

	private void updateSignals()
	{
		// Main
		switch(signalHeadMainAspect)
		{
			case Dark:
				// Check to see if it's lit and shouldn't be
				if (signalHeadMainOn)
				{
					updateSignal(true, new PropertyValue(BlockSignalHead.ASPECT, EnumAspect.Dark));
					signalHeadMainOn = false;
				}
				break;
				
			case Red:
				if (!mainFlashMode && !signalHeadMainOn)
				{
					updateSignal(true, new PropertyValue(BlockSignalHead.ASPECT, EnumAspect.Red));
					signalHeadMainOn = true;
				}
				else if (mainFlashMode)
				{
					performFlashTick(true, EnumAspect.Red);
				}
				break;
				
			case Yellow:
				if (!mainFlashMode && !signalHeadMainOn)
				{
					updateSignal(true, new PropertyValue(BlockSignalHead.ASPECT, EnumAspect.Yellow));
					signalHeadMainOn = true;
				}
				else if (mainFlashMode)
				{
					performFlashTick(true, EnumAspect.Yellow);
				}
				break;
				
			case Green:
				if (!signalHeadMainOn)
				{
					updateSignal(true, new PropertyValue(BlockSignalHead.ASPECT, EnumAspect.Green));
					signalHeadMainOn = true;
				}
				break;
		}
		
		// Diverge
		switch(signalHeadDivergeAspect)
		{
			case Dark:
				// Check to see if it's lit and shouldn't be
				if (signalHeadDivergeOn)
				{
					updateSignal(false, new PropertyValue(BlockSignalHead.ASPECT, EnumAspect.Dark));
					signalHeadDivergeOn = false;
				}
				break;
				
			case Red:
				if (!divergeFlashMode && !signalHeadDivergeOn)
				{
					updateSignal(false, new PropertyValue(BlockSignalHead.ASPECT, EnumAspect.Red));
					signalHeadDivergeOn = true;
				}
				else if (divergeFlashMode)
				{
					performFlashTick(false, EnumAspect.Red);
				}
				break;
				
			case Yellow:
				if (!divergeFlashMode && !signalHeadDivergeOn)
				{
					updateSignal(false, new PropertyValue(BlockSignalHead.ASPECT, EnumAspect.Yellow));
					signalHeadDivergeOn = true;
				}
				else if (divergeFlashMode)
				{
					performFlashTick(false, EnumAspect.Yellow);
				}
				break;
				
			case Green:
				if (!signalHeadDivergeOn)
				{
					updateSignal(false, new PropertyValue(BlockSignalHead.ASPECT, EnumAspect.Green));
					signalHeadDivergeOn = true;
				}
				break;
		}
	}
	
	private void performFlashTick(boolean isMain, EnumAspect defaultAspect)
	{
		int flashDelay = (isMain) ? signalHeadMainFlashDelay : signalHeadDivergeFlashDelay;
		boolean isOn = (isMain) ? signalHeadMainOn : signalHeadDivergeOn;
		
		if (flashDelay < 20)
		{
			if (isMain)
			{
				signalHeadMainFlashDelay++;
			}
			else
			{
				signalHeadDivergeFlashDelay++;
			}
			return;
		}
		else
		{
			EnumAspect newAspect = (isOn) ? EnumAspect.Dark : defaultAspect;
			
			updateSignal(isMain, new PropertyValue(BlockSignalHead.ASPECT, newAspect));
			
			if (isMain)
			{
				signalHeadMainOn = !isOn;
				setSignalHeadMainAspect(newAspect);
				signalHeadMainFlashDelay = 0;
			}
			else
			{
				signalHeadDivergeOn = !isOn;
				setSignalHeadDivergeAspect(newAspect);
				signalHeadDivergeFlashDelay = 0;
			}
		}
	}
	
	private void updateSignal(boolean mainSignal, PropertyValue ...propertiesToSet)
	{
		BlockPos posToUse = (mainSignal) ? signalHeadMainPos : signalHeadDivergePos;
	
		IBlockState oldState = world.getBlockState(posToUse);
		IBlockState newState = IBlockStateUtils.clone(oldState);
		for(PropertyValue propVal : propertiesToSet)
		{
			newState = newState.withProperty(propVal.getProperty(), propVal.getValue());
		}
	}
	
	// Setters
	private void setSignalHeadMainPos(BlockPos signalHeadMainPos) {
		this.signalHeadMainPos = signalHeadMainPos;
		markDirty();
	}

	private void setSignalHeadDivergePos(BlockPos signalHeadDivergePos) {
		this.signalHeadDivergePos = signalHeadDivergePos;
		markDirty();
	}

	private void setMainEndPoint(BlockPos mainEndPoint) {
		this.mainEndPoint = mainEndPoint;
		markDirty();
	}

	private void setDivergeEndPoint(BlockPos divergeEndPoint) {
		this.divergeEndPoint = divergeEndPoint;
		markDirty();
	}

	private void setSignalHeadMainAspect(EnumAspect signalHeadMainAspect) {
		this.signalHeadMainAspect = signalHeadMainAspect;
		markDirty();
	}

	private void setSignalHeadDivergeAspect(EnumAspect signalHeadDivergeAspect) {
		this.signalHeadDivergeAspect = signalHeadDivergeAspect;
		markDirty();
	}

	private void setMainFlashMode(boolean mainFlashMode) {
		this.mainFlashMode = mainFlashMode;
		markDirty();
	}

	private void setDivergeFlashMode(boolean divergeFlashMode) {
		this.divergeFlashMode = divergeFlashMode;
		markDirty();
	}

	// Utility classes
	class PropertyValue
	{
		IProperty<?> property;
		Object value;
		
		public PropertyValue(IProperty<?> property, Object value)
		{
			this.property = property;
			this.value = value;
		}
		
		public <T extends Comparable<T>> T getProperty() { return (T)property; }
		public <V> V getValue() { return (V)value; }
		public void setProperty(IProperty<?> property) { this.property = property;}
		public void setValue(Object value) { this.value = value; }
	}
}
