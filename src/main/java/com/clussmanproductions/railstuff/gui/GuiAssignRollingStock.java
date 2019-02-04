package com.clussmanproductions.railstuff.gui;

import java.io.IOException;
import java.util.UUID;

import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSetIdentifierOnServer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiAssignRollingStock extends GuiScreen {
	private UUID id;
	GuiTextField text;
	GuiButton save;
	
	public GuiAssignRollingStock(UUID id)
	{
		this.id = id;
	}
	
	@Override
	public void initGui() {
		int horizontalCenter = width / 2;
		int verticalCenter = height / 2;
		text = new GuiTextField(0, fontRenderer, horizontalCenter - 150, verticalCenter, 300, 20);
		text.setMaxStringLength(11);
		text.setVisible(true);
		text.setFocused(true);
		save = new GuiButton(1, horizontalCenter - 150, verticalCenter + 30, 300, 20, "Save");
		
		buttonList.add(save);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawString(fontRenderer, "Stock ID", (width / 2) - 150, (height / 2) - 15, 16777215);
		text.drawTextBox();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	public void setText(String text)
	{
		this.text.setText(text);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 1)
		{
			PacketSetIdentifierOnServer packet = new PacketSetIdentifierOnServer();
			packet.id = id;
			packet.newName = text.getText();
			
			PacketHandler.INSTANCE.sendToServer(packet);
			Minecraft.getMinecraft().player.closeScreen();
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		text.textboxKeyTyped(typedChar, keyCode);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		this.text.updateCursorCounter();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.text.mouseClicked(mouseX, mouseY, mouseButton);
	}
}
