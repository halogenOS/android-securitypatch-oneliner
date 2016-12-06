package org.halogenos.android.secpatch.oneliner;

import org.xdevs23.debugutils.StackTraceParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final int REQUIRED_ARGS_LENGTH = 1;
    private static final String PATCH_SITE = "https://source.android.com/security/bulletin/%s.html";

    private static String oneliner = "cd $TOP;";

    private static void cout(String... msgs) {
        for (String msg : msgs)
            System.out.println(msg);
    }

    private static void addToOl(String... toadd) {
        for (String s : toadd) {
            oneliner += " " + s + "; [ $? -ne 0 ] && echo 'Failure at " + s + "' && return 1";
        }
    }

    private static void newDirOl(String dir) {
        addToOl("cd $TOP/" + dir, "addAosp", "addCaf");
    }

    private static void addCommitOl(String... commits) {
        for (String c : commits) {
            addToOl("git cherry-pick -s " + c);
        }
    }

    private static String findDir(String url) {
        return url.substring(url.indexOf("platform/", url.indexOf("http"))
            + "platform/".length(),
                url.substring(url.indexOf("platform/"),
                        (url.indexOf("/>") > 0 ? url.indexOf("/>") - 1 :
                                url.length() - 1)).lastIndexOf("/"));
    }

    private static String findCommit(String url) {
        String dir = findDir(url);
        return url.substring(url.indexOf(dir) + dir.length(),
                (url.indexOf("/>") > 0 ? url.indexOf("/>") - 1 :
                        url.length() - 1));
    }

    public static void main(String args[]) {
        if(args.length < REQUIRED_ARGS_LENGTH) {
            cout(
                    "Please specify all required parameters:",
                    "  <security patch string> <output file for oneliner>"
                );
            return;
        }
        String patchString = args[0];
        String outputFile = (args.length >= 2 ? args[1] : null);
        String[] patchSite;
        String oneliner = "cd $TOP";
        try {
            patchSite =
                    DownloadUtils.downloadFileStringArray(String.format(PATCH_SITE, patchString));
        } catch(Exception e) {
            cout("Failed to load the web page!", StackTraceParser.parse(e));
            return;
        }
        String currentDir = "";
        for ( int i = 0; i < patchSite.length; i++ ) {
            String line = patchSite[i], nextLine = (i < patchSite.length - 1 ? patchSite[i+1] : "");
            cout("Processing line: ", line);
            // <a href="https://...xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx">A-XXXXXXXX</a>
            Pattern pahref = Pattern.compile(".*<a href=\".*\">.*");
            Pattern abugid = Pattern.compile("((.*)(A-........)(.*))");
            Matcher curmpahref = pahref.matcher(line),
                    curmabugid = abugid.matcher(line), nexmabugid = abugid.matcher(nextLine);

            if (curmpahref.find() && curmabugid.find()) {
                String dir = findDir(line);
                if(!currentDir.equals(dir)) {
                    currentDir = findDir(line);
                    newDirOl(dir);
                }
                addCommitOl(findCommit(line));
            } else if(curmpahref.find() && nexmabugid.find()) {
                String dir = findDir(line);
                if(!currentDir.equals(dir)) {
                    currentDir = findDir(line);
                    newDirOl(dir);
                }
                addCommitOl(findCommit(nextLine));
            }
        }
        cout(oneliner);
    }

}
