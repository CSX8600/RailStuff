package com.clussmanproductions.railstuff.network;

import java.util.UUID;
import org.apache.logging.log4j.Level;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.thirdparty.CommonAPI;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.google.common.base.Predicate;
import com.jcraft.jogg.Packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class PacketSetIdentifierOnServer implements IMessage {

	public UUID id;
	public String newName;
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		id = tag.getUniqueId("id");
		newName = tag.getString("newName");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("id", id);
		tag.setString("newName", newName);
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

			EntityRollingStock stock = (EntityRollingStock) world.getEntities(EntityRollingStock.class, p -> {return p.getPersistentID().equals(message.id);}).get(0);
			stock.tag = message.newName;

			PacketSetIdentifierForClient packet = new PacketSetIdentifierForClient();
			packet.id = message.id;
			packet.name = message.newName;
			PacketHandler.INSTANCE.sendToAll(packet);
		}
	}

}
