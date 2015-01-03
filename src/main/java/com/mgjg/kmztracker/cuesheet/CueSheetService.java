package com.mgjg.kmztracker.cuesheet;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by marianne on 1/3/2015.
 */
public class CueSheetService extends IntentService
{

  public CueSheetService()
  {
    super("CueSheetService");

  }

  @Override
  protected void onHandleIntent(Intent workIntent)
  {
    // Gets data from the incoming Intent
    String dataString = workIntent.getDataString();
    // TODO Do work here, based on the contents of dataString
  }
}
