package com.clussmanproductions.railstuff.blocks;

import java.util.Random;

import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.ModItems;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.gui.GuiProxy;
import com.clussmanproductions.railstuff.item.ItemSignalSurveyor;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class BlockMast extends Block {
	public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
	public BlockMast()
	{
		super(Material.IRON);
		setRegistryName("mast");
		setUnlocalizedName(ModRailStuff.MODID + ".mast");
		setHardness(2f);
		setCreativeTab(ModRailStuff.CREATIVE_TAB);
	}
	
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return MastHelper.getBoundingBox(state.getValue(FACING));
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (worldIn.isRemote)
		{
			super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
			return;
		}
		
		// Trigger only on lowest mast
		IBlockState lowerState = worldIn.getBlockState(pos.down());
		if (lowerState.getBlock() == ModBlocks.mast || lowerState.getBlock() == ModBlocks.signal_head)
		{
			return;
		}
		
		BlockPos workingPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
		boolean isPowered = worldIn.isBlockPowered(workingPos);
		while(worldIn.getBlockState(workingPos).getBlock() == ModBlocks.mast || worldIn.getBlockState(workingPos).getBlock() == ModBlocks.signal_head)
		{
			if (worldIn.getBlockState(workingPos).getBlock() == ModBlocks.signal_head)
			{
				SignalTileEntity signalTE = (SignalTileEntity)worldIn.getTileEntity(workingPos);
				signalTE.onNeighborChanged(worldIn, isPowered);
			}
			
			workingPos = workingPos.up();
		}
		
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}
	
	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}
}
