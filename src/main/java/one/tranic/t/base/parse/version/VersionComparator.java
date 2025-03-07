package one.tranic.t.base.parse.version;

import one.tranic.t.utils.Collections;

import java.util.List;
import java.util.regex.Pattern;

/**
 * The VersionComparator class provides utility methods for comparing software version strings.
 * <p>
 * This comparison takes into account both numeric and alphanumeric segments of the versions,
 * including handling different formats such as pre-release identifiers or build metadata.
 */
public class VersionComparator {

    /**
     * Splits the given version string into its individual components.
     * <p>
     * The method extracts the main numeric parts of the version divided by periods (.)
     * and separates any suffix (e.g., pre-release identifiers) that is introduced by a dash (-).
     *
     * @param version the version string to be split into components
     * @return a list of strings where the initial components are the numeric parts of the version,
     * followed by the optional suffix, if present
     */
    private static List<String> splitVersion(String version) {
        String[] mainParts = version.split("-")[0].split("\\+")[0].split("\\.");
        String suffix = version.contains("-") ? version.substring(version.indexOf('-') + 1) : "";
        List<String> result = Collections.newArrayList(mainParts);
        result.add(suffix);
        return result;
    }

    /**
     * Compares two version strings and returns an integer indicating their relative order.
     * <p>
     * This method accounts for numeric and alphanumeric segments in the version strings.
     *
     * @param vLocal  the local version string to be compared
     * @param vRemote the remote version string to be compared
     * @return a negative integer if vLocal is less than vRemote,
     * zero if both versions are equal,
     * or a positive integer if vLocal is greater than vRemote
     */
    public static int cmpVer(String vLocal, String vRemote) {
        List<String> vLocSeg = splitVersion(vLocal);
        List<String> vRemSeg = splitVersion(vRemote);

        int maxLen = Math.max(vLocSeg.size(), vRemSeg.size());

        for (int i = 0; i < maxLen; i++) {
            String localPart = i < vLocSeg.size() ? vLocSeg.get(i) : "0";
            String remotePart = i < vRemSeg.size() ? vRemSeg.get(i) : "0";

            int cmpResult;

            if (Pattern.matches("\\d+", localPart) && Pattern.matches("\\d+", remotePart)) {
                cmpResult = Integer.compare(Integer.parseInt(localPart), Integer.parseInt(remotePart));
            } else if (Pattern.matches("\\d+", localPart)) {
                cmpResult = 1;
            } else if (Pattern.matches("\\d+", remotePart)) {
                cmpResult = -1;
            } else {
                cmpResult = localPart.compareToIgnoreCase(remotePart);
            }

            if (cmpResult != 0) {
                return cmpResult;
            }
        }

        return 0;
    }
}
