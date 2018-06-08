package unimelb.mf.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import arc.mf.client.ServerClient;
import arc.mf.client.ServerClient.Input;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import unimelb.mf.client.session.MFSession;

public class MailUtils {

    public static String getUserSelfEmail(MFSession session) throws Throwable {
        XmlDoc.Element xe = session.execute("user.self.describe");
        String email = xe.value("user/e-mail");
        if (email == null) {
            email = xe.value("user/asset/meta/mf-user/email");
        }
        return email;
    }

    public static void sendMail(MFSession session, String from, Collection<String> recipients, Collection<String> ccs,
            Collection<String> bccs, String subject, String body, boolean async, Collection<Attachment> attachments)
            throws Throwable {
        XmlStringWriter w = new XmlStringWriter();
        if (from != null) {
            w.add("from", from);
        } else {
            String userSelfEmail = getUserSelfEmail(session);
            if (userSelfEmail != null) {
                w.add("from", userSelfEmail);
            }
        }
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("No email recipients.");
        }
        for (String recipient : recipients) {
            w.add("to", recipient);
        }
        if (ccs != null) {
            for (String cc : ccs) {
                w.add("cc", cc);
            }
        }
        if (bccs != null) {
            for (String bcc : bccs) {
                w.add("bcc", bcc);
            }
        }
        if (subject != null) {
            w.add("subject", subject);
        }
        if (body != null) {
            w.add("body", body);
        }
        List<Input> inputs = null;
        if (attachments != null && !attachments.isEmpty()) {
            inputs = new ArrayList<Input>(attachments.size());
            int i = 1;
            for (Attachment attachment : attachments) {
                w.push("attachment");
                if (attachment.name == null) {
                    w.add("name", "attachment" + i);
                } else {
                    w.add("name", attachment.name);
                }
                if (attachment.type != null) {
                    w.add("type", attachment.type);
                }
                w.pop();
                inputs.add(attachment.input);
                i++;
            }
        }
        w.add("async", async);
        session.execute("mail.send", w.document(), inputs);
    }

    public static void sendMail(MFSession session, String from, Collection<String> recipients, Collection<String> ccs,
            Collection<String> bccs, String subject, String body, boolean async) throws Throwable {
        sendMail(session, from, recipients, ccs, bccs, subject, body, async, null);
    }

    public static void sendMail(MFSession session, Collection<String> recipients, String subject, String body,
            boolean async) throws Throwable {
        sendMail(session, null, recipients, null, null, subject, body, async, null);
    }

    public static class Attachment {
        public final String name;
        public final String type;
        public final ServerClient.Input input;

        public Attachment(String name, String type, ServerClient.Input input) {
            this.name = name;
            this.type = type;
            this.input = input;
        }
    }

}
