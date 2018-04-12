package unimelb.mf.client.sync.settings;

public class DownloadSettings extends AbstractSettings {

	@Override
	public final Type type() {
		return Type.TRANSFER;
	}

	@Override
	public final Direction direction() {
		return Direction.DOWNLOAD;
	}

}
