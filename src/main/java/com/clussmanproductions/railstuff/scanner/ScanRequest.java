package com.clussmanproductions.railstuff.scanner;

import java.util.UUID;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ScanRequest {
	private UUID requestID;
	private BlockPos startingPos;
	private BlockPos endingPos;
	private EnumFacing startDirection;
	
	public ScanRequest(UUID requestID, BlockPos startingPos, BlockPos endingPos, EnumFacing startDirection)
	{
		this.requestID = requestID;
		this.startingPos = startingPos;
		this.endingPos = endingPos;
		this.startDirection = startDirection;
	}
	
	public UUID getRequestID() { return requestID; }
	public BlockPos getStartingPos() { return startingPos; }
	public BlockPos getEndingPos() { return endingPos; }
	public EnumFacing getStartDirection() { return startDirection; }
}
