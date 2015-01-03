package com.mgjg.kmztracker.cuesheet.parser;

import android.util.Log;
import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.map.Placemark;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CueSheetJsonParser implements CueSheetParser
{

  private final InputStream inputStream;

  CueSheetJsonParser(InputStream inputStream)
  {
    this.inputStream = inputStream;
  }

  @Override
  public CueSheet parse(CueSheet cueSheet) throws Exception
  {
    return new JsonParser(cueSheet).parse(inputStream);
  }

  private class JSONArrayIterator implements Iterator<JSONObject>
  {
    private final JSONArray theArray;
    private int nextIndex = 0;

    JSONArrayIterator(JSONArray theArray)
    {
      this.theArray = theArray;
    }

    @Override
    public boolean hasNext()
    {
      return (nextIndex < theArray.length());
    }

    @Override
    public JSONObject next()
    {
      if (nextIndex < theArray.length())
      {
        try
        {
          return theArray.getJSONObject(nextIndex++);
        }
        catch (JSONException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      throw new NoSuchElementException("no element available");
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("JSONArrayIterator does not support removal");

    }

  }

  private class JsonParser
  {
    private final CueSheet cueSheet;

    public JsonParser(CueSheet cueSheet) throws Exception
    {
      this.cueSheet = cueSheet;
    }

    public CueSheet parse(InputStream inputStream) throws Exception
    {
      JSONObject jObject = getJSON(inputStream);
      JSONArray placemarks = jObject.getJSONArray("placemarks");

      Iterator<JSONObject> it = new JSONArrayIterator(placemarks);
      while (it.hasNext())
      {
        JSONObject obj = it.next();
        double lat = obj.getDouble("lat");
        double lon = obj.getDouble("lon");
        String title = obj.getString("title");
        String description = obj.getString("description");
        cueSheet.addPlacemark(new Placemark(lat, lon, title, description));
      }
      return cueSheet;
    }

    public JSONObject getJSON(InputStream inputStream) throws Exception
    {
      try
      {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
          sb.append(line + "\n");
        }
        inputStream.close();
        String json = sb.toString();

        // try parse the string to a JSON object
        try
        {
          return new JSONObject(json);
        }
        catch (JSONException e)
        {
          Log.e(cueSheet.getAppName(), "JSON Error parsing data " + e.toString());
          throw e;
        }

      }
      catch (Exception e)
      {
        Log.e(cueSheet.getAppName(), "Error converting result " + e.toString());
        throw e;
      }

    }

  }
}
