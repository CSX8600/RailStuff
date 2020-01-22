package com.clussmanproductions.railstuff;

import com.clussmanproductions.railstuff.blocks.BlockBlueFlag;
import com.clussmanproductions.railstuff.blocks.BlockEndABS;
import com.clussmanproductions.railstuff.blocks.BlockGreenFlag;
import com.clussmanproductions.railstuff.blocks.BlockManualSwitchStand;
import com.clussmanproductions.railstuff.blocks.BlockMast;
import com.clussmanproductions.railstuff.blocks.BlockMilepost;
import com.clussmanproductions.railstuff.blocks.BlockRedFlag;
import com.clussmanproductions.railstuff.blocks.BlockSignalHead;
import com.clussmanproductions.railstuff.blocks.BlockWhistle;
import com.clussmanproductions.railstuff.blocks.BlockYellowFlag;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("railstuff")
public class ModBlocks {
	@ObjectHolder("manual_switch_stand")
	public static BlockManualSwitchStand manual_switch_stand;
	@ObjectHolder("red_flag")
	public static BlockRedFlag red_flag;
	@ObjectHolder("yellow_flag")
	public static BlockYellowFlag yellow_flag;
	@ObjectHolder("green_flag")
	public static BlockGreenFlag green_flag;
	@ObjectHolder("blue_flag")
	public static BlockBlueFlag blue_flag;
	@ObjectHolder("mast")
	public static BlockMast mast;
	@ObjectHolder("signal_head")
	public static BlockSignalHead signal_head;
	@ObjectHolder("end_abs")
	public static BlockEndABS end_abs;
	@ObjectHolder("milepost")
	public static BlockMilepost milepost;
	@ObjectHolder("whistle")
	public static BlockWhistle whistle;
	
	public static void initModels()
	{
		manual_switch_stand.initModel();
		red_flag.initModel();
		yellow_flag.initModel();
		green_flag.initModel();
		blue_flag.initModel();
		end_abs.initModel();
		mast.initModel();
		signal_head.initModel();
		milepost.initModel();
		whistle.initModel();
	}
}
