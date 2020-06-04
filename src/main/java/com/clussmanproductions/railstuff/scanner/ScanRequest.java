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
	
	@Override
	public int hashCode() {
		int hashCode = 19;
		
		hashCode = 487 * hashCode + requestID.hashCode();
		hashCode = 487 * hashCode + startingPos.hashCode();
		hashCode = 487 * hashCode + endingPos.hashCode();
		hashCode = 487 * hashCode + startDirection.hashCode();
		
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) { return true; }
		if (obj == null) { return false; }
		if (obj.getClass() != this.getClass()) { return false; }
		
		ScanRequest scanRequest = (ScanRequest)obj;
		
		return requestID.equals(scanRequest.requestID) &&
				startingPos.equals(scanRequest.startingPos) &&
				endingPos.equals(scanRequest.endingPos) &&
				startDirection.equals(scanRequest.startDirection);
		
	}
}
