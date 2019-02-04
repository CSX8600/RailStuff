package com.clussmanproductions.railstuff.network;

import java.util.UUID;

import com.clussmanproductions.railstuff.proxy.ClientProxy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetIdentifierForClient implements IMessage {
	public UUID id;
	public String name;
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		id = tag.getUniqueId("id");
		name = tag.getString("name");
	}
	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("id", id);
		tag.setString("name", name);
		
		ByteBufUtils.writeTag(buf, tag);
	}
	
	public static class Handler implements IMessageHandler<PacketSetIdentifierForClient, IMessage>
	{

		@Override
		public IMessage onMessage(PacketSetIdentifierForClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(PacketSetIdentifierForClient message, MessageContext ctx)
		{
			ClientProxy.handleSetIdentifierForClient(message);
		}
	}
}
