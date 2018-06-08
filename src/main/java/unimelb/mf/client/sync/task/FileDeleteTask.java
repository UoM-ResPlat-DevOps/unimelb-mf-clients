package unimelb.mf.client.sync.task;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.task.AbstractMFTask;

public class FileDeleteTask extends AbstractMFTask {

    private List<Path> _files;

    protected FileDeleteTask(MFSession session, Logger logger) {
        super(session, logger);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void execute() throws Throwable {
        // TODO Auto-generated method stub

    }

}
