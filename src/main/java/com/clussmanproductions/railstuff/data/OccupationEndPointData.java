package com.clussmanproductions.railstuff.data;

import java.util.HashMap;

import com.clussmanproductions.railstuff.Config;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;
import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class OccupationEndPointData extends WorldSavedData {

	private static final String DATA_NAME = ModRailStuff.MODID + "_OccupationEndPointData";
	private HashMap<BlockPos, BlockPos> endpoints = new HashMap<BlockPos, BlockPos>();
	
	public OccupationEndPointData(String name)
	{
		super(DATA_NAME);
	}
	
	public OccupationEndPointData()
	{
		super(DATA_NAME);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		int endpointIndex = 0;
		for(BlockPos endpoint : endpoints.keySet())
		{
			String keyKey = "endpoint" + endpointIndex + "_key";
			compound.setIntArray(keyKey, new int[] { endpoint.getX(), endpoint.getY(), endpoint.getZ() });
			
			String keyValue = "endpoint" + endpointIndex + "_value";
			BlockPos endpointBlock = endpoints.get(endpoint);
			compound.setIntArray(keyValue, new int[] { endpointBlock.getX(), endpointBlock.getY(), endpointBlock.getZ() });
		}
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		endpoints = new HashMap<BlockPos, BlockPos>();
		for(String key : nbt.getKeySet())
		{
			if (!key.startsWith("endpoint"))
			{
				continue;
			}
			
			if (!key.endsWith("_key"))
			{
				continue;
			}
			
			int[] endpoint = nbt.getIntArray(key);
			String keyValue = key.substring(0, key.indexOf("_key")) + "_value";
			
			int[] value = nbt.getIntArray(keyValue);
			endpoints.put(new BlockPos(endpoint[0], endpoint[1], endpoint[2]), new BlockPos(value[0], value[1], value[2]));
		}
	}
	
	public static OccupationEndPointData get(World world)
	{
		MapStorage storage = world.getMapStorage();
		OccupationEndPointData data = (OccupationEndPointData)storage.getOrLoadData(OccupationEndPointData.class, DATA_NAME);
		
		if (data == null)
		{
			data = new OccupationEndPointData();
			storage.setData(DATA_NAME, data);
		}
		
		return data;
	}
	
	public void addEndPoint(BlockPos pos, BlockPos masterPos)
	{
		endpoints.put(pos, masterPos);
		markDirty();
	}
	
	public boolean hasEndPoint(BlockPos pos)
	{
		return endpoints.keySet().contains(pos);
	}
	
	public void removeEndPoint(BlockPos pos)
	{
		endpoints.remove(pos);
		markDirty();
	}
	
	public BlockPos getEndPointMasterPos(BlockPos pos)
	{
		if (endpoints.containsKey(pos))
		{
			return endpoints.get(pos);
		}
		
		return new BlockPos(0, -1, 0);
	}

	public ImmutableList<BlockPos> getEndPoints()
	{
		return ImmutableList.copyOf(endpoints.keySet());
	}
	
	public static class CleanupTask
	{
		private int tickCount = 0;
		public void tick(World world)
		{
			tickCount++;
			
			if (tickCount >= 1200 * Config.occupationBlockCleanupTask)
			{
				performCleanup(world);
				tickCount = 0;
			}
		}
		
		private void performCleanup(World world)
		{
			OccupationEndPointData data = OccupationEndPointData.get(world);
			
			for(BlockPos key : data.getEndPoints())
			{
				if (!ImmersiveRailroadingHelper.blockPosIsRail(key, world))
				{
					data.removeEndPoint(key);
				}
			}
		}
	}
}
