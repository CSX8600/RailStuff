package com.clussmanproductions.railstuff.blocks.model;

import java.util.Collection;
import java.util.function.Function;

import javax.vecmath.Vector3f;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class ModelBNCASwitchStand implements IModel {

	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableList.of(
				new ResourceLocation(ModRailStuff.MODID, "blocks/bnca-stand-base.mtl"),
				new ResourceLocation(ModRailStuff.MODID, "blocks/bnca-stand-handle.mtl"));
	}
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		ImmutableMap.Builder<TransformType, TRSRTransformation> transformBuilder = ImmutableMap.<TransformType, TRSRTransformation>builder()
				.put(TransformType.FIRST_PERSON_RIGHT_HAND, new TRSRTransformation(
						new Vector3f(0.4F, 0.7F, 0.7F), TRSRTransformation.quatFromXYZDegrees(new Vector3f(90F, 90F, 0)), null, null))
				.put(TransformType.FIRST_PERSON_LEFT_HAND, new TRSRTransformation(
						new Vector3f(0.4F, -0.3F, 0.7F), TRSRTransformation.quatFromXYZDegrees(new Vector3f(90F, 90F, 0)), null, null))
				.put(TransformType.GROUND, new TRSRTransformation(
						new Vector3f(0.5F, 0.3F, 0.5F), null, null, null))
				.put(TransformType.GUI, new TRSRTransformation(
						new Vector3f(0.5F, 0.1F, 0), null, new Vector3f(1.5F, 1.5F, 1.5F), null))
				.put(TransformType.THIRD_PERSON_RIGHT_HAND, new TRSRTransformation(
						new Vector3f(0.5F, 0.5F, 0.6F), null, null, null))
				.put(TransformType.THIRD_PERSON_LEFT_HAND, new TRSRTransformation(
						new Vector3f(-0.5F, 0.5F, 0.6F), null, null, null))
				.put(TransformType.FIXED, new TRSRTransformation(
						new Vector3f(0.5F, 0.5F, 0.55F), TRSRTransformation.quatFromXYZDegrees(new Vector3f(270F, 0, 0)), null, null));
		
		try 
		{
			return new BakedModelBNCASwitchStand(format,
					OBJLoader.INSTANCE.loadModel(new ResourceLocation(ModRailStuff.MODID, "models/block/bnca-stand-base.obj")).bake(state, format, bakedTextureGetter),
					OBJLoader.INSTANCE.loadModel(new ResourceLocation(ModRailStuff.MODID, "models/block/bnca-stand-handle.obj")).bake(state, format, bakedTextureGetter),
					transformBuilder.build());
		} 
		catch (Exception e)
		{
			Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(e, "Failed to load model"));
			return null;
		}
	}
}
