package unimelb.mf.client.sync.settings;

import java.util.List;

public interface Settings {

	List<Job> jobs();

	Type type();

	Direction direction();

}
