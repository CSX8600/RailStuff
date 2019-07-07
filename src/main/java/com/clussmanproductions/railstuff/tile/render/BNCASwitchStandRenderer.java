package com.clussmanproductions.railstuff.tile.render;

import javax.vecmath.Vector3f;

import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.BlockBNCASwitchStand;
import com.clussmanproductions.railstuff.tile.TileEntityBNCASwitchStand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;

public class BNCASwitchStandRenderer extends TileEntitySpecialRenderer<TileEntityBNCASwitchStand> {
	@Override
	public void render(TileEntityBNCASwitchStand te, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		IModel handleModel;
		try {
			handleModel = OBJLoader.INSTANCE.loadModel(new ResourceLocation(ModRailStuff.MODID, "models/block/bnca-stand-handle.obj"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockBNCASwitchStand.FACING);
		
		float yRotation = (-((facing.getHorizontalIndex() + 1) * 90)) + 180;
		IBakedModel baked = handleModel.bake(new TRSRTransformation(new Vector3f(-0.5F, 0F, 0.5F), null, new Vector3f(2, 2, 2), null), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
		
		int bright = te.getWorld().getCombinedLight(te.getPos(), 15728640);
        int brightX = bright % 65536;
        int brightY = bright / 65536;
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
        GlStateManager.translate(x, y, z);
        
        GlStateManager.translate(0.5, 0, 0.5);
        GlStateManager.rotate(yRotation, 0, 1, 0);
        GlStateManager.translate(-0.5, 0, -0.5);
        
        GlStateManager.translate(0, 0.14, 0.5);
        GlStateManager.rotate(-te.getRotationAmount(), 1, 0, 0);
        GlStateManager.translate(0, -0.14, -0.5);
        
        GlStateManager.disableTexture2D();
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightness(baked, ModBlocks.bnca_switch_stand.getDefaultState(), bright, true);
        GlStateManager.enableTexture2D();
        
//        Tessellator tess = Tessellator.getInstance();
//        BufferBuilder builder = tess.getBuffer();
//        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
//        
//        for(BakedQuad quad : baked.getQuads(null, null, 0))
//        {
//        	builder.addVertexData(quad.getVertexData());
//        }
//        
//        tess.draw();
        GlStateManager.popMatrix();
	}
	
	private BakedQuad updateQuadBrightness(BakedQuad quad, float light)
	{
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(DefaultVertexFormats.BLOCK);
		
		IVertexConsumer consumer = new VertexTransformer(builder)
		{
			@Override
			public void put(int element, float... data) {
				VertexFormatElement formatElement = DefaultVertexFormats.BLOCK.getElement(element);
				switch(formatElement.getUsage())
				{
					case COLOR:
						parent.put(element, data[0] * light, data[1] * light, data[2] * light, 1F);
						break;
					default:
						parent.put(element, data);
						break;
				}
			}
		};
		
		quad.pipe(consumer);
		return builder.build();
	}
	
//	@Override
//	public void renderTileEntityFast(TileEntityBNCASwitchStand te, double x, double y, double z, float partialTicks,
//			int destroyStage, float partial, BufferBuilder buffer) {
//		IModel handleModel;
//		try {
//			handleModel = OBJLoader.INSTANCE.loadModel(new ResourceLocation(ModRailStuff.MODID, "models/block/bnca-stand-handle.obj"));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		IBakedModel baked = handleModel.bake(new TRSRTransformation(new Vector3f(0.5F + (float)x, 0F + (float)y, 0.5F + (float)z), TRSRTransformation.quatFromXYZDegrees(new Vector3f(0, 90, 0)), new Vector3f(2, 2, 2), null), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
//		List<BakedQuad> quads = baked.getQuads(null, null, 0);
//				
////		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(te.getWorld(), baked, ModBlocks.bnca_switch_stand.getDefaultState(), te.getPos(), buffer, true);
////		
//		for(BakedQuad quad : quads)
//		{
//			buffer.addVertexData(quad.getVertexData());
//		}
//	}
}
