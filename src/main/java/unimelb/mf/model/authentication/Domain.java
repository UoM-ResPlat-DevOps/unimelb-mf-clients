package unimelb.mf.model.authentication;

public class Domain {
    public static final String RESOURCE_NAME = "domain";

    public enum Type {
        LOCAL("Local"), EXTERNAL("External"), PLUGIN("Plugin");

        private String _userValue;

        private Type(String userValue) {
            _userValue = userValue;
        }

        public String userValue() {
            return _userValue;
        }
    }

    public static final Authority AUTHORITY_MEDIAFLUX = new Authority("mediaflux", null);

    private Authority _authority;
    private String _description;
    private Type _type;

    public Authority authority() {
        return _authority;
    }

    public String description() {
        return _description;
    }

    public Type type() {
        return _type;
    }

    protected Domain() {
        // TODO
        // NOT YET PORTED
    }

}
