package unimelb.mf.model.authentication;

import arc.xml.XmlDoc;

public class UserRef {

    private long _id;
    private DomainRef _domain;
    private String _user;
    private String _name;
    private String _email;

    public UserRef(long id, String domain, String user) {
        this(id, new DomainRef(domain), user);
    }

    public UserRef(long id, DomainRef domain, String user) {
        _id = id;
        _domain = domain;
        _user = user;
    }

    public UserRef(XmlDoc.Element xe) throws Throwable {
        this(xe.longValue("@id", -1), xe.value("domain"), xe.value("user"));

        _name = xe.value("name");
        _email = xe.value("email");
    }

    public long id() {
        return _id;
    }

    public DomainRef domain() {
        return _domain;
    }

    public String user() {
        return _user;
    }

    public String name() {
        return _name;
    }

    public String email() {
        return _email;
    }
}
