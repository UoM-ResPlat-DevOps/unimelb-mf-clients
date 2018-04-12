package unimelb.mf.client.sync.settings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSettings implements Settings {

	private List<Job> _jobs;

	@Override
	public List<Job> jobs() {
		if (_jobs != null && !_jobs.isEmpty()) {
			return Collections.unmodifiableList(_jobs);
		}
		return null;
	}

	public void addJob(Path dir, String ns, boolean isDestinationParent) {
		addJob(new Job(dir, ns, isDestinationParent));
	}

	public void addJob(Job... jobs) {
		if (_jobs == null) {
			_jobs = new ArrayList<Job>();
		}
		if (jobs != null && jobs.length > 0) {
			for (Job job : jobs) {
				_jobs.add(job);
			}
		}
	}

	public void clearJobs() {
		if (_jobs != null) {
			_jobs.clear();
		}
	}

	public void setJobs(Collection<Job> jobs) {
		clearJobs();
		if(jobs!=null) {
			for(Job job: jobs) {
				addJob(job);
			}
		}
	}

}
