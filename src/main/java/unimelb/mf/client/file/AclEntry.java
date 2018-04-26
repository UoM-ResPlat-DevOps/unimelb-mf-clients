package unimelb.mf.client.file;

import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import arc.xml.XmlDoc;
import arc.xml.XmlWriter;

public class AclEntry {

    public static enum Type {
        ALLOW, DENY, AUDIT, ALARM;
        public static Type fromString(String s) {
            if (s != null) {
                Type[] vs = Type.values();
                for (Type v : vs) {
                    if (v.name().equalsIgnoreCase(s)) {
                        return v;
                    }
                }
            }
            return null;
        }

        public static Type convertFrom(AclEntryType type) {
            if (type != null) {
                return fromString(type.name());
            }
            return null;
        }
    }

    public static enum Permission {
        READ_DATA, WRITE_DATA, APPEND_DATA, READ_NAMED_ATTRS, WRITE_NAMED_ATTRS, EXECUTE, DELETE_CHILD, READ_ATTRIBUTES, WRITE_ATTRIBUTES, DELETE, READ_ACL, WRITE_ACL, WRITE_OWNER, SYNCHRONIZE;
        public static Permission fromString(String s) {
            if (s != null) {
                Permission[] vs = Permission.values();
                for (Permission v : vs) {
                    if (v.name().equalsIgnoreCase(s)) {
                        return v;
                    }
                }
            }
            return null;
        }

        public static Permission convertFrom(AclEntryPermission perm) {
            if (perm != null) {
                return fromString(perm.name());
            }
            return null;
        }

        public static Set<Permission> convertFrom(Set<AclEntryPermission> perms) {
            if (perms != null && !perms.isEmpty()) {
                Set<Permission> permissions = new LinkedHashSet<Permission>(perms.size());
                for (AclEntryPermission perm : perms) {
                    permissions.add(convertFrom(perm));
                }
                return permissions;
            }
            return null;
        }
    }

    public static enum Flag {
        FILE_INHERIT, DIRECTORY_INHERIT, NO_PROPAGATE_INHERIT, INHERIT_ONLY;
        public static Flag fromString(String s) {
            if (s != null) {
                Flag[] vs = Flag.values();
                for (Flag v : vs) {
                    if (v.name().equalsIgnoreCase(s)) {
                        return v;
                    }
                }
            }
            return null;
        }

        public static Flag convertFrom(AclEntryFlag flag) {
            if (flag != null) {
                return fromString(flag.name());
            }
            return null;
        }

        public static Set<Flag> convertFrom(Set<AclEntryFlag> flags) {
            if (flags != null && !flags.isEmpty()) {
                Set<Flag> fs = new LinkedHashSet<Flag>(flags.size());
                for (AclEntryFlag flag : flags) {
                    fs.add(convertFrom(flag));
                }
                return fs;
            }
            return null;
        }
    }

    private Type _type;
    private String _principal;
    private Set<Permission> _permissions;
    private Set<Flag> _flags;

    public AclEntry(java.nio.file.attribute.AclEntry entry) {
        _type = Type.convertFrom(entry.type());
        _principal = entry.principal() == null ? null : entry.principal().getName();
        _permissions = Permission.convertFrom(entry.permissions());
        _flags = Flag.convertFrom(entry.flags());
    }

    public AclEntry(XmlDoc.Element aclElement) throws Throwable {
        _type = Type.fromString(aclElement.value("@type"));
        _principal = aclElement.value("principal");
        if (aclElement.elementExists("perm")) {
            Collection<String> permValues = aclElement.values("perm");
            _permissions = new LinkedHashSet<Permission>(permValues.size());
            for (String permValue : permValues) {
                _permissions.add(Permission.fromString(permValue));
            }
        }
        if (aclElement.elementExists("flag")) {
            Collection<String> flagValues = aclElement.values("flag");
            _flags = new LinkedHashSet<Flag>(flagValues.size());
            for (String flagValue : flagValues) {
                _flags.add(Flag.fromString(flagValue));
            }
        }
    }

    public void save(XmlWriter w) throws Throwable {
        w.push("acl", new String[] { "type", _type == null ? null : _type.name() });
        if (_principal != null) {
            w.add("principal", _principal);
        }
        if (_permissions != null) {
            for (Permission perm : _permissions) {
                w.add("perm", perm.name());
            }
        }
        if (_flags != null) {
            for (Flag flag : _flags) {
                w.add("flag", flag.name());
            }
        }
        w.pop();
    }

    public Type type() {
        return _type;
    }

    public String principal() {
        return _principal;
    }

    public Set<Permission> permissions() {
        return _permissions == null ? null : new HashSet<Permission>(_permissions);
    }

    public Set<Flag> flags() {
        return _flags == null ? null : new HashSet<Flag>(_flags);
    }

    public static List<AclEntry> convertFrom(List<java.nio.file.attribute.AclEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        List<AclEntry> es = new ArrayList<AclEntry>(entries.size());
        for (java.nio.file.attribute.AclEntry entry : entries) {
            es.add(new AclEntry(entry));
        }
        return es;
    }

}
