package unimelb.mf.client.session;

import java.util.List;

import arc.mf.client.ServerClient;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import arc.xml.XmlWriter;

public interface MFService {

    String name();

    void serviceArgs(XmlWriter w) throws Throwable;

    default void validateArgs() throws IllegalArgumentException {

    }

    default List<ServerClient.Input> inputs() {
        return null;
    }

    default ServerClient.Output output() {
        return null;
    }

    default long executeBackground(MFSession session) throws Throwable {
        validateArgs();
        XmlStringWriter w = new XmlStringWriter();
        w.add("background", true);
        w.push("service", new String[] { "name", name() });
        serviceArgs(w);
        w.pop();
        return session.execute("service.execute", w.document(), inputs(), output()).longValue("id");
    }

    default XmlDoc.Element execute(MFSession session) throws Throwable {
        validateArgs();
        return session.execute(name(), serviceArgs(), inputs(), output());
    }

    default String serviceArgs() throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        serviceArgs(w);
        return w.document();
    }

}
