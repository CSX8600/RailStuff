package com.clussmanproductions.railstuff.item;

import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.BlockMast;
import com.clussmanproductions.railstuff.blocks.BlockMastFake;
import com.clussmanproductions.railstuff.blocks.BlockSignalHead;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;
import com.clussmanproductions.railstuff.util.EnumAspect;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class ItemSignal extends Item {
	public ItemSignal()
	{
		setRegistryName("signal");
		setUnlocalizedName(ModRailStuff.MODID + ".signal");
		setCreativeTab(ModRailStuff.CREATIVE_TAB);
	}
	
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
		{
			return EnumActionResult.SUCCESS;
		}
		
		BlockPos basePos = new BlockPos(pos.getX(), pos.getY(), pos.getZ()).up();
		
		if (!checkSpacing(basePos, worldIn))
		{
			return EnumActionResult.SUCCESS;
		}
		
		IBlockState oldMastState = worldIn.getBlockState(basePos);
		IBlockState newMastState = ModBlocks.mast.getDefaultState().withProperty(BlockMast.FACING, player.getHorizontalFacing());
		worldIn.setBlockState(basePos, newMastState, 3);
		SignalTileEntity master = (SignalTileEntity)worldIn.getTileEntity(basePos);
		master.setMaster();
		
		worldIn.notifyBlockUpdate(basePos, oldMastState, newMastState, 3);
		
		basePos = basePos.up();
		for(int i = 0; i < 3; i++)
		{
			IBlockState old = worldIn.getBlockState(basePos);
			IBlockState newState = ModBlocks.mast_fake.getDefaultState().withProperty(BlockMastFake.FACING, player.getHorizontalFacing());
			worldIn.setBlockState(basePos, newState);
			
			SignalTileEntity fake = (SignalTileEntity)worldIn.getTileEntity(basePos);
			fake.setMasterLocation(master.getPos());
			
			worldIn.notifyBlockUpdate(basePos, old, newState, 3);
			
			basePos = basePos.up();
		}
		
		IBlockState old = worldIn.getBlockState(basePos);
		IBlockState newState = ModBlocks.signal_head.getDefaultState()
				.withProperty(BlockSignalHead.FACING, player.getHorizontalFacing())
				.withProperty(BlockSignalHead.ASPECT, EnumAspect.Dark);
		worldIn.setBlockState(basePos, newState);
		
		SignalTileEntity signalHeadTE = (SignalTileEntity)worldIn.getTileEntity(basePos);
		signalHeadTE.setMasterLocation(master.getPos());
		master.setSignalHeadLocation(basePos);
		
		worldIn.notifyBlockUpdate(basePos, old, newState, 3);
		
		if (!player.isCreative())
		{
			ItemStack currentItemStack = player.inventory.getCurrentItem();
			currentItemStack.setCount(currentItemStack.getCount() - 1);
		}
		
		return EnumActionResult.PASS;
	}
	
	private boolean checkSpacing(BlockPos basePos, World world)
	{
		BlockPos pos = new BlockPos(basePos.getX(), basePos.getY(), basePos.getZ());
		
		for(int i = 0; i < 5; i++)
		{
			IBlockState state = world.getBlockState(pos);
			if (!state.getBlock().isReplaceable(world, pos))
			{
				return false;
			}
			
			pos = pos.up();
		}
		
		return true;
	}
}
