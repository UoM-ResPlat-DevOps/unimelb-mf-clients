package unimelb.mf.client.sync.app;

import unimelb.mf.client.session.MFSession;
import unimelb.mf.client.sync.settings.MFUploadSettings;

public class MFUploadApp extends AbstractSyncApp<MFUploadSettings> {

    public static final String APP_NAME = "unimelb-mf-upload";

    protected MFUploadApp() {
        super(new MFUploadSettings());
    }

    @Override
    public final String applicationName() {
        return APP_NAME;
    }

    @Override
    public final String description() {
        return "A command line tool to upload local files to remote Mediaflux server.";
    }

    @Override
    public void execute(MFSession session, MFUploadSettings settings) throws Throwable {
        // TODO Auto-generated method stub

    }
}