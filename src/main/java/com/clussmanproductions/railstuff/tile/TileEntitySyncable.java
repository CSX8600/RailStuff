package com.clussmanproductions.railstuff.tile;

import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSyncTEOnServer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntitySyncable extends TileEntity {
	public abstract NBTTagCompound getSyncPacket();
	public abstract void readSyncPacket(NBTTagCompound tag);
	public void sendSyncPacket()
	{
		NBTTagCompound tag = getSyncPacket();
		BlockPos currentPos = getPos();
		tag.setInteger("x", currentPos.getX());
		tag.setInteger("y", currentPos.getY());
		tag.setInteger("z", currentPos.getZ());
		
		PacketSyncTEOnServer packet = new PacketSyncTEOnServer();
		packet.nbt = tag;
		PacketHandler.INSTANCE.sendToServer(packet);
	}
}
