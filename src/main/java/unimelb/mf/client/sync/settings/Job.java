package unimelb.mf.client.sync.settings;

import java.nio.file.Path;

public class Job {

    private Action _action;
    private Direction _direction;
    private String _ns;
    private Path _dir;
    private boolean _isParent;

    public Job(Action action, Direction direction, Path dir, String ns, boolean isDestinationParent) {
        _action = action;
        _direction = direction;
        _dir = dir;
        _ns = ns;
        _isParent = isDestinationParent;
    }

    public final Action action() {
        return _action;
    }

    public final Direction direction() {
        return _direction;
    }

    public final String namespace() {
        return _ns;
    }

    public final Path directory() {
        return _dir;
    }

    public final boolean isDestinationParent() {
        return _isParent;
    }

}
