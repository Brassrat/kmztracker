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
import java.util.HashMap;

public class CueSheetKmlParser extends CueSheetParserFactory.CueSheetFactory
{
  private InputStream inputStream;

  CueSheetKmlParser(String url)
  {
    super(url);
  }


  @Override
  public CueSheet parse(CueSheet cueSheet) throws Exception
  {
    // sax stuff
    try
    {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();

      XMLReader xr = sp.getXMLReader();

      DefaultHandler dataHandler = new KmlParser(cueSheet);

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

  private static class KmlParser extends DefaultHandler
  {
    // ===========================================================
    // Fields
    // ===========================================================

    private final CueSheet cueSheet;

    private Element currentElement = Element.NOTAG;

    public KmlParser(CueSheet cueSheet)
    {
      this.cueSheet = cueSheet;
    }

    private void addPlacemark(double lat, double lon, String title, String description)
    {
      cueSheet.addPlacemark(new Placemark(lat, lon, title, description));
    }

    private enum Element
    {
      NOTAG,
      KML,
      PLACEMARK
          {
            private KmlParser parser;
            private String title;
            private String description;
            private String latLon;

            @Override
            protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
            {
              title = null;
              description = null;
              latLon = null;
            }

            @Override
            protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
            {

              // if ("Route".equals(activePlacemark.getTitle()))
              // cueSheet.setRoutePlacemark(activePlacemark);
              // else
              // TODO parse latLon
              String[] latLon = this.latLon.split(",");
              double lat = Double.parseDouble(latLon[0]);
              double lon = Double.parseDouble(latLon[1]);
              ;
              parser.addPlacemark(lat, lon, title, description);
              title = null;
              description = null;
              latLon = null;
            }

            @Override
            protected void setTitle(String title)
            {
              this.title = title;
            }

            @Override
            protected void setDescription(String description)
            {
              this.description = description;
            }

            @Override
            protected void setLatLon(String latLon)
            {
              // called from child ...
              this.latLon = latLon;
            }
          },
      NAME
          {
            private String title;
            Element parent;

            @Override
            protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
            {
              title = "";
            }

            @Override
            protected void characters(char ch[], int start, int length)
            {
              title = new String(ch, start, length);
            }

            @Override
            protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
            {
              parent.setTitle(title);
            }
          },
      DESCRIPTION
          {

            private String description;
            Element parent;

            @Override
            protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
            {
              description = "";
            }

            protected void characters(char ch[], int start, int length)
            {
              description = new String(ch, start, length);
            }

            protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
            {
              parent.setDescription(description);
            }

          },
      GEOMETRY,
      LINE,
      POINT,
      COORDINATES
          {
            private StringBuffer latLon;

            @Override
            protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
            {
              latLon = null;
            }

            @Override
            protected void characters(char ch[], int start, int length)
            {
              if (null == latLon)
              {
                latLon = new StringBuffer();
              }
              latLon.append(ch, start, length);
            }

            @Override
            protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
            {
              parent.setLatLon(latLon.toString());
            }

          };

      Element parent;

      private Element push(Element currentElement)
      {
        this.parent = currentElement;
        return this;
      }

      protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
          SAXException
      {

      }

      protected void characters(char ch[], int start, int length)
      {

      }

      protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
      {

      }

      protected void setTitle(String title)
      {

      }

      protected void setDescription(String description)
      {

      }

      protected void setLatLon(String latLon)
      {

      }
    }

    ;

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

    private static final HashMap<String, Element> knownTags = new HashMap<String, Element>();

    static
    {
      knownTags.put("kml", Element.KML);
      knownTags.put("Placement", Element.PLACEMARK);
      knownTags.put("name", Element.NAME);
      knownTags.put("description", Element.DESCRIPTION);
      knownTags.put("GeometryCollection", Element.GEOMETRY);
      knownTags.put("LineString", Element.LINE);
      knownTags.put("point", Element.POINT);
      knownTags.put("coordinates", Element.COORDINATES);
    }

    /**
     * Called when opening tag is processed, e.g., &lt;tag&gt. Attribute(s) can be specified by
     * attribute="attributeValue" before &gt;, e.g., &lt;tag attr1="value1" attr2="value2" &gt;
     */
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
    {
      Element newElement = knownTags.get(localName);
      if (null == newElement)
      {
        Log.e(cueSheet.getAppName(), "Unknown tag, ignore: " + localName);
        return;
      }
      newElement.startElement(namespaceURI, localName, qName, atts);
      currentElement = newElement.push(currentElement);
    }

    /**
     * Called when closing tag is processed, e.g., &lt;/tag&gt;
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
      currentElement.endElement(namespaceURI, localName, qName);
      currentElement = Element.NOTAG;
    }

    /**
     * Called on text contained between starting and end tags. e.g., &lt;tag&gt;TEXT&lt;/tag&gt;
     */
    @Override
    public void characters(char ch[], int start, int length)
    {
      currentElement.characters(ch, start, length);
    }

  }
}
