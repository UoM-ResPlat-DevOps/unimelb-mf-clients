package unimelb.mf.client.dao;

import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;

public class NamespaceDAO {

    public static XmlDoc.Element list(MFSession session, Long idx, Integer size, Boolean assets, String assetAction)
            throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        if (idx != null) {
            w.add("idx", idx);
        }
        if (size != null) {
            w.add("size", size);
        }
        if (assets != null) {
            w.add("assets", assets);
            if (assets) {
                if (assetAction != null) {
                    w.add("action", assetAction);
                }
            }
        }
        return session.execute("asset.namespace.list", w.document());
    }

}
