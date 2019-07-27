package com.clussmanproductions.railstuff.blocks;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.gui.GuiProxy;
import com.clussmanproductions.railstuff.tile.TileEntityMilepost;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class BlockMilepost extends Block implements ITileEntityProvider {
	public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public BlockMilepost()
	{
		super(Material.IRON);
		setRegistryName("milepost");
		setUnlocalizedName(ModRailStuff.MODID + ".milepost");
		setHardness(2f);
		setCreativeTab(ModRailStuff.CREATIVE_TAB);
	}
	
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMilepost();
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		if (!worldIn.isRemote)
		{
			return;
		}
		
		if (placer != Minecraft.getMinecraft().player)
		{
			return;
		}
		
		Minecraft.getMinecraft().player.openGui(ModRailStuff.instance, GuiProxy.GuiIDs.MILEPOST, worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch(state.getValue(FACING))
		{
			case WEST:
				return new AxisAlignedBB(0.4375, 0, 0.125, 0.5625, 1.5, 1);
			case EAST:
				return new AxisAlignedBB(0.4375, 0, 0, 0.5625, 1.5, 0.875);
			case NORTH:
				return new AxisAlignedBB(0, 0, 0.4375, 0.875, 1.5, 0.5625);
			case SOUTH:
				return new AxisAlignedBB(0.125, 0, 0.4375, 1, 1.5, 0.5625);
		}
		
		return FULL_BLOCK_AABB;
	}
}
