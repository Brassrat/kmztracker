package com.mgjg.kmztracker.cuesheet.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UnknownFormatConversionException;

import android.util.Log;

public class CueSheetParserFactory
{
    private static URL getConnection(String urlString) throws IOException
    {
        URL url = new URL(urlString);

        final URLConnection conn = url.openConnection();
        conn.setReadTimeout(15 * 1000); // timeout for reading the google maps data: 15 secs
        conn.connect();
        return url;
    }

    public static CueSheetParser makeParser(String appName, String urlString) throws Exception
    {
        try
        {
            // new FileInputStream(filePath)
            // return parse(cueSheet, url.openStream());
            if (urlString.endsWith(".kml"))
            {
                return new CueSheetKmlParser(getConnection(urlString).openStream());
            }
            throw new UnknownFormatConversionException(urlString);
        }
        catch (MalformedURLException e)
        {
            Log.e(appName, "can not open url " + urlString + " because " + e.getMessage(), e);
            throw e;
        }
        catch (IOException e)
        {
            Log.e(appName, "can not open url " + urlString + " because " + e.getMessage(), e);
            throw e;
        }
        catch (Exception e)
        {
            Log.e(appName, "can not open url " + urlString + " because " + e.getMessage(), e);
            throw e;
        }

    }
}
