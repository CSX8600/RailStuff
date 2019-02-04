package com.clussmanproductions.railstuff.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

private static int packetId = 0;
	
	public static SimpleNetworkWrapper INSTANCE = null;
	
	public PacketHandler() {}
	
	public static int nextID()
	{
		return packetId++;
	}
	
	public static void registerMessages(String channelName)
	{
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
		registerMessages();
	}
	
	private static void registerMessages()
	{
		INSTANCE.registerMessage(PacketGetIdentifierForAssignGUI.Handler.class, PacketGetIdentifierForAssignGUI.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketSetIdentifierForAssignGUI.Handler.class, PacketSetIdentifierForAssignGUI.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(PacketSetIdentifierOnServer.Handler.class, PacketSetIdentifierOnServer.class, nextID(), Side.SERVER);
		INSTANCE.registerMessage(PacketSetAllIdentifiersForClient.Handler.class, PacketSetAllIdentifiersForClient.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(PacketSetIdentifierForClient.Handler.class, PacketSetIdentifierForClient.class, nextID(), Side.CLIENT);
		INSTANCE.registerMessage(PacketSyncTEOnServer.Handler.class, PacketSyncTEOnServer.class, nextID(), Side.SERVER);
	}
}
