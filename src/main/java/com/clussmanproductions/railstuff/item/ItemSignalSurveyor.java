package com.clussmanproductions.railstuff.item;

import com.clussmanproductions.railstuff.ModBlocks;
import com.clussmanproductions.railstuff.ModRailStuff;
import com.clussmanproductions.railstuff.blocks.BlockEndABS;
import com.clussmanproductions.railstuff.blocks.BlockSignalHead;
import com.clussmanproductions.railstuff.network.PacketHandler;
import com.clussmanproductions.railstuff.network.PacketSetSignalOccupationOriginOnServer;
import com.clussmanproductions.railstuff.tile.SignalTileEntity;
import com.clussmanproductions.railstuff.util.ImmersiveRailroadingHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import scala.Tuple3;

public class ItemSignalSurveyor extends Item {
	public ItemSignalSurveyor()
	{
		setRegistryName("signal_surveyor");
		setUnlocalizedName(ModRailStuff.MODID + ".signal_surveyor");
		setMaxStackSize(1);
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
		
		if (worldIn.getBlockState(pos).getBlock() == ModBlocks.signal_head)
		{
			SignalTileEntity te = (SignalTileEntity)worldIn.getTileEntity(pos);
			
			if (te != null)
			{
				return pairToSignal(worldIn, te, player);
			}
		}
		
		IBlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof BlockEndABS)
		{
			return pairToSign(worldIn, pos, player);
		}
		
		if (ModRailStuff.IR_INSTALLED)
		{
			EnumActionResult res = ImmersiveRailroadingHelper.handleSignalCustomOrigin(() -> setOccupationOrigin(worldIn, pos, player), pos, worldIn);
			if (res != null)
			{
				return res;
			}
		}
		
		return EnumActionResult.PASS;
	}

	private EnumActionResult pairToSignal(World worldIn, SignalTileEntity te, EntityPlayer player)
	{		
		NBTTagCompound tag = getTagOfSurveyor(player);
		
		int[] pairingpos = null;		
		if (player.isSneaking())
		{
			if (tag.hasKey("pairingpos"))
			{
				tag.removeTag("pairingpos");;
				player.sendMessage(new TextComponentString("Unpaired from Signal"));
			}
			

			if (tag.hasKey("occupationpairingpos"))
			{
				tag.removeTag("occupationpairingpos");
				player.sendMessage(new TextComponentString("Stopped setting occupation origin"));
			}
			else
			{
				tag.setIntArray("occupationpairingpos", new int[] { te.getPos().getX(), te.getPos().getY(), te.getPos().getZ() });
				player.sendMessage(new TextComponentString("Starting setting occupation origin"));
			}
			return EnumActionResult.SUCCESS;
		}
		
		if (tag.hasKey("occupationpairingpos"))
		{
			tag.removeTag("occupationpairingpos");
			player.sendMessage(new TextComponentString("Stopped setting occupation origin"));
		}
		
		if (tag.hasKey("pairingpos"))
		{
			pairingpos = tag.getIntArray("pairingpos");
			if (pairingpos[0] == te.getPos().getX() &&
					pairingpos[1] == te.getPos().getY() &&
					pairingpos[2] == te.getPos().getZ())
			{
				player.sendMessage(new TextComponentString("Unpaired from Signal"));
			}
			else
			{
				SignalTileEntity pairParent = (SignalTileEntity)worldIn.getTileEntity(new BlockPos(pairingpos[0], pairingpos[1], pairingpos[2]));
				if (pairParent == null)
				{
					player.sendMessage(new TextComponentString("Could not find the pairing origin.  Clearing pair origin."));
				}
				else
				{
					pairParent.setRegisteredEndPoint(new Tuple3<BlockPos, BlockPos, String>(te.getOccupationOriginBlockPos(), te.getPos(), te.getName()));
					player.sendMessage(new TextComponentString("Paired!  Clearing pair origin."));
				}
			}
			tag.removeTag("pairingpos");
		}
		else
		{
			tag.setIntArray("pairingpos", new int[] { te.getPos().getX(), te.getPos().getY(), te.getPos().getZ() });
			player.sendMessage(new TextComponentString("Paired to Signal"));
		}
		
		player.inventory.getCurrentItem().setTagCompound(tag);
		return EnumActionResult.SUCCESS;
	}

	private EnumActionResult pairToSign(World worldIn, BlockPos pos, EntityPlayer player)
	{
		NBTTagCompound tag = getTagOfSurveyor(player);
		
		int[] pairingpos = null;
		if (tag.hasKey("pairingpos"))
		{
			pairingpos = tag.getIntArray("pairingpos");
			BlockPos parentPos = new BlockPos(pairingpos[0], pairingpos[1], pairingpos[2]);
			SignalTileEntity te = (SignalTileEntity)worldIn.getTileEntity(parentPos);
			
			if (te == null)
			{
				player.sendMessage(new TextComponentString("Could not find the pairing origin.  Clearing pair origin."));
			}
			else
			{
				IBlockState signState = worldIn.getBlockState(pos);
				EnumFacing facing = signState.getValue(BlockEndABS.FACING);
				Vec3d origin = ImmersiveRailroadingHelper.findOrigin(pos, facing, worldIn);
				
				if (origin.y == -1)
				{
					player.sendMessage(new TextComponentString("Could not find track nearby.  Try again."));
					return EnumActionResult.SUCCESS;
				}
				
				te.setRegisteredEndPoint(new Tuple3<BlockPos, BlockPos, String>(new BlockPos(origin.x, origin.y, origin.z), pos, "End ABS"));
				player.sendMessage(new TextComponentString("Paired!  Clearing pair origin."));
			}
			
			tag.removeTag("pairingpos");
		}

		player.inventory.getCurrentItem().setTagCompound(tag);
		return EnumActionResult.SUCCESS;
	}
	
	private EnumActionResult setOccupationOrigin(World worldIn, BlockPos pos, EntityPlayer player)
	{
		NBTTagCompound tag = getTagOfSurveyor(player);
		
		if (!tag.hasKey("occupationpairingpos"))
		{
			return EnumActionResult.PASS;
		}
		
		if (tag.hasKey("pairingpos"))
		{
			tag.removeTag("pairingpos");
			player.sendMessage(new TextComponentString("Stopped pairing with signal"));
		}
		
		int[] pairingposarray = tag.getIntArray("occupationpairingpos");
		BlockPos pairingpos = new BlockPos(pairingposarray[0], pairingposarray[1], pairingposarray[2]);
		IBlockState signalState = worldIn.getBlockState(pairingpos);		
		Vec3d trackPos = ImmersiveRailroadingHelper.findOrigin(pos, signalState.getValue(BlockSignalHead.FACING), worldIn);
		BlockPos originPos = new BlockPos(trackPos.x, trackPos.y, trackPos.z);
		SignalTileEntity signalTE = (SignalTileEntity)worldIn.getTileEntity(pairingpos);
		signalTE.setOccupationOrigin(originPos);
		
		player.sendMessage(new TextComponentString("Set origin!  Clearing"));
		tag.removeTag("occupationpairingpos");
		
		return EnumActionResult.SUCCESS;
	}
	
	private NBTTagCompound getTagOfSurveyor(EntityPlayer player)
	{
		NBTTagCompound tag = player.inventory.getCurrentItem().getTagCompound();
		if (tag == null)
		{
			tag = new NBTTagCompound();
			player.inventory.getCurrentItem().setTagCompound(tag);
		}
		
		return tag;
	}
}
