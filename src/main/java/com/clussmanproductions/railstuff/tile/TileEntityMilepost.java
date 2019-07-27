package com.clussmanproductions.railstuff.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TileEntityMilepost extends TileEntitySyncable {

	private String number= "";
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("number", number);
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		number = compound.getString("number");
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag.setString("number", number);
		return tag;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		number = tag.getString("number");
		super.handleUpdateTag(tag);
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getSyncPacket() {
		return getUpdateTag();
	}

	@Override
	public void readSyncPacket(NBTTagCompound tag) {
		handleUpdateTag(tag);
		markDirty();
		world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), world.getBlockState(getPos()), 3);
	}


	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number)
	{
		this.number = number;
	}

	public IBlockState getBlockState()
	{
		return world.getBlockState(getPos());
	}
}
