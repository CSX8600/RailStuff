package com.clussmanproductions.railstuff.event;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.thirdparty.event.TagEvent;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSetIdentifierForClient;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

@Mod.EventBusSubscriber
public class SetTagEventHandler
{
    @SubscribeEvent
    public static void setTag(TagEvent.SetTagEvent e) {

        ModRailStuff.logger.log(Level.INFO, "GetTag");
        //World world = DimensionManager.getWorld(DimensionManager.getRegisteredDimensions().get(DimensionType.OVERWORLD).firstInt());
        World world = null;
        for (World w : DimensionManager.getWorlds()) {
            if (w.getEntities(EntityRollingStock.class, p -> {return p.getPersistentID().equals(e.stockID);}).size() > 0) {
                world = w;
                break;
            }
        }
        RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
        data.setIdentifierGivenUUID(e.stockID, e.tag);

        PacketSetIdentifierForClient packet = new PacketSetIdentifierForClient();
        packet.id = e.stockID;
        packet.name = e.tag;
        PacketHandler.INSTANCE.sendToAll(packet);

    }

}
