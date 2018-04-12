package unimelb.mf.client.sync.item;

import arc.utils.ObjectUtil;

public interface Item extends HasContent, HasContextPath, Comparable {

	default Comparable.Result compare(Item item){
		boolean namesMatch = ObjectUtil.equals(item.name(), name());
		boolean sizesMatch = item.lengthEquals(item);
		boolean checksumsMatch = item.anyChecksumMatches(item);
		return new Comparable.Result(namesMatch, sizesMatch, checksumsMatch);
	}
}
