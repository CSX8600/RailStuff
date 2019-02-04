package com.clussmanproductions.railstuff.blocks.model;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.BlockBlueFlag;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

public class BakedModelBlueFlag extends BaseBakedModel {

	TextureAtlasSprite generic;
	TextureAtlasSprite blue_flag;
	UVMapping poleMappingSide = new UVMapping(
			0,0,
			0,16,
			1,16,
			1,0);
	UVMapping poleMappingEnd = new UVMapping(
			0,0,
			0,1,
			1,1,
			1,0);
	
	UVMapping signMapping = new UVMapping(
			0,0,
			0,6,
			9,6,
			9,0);
	
	public BakedModelBlueFlag(VertexFormat format) {
		super(format);
		
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		generic = map.getAtlasSprite(ModRailStuff.MODID + ":blocks/generic");
		blue_flag = map.getAtlasSprite(ModRailStuff.MODID + ":blocks/blue_flag");
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if (state == null)
		{
			return null;
		}
		
		ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
		
		BoxTextureCollection postCollection = new BoxTextureCollection(generic, generic, generic, generic, generic, generic, poleMappingSide, poleMappingSide, poleMappingSide, poleMappingSide, poleMappingEnd, poleMappingEnd);
		
		switch(state.getValue(BlockBlueFlag.FACING))
		{
			case NORTH:
				quads.addAll(createBox(4, -16, 8, 1, 16, 1, postCollection));
				quads.add(createQuad(v(9, 6, 8.5), v(9, 0, 8.5), v(0, 0, 8.5), v(0, 6, 8.5), blue_flag, signMapping));
				quads.add(createQuad(v(0, 6, 8.5), v(0, 0, 8.5), v(9, 0, 8.5), v(9, 6, 8.5), blue_flag, signMapping));
				break;
			case SOUTH:
				quads.addAll(createBox(12, -16, 8, 1, 16, 1, postCollection));
				quads.add(createQuad(v(16, 6, 8.5), v(16, 0, 8.5), v(7, 0, 8.5), v(7, 6, 8.5), blue_flag, signMapping));
				quads.add(createQuad(v(7, 6, 8.5), v(7, 0, 8.5), v(16, 0, 8.5), v(16, 6, 8.5), blue_flag, signMapping));
				break;
			case WEST:
				quads.addAll(createBox(8, -16, 11, 1, 16, 1, postCollection));
				quads.add(createQuad(v(8.5, 6, 16), v(8.5, 0, 16), v(8.5, 0, 7), v(8.5, 6, 7), blue_flag, signMapping));
				quads.add(createQuad(v(8.5, 6, 7), v(8.5, 0, 7), v(8.5, 0, 16), v(8.5, 6, 16), blue_flag, signMapping));
				break;
			case EAST:
				quads.addAll(createBox(8, -16, 4, 1, 16, 1, postCollection));
				quads.add(createQuad(v(8.5, 6, 9), v(8.5, 0, 9), v(8.5, 0, 0), v(8.5, 6, 0), blue_flag, signMapping));
				quads.add(createQuad(v(8.5, 6, 0), v(8.5, 0, 0), v(8.5, 0, 9), v(8.5, 6, 9), blue_flag, signMapping));
				break;
		}
				
		return quads;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return blue_flag;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		// TODO Auto-generated method stub
		return super.handlePerspective(cameraTransformType);
	}
}
