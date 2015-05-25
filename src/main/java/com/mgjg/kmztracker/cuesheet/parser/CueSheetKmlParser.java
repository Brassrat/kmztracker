package com.mgjg.kmztracker.cuesheet.parser;

import android.util.Log;

import com.mgjg.kmztracker.cuesheet.CueSheet;
import com.mgjg.kmztracker.map.Placemark;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CueSheetKmlParser extends CueSheetParserFactory.CueSheetFactory
{

    KmlParser parser;

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

            parser = new KmlParser(cueSheet);

            xr.setContentHandler(parser);

            xr.parse(new InputSource(openConnection()));
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

    private abstract class Element
    {

        protected Element parent;

        protected Element push(Element currentElement)
        {
            this.parent = currentElement;
            return this;
        }

        protected Element pop()
        {
            return parent;
        }

        public void addPt(double lat, double lon, double alt)
        {
            parser.addPt(lat, lon, alt);
        }

        public void addPt(double lat, double lon)
        {
            parser.addPt(lat, lon);
        }

        public void addPlacemark(double lat, double lon, String title, String description)
        {
            parser.addPlacemark(lat, lon, title, description);
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

    private class ElementNOTAG extends Element
    {
    }

    private class ElementKML extends Element
    {
    }

    private class ElementDOCUMENT extends Element
    {
        private String description;
        private String name;

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
            description = "";
            name = "";
        }

        @Override
        protected void setDescription(String description)
        {
            this.description = description;
        }

        protected void setTitle(String title)
        {
            this.name = title;
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            description = null;
            name = null;
        }
    }

    private class ElementPLACEMARK extends Element
    {
        private String description;
        private String title;
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
            if (null != latLon)
            {
                String[] latLonAlt = latLon.split(",");
                double lat = Double.parseDouble(latLonAlt[0]);
                double lon = Double.parseDouble(latLonAlt[1]);
                addPlacemark(lat, lon, title, description);
            }
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
    }

    private class ElementNAME extends Element
    {
        private String title;

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
    }

    private class ElementDESCRIPTION extends Element
    {

        private String description;

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

    }

    private class ElementGEOMETRY extends Element
    {
    }

    private class ElementLINE extends Element
    {

        private ArrayList<String> pts = new ArrayList<String>();

        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {

        }

        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            // TODO add Strings to ArrayList and push them to parent at endELement
            for (String pt : pts)
            {
                String[] latLonAlt = pt.split(",");
                double lon = Double.parseDouble(latLonAlt[0]);
                double lat = Double.parseDouble(latLonAlt[1]);
                double alt = (latLonAlt.length > 2) ? Double.parseDouble(latLonAlt[2]) : 0;
                addPt(lat, lon, alt);
            }
        }

        @Override
        protected void setLatLon(String latAndLon)
        {
            // called from coordinates child on each pair
            pts.add(latAndLon);
        }

    }

    private class ElementPOINT extends Element
    {
    }

    private class ElementCOORDINATES extends Element
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
            // whitespace separates values
            if ((length > 0) && ((ch[start] == '\n') || (ch[start] == ' ')))
            {
                // save value from before
                if (latLon.length() > 0)
                {
                    parent.setLatLon(latLon.toString());
                    latLon.setLength(0);
                }
                while ((length > 0) && ((ch[start] == '\n') || (ch[start] == ' ')))
                {
                    // skip new line
                    ++start;
                    --length;
                }
            }
            // if any non-whitespace left, copy it
            if (length > 0)
            {
                latLon.append(ch, start, length);
            }
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            if (latLon.length() > 0)
            {
                parent.setLatLon(latLon.toString());
                latLon.setLength(0);
            }
        }

    }

    private class KmlParser extends DefaultHandler
    {
        private final CueSheet cueSheet;

        private Element NOTAG = new ElementNOTAG();
        private Element currentElement = NOTAG;

        private final HashMap<String, Element> knownTags = new HashMap<String, Element>();

        KmlParser(CueSheet cueSheet)
        {
            this.cueSheet = cueSheet;
        }

        public void addPt(double lat, double lon, double alt)
        {
            cueSheet.addPt(lat, lon, alt);
        }

        public void addPt(double lat, double lon)
        {
            cueSheet.addPt(lat, lon);
        }

        public void addPlacemark(double lat, double lon, double altitude, String title, String description)
        {
            cueSheet.addPlacemark(new Placemark(lat, lon, altitude, title, description));
        }

        public void addPlacemark(double lat, double lon, String title, String description)
        {
            cueSheet.addPlacemark(new Placemark(lat, lon, title, description));
        }

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
         * Called when opening tag is processed, e.g., &lt;tag&gt. Attribute(s) can be specified by
         * attribute="attributeValue" before &gt;, e.g., &lt;tag attr1="value1" attr2="value2" &gt;
         */
        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
        {
            Element newElement;
            if ("kml".equals(localName))
            {
                newElement = new ElementKML();
            }
            else if ("Document".equals(localName))
            {
                newElement = new ElementDOCUMENT();
            }
            else if ("Placemark".equals(localName))
            {
                newElement = new ElementPLACEMARK();
            }
            else if ("description".equals(localName))
            {
                newElement = new ElementDESCRIPTION();
            }
            else if ("name".equals(localName))
            {
                newElement = new ElementNAME();
            }
            else if ("LineString".equals(localName))
            {
                newElement = new ElementLINE();
            }
            else if ("coordinates".equals(localName))
            {
                newElement = new ElementCOORDINATES();
            }
            //knownTags.put("GeometryCollection", new ElementGEOMETRY());
            //knownTags.put("point", new ElementPOINT());
            else
            {
                Log.i(cueSheet.getAppName(), "Unknown tag, ignore: " + localName);
                newElement = new ElementNOTAG();
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
            currentElement = currentElement.pop();
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
