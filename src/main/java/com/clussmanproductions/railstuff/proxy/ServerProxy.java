package com.clussmanproductions.railstuff.proxy;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSetIdentifierForClient;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber//(Side.SERVER)
public class ServerProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(this.getClass());
    }

    static HashMap<UUID, String> tags = new HashMap<>();

    @SubscribeEvent
    public static void onTick(TickEvent.WorldTickEvent e) {

        World world = e.world;

        for (EntityRollingStock stock : world.getEntities(EntityRollingStock.class, p -> {return true;})) {

            UUID uuid = stock.getPersistentID();
            String tag = stock.tag;

            if (tags.containsKey(uuid) && tags.get(uuid).equals(tag))
            {
                continue;
            }

            tags.put(uuid, tag);

            PacketSetIdentifierForClient packet = new PacketSetIdentifierForClient();
            packet.name = tag;
            packet.id = uuid;
            PacketHandler.INSTANCE.sendToAll(packet);
        }
    }
}
