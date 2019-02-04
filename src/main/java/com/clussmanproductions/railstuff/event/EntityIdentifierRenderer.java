package com.clussmanproductions.railstuff.event;


import java.util.List;

import org.lwjgl.opengl.GL11;

import com.clussmanproductions.railstuff.data.RollingStockIdentificationData;
import com.clussmanproductions.railstuff.item.ItemPaperwork;
import com.clussmanproductions.railstuff.item.ItemRollingStockAssigner;
import com.clussmanproductions.railstuff.network.PacketGetIdentifierForAssignGUI;
import com.clussmanproductions.railstuff.network.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class EntityIdentifierRenderer {	
	public static void renderNameTag(Entity entity, float posX, float posY,
			float posX2, RenderManager renderManager, FontRenderer fontRenderer, String name) {

			float f = 1.6F;
			float f1 = 0.016666668F * f;
			
			// Push for Text
			GlStateManager.pushMatrix();
			GL11.glColor3f(0F, 0F, 1F);
			GL11.glTranslatef((float)posX, (float)posY + 7F, (float)posX2);
			GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-f1, -f1, f1);
			
			GlStateManager.disableLighting();
			byte b0 = 0;
			
			fontRenderer.drawString(name, -fontRenderer.getStringWidth(name) / 2, b0, 255);
			fontRenderer.drawString(name, -fontRenderer.getStringWidth(name) / 2, b0, 255);
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();

			// Push for Line
			GlStateManager.pushMatrix();
			GlStateManager.color(0, 0, 1, 1);
			GL11.glTranslatef((float)posX, (float)posY + 7F, (float)posX2);
			GL11.glScalef(f1, f1, f1);
			
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			
			GlStateManager.glLineWidth(1);
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder builder = tess.getBuffer();
			
			builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			builder.pos(posX, posY, posX2).endVertex();
			builder.pos(posX, posY - 175, posX2).endVertex();

			tess.draw();

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			
			GL11.glColor3f(1F, 1F, 1F);
			GL11.glPopMatrix();
			}

	@SubscribeEvent
	public static void renderWorldEvent(RenderWorldLastEvent e)
	{
		ItemStack heldItem = Minecraft.getMinecraft().player.inventory.getCurrentItem();
		if (heldItem == null || !(heldItem.getItem() instanceof ItemPaperwork))
		{
			return;
		}
		
		Class rollingStockClass = null;
		try {
			rollingStockClass = Class.forName("cam72cam.immersiverailroading.entity.EntityRollingStock");
		} catch (ClassNotFoundException e1) {
			return;
		}
		List<Entity> entities = Minecraft.getMinecraft().world.getLoadedEntityList();
		RollingStockIdentificationData data = RollingStockIdentificationData.get(Minecraft.getMinecraft().world);
		
		for(Entity entity : entities)
		{
			if (!rollingStockClass.isAssignableFrom(entity.getClass()))
			{
				continue;
			}
			String name = data.getIdentifierByUUID(entity.getPersistentID());
			if (name.equals(""))
			{
				continue;
			}
			
			double x = interpolateValue(entity.prevPosX, entity.posX, e.getPartialTicks()) - Minecraft.getMinecraft().getRenderManager().renderViewEntity.posX;
			double y = interpolateValue(entity.prevPosY, entity.posY, e.getPartialTicks()) - Minecraft.getMinecraft().getRenderManager().renderViewEntity.posY;
			double z = interpolateValue(entity.prevPosZ, entity.posZ, e.getPartialTicks()) - Minecraft.getMinecraft().getRenderManager().renderViewEntity.posZ;
						
			renderNameTag(entity, (float)x, (float)y, (float)z, Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderManager().getFontRenderer(), name);
		}
	}
	
	private static double interpolateValue(double start, double end, double pct)
    {
        return start + (end - start) * pct;
    }

	@SubscribeEvent
	public static void playerInteract(PlayerInteractEvent.EntityInteract e)
	{
		Class rollingStockClass = null;
		try {
			rollingStockClass = Class.forName("cam72cam.immersiverailroading.entity.EntityRollingStock");
		} catch (ClassNotFoundException e1) {
			return;
		}
		
		if (!rollingStockClass.isAssignableFrom(e.getTarget().getClass()))
		{
			return;
		}
		
		if (!(e.getEntityPlayer().inventory.getCurrentItem().getItem() instanceof ItemRollingStockAssigner))
		{
			return;
		}
		
		e.setCanceled(true);
		Entity entity = e.getTarget();
		int x = (int)Math.floor(entity.posX);
		int y = (int)Math.floor(entity.posY);
		int z = (int)Math.floor(entity.posZ);
		
		PacketGetIdentifierForAssignGUI packet = new PacketGetIdentifierForAssignGUI();
		packet.id = entity.getPersistentID();
		packet.x = x;
		packet.y = y;
		packet.z = z;
		
		PacketHandler.INSTANCE.sendToServer(packet);
	}
}
