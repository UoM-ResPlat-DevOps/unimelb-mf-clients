package unimelb.mf.client.sync.settings;

public class SyncCheckSettings extends AbstractSettings{

	@Override
	public final Type type() {
		return Type.CHECK;
	}

	@Override
	public final Direction direction() {
		return Direction.SYNC;
	}

}
