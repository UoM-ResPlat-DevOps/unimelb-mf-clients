package unimelb.mf.client.sync.settings;

public class UploadSettings extends AbstractSettings {

	@Override
	public final Type type() {
		return Type.TRANSFER;
	}

	@Override
	public final Direction direction() {
		return Direction.UPLOAD;
	}

}
