package com.clussmanproductions.railstuff.network;

import java.util.UUID;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.thirdparty.CommonAPI;
import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetIdentifierForAssignGUI implements IMessage {

	public UUID id;
	public int x;
	public int y;
	public int z;
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		id = tag.getUniqueId("uuid");
		x = tag.getInteger("x");
		y = tag.getInteger("y");
		z = tag.getInteger("z");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setUniqueId("uuid", id);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketGetIdentifierForAssignGUI, IMessage>
	{

		@Override
		public IMessage onMessage(PacketGetIdentifierForAssignGUI message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(PacketGetIdentifierForAssignGUI message, MessageContext ctx)
		{
			World world = ctx.getServerHandler().player.world;
			EntityRollingStock stock = world.getEntities(EntityRollingStock.class, p -> {return p.getPersistentID().equals(message.id);}).get(0);
			String apiTag = stock.tag;
			
			PacketSetIdentifierForAssignGUI packet = new PacketSetIdentifierForAssignGUI();
			packet.id = message.id;
			packet.name = apiTag;
			packet.x = message.x;
			packet.y = message.y;
			packet.z = message.z;
			
			PacketHandler.INSTANCE.sendTo(packet, ctx.getServerHandler().player);
		}
	}
}
