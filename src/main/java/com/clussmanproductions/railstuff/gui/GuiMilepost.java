package com.clussmanproductions.railstuff.gui;

import java.io.IOException;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.tile.TileEntityMilepost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiMilepost extends GuiScreen {
	TileEntityMilepost milepost;
	GuiMilepostTextbox textbox;
	
	ResourceLocation guiTexture = new ResourceLocation(ModRailStuff.MODID, "textures/gui/milepost_gui.png");
	int texHeight = 256;
	int texWidth = 256;
	
	public GuiMilepost(TileEntityMilepost te)
	{
		milepost = te;
	}
	
	@Override
	public void initGui() {
		int horizontalCenter = width / 2;
		int verticalCenter = height / 2;
		
		textbox = new GuiMilepostTextbox(milepost.getNumber(), horizontalCenter - 72, verticalCenter - 112);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		int horizontalCenter = width / 2;
		int verticalCenter = height / 2;
		
		Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture);
		drawScaledCustomSizeModalRect(horizontalCenter - (texWidth / 2), verticalCenter - (texHeight / 2), 0, 0, 16, 16, texWidth, texHeight, 16, 16);
		
		textbox.draw(fontRenderer);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		textbox.keyTyped(typedChar);
	}
	
	@Override
	public void onGuiClosed() {
		milepost.setNumber(textbox.getText());
		milepost.sendSyncPacket();
	}
}
