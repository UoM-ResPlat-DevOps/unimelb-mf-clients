package unimelb.mf.model.task;

import java.util.ArrayList;
import java.util.List;

import arc.xml.XmlDoc;

public class UnitsOfWork {

    private String _name;
    private long _total;
    private long _completed;
    private long _failed;

    public UnitsOfWork(String name, long total, long completed, long failed) {
        _name = name;
        _total = total;
        _completed = completed;
        _failed = failed;
    }

    public String name() {
        return _name;
    }

    public long total() {
        return _total;
    }

    public long completed() {
        return _completed;
    }

    public long failed() {
        return _failed;
    }

    public static UnitsOfWork fromXml(XmlDoc.Element xe) throws Throwable {
        if (xe == null) {
            return null;
        }

        String name = xe.value("@name");
        long total = xe.longValue("total", 0);
        long completed = xe.longValue("completed", 0);
        long failed = xe.longValue("failed", 0);

        return new UnitsOfWork(name, total, completed, failed);
    }

    public static List<UnitsOfWork> listFromXml(XmlDoc.Element xe) throws Throwable {
        if (xe == null) {
            return null;
        }

        List<UnitsOfWork> list = null;

        List<XmlDoc.Element> uows = xe.elements("units-of-work");
        if (uows != null && !uows.isEmpty()) {
            for (XmlDoc.Element e : uows) {

                UnitsOfWork uow = fromXml(e);

                if (uow != null) {
                    if (list == null) {
                        list = new ArrayList<UnitsOfWork>();
                    }
                    list.add(uow);
                }
            }
        }

        return list;
    }

}
