package unimelb.mf.client.sync.item;

public interface HasLength {

	long length();

	default boolean lengthEquals(HasLength o) {
		if (o != null) {
			if (length() >= 0 && o.length() >= 0) {
				return length() == o.length();
			}
		}
		return false;
	}

}
