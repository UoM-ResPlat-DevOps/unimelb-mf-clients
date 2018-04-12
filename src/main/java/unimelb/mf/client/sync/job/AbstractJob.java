package unimelb.mf.client.sync.job;

import java.nio.file.Path;

public abstract class AbstractJob implements Job {

	private String _ns;
	private Path _dir;
	private boolean _isParent;

	protected AbstractJob(String ns, Path dir, boolean isParent) {
		_ns = ns;
		_dir = dir;
		_isParent = isParent;
	}

	@Override
	public String namespace() {
		return _ns;
	}

	@Override
	public Path directory() {
		return _dir;
	}

	@Override
	public boolean isDestinationParent() {
		return _isParent;
	}

}
