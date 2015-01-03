package com.mgjg.kmztracker.cuesheet.parser;

import android.util.Log;
import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.map.Placemark;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

public class CueSheetXmlParser extends CueSheetParserFactory.CueSheetFactory
{

  private InputStream inputStream;

  CueSheetXmlParser(String url)
  {
    super(url);
  }

  public CueSheet parse(CueSheet cueSheet) throws Exception
  {

    // sax stuff
    try
    {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();

      XMLReader xr = sp.getXMLReader();

      DefaultHandler dataHandler = new XmlParser(cueSheet);

      xr.setContentHandler(dataHandler);

      xr.parse(new InputSource(inputStream));

    }
    catch (ParserConfigurationException pce)
    {
      Log.e(cueSheet.getAppName() + "(SAX XML)", "sax parse error", pce);
      throw pce;
    }
    catch (SAXException se)
    {
      Log.e(cueSheet.getAppName() + "(SAX XML)", "sax error", se);
      throw se;
    }
    catch (IOException ioe)
    {
      Log.e(cueSheet.getAppName() + "(SAX XML)", "sax parse io error", ioe);
      throw ioe;
    }

    return cueSheet;
  }

  @SuppressWarnings("unused")
  private class XmlParser extends DefaultHandler
  {
    // ===========================================================
    // Fields
    // ===========================================================

    private boolean inKmlTag = false;
    private boolean inPlacemarkTag = false;
    private boolean inNameTag = false;
    private boolean inDescriptionTag = false;
    private boolean inGeometryCollectionTag = false;
    private boolean inLinestringTag = false;
    private boolean inPointTag = false;
    private boolean inCoordinatesTag = false;

    private final CueSheet cueSheet;

    private String title = "";
    private String description = "";
    private StringBuffer latLon = new StringBuffer();

    public XmlParser(CueSheet cueSheet)
    {
      this.cueSheet = cueSheet;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException
    {
      // this.cueSheet = new CueSheet();
    }

    @Override
    public void endDocument() throws SAXException
    {
      // Nothing to do
    }

    /**
     * Gets be called on opening tags like: <tag> Can provide attribute(s), when xml was like: <tag
     * attribute="attributeValue">
     */
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
    {
      if (localName.equals("kml"))
      {
        this.inKmlTag = true;
      }
      else if (localName.equals("Placemark"))
      {
        this.inPlacemarkTag = true;
        title = "";
        description = "";
        latLon = new StringBuffer();
      }
      else if (localName.equals("name"))
      {
        this.inNameTag = true;
      }
      else if (localName.equals("description"))
      {
        this.inDescriptionTag = true;
      }
      else if (localName.equals("GeometryCollection"))
      {
        this.inGeometryCollectionTag = true;
      }
      else if (localName.equals("LineString"))
      {
        this.inLinestringTag = true;
      }
      else if (localName.equals("point"))
      {
        this.inPointTag = true;
      }
      else if (localName.equals("coordinates"))
      {
        this.inCoordinatesTag = true;
      }
    }

    /**
     * Gets be called on closing tags like: </tag>
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
      if (localName.equals("kml"))
      {
        this.inKmlTag = false;
      }
      else if (localName.equals("Placemark"))
      {
        this.inPlacemarkTag = false;

        // if ("Route".equals(activePlacemark.getTitle()))
        // cueSheet.setRoutePlacemark(activePlacemark);
        // else
        // TODO parse latLon buffer
        double lat = 0;
        double lon = 0;
        cueSheet.addPlacemark(new Placemark(lat, lon, title, description));
      }
      else if (localName.equals("name"))
      {
        this.inNameTag = false;
      }
      else if (localName.equals("description"))
      {
        this.inDescriptionTag = false;
      }
      else if (localName.equals("GeometryCollection"))
      {
        this.inGeometryCollectionTag = false;
      }
      else if (localName.equals("LineString"))
      {
        this.inLinestringTag = false;
      }
      else if (localName.equals("point"))
      {
        this.inPointTag = false;
      }
      else if (localName.equals("coordinates"))
      {
        this.inCoordinatesTag = false;
      }
    }

    /**
     * Gets be called on the following structure: <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length)
    {
      if (this.inNameTag)
      {
        title = new String(ch, start, length);
      }
      else if (this.inDescriptionTag)
      {
        description = new String(ch, start, length);
      }
      else if (this.inCoordinatesTag)
      {
        if (null == latLon)
        {
          latLon = new StringBuffer();
        }
        latLon.append(ch, start, length);
      }
    }

  }
}
