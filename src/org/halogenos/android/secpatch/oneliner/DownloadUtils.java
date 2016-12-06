package org.halogenos.android.secpatch.oneliner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class DownloadUtils {

    public static String[] downloadFileStringArray(String directurl) throws IOException {
        URL url = new URL(directurl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        ArrayList<String> sa = new ArrayList<>();
        String s = null;
        while ((s = reader.readLine()) != null)
            sa.add(s);
        return sa.toArray(new String[sa.size()]);
    }

    public static String downloadFileString(String directurl) throws IOException {
        String finalString = "";
        for ( String s : downloadFileStringArray(directurl) )
            finalString += s + "\n";
        return finalString;
    }

}
