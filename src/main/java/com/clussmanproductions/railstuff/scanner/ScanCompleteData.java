package com.clussmanproductions.railstuff.scanner;

import net.minecraft.util.EnumFacing;

public class ScanCompleteData {
	private ScanRequest scanRequest;
	private boolean timedOut;
	private boolean trainFound;
	private boolean trainMovingTowardsDestination;
	private String message;
	
	public ScanCompleteData(ScanRequest scanRequest, boolean timedOut, boolean trainFound, boolean trainMovingTowardsDestination, String message)
	{
		this.scanRequest = scanRequest;
		this.timedOut = timedOut;
		this.trainFound = trainFound;
		this.trainMovingTowardsDestination = trainMovingTowardsDestination;
		this.message = message;
	}
	
	public ScanRequest getScanRequest() { return scanRequest; }
	public boolean getTimedOut() { return timedOut; }
	public boolean getTrainFound() { return trainFound; }
	public boolean getTrainMovingTowardsDestination() { return trainMovingTowardsDestination; }
	public String getMessage() { return message; }
}
