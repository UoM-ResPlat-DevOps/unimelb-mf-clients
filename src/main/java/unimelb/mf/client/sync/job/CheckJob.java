package unimelb.mf.client.sync.job;

import java.nio.file.Path;

public abstract class CheckJob extends AbstractJob {

	protected CheckJob(String ns, Path dir, boolean isParent) {
		super(ns, dir, isParent);
	}

	@Override
	public final Type type() {
		return Type.CHECK;
	}

}
