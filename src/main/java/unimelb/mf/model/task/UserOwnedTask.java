package unimelb.mf.model.task;

import arc.xml.XmlDoc;
import unimelb.mf.model.authentication.UserRef;

public class UserOwnedTask extends Task {

    private UserRef _user;

    public UserOwnedTask(XmlDoc.Element xe) throws Throwable {
        super(xe);

        XmlDoc.Element ue = xe.element("user");
        if (ue == null) {
            _user = null;
        } else {
            _user = new UserRef(ue);
        }
    }

    /**
     * Returns the owner, if any, of the task.
     * 
     * @return
     */
    public UserRef owner() {
        return _user;
    }

}
