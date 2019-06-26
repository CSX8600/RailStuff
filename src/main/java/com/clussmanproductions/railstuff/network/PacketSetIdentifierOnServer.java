package com.clussmanproductions.railstuff.network;

import java.util.UUID;

import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.jcraft.jogg.Packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetIdentifierOnServer implements IMessage {

	public UUID id;
	public String newName;
	public boolean overwrite;
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		id = tag.getUniqueId("id");
		newName = tag.getString("newName");
		overwrite = tag.getBoolean("overwrite");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("id", id);
		tag.setString("newName", newName);
		tag.setBoolean("overwrite", overwrite);
		ByteBufUtils.writeTag(buf, tag);
	}
	
	public static class Handler implements IMessageHandler<PacketSetIdentifierOnServer, IMessage>
	{

		@Override
		public IMessage onMessage(PacketSetIdentifierOnServer message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(PacketSetIdentifierOnServer message, MessageContext ctx)
		{
			World world = ctx.getServerHandler().player.world;
			RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
			data.setIdentifierGivenUUID(message.id, message.newName);
			data.setOverwriteOcTagsGivenUUID(message.id, message.overwrite);
			
			PacketSetIdentifierForClient packet = new PacketSetIdentifierForClient();
			packet.id = message.id;
			packet.name = message.newName;
			packet.overwrite = message.overwrite;
			PacketHandler.INSTANCE.sendToAll(packet);
		}
	}

}
