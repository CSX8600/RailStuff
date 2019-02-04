package com.clussmanproductions.railstuff.network;

import java.util.HashMap;
import java.util.UUID;

import com.clussmanproductions.railstuff.proxy.ClientProxy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetAllIdentifiersForClient implements IMessage {

	public HashMap<UUID, String> values;
	@Override
	public void fromBytes(ByteBuf buf) {
		values = new HashMap<UUID, String>();
		
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		for(String key : tag.getKeySet())
		{
			if (!key.contains("_key"))
			{
				continue;
			}
			
			String formattedKey = key.replace("_keyLeast", "_key").replace("_keyMost", "_key");
			
			UUID id = tag.getUniqueId(formattedKey);
			String valueKey = formattedKey.replace("_key", "_value");
			String value = tag.getString(valueKey);
			
			values.put(id, value);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		
		int index = 0;
		for(UUID key : values.keySet())
		{
			tag.setUniqueId("item" + index + "_key", key);
			tag.setString("item" + index + "_value", values.get(key));
			
			index++;
		}
		
		ByteBufUtils.writeTag(buf, tag);
	}
	
	public static class Handler implements IMessageHandler<PacketSetAllIdentifiersForClient, IMessage>
	{

		@Override
		public IMessage onMessage(PacketSetAllIdentifiersForClient message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(PacketSetAllIdentifiersForClient message, MessageContext ctx)
		{
			ClientProxy.handleSetAllIdentifiersForClient(message);
		}
	}
}
