package unimelb.mf.client.sync.item;

public interface HasContent extends HasLength, HasChecksum {

	default boolean contentEquals(HasContent o, ChecksumType checksumType) {
		if (checksumType != null) {
			return lengthEquals(o) && checksumEquals(checksumType, o);
		} else {
			return lengthEquals(o);
		}
	}

}
