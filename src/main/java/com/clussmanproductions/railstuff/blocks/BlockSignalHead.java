package com.clussmanproductions.railstuff.blocks;

import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.model.BlockFacing;
import com.clussmanproductions.railstuff.gui.GuiProxy;
import com.clussmanproductions.railstuff.item.ItemSignalSurveyor;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;
import com.clussmanproductions.railstuff.util.EnumAspect;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
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

public class BlockSignalHead extends BlockFacing implements ITileEntityProvider {
	public static PropertyEnum<EnumAspect> ASPECT = PropertyEnum.create("aspect", EnumAspect.class);
	
	public BlockSignalHead()
	{
		super(Material.IRON);
		setRegistryName("signal_head");
		setUnlocalizedName(ModRailStuff.MODID + ".signal_head");
		setHardness(2f);
		setCreativeTab(ModRailStuff.CREATIVE_TAB);
	}
	
	public void initModel()
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ASPECT);
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
	public int getMetaFromState(IBlockState state) {
		int baseMeta = super.getMetaFromState(state);
		int modifier = 0;
		
		switch(state.getValue(ASPECT))
		{
			case Green:
				modifier = 4;
				break;
			case Yellow:
				modifier = 8;
				break;
			case Red:
				modifier = 12;
				break;
		}
		
		return baseMeta + modifier;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int iterationTimes = 0;
		int workingMeta = meta;
		
		while (workingMeta > 3)
		{
			iterationTimes++;
			workingMeta -= 4;
		};
		
		EnumAspect aspect;
		switch(iterationTimes)
		{
			case 3:
				aspect = EnumAspect.Red;
				break;
			case 2:
				aspect = EnumAspect.Yellow;
				break;
			case 1:
				aspect = EnumAspect.Green;
				break;
			default:
				aspect = EnumAspect.Dark;
				break;
		}
		
		return getDefaultState()
				.withProperty(FACING, EnumFacing.getHorizontal(workingMeta))
				.withProperty(ASPECT, aspect);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote)
		{
			return !(playerIn.inventory.getCurrentItem().getItem() instanceof ItemSignalSurveyor);
		}
		
		if (playerIn.inventory.getCurrentItem().getItem() instanceof ItemSignalSurveyor)
		{
			return false;
		}
		
		playerIn.openGui(ModRailStuff.instance, GuiProxy.GuiIDs.SIGNAL, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(ASPECT) == EnumAspect.Dark)
		{
			return 0;
		}
		
		return 15;
	}
	
	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
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
