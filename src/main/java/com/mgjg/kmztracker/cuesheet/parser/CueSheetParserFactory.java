package com.mgjg.kmztracker.cuesheet.parser;

import android.util.Log;

import com.mgjg.kmztracker.MainActivity;
import com.mgjg.kmztracker.cuesheet.CueSheet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UnknownFormatConversionException;

public class CueSheetParserFactory
{
    private static final String TAG = "kmztracker.parser";

    public static abstract class CueSheetFactory implements CueSheetParser
    {
        private final String urlString;

        CueSheetFactory(String urlString)
        {
            this.urlString = urlString;
        }

        protected InputStream openConnection() throws IOException
        {
            return getConnection().openStream();
        }

        protected URL getConnection() throws IOException
        {
            try
            {
                URL url = new URL(urlString);

                final URLConnection conn = url.openConnection();
                conn.setConnectTimeout(10 * 1000);
                conn.setReadTimeout(15 * 1000); // timeout for reading the google maps data: 15 secs
                conn.connect();
                return url;
            }
            catch (MalformedURLException e)
            {
                Log.e(TAG, "can not open url " + urlString + " because " + e.getMessage(), e);
                throw e;
            }
            catch (IOException e)
            {
                Log.e(TAG, "can not open url " + urlString + " because " + e.getMessage(), e);
                throw e;
            }
        }
    }

    public static class CueSheetUpdater implements Runnable
    {

        private final CueSheet cueSheet;
        private final String urlString;
        private final int color;

        public CueSheetUpdater(CueSheet cueSheet, String urlString, int color)
        {
            this.cueSheet = cueSheet;
            this.urlString = urlString;
            this.color = color;
        }

        @Override
        public void run()
        {
            // new FileInputStream(filePath)
            // return parse(cueSheet, url.openStream());
            CueSheetParser parser;
            try
            {
                if (urlString.endsWith(".kml"))
                {
                    parser = new CueSheetKmlParser(urlString);
                }
                else if (urlString.endsWith(".gpx"))
                {
                    parser = new CueSheetGpxParser(urlString);
                }
                else if (urlString.endsWith(".xml"))
                {
                    parser = new CueSheetXmlParser(urlString);
                }
                else
                {
                    throw new UnknownFormatConversionException(urlString);
                }

                cueSheet.clear();
                parser.parse(cueSheet);

      /* Set the result to be displayed in our GUI. */
                Log.d(cueSheet.getAppName(), "CueSheet: " + cueSheet.toString());

                MainActivity.runOnUi(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        cueSheet.clearMap();
                        cueSheet.drawRoute(color);
                    }
                });
            }
            catch (Exception e)
            {
                Log.e(TAG, "unable to process url(" + urlString + ") because " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void parseUrl(CueSheet cueSheet, String urlString, int color)
    {
//   /*
//   * Creates a new Intent to start the CueSheetService
//   * IntentService. Passes a URI in the
//   * Intent's "data" field.
//   */
//    Intent ii = new Intent(aa, CueSheetService.class);
//    ii.putExtra("url", urlString);
        // NO WAY TO PASS object to service ...
//        ii.putExtra("cuesheet", cueSheet);
//    ii.putExtra("color", color);
//    //ii.setData(Uri.parse(dataUrl));
//    // Starts the IntentService
//    aa.startService(ii);

        // use background thread which updates activity-aware object
        Thread th = new Thread(new CueSheetUpdater(cueSheet, urlString, color), "CueSheetUpdater");
        th.setDaemon(true);
        th.start();
    }
}
