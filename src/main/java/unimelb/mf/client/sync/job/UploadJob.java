package unimelb.mf.client.sync.job;

import java.nio.file.Path;

public class UploadJob extends TransferJob {

	public UploadJob(Path srcDirectory, String dstNamespace, boolean isParent) {
		super(dstNamespace, srcDirectory, isParent);
	}

	@Override
	public final Direction direction() {
		return Direction.UPLOAD;
	}

}
