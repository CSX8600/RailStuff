package com.clussmanproductions.railstuff.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiButtonTextured extends GuiButton {

	ResourceLocation texture;
	boolean isSelected;
	public GuiButtonTextured(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation texture, boolean isSelected, String text) {
		super(buttonId, x, y, widthIn, heightIn, text);
		this.texture = texture;
		this.isSelected = isSelected;
		this.visible = false;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {            
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            
            if (this.isSelected)
            {
            	i = 2;
            }
            
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
            
            if (texture != null)
            {
    	        mc.getTextureManager().bindTexture(texture);
    	        this.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
            }
        }
	}
	
	@Deprecated
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
