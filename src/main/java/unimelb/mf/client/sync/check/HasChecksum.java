package unimelb.mf.client.sync.check;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public interface HasChecksum {

	default String checksum(ChecksumType type) {
		Map<ChecksumType, String> checksums = checksums();
		if (checksums != null) {
			return checksums.get(type);
		}

		return null;
	}

	default boolean checksumEquals(ChecksumType type, HasChecksum o) {
		if (o != null) {
			String csum1 = checksum(type);
			String csum2 = o.checksum(type);
			return (csum1 != null && csum2 != null && csum1.equalsIgnoreCase(csum2));
		}
		return false;
	}

	default boolean anyChecksumMatches(HasChecksum o) {
		Map<ChecksumType, String> checksums1 = checksums();
		Map<ChecksumType, String> checksums2 = o.checksums();
		if (checksums1 != null && checksums2 != null) {
			Set<ChecksumType> types1 = checksums1.keySet();
			Set<ChecksumType> types2 = checksums2.keySet();
			Set<ChecksumType> types = new LinkedHashSet<ChecksumType>(types1);
			types.retainAll(types2);
			if (!types.isEmpty()) {
				for (ChecksumType type : types) {
					String checksum1 = checksums1.get(type);
					String checksum2 = checksums2.get(type);
					return checksum1 != null && checksum2 != null && checksum1.equalsIgnoreCase(checksum2);
				}
			}
		}
		return false;
	}

	Map<ChecksumType, String> checksums();

}
