package com.clussmanproductions.railstuff.event;

import cam72cam.immersiverailroading.thirdparty.event.TagEvent;
import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.SERVER)
public class GetTagEventHandler {

    @SubscribeEvent
    public static void getTag(TagEvent.GetTagEvent e) {



        World world = null;
        RollingStockIdentificationData data = RollingStockIdentificationData.get(world);
        e.tag = data.getIdentifierByUUID(e.stockID);

    }
}
