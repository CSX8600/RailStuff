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

public class PacketSetIdentifierForAssignGUI implements IMessage {

	public UUID id;
	public String name;
	public boolean overwrite;
	public int x;
	public int y;
	public int z;
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		id = tag.getUniqueId("id");
		name = tag.getString("name");
		overwrite = tag.getBoolean("overwrite");
		x = tag.getInteger("x");
		y = tag.getInteger("y");
		z = tag.getInteger("z");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("id", id);
		tag.setString("name", name);
		tag.setBoolean("overwrite", overwrite);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketSetIdentifierForAssignGUI, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSetIdentifierForAssignGUI message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(PacketSetIdentifierForAssignGUI message, MessageContext ctx)
		{
			ClientProxy.handleSetIdentifierForAssignGUI(message);
		}
	}
}
