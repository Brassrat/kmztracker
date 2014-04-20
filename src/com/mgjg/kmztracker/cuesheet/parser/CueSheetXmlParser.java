package com.mgjg.kmztracker.cuesheet.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.map.Placemark;

public class CueSheetXmlParser implements CueSheetParser
{

    private final InputStream inputStream;

    CueSheetXmlParser(InputStream inputStream)
    {
        this.inputStream = inputStream;
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

    private class XmlParser extends DefaultHandler
    {
        // ===========================================================
        // Fields
        // ===========================================================

        private boolean in_kmltag = false;
        private boolean in_placemarktag = false;
        private boolean in_nametag = false;
        private boolean in_descriptiontag = false;
        private boolean in_geometrycollectiontag = false;
        private boolean in_linestringtag = false;
        private boolean in_pointtag = false;
        private boolean in_coordinatestag = false;

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
                this.in_kmltag = true;
            }
            else if (localName.equals("Placemark"))
            {
                this.in_placemarktag = true;
                title = "";
                description = "";
                latLon = new StringBuffer();
            }
            else if (localName.equals("name"))
            {
                this.in_nametag = true;
            }
            else if (localName.equals("description"))
            {
                this.in_descriptiontag = true;
            }
            else if (localName.equals("GeometryCollection"))
            {
                this.in_geometrycollectiontag = true;
            }
            else if (localName.equals("LineString"))
            {
                this.in_linestringtag = true;
            }
            else if (localName.equals("point"))
            {
                this.in_pointtag = true;
            }
            else if (localName.equals("coordinates"))
            {
                this.in_coordinatestag = true;
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
                this.in_kmltag = false;
            }
            else if (localName.equals("Placemark"))
            {
                this.in_placemarktag = false;

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
                this.in_nametag = false;
            }
            else if (localName.equals("description"))
            {
                this.in_descriptiontag = false;
            }
            else if (localName.equals("GeometryCollection"))
            {
                this.in_geometrycollectiontag = false;
            }
            else if (localName.equals("LineString"))
            {
                this.in_linestringtag = false;
            }
            else if (localName.equals("point"))
            {
                this.in_pointtag = false;
            }
            else if (localName.equals("coordinates"))
            {
                this.in_coordinatestag = false;
            }
        }

        /**
         * Gets be called on the following structure: <tag>characters</tag>
         */
        @Override
        public void characters(char ch[], int start, int length)
        {
            if (this.in_nametag)
            {
                title = new String(ch, start, length);
            }
            else if (this.in_descriptiontag)
            {
                description = new String(ch, start, length);
            }
            else if (this.in_coordinatestag)
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
