package com.clussmanproductions.railstuff.blocks.model;

import java.util.Set;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.google.common.collect.ImmutableSet;

import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class ModelLoader implements ICustomModelLoader {
	private final Set<String> HANDLEABLE_NAMES = ImmutableSet.of(
			"blue_flag");
	private final Set<String> HANDLEABLE_INVENTORY_MODELS = ImmutableSet.of();

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if (!(modelLocation instanceof ModelResourceLocation) || !modelLocation.getResourceDomain().equals(ModRailStuff.MODID))
		{
			return false;
		}
		
		ModelResourceLocation modelResourceLocation = (ModelResourceLocation)modelLocation;
		if (modelResourceLocation.getVariant().equals("inventory"))
		{
			return HANDLEABLE_INVENTORY_MODELS.contains(modelResourceLocation.getResourcePath());
		}
		
		return HANDLEABLE_NAMES.contains(modelResourceLocation.getResourcePath());
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		ModelResourceLocation resourceLocation = (ModelResourceLocation)modelLocation;
		switch(resourceLocation.getResourcePath())
		{
			case "blue_flag":
				return new ModelBlueFlag();
		}
		
		throw new Exception("Model not found");
	}

}
