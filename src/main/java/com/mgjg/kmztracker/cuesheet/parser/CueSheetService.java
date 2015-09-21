package com.mgjg.kmztracker.cuesheet.parser;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.cuesheet.parser.CueSheetKmlParser;
import com.mgjg.kmztracker.cuesheet.parser.CueSheetParser;
import com.mgjg.kmztracker.cuesheet.parser.CueSheetXmlParser;

import java.util.UnknownFormatConversionException;

/**
 * Created by marianne on 1/3/2015.
 */
public class CueSheetService extends IntentService
{
    private static final String TAG = "cueSheetService";


    public CueSheetService()
    {
        super("CueSheetService");

    }

    @Override
    protected void onHandleIntent(Intent workIntent)
    {
        String appName = "kmztracker";
        Activity context = null; // TBD
        GoogleMap map = null; // TBD
        final int color = workIntent.getIntExtra("color", Color.BLACK);
        final CueSheet cueSheet = new CueSheet(appName, context, map);
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        // TODO Do work here, based on the contents of dataString
        // new FileInputStream(filePath)
        // return parse(cueSheet, url.openStream());
        String urlString = workIntent.getStringExtra("url");
        CueSheetParser parser;
        try
        {
        if (urlString.endsWith(".kml"))
        {
            parser = new CueSheetKmlParser(urlString);
        }
        else if (urlString.endsWith(".xml"))
        {
            parser = new CueSheetXmlParser(urlString);
        }
        else if (urlString.endsWith(".gpx"))
        {
            parser = new CueSheetGpxParser(urlString);
        }
        else
        {
            throw new UnknownFormatConversionException(urlString);
        }

            parser.parse(cueSheet);

      /* Set the result to be displayed in our GUI. */
            Log.d(cueSheet.getAppName(), "CueSheet: " + cueSheet.toString());

            cueSheet.runOnUi(new Runnable()
            {

                @Override
                public void run()
                {
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
