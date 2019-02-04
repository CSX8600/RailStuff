package com.clussmanproductions.railstuff.tile.render;

import org.lwjgl.opengl.GL11;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.BlockManualSwitchStand;
import com.clussmanproductions.railstuff.tile.TileEntityManualSwitchStand;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import scala.Tuple3;

public class ManualSwitchStandRenderer extends RendererBase<TileEntityManualSwitchStand> {
	private ResourceLocation generic = new ResourceLocation(ModRailStuff.MODID, "textures/blocks/generic.png");
	@Override
	public void render(TileEntityManualSwitchStand te, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		
		if (!(state.getBlock() instanceof BlockManualSwitchStand))
		{
			return;
		}
		
		EnumFacing facing = state.getValue(BlockManualSwitchStand.FACING);
		
		GlStateManager.pushMatrix();
		
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuffer();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		GlStateManager.translate(x, y, z);
		Tuple3<Double, Double, Double> poleTranslation = te.getPoleTranslation(facing);
		GlStateManager.translate(poleTranslation._1(), poleTranslation._2(), poleTranslation._3());
		GlStateManager.rotate(te.getPoleYRotation(facing), 0, 1, 0);
		buildMast(buf);
		
		tess.draw();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		GlStateManager.translate(-poleTranslation._1(), -poleTranslation._2(), -poleTranslation._3());
		Tuple3<Double, Double, Double> plateTranslation = te.getPlateTranslation(facing);
		GlStateManager.translate(plateTranslation._1(), plateTranslation._2(), plateTranslation._3());
		buildPlate(buf);
		
		tess.draw();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		GlStateManager.translate(-plateTranslation._1(), -plateTranslation._2(), -plateTranslation._3());
		Tuple3<Double, Double, Double> leverHorizontalTranslation = te.getLeverHorizontalTranslation(facing);
		GlStateManager.translate(leverHorizontalTranslation._1(), leverHorizontalTranslation._2(), leverHorizontalTranslation._3());
		buildLeverHorizontal(buf);
		
		tess.draw();
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		GlStateManager.translate(-leverHorizontalTranslation._1(), -leverHorizontalTranslation._2(), -leverHorizontalTranslation._3());
		Tuple3<Double, Double, Double> leverTranslation = te.getLeverTranslation(facing);
		GlStateManager.translate(leverTranslation._1(), leverTranslation._2(), leverTranslation._3());
		GlStateManager.rotate(-te.getLeverRotation(facing), 0, 0, 1);
		buildLever(buf, facing);
		
		tess.draw();
		
		GlStateManager.popMatrix();
	}
	
	private void buildMast(BufferBuilder buffer)
	{
		TextureInfo side = new TextureInfo(generic, 0, 0, 1, 9);
		TextureInfo end = new TextureInfo(generic, 0, 0, 1, 1);
		
		TextureInfoCollection collection = new TextureInfoCollection(
				side,
				end,
				side, 
				end, 
				side, 
				side);
		
		new Box(-0.5, 0,0.5, 1, 9, -1, collection).render(buffer);
	}

	private void buildPlate(BufferBuilder buffer)
	{
		ResourceLocation redPlate = new ResourceLocation(ModRailStuff.MODID, "textures/blocks/red_circle.png");
		TextureInfo plate = new TextureInfo(redPlate, 0, 0, 16, 16);
		TextureInfo other = new TextureInfo(generic, 0, 0, 0, 5);
		TextureInfoCollection collection = new TextureInfoCollection(
				other, 
				other,
				other,
				other, 
				plate, 
				plate);
		
		new Box(-0.5, 0, 0.5, -0.1, 5, 5, collection).render(buffer);
	}

	private void buildLeverHorizontal(BufferBuilder buffer)
	{
		TextureInfo side = new TextureInfo(generic, 0, 0, 3, 1);
		TextureInfo end = new TextureInfo(generic, 0, 0, 1, 1);
		TextureInfoCollection collection = new TextureInfoCollection(
				end,
				side,
				end,
				side,
				side,
				side);
		
		new Box(0.0, 0.0, 0.0, 4.0, 1.0, -1.0, collection).render(buffer);
	}
	
	private void buildLever(BufferBuilder buffer, EnumFacing facing)
	{
		TextureInfo side = new TextureInfo(generic, 0, 0, 1, 4);
		TextureInfo end = new TextureInfo(generic, 0, 0, 1, 1);
		TextureInfoCollection collection = new TextureInfoCollection(
				side,
				end,
				side,
				end,
				side,
				side);
		
		int xModifier = 1;
		
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST)
		{
			xModifier = 0;
		}
		
		new Box(-1 * xModifier, -3.0, 0.0, 1.0, 4.0, -1.0, collection).render(buffer);
	}
}
