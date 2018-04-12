package unimelb.mf.client.sync.job;

import java.nio.file.Path;

public interface Job {
	
	public static enum Type {
		TRANSFER, CHECK
	}
	
	public static enum Direction {
		DOWNLOAD, UPLOAD, SYNC;
	}
	
	Type type();
	
	Direction direction();
	
	String namespace();
	
	Path directory();
	
	boolean isDestinationParent();

}
