package com.clussmanproductions.railstuff.blocks;

import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.model.BlockFacing;
import com.clussmanproductions.railstuff.gui.GuiProxy;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMastFake extends BlockFacing implements ITileEntityProvider {
	public BlockMastFake()
	{
		super(Material.IRON);
		setRegistryName("mast_fake");
		setUnlocalizedName(ModRailStuff.MODID + ".mast_fake");
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
		
		SignalTileEntity master = te.getMaster(worldIn);
		if (master == null)
		{
			super.breakBlock(worldIn, pos, state);
			return;
		}
		
		master.onBreak(worldIn);
		super.breakBlock(worldIn, pos, state);
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote)
		{
			return true;
		}
		
		SignalTileEntity te = (SignalTileEntity)worldIn.getTileEntity(pos);
		IBlockState masterState = worldIn.getBlockState(te.getMaster(worldIn).getPos());
		
		return ModBlocks.mast.onBlockActivated(worldIn, te.getPos(), masterState, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}
}
