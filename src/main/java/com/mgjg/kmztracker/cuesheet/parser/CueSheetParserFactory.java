package com.mgjg.kmztracker.cuesheet.parser;

import android.app.Activity;
import android.util.Log;
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

    public CueSheetUpdater(CueSheet cueSheet, String urlString)
    {
      this.cueSheet = cueSheet;
      this.urlString = urlString;
    }

    @Override
    public void run()
    {
      // new FileInputStream(filePath)
      // return parse(cueSheet, url.openStream());
      CueSheetParser parser;
      if (urlString.endsWith(".kml"))
      {
        parser = new CueSheetKmlParser(urlString);
      }
      else if (urlString.endsWith(".xml"))
      {
        parser = new CueSheetXmlParser(urlString);
      }
      else
      {
        throw new UnknownFormatConversionException(urlString);
      }
      try
      {
        parser.parse(cueSheet);

      /* Set the result to be displayed in our GUI. */
        Log.d(cueSheet.getAppName(), "CueSheet: " + cueSheet.toString());
      }
      catch (Exception e)
      {
        Log.e(TAG, "unable to process url(" + urlString + ") because " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public static void parseUrl(CueSheet cueSheet, String urlString)
  {
//   /*
//   * Creates a new Intent to start the CueSheetService
//   * IntentService. Passes a URI in the
//   * Intent's "data" field.
//   */
//    Intent ii = new Intent(aa, CueSheetService.class);
//    ii.putExtra("URL", urlString);
//    ii.put
//    //ii.setData(Uri.parse(dataUrl));
//    // Starts the IntentService
//    aa.startService(ii);
//    return ii;

    Thread th = new Thread(new CueSheetUpdater(cueSheet, urlString), "CueSheetService");
    th.setDaemon(true);
    th.start();
  }
}
