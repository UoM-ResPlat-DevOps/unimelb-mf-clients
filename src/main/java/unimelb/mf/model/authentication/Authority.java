package unimelb.mf.model.authentication;

import arc.utils.ObjectUtil;

public class Authority {
    private String _protocol;
    private String _name;
    
    public Authority(String protocol,String name) {
        _protocol = protocol;
        _name = name;
    }
    
    public String protocol() {
        return _protocol;
    }
    
    public String name() {
        return _name;
    }

    public static boolean isServerAuthority(Authority authority) {
        if ( authority == null ) {
            return true;
        }
        
        if ( authority.protocol().equals(Domain.AUTHORITY_MEDIAFLUX.protocol()) ) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isLocalServerAuthority(Authority authority) {
        if ( authority == null ) {
            return true;
        }
        
        if ( authority.name() == null ) {
            return true;
        }
        
        return false;
    }

    public boolean isSameAs(Authority anotherAuthority) {
        return ObjectUtil.equals( _protocol, anotherAuthority.protocol() ) && ObjectUtil.equals( _name, anotherAuthority.name() );
    }
}
