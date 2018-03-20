package unimelb.mf.model.asset.worm;

import java.util.Date;

import arc.xml.XmlWriter;

public class Worm {

    public static enum AppliesTo {
        METADATA, CONTENT, ALL;
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static enum CanAlterIf {
        CAN_MODIFY, ADMINISTRATOR, NEVER;
        public String toString() {
            return name().toLowerCase();
        }
    }

    private AppliesTo _appliesTo = AppliesTo.ALL;
    private Boolean _canAddVersions = false;
    private CanAlterIf _canAlterIf = CanAlterIf.ADMINISTRATOR;
    private Boolean _canMove = true;
    private Boolean _destroyOnExpiry = false;
    private Date _expiry;

    public Worm() {

    }

    public AppliesTo appliesTo() {
        return _appliesTo;
    }

    public Boolean canAddVersions() {
        return _canAddVersions;
    }

    public CanAlterIf canAlterIf() {
        return _canAlterIf;
    }

    public Boolean canMove() {
        return _canMove;
    }

    public Boolean destroyOnExpiry() {
        return _destroyOnExpiry;
    }

    public Date expiry() {
        return _expiry;
    }

    public void setExpiry(Date expiry) {
        _expiry = expiry;
    }

    public void setDestroyOnExpiry(Boolean destroyOnExpiry) {
        _destroyOnExpiry = destroyOnExpiry;
    }

    public void setCanMove(Boolean canMove) {
        _canMove = canMove;
    }

    public void setCanAlterIf(CanAlterIf canAlterIf) {
        _canAlterIf = canAlterIf;
    }

    public void setCanAddVersions(Boolean canAddVersions) {
        _canAddVersions = canAddVersions;
    }

    public void setAppliesTo(AppliesTo appliesTo) {
        _appliesTo = appliesTo;
    }

    public void save(XmlWriter w) throws Throwable {
        if (_expiry != null) {
            w.add("expiry", _expiry);
        }
        if (_destroyOnExpiry != null) {
            w.add("destroy-on-expiry", _destroyOnExpiry);
        }
        if (_canMove != null) {
            w.add("can-move", _canMove);
        }
        if (_canAlterIf != null) {
            w.add("can-alter-if", _canAlterIf);
        }
        if (_canAddVersions != null) {
            w.add("can-add-versions", _canAddVersions);
        }
        if (_appliesTo != null) {
            w.add("applies-to", _appliesTo);
        }
    }

}
