package com.clussmanproductions.railstuff.scanner;

import java.util.List;

public interface IScannerSubscriber {
	List<ScanRequest> getScanRequests();
	void onScanComplete(ScanCompleteData scanCompleteData);
}
