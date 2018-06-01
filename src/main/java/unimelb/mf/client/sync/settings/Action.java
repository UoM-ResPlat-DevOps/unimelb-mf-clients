package unimelb.mf.client.sync.settings;

public enum Action {

    // @formatter:off
    UPLOAD(Type.TRANSFER, Direction.UP), 
    DOWNLOAD(Type.TRANSFER, Direction.DOWN), 
    SYNC(Type.TRANSFER, Direction.BOTH),
    CHECK_UPLOAD(Type.CHECK, Direction.UP),
    CHECK_DOWNLOAD(Type.CHECK, Direction.DOWN),
    CHECK_SYNC(Type.CHECK, Direction.BOTH);
    // @formatter:on

    private Type _type;
    private Direction _direction;

    Action(Type type, Direction direction) {
        _type = type;
        _direction = direction;
    }

    @Override
    public final String toString() {
        return name().toLowerCase().replace('_', '-');
    }

    public final Type type() {
        return _type;
    }

    public final Direction direction() {
        return _direction;
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

    public static enum Type {
        TRANSFER, CHECK
    }

    public static enum Direction {
        UP, DOWN, BOTH;

        public static Direction fromString(String d) {
            if (d != null) {
                Direction[] vs = values();
                for (Direction v : vs) {
                    if (v.name().equalsIgnoreCase(d)) {
                        return v;
                    }
                }
            }
            return null;
        }
    }

    public static Action get(Type type, Direction direction) {
        if (type == Type.TRANSFER) {
            switch (direction) {
            case UP:
                return Action.UPLOAD;
            case DOWN:
                return Action.DOWNLOAD;
            default:
                return Action.SYNC;
            }
        } else {
            switch (direction) {
            case UP:
                return Action.CHECK_UPLOAD;
            case DOWN:
                return Action.CHECK_DOWNLOAD;
            default:
                return Action.CHECK_SYNC;
            }
        }
    }
}