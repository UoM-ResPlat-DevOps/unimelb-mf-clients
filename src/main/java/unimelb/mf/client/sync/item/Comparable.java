package unimelb.mf.client.sync.item;

public interface Comparable {

	public static class Result {
		private boolean _namesMatch;
		private boolean _sizesMatch;
		private boolean _checksumsMatch;

		public Result(boolean namesMatch, boolean sizesMatch, boolean checksumsMatch) {
			_namesMatch = namesMatch;
			_sizesMatch = sizesMatch;
			_checksumsMatch = checksumsMatch;
		}

		public final boolean sizesMatch() {
			return _sizesMatch;
		}

		public final boolean namesMatch() {
			return _namesMatch;
		}

		public final boolean checksumsMatch() {
			return _checksumsMatch;
		}
	}

	Result compare(Item item);
}
