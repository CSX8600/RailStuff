package com.clussmanproductions.railstuff.event;

import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSetIdentifierForClient;

import cam72cam.immersiverailroading.thirdparty.event.TagEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SetTagEventHandler
{
    @SubscribeEvent
    public static void setTag(TagEvent.SetTagEvent e) {

        World world = DimensionManager.getWorld(DimensionManager.getRegisteredDimensions().get(DimensionType.OVERWORLD).firstInt());
        /* In case Multi-Dimension Support gets added to RollingStockIdentificationData
         * 
        World world = null;
        for (World w : DimensionManager.getWorlds()) {
            if (w.getEntities(EntityRollingStock.class, p -> p.getPersistentID().equals(e.stockID)).size() > 0) {
                world = w;
                break;
            }
        }*/
        RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
        
        if (!data.getOverwriteOcTagsByUUID(e.stockID)) return;
        
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
