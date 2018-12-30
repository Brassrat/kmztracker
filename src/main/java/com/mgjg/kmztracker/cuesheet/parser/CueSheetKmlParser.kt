@file:Suppress("unused")

package com.mgjg.kmztracker.cuesheet.parser

import android.util.Log
import com.mgjg.kmztracker.cuesheet.CueSheet
import com.mgjg.kmztracker.map.Placemark
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.util.*
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

class CueSheetKmlParser internal constructor(url: String) :
  CueSheetParserFactory.CueSheetFactory(url) {

  private lateinit var parser: KmlParser

  @Throws(Exception::class)
  override fun parse(cueSheet: CueSheet): CueSheet {
    // sax stuff
    try {
      val spf = SAXParserFactory.newInstance()
      val sp = spf.newSAXParser()

      val xr = sp.xmlReader

      parser = KmlParser(cueSheet)

      xr.contentHandler = parser

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

  private abstract inner class Element {

    protected lateinit var parent: Element

    fun push(currentElement: Element): Element {
      this.parent = currentElement
      return this
    }

    fun pop(): Element {
      return parent
    }

    fun addPt(lat: Double, lon: Double, alt: Double) {
      parser.addPt(lat, lon, alt)
    }

    fun addPt(lat: Double, lon: Double) {
      parser.addPt(lat, lon)
    }

    fun addPlacemark(lat: Double, lon: Double, title: String?, description: String?) {
      parser.addPlacemark(lat, lon, title, description)
    }

    @Throws(SAXException::class)
    open fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {

    }

    open fun characters(ch: CharArray, start: Int, length: Int) {

    }

    @Throws(SAXException::class)
    open fun endElement(namespaceURI: String, localName: String, qName: String) {

    }

    open fun setTitle(value: String?) {

    }

    open fun setDescription(value: String?) {

    }

    open fun setLatLon(value: String) {

    }
  }

  private inner class ElementNOTAG : Element()

  private inner class ElementKML : Element()

  private inner class ElementDOCUMENT : Element() {
    private var description: String? = null
    private var name: String? = null

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      description = ""
      name = ""
    }

    override fun setDescription(value: String?) {
      this.description = value
    }

    override fun setTitle(value: String?) {
      this.name = value
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      description = null
      name = null
    }
  }

  private inner class ElementPLACEMARK : Element() {
    private var description: String? = null
    private var title: String? = null
    private var latLon: String? = null

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      title = null
      description = null
      latLon = null
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {

      // if ("Route".equals(activePlacemark.getTitle()))
      // cueSheet.setRoutePlacemark(activePlacemark);
      // else
      // TODO parse latLon
      if (null != latLon) {
        val latLonAlt = latLon!!.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val lat = java.lang.Double.parseDouble(latLonAlt[0])
        val lon = java.lang.Double.parseDouble(latLonAlt[1])
        addPlacemark(lat, lon, title, description)
      }
      title = null
      description = null
      latLon = null
    }

    override fun setTitle(value: String?) {
      this.title = value
    }

    override fun setDescription(value: String?) {
      this.description = value
    }

    override fun setLatLon(value: String) {
      // called from child ...
      this.latLon = value
    }
  }

  private inner class ElementNAME : Element() {
    private var title: String? = null

    override fun setTitle(value: String?) { title = value}

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      title = ""
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
      title = String(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      parent.setTitle(title)
    }
  }

  private inner class ElementDESCRIPTION : Element() {

    private var description: String? = null

    override fun setDescription(value: String?) { description = value}

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      description = ""
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
      description = String(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      parent.setDescription(description)
    }

  }

  private inner class ElementGEOMETRY : Element()

  private inner class ElementLINE : Element() {

    private val pts = ArrayList<String>()

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {

    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      // TODO add Strings to ArrayList and push them to parent at endELement
      for (pt in pts) {
        val latLonAlt = pt.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val lon = java.lang.Double.parseDouble(latLonAlt[0])
        val lat = java.lang.Double.parseDouble(latLonAlt[1])
        val alt = if (latLonAlt.size > 2) java.lang.Double.parseDouble(latLonAlt[2]) else 0.0
        addPt(lat, lon, alt)
      }
    }

    override fun setLatLon(value: String) {
      // called from coordinates child on each pair
      pts.add(value)
    }

  }

  private inner class ElementPOINT : Element()

  private inner class ElementCOORDINATES : Element() {
    private var latLon: StringBuffer? = null

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      latLon = null
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
      if (null == latLon) {
        latLon = StringBuffer()
      }
      var astart = start
      var alength = length
      // whitespace separates values
      if (alength > 0 && (ch[astart] == '\n' || ch[astart] == ' ')) {
        // save value from before
        if (latLon!!.isNotEmpty()) {
          parent.setLatLon(latLon.toString())
          latLon!!.setLength(0)
        }
        while (alength > 0 && (ch[astart] == '\n' || ch[astart] == ' ')) {
          // skip new line
          ++astart
          --alength
        }
      }
      // if any non-whitespace left, copy it
      if (alength > 0) {
        latLon!!.append(ch, astart, alength)
      }
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      if (latLon!!.isNotEmpty()) {
        parent.setLatLon(latLon!!.toString())
        latLon!!.setLength(0)
      }
    }

  }

  private inner class KmlParser internal constructor(private val cueSheet: CueSheet) :
    DefaultHandler() {

    private var currentElement : Element = ElementNOTAG()

    private val knownTags = HashMap<String, Element>()

    fun addPt(lat: Double, lon: Double, alt: Double) {
      cueSheet.addPt(lat, lon, alt)
    }

    fun addPt(lat: Double, lon: Double) {
      cueSheet.addPt(lat, lon)
    }

    fun addPlacemark(
      lat: Double,
      lon: Double,
      altitude: Double,
      title: String,
      description: String
    ) {
      cueSheet.addPlacemark(Placemark(lat, lon, altitude, title, description))
    }

    fun addPlacemark(lat: Double, lon: Double, title: String?, description: String?) {
      cueSheet.addPlacemark(Placemark(lat, lon, title ?: "" , description ?: ""))
    }

    @Throws(SAXException::class)
    override fun startDocument() {
      // this.cueSheet = new CueSheet();
    }

    @Throws(SAXException::class)
    override fun endDocument() {
      // Nothing to do
    }

    /**
     * Called when opening tag is processed, e.g., &lt;tag&gt. Attribute(s) can be specified by
     * attribute="attributeValue" before &gt;, e.g., &lt;tag attr1="value1" attr2="value2" &gt;
     */
    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      val newElement: Element = when (localName) {
        "kml" -> ElementKML()
        "Document" -> ElementDOCUMENT()
        "Placemark" -> ElementPLACEMARK()
        "description" -> ElementDESCRIPTION()
        "name" -> ElementNAME()
        "LineString" -> ElementLINE()
        "coordinates" -> ElementCOORDINATES()
        else -> {
          Log.i(cueSheet.appName, "Unknown tag, ignore: $localName")
          ElementNOTAG()
        }
      }//knownTags.put("GeometryCollection", new ElementGEOMETRY());
      //knownTags.put("point", new ElementPOINT());
      newElement.startElement(namespaceURI, localName, qName, atts)
      currentElement = newElement.push(currentElement)
    }

    /**
     * Called when closing tag is processed, e.g., &lt;/tag&gt;
     */
    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      currentElement.endElement(namespaceURI, localName, qName)
      currentElement = currentElement.pop()
    }

    /**
     * Called on text contained between starting and end tags. e.g., &lt;tag&gt;TEXT&lt;/tag&gt;
     */
    override fun characters(ch: CharArray, start: Int, length: Int) {
      currentElement.characters(ch, start, length)
    }

  }
}
