package unimelb.mf.client.util;

import java.util.logging.Logger;

import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;

public class AssetNamespaceUtils {

    public static void createAssetNamespace(MFSession session, String ns, Logger logger) throws Throwable {
        if (!assetNamespaceExists(session, ns)) {
            try {
                if (logger != null) {
                    logger.info("Creating asset namespace: '" + ns + "'");
                }
                XmlStringWriter w = new XmlStringWriter();
                w.add("namespace", ns);
                session.execute("asset.namespace.create", w.document(), null, null);
            } catch (Throwable e) {
                String msg = e.getMessage();
                if (msg != null && (msg.contains("already exists") || msg.contains("not accessible"))) {
                    if (logger != null) {
                        logger.info("Asset namespace: '" + ns + "' already exists.");
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    public static boolean assetNamespaceExists(MFSession session, String ns) throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        w.add("namespace", ns);
        return session.execute("asset.namespace.exists", w.document(), null, null).booleanValue("exists");
    }
}
