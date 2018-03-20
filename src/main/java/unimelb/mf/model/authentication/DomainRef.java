package unimelb.mf.model.authentication;

public class DomainRef {
    private Authority _authority;
    private String _name;
    private Domain.Type _type;
    private String _description;

    public DomainRef(String name) {
        this(name, null);
    }

    public DomainRef(String name, String description) {
        this(Domain.AUTHORITY_MEDIAFLUX, name, Domain.Type.LOCAL, description);
    }

    public DomainRef(Authority authority, String name, Domain.Type type, String description) {
        _authority = authority;
        _name = name;
        _type = type;
        _description = description;
    }

    public Authority authority() {
        return _authority;
    }

    public String name() {
        return _name;
    }

    public Domain.Type type() {
        return _type;
    }

    public String description() {
        return _description;
    }

}
