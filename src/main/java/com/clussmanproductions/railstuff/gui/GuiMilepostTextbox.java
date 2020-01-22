package com.clussmanproductions.railstuff.gui;

import com.clussmanproductions.railstuff.ModRailStuff;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class GuiMilepostTextbox {

	private String text;
	private int x;
	private int y;
	private final float scaleFactor = 9F;
	
	public GuiMilepostTextbox(String text, int x, int y)
	{
		this.text = text;
		this.x = x;
		this.y = y;
	}
	
	public void draw(FontRenderer fontRenderer)
	{
		int black = 0x000000;
		
		GlStateManager.scale(scaleFactor,scaleFactor,scaleFactor);
        float mSize = (float)Math.pow(scaleFactor,-1);
        
        String textToDraw = text;
        
        if (text.length() < 3)
        {
        	textToDraw += "_";
        }
        
        fontRenderer.drawString(textToDraw,Math.round(x / scaleFactor),Math.round(y / scaleFactor),black);
        GlStateManager.scale(mSize,mSize,mSize);
	}
	
	public void keyTyped(char key)
	{
		if ((key >= '0' && key <= '9') || key == '.')
		{
			if (text.length() < 3)
			{
				text += key;
			}
		}
		
		if (key == '\b' && text.length() > 0)
		{
			text = text.substring(0, text.length() - 1);
		}
	}

	public String getText() {
		return text;
	}
}
