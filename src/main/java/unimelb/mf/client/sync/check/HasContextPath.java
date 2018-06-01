package unimelb.mf.client.sync.check;

import unimelb.mf.client.util.PathUtils;

public interface HasContextPath {

	String fullPath();

	default String relativePath() {
		String base = basePath();
		String path = fullPath();
		if (base != null && path != null) {
			return PathUtils.getRelativePathSI(basePath(), fullPath());
		}
		return null;
	}
	
	default String name() {
		String path = fullPath();
		if(path!=null) {
			return PathUtils.getFileName(path);
		}
		return null;
	}

	String basePath();

}
