package com.clussmanproductions.railstuff.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiSignalEndPointList extends GuiScrollingList {

	private ArrayList<EndPointElement> endPointElements = new ArrayList<EndPointElement>();
	private int current;
	public GuiSignalEndPointList(Minecraft client, int width, int height, int top, int bottom, int left,
			int entryHeight, int screenWidth, int screenHeight) {
		super(client, width, height, top, bottom, left, entryHeight, screenWidth, screenHeight);
	}

	@Override
	protected int getSize() {
		return endPointElements.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		current = index;
	}

	@Override
	protected boolean isSelected(int index) {
		return this.current == index;
	}

	@Override
	protected void drawBackground() {
		
	}

	@Override
	protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
		if (slotIdx >= endPointElements.size())
		{
			return;
		}
		
		EndPointElement endPoint = endPointElements.get(slotIdx);
		endPoint.draw(Minecraft.getMinecraft().fontRenderer, left + 5, slotTop, tess);
	}
	
	public void add(EndPointElement element)
	{
		endPointElements.add(element);
	}

	public static class EndPointElement
	{
		private String value;
		public EndPointElement(String value)
		{
			this.value = value;
		}
		
		public void draw(FontRenderer fontRenderer, int entryLeft, int slotTop, Tessellator tess)
		{
			fontRenderer.drawString(value, entryLeft, slotTop, 0xFFFFFFFF);
		}
	}

	public void clear() {
		endPointElements.clear();
	}
}
