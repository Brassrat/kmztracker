package com.mgjg.kmztracker.cuesheet.parser;

import android.util.Log;

import com.mgjg.kmztracker.cuesheet.CueSheet;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CueSheetGpxParser extends CueSheetParserFactory.CueSheetFactory
{

    GpxParser parser;

    CueSheetGpxParser(String url)
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

            parser = new GpxParser(cueSheet);

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

    private class GpxParser extends DefaultHandler
    {
        private final CueSheet cueSheet;

        private Element NOTAG = new ElementNOTAG();
        private Element currentElement = NOTAG;

        private final HashMap<String, Element> knownTags = new HashMap<>();

        GpxParser(CueSheet cueSheet)
        {
            this.cueSheet = cueSheet;
            knownTags.put("gpx", new ElementGPX());
            knownTags.put("author", new ElementAUTHOR());
            knownTags.put("url", new ElementURL());
            knownTags.put("time", new ElementTIME());
            knownTags.put("trk", new ElementTRK());
            knownTags.put("name", new ElementNAME());
            knownTags.put("trkseg", new ElementTRKSEG());
            knownTags.put("trkpt", new ElementTRKPT());
            knownTags.put("ele", new ElementELE());
        }

        public void addTrk(String name)
        {
            cueSheet.addTrk(name);
        }

        public void addTrkpt(double lat, double lon, double alt)
        {
            cueSheet.addPt(lat, lon, alt);
        }

        public void addTrkpt(double lat, double lon)
        {
            cueSheet.addPt(lat, lon);
        }

        @Override
        public void startDocument() throws SAXException
        {
            // this.cueSheet = new CueSheet();
            currentElement = NOTAG;
        }

        @Override
        public void endDocument() throws SAXException
        {
            // Nothing to do
            currentElement = NOTAG;
        }

        /**
         * Called when opening tag is processed, e.g., &lt;tag&gt. Attribute(s) can be specified by
         * attribute="attributeValue" before &gt;, e.g., &lt;tag attr1="value1" attr2="value2" &gt;
         */
        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
        {
            Element newElement = null;
            if (knownTags.containsKey(localName))
            {
                try
                {
                    Element ee = knownTags.get(localName);
                    Class<? extends Element> cc = ee.getClass();
                    Constructor<? extends Element> cons = cc.getConstructor(new Class[]{CueSheetGpxParser.class});
                    newElement = cons.newInstance(CueSheetGpxParser.this);
                    //newElement = knownTags.get(localName).getClass().getConstructor().newInstance();
                }
                catch (Exception e)
                {
                    newElement = null;
                }
            }

            if (newElement == null)
            {
                if ("gpx".equals(localName))
                {
                    newElement = new ElementGPX();
                }
                else if ("author".equals(localName))
                {
                    newElement = new ElementAUTHOR();
                }
                else if ("url".equals(localName))
                {
                    newElement = new ElementURL();
                }
                else if ("time".equals(localName))
                {
                    newElement = new ElementTIME();
                }
                else if ("trk".equals(localName))
                {
                    newElement = new ElementTRK();
                }
                else if ("name".equals(localName))
                {
                    newElement = new ElementNAME();
                }
                else if ("trkseg".equals(localName))
                {
                    newElement = new ElementTRKSEG();
                }
                else if ("trkpt".equals(localName))
                {
                    newElement = new ElementTRKPT();
                }
                else if ("ele".equals(localName))
                {
                    newElement = new ElementELE();
                }
            }

            if (null == newElement)
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

        public void setAuthor(String name)
        {
        }

        public void setUrl(String name)
        {
        }

        public void setTime(String name)
        {
        }

        public void setName(String name)
        {
        }

        public void addTrk(String name)
        {
            parser.addTrk(name);
        }

        public void addTrkpt(double lat, double lon, double alt)
        {
            parser.addTrkpt(lat, lon, alt);
        }

        public void addTrkpt(double lat, double lon)
        {
            parser.addTrkpt(lat, lon);
        }

        /*
        public void addPlacemark(double lat, double lon, String title, String description)
        {
            parser.addPlacemark(lat, lon, title, description);
        }
        */

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

        protected void setEle(String ele)
        {

        }
    }

    private class ElementNOTAG extends Element
    {
    }

    private class ElementGPX extends Element
    {
        String author;
        String url;
        String time;

        public void setAuthor(String author)
        {
            this.author = author;
        }

        public void setUrl(String url)
        {
            this.url = url;
        }

        public void setTime(String time)
        {
            this.time = time;
        }

    }

    private class ElementAUTHOR extends Element
    {
        private String author;

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
            author = "";
        }

        @Override
        protected void characters(char ch[], int start, int length)
        {
            author = new String(ch, start, length);
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            parent.setAuthor(author);
            author = null;
        }
    }

    private class ElementTIME extends Element
    {
        private String time;

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
            time = "";
        }

        @Override
        protected void characters(char ch[], int start, int length)
        {
            time = new String(ch, start, length);
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            parent.setTime(time);
            time = null;
        }
    }

    private class ElementURL extends Element
    {
        private String url;

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
            url = "";
        }

        @Override
        protected void characters(char ch[], int start, int length)
        {
            url = new String(ch, start, length);
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            parent.setUrl(url);
            url = null;
        }
    }

    private class ElementTRK extends Element
    {
        private String name;

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
            name = "";
        }

        public void setName(String name)
        {
            this.name = name;
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            name = null;
        }
    }

    private class ElementNAME extends Element
    {
        private String name;

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
            name = "";
        }

        @Override
        protected void characters(char ch[], int start, int length)
        {
            name = new String(ch, start, length);
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
            parent.setName(name);
            name = null;
        }
    }

    private class ElementTRKSEG extends Element
    {

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
        }

        @Override
        protected void characters(char ch[], int start, int length)
        {
        }

        public void addTrkpt(double lat, double lon, double alt)
        {
            parser.addTrkpt(lat, lon, alt);
        }

        public void addTrkpt(double lat, double lon)
        {
            parser.addTrkpt(lat, lon);
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {
        }
    }

    private class ElementTRKPT extends Element
    {
        private String lat;
        private String lon;
        private String ele;

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
            lat = null;
            lon = null;
            ele = null;
            if ((null != atts) && (atts.getLength() > 0))
            {
                lat = atts.getValue("lat");
                lon = atts.getValue("lon");
            }
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {

            if ((null != lat) && (null != lon))
            {
                double dlat = Double.parseDouble(lat);
                double dlon = Double.parseDouble(lon);
                if (null != ele)
                {
                    double dele = Double.parseDouble(ele);
                    parent.addTrkpt(dlat, dlon, dele);
                }
                else
                {
                    parent.addTrkpt(dlat, dlon);
                }
            }
            lat = null;
            lon = null;
            ele = null;
        }

        @Override
        protected void setEle(String ele)
        {
            // called from child ...
            this.ele = ele;
        }
    }

    private class ElementELE extends Element
    {
        private String ele;

        @Override
        protected void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws
                SAXException
        {
            ele = null;
        }

        @Override
        protected void characters(char ch[], int start, int length)
        {
            ele = new String(ch, start, length);
        }

        @Override
        protected void endElement(String namespaceURI, String localName, String qName) throws SAXException
        {

            if (null != ele)
            {
                parent.setEle(ele);
            }
            ele = null;
        }

    }

}

