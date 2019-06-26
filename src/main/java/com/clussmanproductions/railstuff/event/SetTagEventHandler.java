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

public class SetTagEventHandler
{
    @SubscribeEvent
    public static void setTag(TagEvent.SetTagEvent e) {

        //World world = DimensionManager.getWorld(DimensionManager.getRegisteredDimensions().get(DimensionType.OVERWORLD).firstInt());
        World world = null;
        for (World w : DimensionManager.getWorlds()) {
            if (w.getEntities(EntityRollingStock.class, p -> p.getPersistentID().equals(e.stockID)).size() > 0) {
                world = w;
                break;
            }
        }
        RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
        String croppedTag = e.tag.substring(0, Math.min(e.tag.length(), RollingStockIdentificationData.MAX_IDENTIFIER_LENGTH));
        data.setIdentifierGivenUUID(e.stockID, croppedTag);
        boolean overwrite = e.tag.length() <= RollingStockIdentificationData.MAX_IDENTIFIER_LENGTH;
        data.setOverwriteOcTagsGivenUUID(e.stockID, overwrite);

        PacketSetIdentifierForClient packet = new PacketSetIdentifierForClient();
        packet.id = e.stockID;
        packet.name = croppedTag;
        packet.overwrite = overwrite;
        PacketHandler.INSTANCE.sendToAll(packet);

    }

}
