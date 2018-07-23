package unimelb.mf.client.util;

public class TimeUtils {

    public static String humanReadableDuration(long durationMillis) {

        long days = durationMillis / 86400000L;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(days > 1 ? " days" : " day");
            sb.append(" ").append(humanReadableDuration(durationMillis % 86400000L));
            String r = sb.toString().trim();
            if (r.endsWith(" 0.000 second")) {
                return r.substring(0, r.length() - " 0.000 second".length());
            } else {
                return r;
            }
        }

        long hours = durationMillis / 3600000L;
        if (hours > 0) {
            sb.append(hours).append(hours > 1 ? " hours" : " hour");
            sb.append(" ").append(humanReadableDuration(durationMillis % 3600000L));
            String r = sb.toString().trim();
            if (r.endsWith(" 0.000 second")) {
                return r.substring(0, r.length() - " 0.000 second".length());
            } else {
                return r;
            }
        }

        long minutes = durationMillis / 60000L;
        if (minutes > 0) {
            sb.append(minutes).append(minutes > 1 ? " minutes" : " minute");
            sb.append(" ").append(humanReadableDuration(durationMillis % 60000L));
            String r = sb.toString().trim();
            if (r.endsWith(" 0.000 second")) {
                return r.substring(0, r.length() - " 0.000 second".length());
            } else {
                return r;
            }
        }
        
        double seconds = ((double) durationMillis) / 1000.0;
        sb.append(String.format("%2.3f second", seconds));
        if (seconds > 1) {
            sb.append("s");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(humanReadableDuration(7200000));
        System.out.println(humanReadableDuration(86400000));
    }
}
