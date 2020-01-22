package com.clussmanproductions.railstuff.event;

import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;

import cam72cam.immersiverailroading.entity.EntityRollingStock;
import cam72cam.immersiverailroading.thirdparty.event.TagEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GetTagEventHandler {

    @SubscribeEvent
    public static void getTag(TagEvent.GetTagEvent e) {

        World world = DimensionManager.getWorld(DimensionManager.getRegisteredDimensions().get(DimensionType.OVERWORLD).firstInt());
        /* In case Multi-Dimension Support gets added to RollingStockIdentificationData
         * 
        World world = null;
        for (World w : DimensionManager.getWorlds()) {
            if (w.getEntities(Entity.class, p -> {return p.getPersistentID().equals(e.stockID);}).size() > 0) {
                world = w;
                break;
            }
        }*/
        RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
        if (data.getOverwriteOcTagsByUUID(e.stockID))
            e.tag = data.getIdentifierByUUID(e.stockID);

    }
}
