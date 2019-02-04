package com.clussmanproductions.railstuff.gui;

import java.io.IOException;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;
import com.clussmanproductions.railstuff.tile.SignalTileEntity.Aspect;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

public class GuiSignal extends GuiScreen {
	private SignalTileEntity signal;
	GuiButton modeManual;
	GuiButton modeOccupation;
	GuiButton modeDiamond;
	GuiTextField name;
	
	// Manual options
	GuiButtonTextured unpoweredDark;
	GuiButtonTextured unpoweredRed;
	GuiButtonTextured unpoweredRedFlash;
	GuiButtonTextured unpoweredYellow;
	GuiButtonTextured unpoweredYellowFlash;
	GuiButtonTextured unpoweredGreen;
	GuiButtonTextured poweredDark;
	GuiButtonTextured poweredRed;
	GuiButtonTextured poweredRedFlash;
	GuiButtonTextured poweredYellow;
	GuiButtonTextured poweredYellowFlash;
	GuiButtonTextured poweredGreen;
	
	public GuiSignal(SignalTileEntity te)
	{
		signal = te;
	}
	
	@Override
	public void initGui() {
		int horizontalCenter = width / 2;
		int verticalCenter = height / 2;
		name = new GuiTextField(ComponentIDs.NAME, fontRenderer, horizontalCenter - 50, 20, 100, 20);
		name.setFocused(true);
		name.setText(signal.getName());
		modeManual = new GuiButton(ComponentIDs.MODE_MANUAL, 20, 20, 40, 20, "Manual");
		
		switch(signal.getMode())
		{
			case Manual:
				int verticalUpper = verticalCenter - 24;
				int verticalLower = verticalCenter + 24;
				
				ResourceLocation dark = new ResourceLocation(ModRailStuff.MODID, "textures/gui/dark.png");
				ResourceLocation red = new ResourceLocation(ModRailStuff.MODID, "textures/gui/red.png");
				ResourceLocation red_flash = new ResourceLocation(ModRailStuff.MODID, "textures/gui/red_flash.png");
				ResourceLocation yellow = new ResourceLocation(ModRailStuff.MODID, "textures/gui/yellow.png");
				ResourceLocation yellow_flash = new ResourceLocation(ModRailStuff.MODID, "textures/gui/yellow_flash.png");
				ResourceLocation green = new ResourceLocation(ModRailStuff.MODID, "textures/gui/green.png");				
				
				unpoweredDark = new GuiButtonTextured(ComponentIDs.UNPOWERED_DARK, horizontalCenter - 68, verticalUpper, 16, 16, dark, false);
				unpoweredRed = new GuiButtonTextured(ComponentIDs.UNPOWERED_RED, horizontalCenter - 44, verticalUpper, 16, 16, red, false);
				unpoweredRedFlash = new GuiButtonTextured(ComponentIDs.UNPOWERED_RED_FLASH, horizontalCenter - 20, verticalUpper, 16, 16, red_flash, false);
				unpoweredYellow = new GuiButtonTextured(ComponentIDs.UNPOWERED_YELLOW, horizontalCenter + 4, verticalUpper, 16, 16, yellow, false);
				unpoweredYellowFlash = new GuiButtonTextured(ComponentIDs.UNPOWERED_YELLOW_FLASH, horizontalCenter + 28, verticalUpper, 16, 16, yellow_flash, false);
				unpoweredGreen = new GuiButtonTextured(ComponentIDs.UNPOWERED_GREEN, horizontalCenter + 52, verticalUpper, 16, 16, green, false);
				
				poweredDark = new GuiButtonTextured(ComponentIDs.POWERED_DARK, horizontalCenter - 68, verticalLower, 16, 16, dark, false);
				poweredRed = new GuiButtonTextured(ComponentIDs.POWERED_RED, horizontalCenter - 44, verticalLower, 16, 16, red, false);
				poweredRedFlash = new GuiButtonTextured(ComponentIDs.POWERED_RED_FLASH, horizontalCenter - 20, verticalLower, 16, 16, red_flash, false);
				poweredYellow = new GuiButtonTextured(ComponentIDs.POWERED_YELLOW, horizontalCenter + 4, verticalLower, 16, 16, yellow, false);
				poweredYellowFlash = new GuiButtonTextured(ComponentIDs.POWERED_YELLOW_FLASH, horizontalCenter + 28, verticalLower, 16, 16, yellow_flash, false);
				poweredGreen = new GuiButtonTextured(ComponentIDs.POWERED_GREEN, horizontalCenter + 52, verticalLower, 16, 16, green, false);
				break;
		}
		
		setButtonSelections();
		
		buttonList.addAll(ImmutableSet
				.<GuiButton>builder()
				.add(modeManual)
				.add(unpoweredDark)
				.add(unpoweredRed)
				.add(unpoweredRedFlash)
				.add(unpoweredYellow)
				.add(unpoweredYellowFlash)
				.add(unpoweredGreen)
				.add(poweredDark)
				.add(poweredRed)
				.add(poweredRedFlash)
				.add(poweredYellow)
				.add(poweredYellowFlash)
				.add(poweredGreen)
				.build());
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

//		drawDefaultBackground();
		
		int horizontalCenter = width / 2;
		int verticalCenter = height / 2;
		int colorWhite = 16777215;
		
		name.drawTextBox();
		int stringWidth = fontRenderer.getStringWidth("Name:");
		drawString(fontRenderer, "Name:", horizontalCenter - stringWidth - 70, 25, colorWhite);
		
		switch(signal.getMode())
		{
			case Manual:
				int verticalUpper = verticalCenter - 24;
				int verticalLower = verticalCenter + 24;
				
				stringWidth = fontRenderer.getStringWidth("Unpowered aspect:");
				drawString(fontRenderer, "Unpowered aspect:", horizontalCenter - stringWidth - 88, verticalUpper + 5, colorWhite);
				
				stringWidth = fontRenderer.getStringWidth("Powered aspect:");
				drawString(fontRenderer, "Powered aspect:", horizontalCenter - stringWidth - 88, verticalLower + 5, colorWhite);
				break;
		}
	}
	
