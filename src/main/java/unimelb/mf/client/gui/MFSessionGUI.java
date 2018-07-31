package unimelb.mf.client.gui;

import unimelb.mf.client.session.MFConnectionSettings;
import unimelb.mf.client.session.MFSession;

public class MFSessionGUI extends MFSession {

    private ErrorDialog _ed;
    private LogonDialog _ld;

    public MFSessionGUI(MFConnectionSettings settings, LogonDialog ld, ErrorDialog ed) {
        super(settings);
        this.settings.setExecuteRetryTimes(0);
        this.settings.setExecuteRetryInterval(0);
        this.settings.setConnectRetryTimes(0);
        this.settings.setConnectRetryInterval(0);
        _ld = ld;
        _ed = ed;
    }

    public void setErrorDialog(ErrorDialog ed) {
        _ed = ed;
    }

    public void setLogonDialog(LogonDialog ld) {
        _ld = ld;
    }

    public void displayErrorDialog(String context, Throwable error) {
        if (_ed != null) {
            _ed.displayError(context, error);
        }
    }

    public void displyLogonDialog(LogonResponseHandler rh) {
        if (_ld != null) {
            _ld.show(rh);
        }
    }

    // TODO not completed...
}
