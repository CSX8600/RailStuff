package com.clussmanproductions.railstuff.blocks.model;

import java.util.Collection;
import java.util.function.Function;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelState;

public class ModelBNCASwitchStand implements IModel {

	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableList.of(
				new ResourceLocation(ModRailStuff.MODID, "blocks/bnca-stand.mtl"));
	}
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		try {
			return new BakedModelBNCASwitchStand(format, OBJLoader.INSTANCE.loadModel(new ResourceLocation(ModRailStuff.MODID, "models/block/bnca-stand.obj")).bake(state, format, bakedTextureGetter));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	
}
