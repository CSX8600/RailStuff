package com.clussmanproductions.railstuff.tile;

import com.clussmanproductions.railstuff.util.BlockPosUtils;
import com.clussmanproductions.railstuff.util.EnumAspect;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
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
	boolean signalHeadDiverageOn = false;
	int signalHeadMainFlashDelay = 0;
	int signalHeadDiverageFlashDelay = 0;
	
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
		// TODO Auto-generated method stub
		
	}

}
