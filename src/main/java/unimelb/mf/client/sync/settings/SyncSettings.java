package unimelb.mf.client.sync.settings;

public class SyncSettings extends AbstractSettings {

	@Override
	public final Type type() {
		return Type.TRANSFER;
	}

	@Override
	public final Direction direction() {
		return Direction.SYNC;
	}

}
