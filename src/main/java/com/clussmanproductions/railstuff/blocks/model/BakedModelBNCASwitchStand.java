package com.clussmanproductions.railstuff.blocks.model;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.commons.lang3.tuple.Pair;

import com.clussmanproductions.railstuff.blocks.BlockBNCASwitchStand;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;

public class BakedModelBNCASwitchStand extends PerspectiveMapWrapper {

	IBakedModel originalModel;
	VertexFormat format;
	public BakedModelBNCASwitchStand(VertexFormat format, IBakedModel originalModel) {
		this.format = format;
		this.originalModel = originalModel;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		List<BakedQuad> quads = originalModel.getQuads(state, side, rand);
		return transformedQuads(state.getValue(BlockBNCASwitchStand.FACING), quads);
	}
	
	private List<BakedQuad> transformedQuads(EnumFacing facing, List<BakedQuad> quads)
	{
		List<BakedQuad> newQuads = new ArrayList<BakedQuad>();
		for(BakedQuad quad : quads)
		{
			newQuads.add(transformQuad(facing, quad));
		}
		
		return newQuads;
	}
	
	private BakedQuad transformQuad(EnumFacing rotation, BakedQuad quad)
	{
		UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		
		float rotationLeftAmount = 0F;
		float rotationRightAmount = 0F;
		switch(rotation)
		{
			case WEST:
				rotationLeftAmount = 1F;
				break;
			case SOUTH:
				rotationLeftAmount = 1F;
				rotationRightAmount = 1F;
				break;
			case EAST:
				rotationLeftAmount = -1F;
				break;
		}
		
		TRSRTransformation trans = new TRSRTransformation(new Vector3f(0.5F, 0F, 0.5F), new Quat4f(0, rotationLeftAmount, 0, 1), new Vector3f(2, 2, 2), new Quat4f(0, rotationRightAmount, 0, 1));
		IVertexConsumer consumer = new VertexTransformer(builder)
				{
					@Override
					public void put(int element, float... data) {
						VertexFormatElement formatElement = format.getElement(element);
						switch(formatElement.getUsage())
						{
							case POSITION:
								Vector4f vec = new Vector4f(data);
								trans.getMatrix().transform(vec);
								float[] newData = new float[4];
								vec.get(newData);
								
								parent.put(element, newData);
								break;
							default:
								parent.put(element, data);
						}
					}
				};
				
		quad.pipe(consumer);
		return builder.build();
	}

	@Override
	public boolean isAmbientOcclusion() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isGui3d() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		// TODO Auto-generated method stub
		return IBakedModel.super.handlePerspective(cameraTransformType);
	}
}
