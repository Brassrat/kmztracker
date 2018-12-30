package com.mgjg.kmztracker.cuesheet.parser

import android.util.Log
import com.mgjg.kmztracker.cuesheet.CueSheet
import com.mgjg.kmztracker.map.Placemark
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

class CueSheetXmlParser internal constructor(url: String) :
  CueSheetParserFactory.CueSheetFactory(url) {

  @Throws(Exception::class)
  override fun parse(cueSheet: CueSheet): CueSheet {

    // sax stuff
    try {
      val spf = SAXParserFactory.newInstance()
      val sp = spf.newSAXParser()

      val xr = sp.xmlReader

      val dataHandler = XmlParser(cueSheet)

      xr.contentHandler = dataHandler

      xr.parse(InputSource(openConnection()))

    } catch (pce: ParserConfigurationException) {
      Log.e(cueSheet.appName + "(SAX XML)", "sax parse error", pce)
      throw pce
    } catch (se: SAXException) {
      Log.e(cueSheet.appName + "(SAX XML)", "sax error", se)
      throw se
    } catch (ioe: IOException) {
      Log.e(cueSheet.appName + "(SAX XML)", "sax parse io error", ioe)
      throw ioe
    }

    return cueSheet
  }

  private inner class XmlParser(private val cueSheet: CueSheet) : DefaultHandler() {
    // ===========================================================
    // Fields
    // ===========================================================

    private var inKmlTag = false
    private var inPlacemarkTag = false
    private var inNameTag = false
    private var inDescriptionTag = false
    private var inGeometryCollectionTag = false
    private var inLinestringTag = false
    private var inPointTag = false
    private var inCoordinatesTag = false

    private var title = ""
    private var description = ""
    private var latLon: StringBuffer? = StringBuffer()

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================
    @Throws(SAXException::class)
    override fun startDocument() {
      // this.cueSheet = new CueSheet();
    }

    @Throws(SAXException::class)
    override fun endDocument() {
      // Nothing to do
    }

    /**
     * Gets be called on opening tags like: <tag> Can provide attribute(s), when xml was like: <tag attribute="attributeValue">
    </tag></tag> */
    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      when (localName) {
        "kml" -> inKmlTag = true
        "Placemark" -> {
          inPlacemarkTag = true
          title = ""
          description = ""
          latLon = StringBuffer()
        }
        "name" -> inNameTag = true
        "description" -> inDescriptionTag = true
        "GeometryCollection" -> inGeometryCollectionTag = true
        "LineString" -> inLinestringTag = true
        "point" -> inPointTag = true
        "coordinates" -> inCoordinatesTag = true
      }
    }

    /**
     * Gets be called on closing tags like:
     */
    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      when (localName) {
        "kml" -> this.inKmlTag = false
        "Placemark" -> {
          this.inPlacemarkTag = false

          // if ("Route".equals(activePlacemark.getTitle()))
          // cueSheet.setRoutePlacemark(activePlacemark);
          // else
          // TODO parse latLon buffer
          val lat = 0.0
          val lon = 0.0
          cueSheet.addPlacemark(Placemark(lat, lon, title, description))
        }
        "name" -> this.inNameTag = false
        "description" -> this.inDescriptionTag = false
        "GeometryCollection" -> this.inGeometryCollectionTag = false
        "LineString" -> this.inLinestringTag = false
        "point" -> this.inPointTag = false
        "coordinates" -> this.inCoordinatesTag = false
      }
    }

    /**
     * Gets be called on the following structure: <tag>characters</tag>
     */
    override fun characters(ch: CharArray, start: Int, length: Int) {
      when {
        this.inNameTag -> title = String(ch, start, length)
        this.inDescriptionTag -> description = String(ch, start, length)
        this.inCoordinatesTag -> {
          if (null == latLon) {
            latLon = StringBuffer()
          }
          latLon!!.append(ch, start, length)
        }
      }
    }

  }
}
