package unimelb.mf.client.sync.job;

import java.nio.file.Path;

public class DownloadJob extends TransferJob{

	public DownloadJob(String srcAssetNamespace, Path dstDirectory, boolean isParent) {
		super(srcAssetNamespace, dstDirectory, isParent);
	}

	@Override
	public final Direction direction() {
		return Direction.DOWNLOAD;
	}

}
