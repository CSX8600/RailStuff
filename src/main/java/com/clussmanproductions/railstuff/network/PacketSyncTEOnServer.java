package com.clussmanproductions.railstuff.network;

import com.clussmanproductions.railstuff.tile.TileEntitySyncable;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncTEOnServer implements IMessage {

	public NBTTagCompound nbt;
	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static class Handler implements IMessageHandler<PacketSyncTEOnServer, IMessage>
	{

		@Override
		public IMessage onMessage(PacketSyncTEOnServer message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		public void handle(PacketSyncTEOnServer message, MessageContext ctx)
		{
			World world = ctx.getServerHandler().player.world;
			NBTTagCompound tag = message.nbt;
			BlockPos tePos = new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
			TileEntitySyncable syncableTE = (TileEntitySyncable)world.getTileEntity(tePos);
			if (syncableTE == null)
			{
				return;
			}
			
			syncableTE.readSyncPacket(tag);
			syncableTE.markDirty();
		}
	}
}
