package unimelb.mf.client.sync.cli;

import java.io.PrintStream;

import unimelb.mf.client.sync.app.MFUploadApp;
import unimelb.mf.client.sync.settings.MFUploadSettings;

public class MFUploadAppCLI extends MFUploadApp implements SyncCLI<MFUploadSettings> {

    protected MFUploadAppCLI() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public int parseArg(MFUploadSettings settings, String[] args, int i) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void printArgs(PrintStream ps) {
        // TODO Auto-generated method stub

    }

    @Override
    public void printExamples(PrintStream ps) {
        // TODO Auto-generated method stub

    }
}
