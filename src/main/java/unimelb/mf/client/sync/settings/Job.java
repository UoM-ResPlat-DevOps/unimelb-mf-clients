package unimelb.mf.client.sync.settings;

import java.nio.file.Path;

public class Job {
	
	private String _ns;
	private Path _dir;
	private boolean _isParent;
	
	public Job(Path dir, String ns, boolean isDestinationParent) {
		_dir = dir;
		_ns = ns;
		_isParent = isDestinationParent;
	}
	
	public final String namespace() {
		return _ns;
	}
	
	public Path directory() {
		return _dir;
	}
	
	public boolean isDestinationParent() {
		return _isParent;
	}

}
