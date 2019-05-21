package com.clussmanproductions.railstuff.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class DoubleSignalBaseTileEntity extends TileEntitySyncable implements ITickable {

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		super.readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		return super.writeToNBT(compound);
	}
	
	@Override
	public NBTTagCompound getSyncPacket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readSyncPacket(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
