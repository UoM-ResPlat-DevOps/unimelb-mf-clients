package unimelb.mf.client.sync.settings;

public enum Action {
    TRANSFER, CHECK;

    @Override
    public final String toString() {
        return name().toLowerCase().replace('_', '-');
    }

    public static Action fromString(String str) {
        if (str != null) {
            Action[] vs = values();
            for (Action v : vs) {
                if (v.toString().equalsIgnoreCase(str.replace('_', '-'))) {
                    return v;
                }
            }
        }
        return null;
    }
}