package com.clussmanproductions.railstuff.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiButtonTextured extends GuiButton {

	ResourceLocation texture;
	boolean isSelected;
	public GuiButtonTextured(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation texture, boolean isSelected) {
		super(buttonId, x, y, widthIn, heightIn, "");
		this.texture = texture;
		this.isSelected = isSelected;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (this.visible)
        {
            super.drawButton(mc, mouseX, mouseY, partialTicks);
            
            mc.getTextureManager().bindTexture(texture);
            this.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
            
            if (isSelected)
            {
            	drawSelectedBox();
            }
        }
	}
	
	private void drawSelectedBox()
	{
		GlStateManager.disableTexture2D();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder builder = tess.getBuffer();
		builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		builder.pos(this.x - 5.0, this.y - 5.0, this.zLevel).color(0, 255, 0, 255).endVertex();
		builder.pos(this.x + this.width + 5.0, this.y - 5.0, this.zLevel).color(0, 255, 0, 255).endVertex();
		
		builder.pos(this.x + this.width + 5.0, this.y - 5.0, this.zLevel).color(0, 255, 0, 255).endVertex();
		builder.pos(this.x + this.width + 5.0, this.y + this.height + 5.0, this.zLevel).color(0, 255, 0, 255).endVertex();
		
		builder.pos(this.x + this.width + 5.0, this.y + this.height + 5.0, this.zLevel).color(0, 255, 0, 255).endVertex();
		builder.pos(this.x - 5.0, this.y + this.height + 5.0, this.zLevel).color(0, 255, 0, 255).endVertex();
		
		builder.pos(this.x - 5.0, this.y + this.height + 5.0, this.zLevel).color(0, 255, 0, 255).endVertex();
		builder.pos(this.x - 5.0, this.y - 5.0, this.zLevel).color(0, 255, 0, 255).endVertex();
		tess.draw();
		GlStateManager.enableTexture2D();
	}
	
	public void setSelected(boolean selected)
	{
		this.isSelected = selected;
	}
}
