package com.clussmanproductions.railstuff.tile.render;

import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.blocks.BlockMilepost;
import com.clussmanproductions.railstuff.tile.TileEntityMilepost;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

public class MilepostRenderer extends TileEntitySpecialRenderer<TileEntityMilepost> {
	@Override
	public void render(TileEntityMilepost te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		IBlockState state = te.getBlockState();
		if (state.getBlock() != ModBlocks.milepost)
		{
			return;
		}
		
		int facingIndex = state.getValue(BlockMilepost.FACING).getHorizontalIndex();
		//facingIndex += 2;
		//if (facingIndex > 3) { facingIndex -= 4; }
		
		int yRotation = 90 * facingIndex;
		
		GlStateManager.pushMatrix();

		float scale = 2F; 
		float textFactor = 0.016666668F;
		
		float finalScale = scale * textFactor;
		
		GlStateManager.translate(x+ .5, y+ .5, z+ .5);
		GlStateManager.rotate(yRotation, 0, 1, 0);
		GlStateManager.translate(-(x+ .5), -(y+ .5), -(z+ .5));
		
		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.scale(-finalScale, -finalScale, finalScale);
		
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int stringWidth = fontRenderer.getStringWidth(te.getNumber());
		int digitsToMove = 3 - te.getNumber().length();
		float numberBuffer = (digitsToMove * 6) * finalScale; 
		
		GlStateManager.translate((x + .3125 - numberBuffer) / -finalScale, (y + 1.45) / -finalScale, (z - 0.00725) / finalScale);	
		fontRenderer.drawString(te.getNumber(), 0, 0, 0x000000);
		GlStateManager.translate(stringWidth, 0, 0.5);
		GlStateManager.rotate(-180, 0, 1, 0);
		fontRenderer.drawString(te.getNumber(), 0, 0, 0x000000);

		GlStateManager.popMatrix();
	}
}
