package com.clussmanproductions.railstuff;

import org.apache.logging.log4j.Level;

import com.clussmanproductions.railstuff.proxy.CommonProxy;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private static final String CATEGORY_GENERAL = "general";
	
	public static int signalDistanceTimeout = 2000;
	public static int signalDistanceTick = 10;
	public static int parallelScans = 1;
	
	public static void readConfig()
	{
		Configuration cfg = CommonProxy.config;
		try
		{
			cfg.load();
			initGeneralConfig(cfg);
		}
		catch (Exception e)
		{
			ModRailStuff.logger.log(Level.ERROR, "Problem loading config file!", e);
		}
		finally
		{
			if (cfg.hasChanged())
			{
				cfg.save();
			}
		}
	}
	
	private static void initGeneralConfig(Configuration cfg)
	{
		cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
		signalDistanceTimeout = cfg.getInt("signalDistanceTimeout", CATEGORY_GENERAL, signalDistanceTimeout, 1, 5000, "How far (in blocks) should signals test for the next signal?");
		signalDistanceTick = cfg.getInt("signalDistanceTick", CATEGORY_GENERAL, signalDistanceTick, 1, 500, "How far (in blocks) should a signal test for the next signal per tick?");
		parallelScans = cfg.getInt("parallelScans", CATEGORY_GENERAL, parallelScans, 1, 20, "How many signals should be scanned per tick?  PERFORMANCE NOTE: Total blocks scanned = signalDistanceTick * parallelScans.  The higher this number, the amount of blocks scanned per tick is multiplied.");
	}
}
