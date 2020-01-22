package com.clussmanproductions.railstuff.network;

import com.clussmanproductions.railstuff.tile.SignalTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetSignalOccupationOriginOnServer implements IMessage {

	public BlockPos tePos;
	public BlockPos newPosition;
	@Override
	public void fromBytes(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		
		tePos = new BlockPos(x, y, z);
		
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		
		newPosition = new BlockPos(x, y, z);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(tePos.getX());
		buf.writeInt(tePos.getY());
		buf.writeInt(tePos.getZ());
		buf.writeInt(newPosition.getX());
		buf.writeInt(newPosition.getY());
		buf.writeInt(newPosition.getZ());
	}

	public static class Handler implements IMessageHandler<PacketSetSignalOccupationOriginOnServer, IMessage>
	{

		@Override
		public IMessage onMessage(PacketSetSignalOccupationOriginOnServer message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(PacketSetSignalOccupationOriginOnServer message, MessageContext ctx)
		{
			World world = ctx.getServerHandler().player.world;
			SignalTileEntity te = (SignalTileEntity)world.getTileEntity(message.tePos);
			te.setOccupationOrigin(message.newPosition);
		}
	}
}
