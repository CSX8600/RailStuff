package com.clussmanproductions.railstuff.blocks;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.tile.TileEntityManualSwitchStand;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class BlockManualSwitchStand extends Block implements ITileEntityProvider {

	public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static PropertyBool STATE = PropertyBool.create("state");
	
	public BlockManualSwitchStand()
	{
		super(Material.IRON);
		setRegistryName("manual_switch_stand");
		setUnlocalizedName(ModRailStuff.MODID + ".manual_switch_stand");
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
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.3125, 0, 0.3125, 0.75, 1.5, 0.75);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, STATE);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		int modifier = 0;
		if (state.getValue(STATE))
		{
			modifier = 4;
		}
		return state.getValue(FACING).getHorizontalIndex() + modifier;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean state = false;
		if (meta >= 4)
		{
			state = true;
			meta -= 4;
		}
		
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(STATE, state);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityManualSwitchStand();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(STATE, false);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		if (worldIn.isRemote)
		{
			return true;
		}
		
		boolean blockState = state.getValue(STATE);
		EnumFacing currentFacing = state.getValue(FACING);
		worldIn.setBlockState(pos, state.withProperty(STATE, !blockState));
		worldIn.notifyNeighborsOfStateChange(pos.down(), state.getBlock(), true);
		worldIn.notifyNeighborsOfStateChange(pos.down()
				.offset(currentFacing), state.getBlock(), true);
		return true;
	}
	
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (!blockState.getValue(STATE))
		{
			return 0;
		}
		
		if (side != EnumFacing.UP)
		{
			return 0;
		}
		
		return 15;
	}
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (!blockState.getValue(STATE))
		{
			return 0;
		}
		
		EnumFacing facing = blockState.getValue(FACING);
		if (side.getOpposite() != facing)
		{
			return 0;
		}
		
		return 15;
	}
	
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}
	
	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		EnumFacing facing = state.getValue(FACING);
		return side == EnumFacing.DOWN || side == facing;
	}
	
	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
}
