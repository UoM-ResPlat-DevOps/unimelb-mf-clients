package unimelb.mf.client.sync.cli;

import java.io.PrintStream;

import unimelb.mf.client.sync.app.MFCheckApp;
import unimelb.mf.client.sync.settings.MFCheckSettings;

public class MFCheckAppCLI extends MFCheckApp implements SyncCLI<MFCheckSettings> {

    protected MFCheckAppCLI() {
        super();
    }

    @Override
    public int parseArg(MFCheckSettings settings, String[] args, int i) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public final String synopsis(String appName) {
        return String.format(
                "    %s [mediaflux-arguments] [--direction <upload|download|both>] [--output <path>] --directory <dir> --namespace <namespace>",
                appName);
    }

    @Override
    public void printArgs(PrintStream ps) {
        //@formatter:off
        ps.println("    --output <output-file>                    Output file path. If not specified, defaults to current work directory.");
        ps.println("    --direction <upload|download|both>        The direction to determine source and destination.");
        ps.println("    --directory                               The local directory.");
        ps.println("    --namespace                               The remote asset namespace.");
        //@formatter:on
    }

    @Override
    public void printExamples(PrintStream ps) {
        // TODO Auto-generated method stub

    }
}
