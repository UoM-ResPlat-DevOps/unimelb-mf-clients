package unimelb.mf.client.util;

import java.io.PrintStream;
import java.util.List;

import arc.xml.XmlDoc;

public class XmlUtils {
    public static void print(PrintStream ps, XmlDoc.Element e) {
        print(ps, e, 0, 4);
    }

    public static void print(PrintStream ps, XmlDoc.Element e, int indent, int tabSize) {
        ps.print(StringUtils.stringOf(' ', indent));
        ps.print(":" + e.name());
        List<XmlDoc.Attribute> attrs = e.attributes();
        if (attrs != null) {
            for (XmlDoc.Attribute attr : attrs) {
                ps.print(String.format(" -%s \"%s\"", attr.name(), attr.value()));
            }
        }
        if (e.value() != null) {
            ps.print(" \"" + e.value() + "\"");
        }
        ps.println();

        List<XmlDoc.Element> ses = e.elements();
        if (ses != null) {
            for (XmlDoc.Element se : ses) {
                print(ps, se, indent + tabSize, tabSize);
            }
        }
    }

}
