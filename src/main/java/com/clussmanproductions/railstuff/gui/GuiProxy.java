package com.clussmanproductions.railstuff.gui;

import java.util.List;

import com.clussmanproductions.railstuff.tile.SignalTileEntity;
import com.clussmanproductions.railstuff.tile.TileEntityMilepost;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID)
		{
			case GuiIDs.ASSIGN_ROLLING_STOCK:
				List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(x-1,y-1,z-1,x + 1,y+1,z+1));
				if (entities.size() > 0)
				{
					Entity entity = entities.get(0);
					return new GuiAssignRollingStock(entity.getPersistentID());
				}
				break;
			case GuiIDs.SIGNAL:
				SignalTileEntity te = (SignalTileEntity)world.getTileEntity(new BlockPos(x, y, z));
				if (te != null)
				{
					return new GuiSignal(te);
				}
				break;
			case GuiIDs.MILEPOST:
				TileEntityMilepost milepostTE = (TileEntityMilepost)world.getTileEntity(new BlockPos(x, y, z));
				if (milepostTE != null)
				{
					return new GuiMilepost(milepostTE);
				}
				break;
		}
		
		return null;
	}

	public static class GuiIDs
	{
		public static final int ASSIGN_ROLLING_STOCK = 1;
		public static final int SIGNAL = 2;
		public static final int MILEPOST = 3;
	}
}
