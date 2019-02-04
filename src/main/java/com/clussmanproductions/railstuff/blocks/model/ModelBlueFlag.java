package com.clussmanproductions.railstuff.blocks.model;

import java.util.Collection;
import java.util.function.Function;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public class ModelBlueFlag implements IModel {

	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableSet.<ResourceLocation>of(
				new ResourceLocation(ModRailStuff.MODID, "blocks/generic"),
				new ResourceLocation(ModRailStuff.MODID, "blocks/blue_flag"));
	}
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new BakedModelBlueFlag(format);
	}

}
