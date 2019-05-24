package com.clussmanproductions.railstuff.util;

import net.minecraft.util.IStringSerializable;

public enum EnumAspect implements IStringSerializable
{
	Red("red", 1),
	Yellow("yellow", 2),
	Green("green", 3),
	Dark("dark", 4); 
	
	private String name;
	public int index;
	EnumAspect(String name, int index)
	{
		this.name = name;
		this.index = index;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public static EnumAspect getAspect(int index)
	{
		for(EnumAspect aspect : EnumAspect.values())
		{
			if (aspect.index == index)
			{
				return aspect;
			}
		}
		
		return null;
	}
}
