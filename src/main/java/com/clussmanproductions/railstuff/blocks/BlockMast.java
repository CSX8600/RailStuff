package com.clussmanproductions.railstuff.blocks;

import java.util.Random;

import com.clussmanproductions.railstuff.ModItems;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.gui.GuiProxy;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMast extends Block implements ITileEntityProvider {
	public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
	public BlockMast()
	{
		super(Material.IRON);
		setRegistryName("mast");
		setUnlocalizedName(ModRailStuff.MODID + ".mast");
		setHardness(2f);
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
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ModItems.signal;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch(state.getValue(FACING))
		{
			case NORTH:
				return new AxisAlignedBB(0.375, 0, 0.25, 0.625, 1.0, 0.0);	
			case SOUTH:
				return new AxisAlignedBB(0.375, 0, 0.75, 0.625, 1.0, 1.0);
			case EAST:
				return new AxisAlignedBB(0.75, 0, 0.375, 1.0, 1.0, 0.625);
			case WEST:
				return new AxisAlignedBB(0.25, 0, 0.375, 0.0, 1.0, 0.625);
			default:
				return FULL_BLOCK_AABB;
		}
	}
	
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(ModItems.signal);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new SignalTileEntity();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		SignalTileEntity te = (SignalTileEntity)worldIn.getTileEntity(pos);
		if (te == null)
		{
			super.breakBlock(worldIn, pos, state);
			return;
		}
		te.getMaster(worldIn).onBreak(worldIn);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote)		{
			return true;
		}
		
		playerIn.openGui(ModRailStuff.instance, GuiProxy.GuiIDs.SIGNAL, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (worldIn.isRemote)
		{
			super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
			return;
		}
		
		SignalTileEntity te = (SignalTileEntity)worldIn.getTileEntity(pos);
		te.onNeighborChanged(worldIn);
		
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}
	
	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}
}
