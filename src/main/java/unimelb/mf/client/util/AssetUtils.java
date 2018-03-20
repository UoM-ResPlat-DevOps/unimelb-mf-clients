package unimelb.mf.client.util;

import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;

public class AssetUtils {

    public static XmlDoc.Element getAssetMeta(MFSession session, String assetId) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("id", assetId);
        return session.execute("asset.get", w.document(), null, null).element("asset");
    }

    public static XmlDoc.Element getAssetMetaByPath(MFSession session, String assetPath) throws Throwable {
        return getAssetMeta(session, "path=" + assetPath);
    }
}