	private void setButtonSelections()
	{
		GuiButtonTextured b = null;
		switch(signal.getUnpoweredAspect())
		{
			case Dark:
				b = unpoweredDark;
				break;
			case Red:
				b = unpoweredRed;
				break;
			case RedFlashing:
				b = unpoweredRedFlash;
				break;
			case Yellow:
				b = unpoweredYellow;
				break;
			case YellowFlashing:
				b = unpoweredYellowFlash;
				break;
			case Green:
				b = unpoweredGreen;
				break;
		}
		
		b.setSelected(true);
		
		switch(signal.getPoweredAspect())
		{
			case Dark:
				b = poweredDark;
				break;
			case Red:
				b = poweredRed;
				break;
			case RedFlashing:
				b = poweredRedFlash;
				break;
			case Yellow:
				b = poweredYellow;
				break;
			case YellowFlashing:
				b = poweredYellowFlash;
				break;
			case Green:
				b = poweredGreen;
				break;
		}
		
		b.setSelected(true);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id > 1 && button.id <= 13)
		{
			unselectAllUnpowered();
			unselectAllPowered();
			
			switch(button.id)
			{
				case ComponentIDs.UNPOWERED_DARK:
					signal.setUnpoweredAspect(Aspect.Dark);
					break;
				case ComponentIDs.UNPOWERED_RED:
					signal.setUnpoweredAspect(Aspect.Red);
					break;
				case ComponentIDs.UNPOWERED_RED_FLASH:
					signal.setUnpoweredAspect(Aspect.RedFlashing);
					break;
				case ComponentIDs.UNPOWERED_YELLOW:
					signal.setUnpoweredAspect(Aspect.Yellow);
					break;
				case ComponentIDs.UNPOWERED_YELLOW_FLASH:
					signal.setUnpoweredAspect(Aspect.YellowFlashing);
					break;
				case ComponentIDs.UNPOWERED_GREEN:
					signal.setUnpoweredAspect(Aspect.Green);
					break;
				case ComponentIDs.POWERED_DARK:
					signal.setPoweredAspect(Aspect.Dark);
					break;
				case ComponentIDs.POWERED_RED:
					signal.setPoweredAspect(Aspect.Red);
					break;
				case ComponentIDs.POWERED_RED_FLASH:
					signal.setPoweredAspect(Aspect.RedFlashing);
					break;
				case ComponentIDs.POWERED_YELLOW:
					signal.setPoweredAspect(Aspect.Yellow);
					break;
				case ComponentIDs.POWERED_YELLOW_FLASH:
					signal.setPoweredAspect(Aspect.YellowFlashing);
					break;
				case ComponentIDs.POWERED_GREEN:
					signal.setPoweredAspect(Aspect.Green);
					break;
			}
			
			setButtonSelections();
		}
	}
	
	private void unselectAllUnpowered()
	{
		unpoweredDark.setSelected(false);
		unpoweredRed.setSelected(false);
		unpoweredRedFlash.setSelected(false);
		unpoweredYellow.setSelected(false);
		unpoweredYellowFlash.setSelected(false);
		unpoweredGreen.setSelected(false);
	}
	
	private void unselectAllPowered()
	{
		poweredDark.setSelected(false);
		poweredRed.setSelected(false);
		poweredRedFlash.setSelected(false);
		poweredYellow.setSelected(false);
		poweredYellowFlash.setSelected(false);
		poweredGreen.setSelected(false);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		name.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		name.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		name.updateCursorCounter();
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void onGuiClosed() {
		signal.setName(name.getText());
		signal.sendSyncPacket();
	}
	
	private static class ComponentIDs
	{
		public static final int NAME = 0;
		public static final int MODE_MANUAL = 1;
		public static final int UNPOWERED_DARK = 2;
		public static final int UNPOWERED_RED = 3;
		public static final int UNPOWERED_RED_FLASH = 4;
		public static final int UNPOWERED_YELLOW = 5;
		public static final int UNPOWERED_YELLOW_FLASH = 6;
		public static final int UNPOWERED_GREEN = 7;
		public static final int POWERED_DARK = 8;
		public static final int POWERED_RED = 9;
		public static final int POWERED_RED_FLASH = 10;
		public static final int POWERED_YELLOW = 11;
		public static final int POWERED_YELLOW_FLASH = 12;
		public static final int POWERED_GREEN = 13;
	}
}
