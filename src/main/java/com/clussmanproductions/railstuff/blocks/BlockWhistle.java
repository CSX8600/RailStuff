package com.clussmanproductions.railstuff.blocks;

import com.clussmanproductions.railstuff.ModRailStuff;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class BlockWhistle extends Block {
	public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public BlockWhistle()
	{
		super(Material.IRON);
		setRegistryName("whistle");
		setUnlocalizedName(ModRailStuff.MODID + ".whistle");
		setHardness(2f);
		setCreativeTab(ModRailStuff.CREATIVE_TAB);
	}
	
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
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
		// TODO Auto-generated method stub
		switch(state.getValue(FACING))
		{
			case NORTH:
				return new AxisAlignedBB(0.4375, 0, 0.0625, 0.5625, 1.5, 0.19375);
			case EAST:
				return new AxisAlignedBB(0.9375, 0, 0.4375, 0.80625, 1.5, 0.5625);
			case WEST:
				return new AxisAlignedBB(0.0625, 0, 0.4375, 0.19375, 1.5, 0.5625);
			case SOUTH:
				return new AxisAlignedBB(0.4375, 0, 0.9375, 0.5625, 1.5, 0.80625);
		}
		return super.getBoundingBox(state, source, pos);
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public int getLightOpacity(IBlockState state) {
		return 0;
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
