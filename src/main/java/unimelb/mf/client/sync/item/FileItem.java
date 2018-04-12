package unimelb.mf.client.sync.item;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import unimelb.mf.client.util.PathUtils;

public class FileItem implements Item {

	private Path _baseDir;

	private Path _file;

	private long _length = -1;

	private Map<ChecksumType, String> _checksums;

	FileItem(Path file, Path baseDir) {
		_file = file.toAbsolutePath();
		_baseDir = baseDir.toAbsolutePath();
		_length = file.toFile().length();
	}

	protected void setChecksum(ChecksumType checksumType, String checksum) {
		if (_checksums == null) {
			_checksums = new LinkedHashMap<ChecksumType, String>();
		}
		_checksums.put(checksumType, checksum);
	}

	protected void setBaseDirectory(Path baseDir) {
		_baseDir = baseDir;
	}

	@Override
	public final long length() {
		if (_length < 0) {
			_length = _file.toFile().length();
		}
		return _length;
	}

	@Override
	public final Map<ChecksumType, String> checksums() {
		if (_checksums != null) {
			return Collections.unmodifiableMap(_checksums);
		}
		return null;
	}

	@Override
	public final String fullPath() {
		return PathUtils.toSystemIndependent(_file.toAbsolutePath().toString());
	}

	@Override
	public final String basePath() {
		if (_baseDir != null) {
			return PathUtils.toSystemIndependent(_baseDir.toAbsolutePath().toString());
		}
		return null;
	}

	public final Path baseDirectory() {
		return _baseDir;
	}

}
