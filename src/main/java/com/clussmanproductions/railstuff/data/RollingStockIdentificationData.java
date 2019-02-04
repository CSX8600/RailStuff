package com.clussmanproductions.railstuff.data;

import java.util.HashMap;
import java.util.UUID;

import com.clussmanproductions.railstuff.ModRailStuff;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class RollingStockIdentificationData extends WorldSavedData {

	private static final String DATA_NAME = ModRailStuff.MODID + "_RollingStockIdentificationData";
	private HashMap<UUID, String> identifiersByUUID = new HashMap<UUID, String>();
	
	public RollingStockIdentificationData(String name)
	{
		super(DATA_NAME);
	}
	
	public RollingStockIdentificationData() {
		super(DATA_NAME);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		identifiersByUUID.clear();
		for(String key : nbt.getKeySet())
		{
			if (!key.contains("_key"))
			{
				continue;
			}
			
			String formattedKey = key.replace("_keyLeast", "_key").replaceAll("_keyMost", "_key");
			
			UUID id = nbt.getUniqueId(formattedKey);
			String valueKey = formattedKey.replace("_key", "_value");
			String value = nbt.getString(valueKey);
			
			identifiersByUUID.put(id, value);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		int index = 0;
		for(UUID id : identifiersByUUID.keySet())
		{
			String keyKey = "item" + index + "_key";
			String valueKey = "item" + index + "_value";
			
			compound.setUniqueId(keyKey, id);
			compound.setString(valueKey, identifiersByUUID.get(id));
			
			index++;
		}
		
		return compound;
	}

	public static RollingStockIdentificationData get(World world)
	{
		MapStorage storage = world.getMapStorage();
		RollingStockIdentificationData data = (RollingStockIdentificationData)storage.getOrLoadData(RollingStockIdentificationData.class, DATA_NAME);
		
		if (data == null)
		{
			data = new RollingStockIdentificationData();
			storage.setData(DATA_NAME, data);
		}
		
		return data;
	}

	public String getIdentifierByUUID(UUID id)
	{
		if (identifiersByUUID.containsKey(id))
		{
			return identifiersByUUID.get(id);
		}
		
		return "";
	}
	
	public void setIdentifierGivenUUID(UUID id, String value)
	{
		identifiersByUUID.put(id, value);
		
		markDirty();
	}

	public void setData(HashMap<UUID, String> data)
	{
		identifiersByUUID = data;
		
		markDirty();
	}
	
	public HashMap<UUID, String> getData()
	{
		return identifiersByUUID;
	}
}
