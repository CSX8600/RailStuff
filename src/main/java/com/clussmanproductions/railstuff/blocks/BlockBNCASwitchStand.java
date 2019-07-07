package com.clussmanproductions.railstuff.blocks;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.tile.TileEntityBNCASwitchStand;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class BlockBNCASwitchStand extends Block implements ITileEntityProvider {
	public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static PropertyBool STATE = PropertyBool.create("state");
	public BlockBNCASwitchStand()
	{
		super(Material.IRON);
		setRegistryName("bnca_switch_stand");
		setUnlocalizedName(ModRailStuff.MODID + ".bnca_switch_stand");
		setHardness(1.5F);
		setCreativeTab(ModRailStuff.CREATIVE_TAB);
	}
	
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
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
		boolean state = meta > 3;
		if (state)
		{
			meta -= 4;
		}
		
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(STATE, state);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(STATE, false);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBNCASwitchStand();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
		{
			return true;
		}
		
		boolean switchState = state.getValue(STATE);
		worldIn.setBlockState(pos, state.withProperty(STATE, !switchState));
		worldIn.notifyNeighborsOfStateChange(pos.down(), state.getBlock(), true);
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (blockState.getValue(STATE) && side == blockState.getValue(FACING).getOpposite())
		{
			return 15;
		}
		
		return 0;
	}
	
	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (blockState.getValue(STATE) && side == EnumFacing.UP)
		{
			return 15;
		}
		
		return 0;
	}
	
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing facing = state.getValue(FACING);
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST)
		{
			return new AxisAlignedBB(0.2, 0, -0.2, 0.8, 0.4, 1.2);
		}
		else
		{
			return new AxisAlignedBB(-0.2, 0, 0.2, 1.2, 0.4, 0.8);
		}
	}
}
