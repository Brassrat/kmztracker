package com.mgjg.kmztracker.cuesheet.parser

import android.util.Log
import com.mgjg.kmztracker.cuesheet.CueSheet
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.lang.Double.parseDouble
import java.util.*
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

class CueSheetGpxParser internal constructor(url: String) :
  CueSheetParserFactory.CueSheetFactory(url) {

  private lateinit var parser: GpxParser

  @Throws(Exception::class)
  override fun parse(cueSheet: CueSheet): CueSheet {
    // sax stuff
    try {
      val spf = SAXParserFactory.newInstance()
      val sp = spf.newSAXParser()

      val xr = sp.xmlReader

      parser = GpxParser(cueSheet)

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

  private inner class GpxParser internal constructor(private val cueSheet: CueSheet) :
    DefaultHandler() {

    private val NOTAG = ElementNOTAG()
    private var currentElement: Element = NOTAG

    private val knownTags = HashMap<String, Element>()

    init {
      knownTags["gpx"] = ElementGPX()
      knownTags["author"] = ElementAUTHOR()
      knownTags["url"] = ElementURL()
      knownTags["time"] = ElementTIME()
      knownTags["trk"] = ElementTRK()
      knownTags["name"] = ElementNAME()
      knownTags["trkseg"] = ElementTRKSEG()
      knownTags["trkpt"] = ElementTRKPT()
      knownTags["ele"] = ElementELE()
    }

    fun addTrk(name: String) {
      cueSheet.addTrk(name)
    }

    fun addTrkpt(lat: Double, lon: Double, alt: Double) {
      cueSheet.addPt(lat, lon, alt)
    }

    fun addTrkpt(lat: Double, lon: Double) {
      cueSheet.addPt(lat, lon)
    }

    @Throws(SAXException::class)
    override fun startDocument() {
      // this.cueSheet = new CueSheet();
      currentElement = NOTAG
    }

    @Throws(SAXException::class)
    override fun endDocument() {
      // Nothing to do
      currentElement = NOTAG
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
      var newElement: Element? = null
      if (knownTags.containsKey(localName)) {
        val ee = knownTags[localName]!!
        try {
          val cc = ee::class
          val ccc = cc.constructors.first()
          //val ccc = cc.primaryConstructor
          newElement = ccc.call(this@CueSheetGpxParser)
          //val cons = cc.getConstructor(CueSheetGpxParser::class.java)
          //newElement = cons.newInstance(this@CueSheetGpxParser)
          //newElement = knownTags.get(localName).getClass().getConstructor().newInstance();
        } catch (e: Exception) {
          newElement = null
        }

      }

      if (newElement == null) {
        when (localName) {
          "gpx" -> newElement = ElementGPX()
          "author" -> newElement = ElementAUTHOR()
          "url" -> newElement = ElementURL()
          "time" -> newElement = ElementTIME()
          "trk" -> newElement = ElementTRK()
          "name" -> newElement = ElementNAME()
          "trkseg" -> newElement = ElementTRKSEG()
          "trkpt" -> newElement = ElementTRKPT()
          "ele" -> newElement = ElementELE()
        }
      }

      if (null == newElement) {
        Log.i(cueSheet.appName, "Unknown tag, ignore: $localName")
        newElement = ElementNOTAG()
      }

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

  private abstract inner class Element {

    protected lateinit var parent: Element

    abstract fun start(): Element

    fun push(currentElement: Element): Element {
      parent = currentElement
      return this
    }

    fun pop(): Element {
      return parent
    }

    open fun setAuthor(value: String?) {}

    open fun setUrl(value: String?) {}

    open fun setTime(value: String?) {}

    open fun setName(value: String?) {}

    fun addTrk(value: String) {
      parser.addTrk(value)
    }

    open fun addTrkpt(lat: Double, lon: Double, alt: Double) {
      parser.addTrkpt(lat, lon, alt)
    }

    open fun addTrkpt(lat: Double, lon: Double) {
      parser.addTrkpt(lat, lon)
    }

    /*
    public void addPlacemark(double lat, double lon, String title, String description)
    {
        parser.addPlacemark(lat, lon, title, description);
    }
    */

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

    open fun setEle(ele: String) {

    }
  }

  private inner class ElementNOTAG : Element() {
    override fun start() = ElementNOTAG()
  }

  private inner class ElementGPX : Element() {
    internal var author: String? = null
    internal var url: String? = null
    internal var time: String? = null

    override fun start(): Element = ElementGPX()

    override fun setAuthor(value: String?) {
      author = value
    }

    override fun setUrl(value: String?) {
      url = value
    }

    override fun setTime(value: String?) {
      time = value
    }

  }

  private inner class ElementAUTHOR : Element() {
    private var author: String? = null

    override fun setAuthor(value: String?) {
      author = value
    }

    override fun start(): Element = ElementAUTHOR()

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      author = ""
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
      author = String(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      parent.setAuthor(author)
      author = null
    }
  }

  private inner class ElementTIME : Element() {
    private var time: String? = null

    override fun start(): Element = ElementTIME()

    override fun setTime(value: String?) {
      time = value
    }

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      time = ""
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
      time = String(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      parent.setTime(time)
      time = null
    }
  }

  private inner class ElementURL : Element() {
    private var url: String? = null

    override fun start(): Element = ElementURL()

    override fun setUrl(value: String?) {
      url = value
    }

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      url = ""
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
      url = String(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      parent.setUrl(url)
      url = null
    }
  }

  private inner class ElementTRK : Element() {
    private var name: String? = null

    override fun start(): Element = ElementTRK()

    override fun setName(value: String?) {
      name = value
    }

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      name = ""
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      name = null
    }
  }

  private inner class ElementNAME : Element() {
    private var name: String? = null

    override fun start(): Element = ElementNAME()

    override fun setName(value: String?) {
      name = value
    }

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      name = ""
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
      name = String(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      parent.setName(name)
      name = null
    }
  }

  private inner class ElementTRKSEG : Element() {

    override fun start(): Element = ElementTRKSEG()

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {}

    override fun addTrkpt(lat: Double, lon: Double, alt: Double) {
      parser.addTrkpt(lat, lon, alt)
    }

    override fun addTrkpt(lat: Double, lon: Double) {
      parser.addTrkpt(lat, lon)
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
    }
  }

  private inner class ElementTRKPT : Element() {
    private var lat: String? = null
    private var lon: String? = null
    private var ele: String? = null

    override fun start(): Element = ElementTRKPT()

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      lat = null
      lon = null
      ele = null
      if (atts.length > 0) {
        lat = atts.getValue("lat")
        lon = atts.getValue("lon")
      }
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      // copy var values into local variables so null checks are valid in if statements
      val zlat = lat
      val zlon = lon
      val zele = ele
      if (null != zlat && null != zlon) {
        val dlat = parseDouble(zlat)
        val dlon = parseDouble(zlon)
        if (null != zele) {
          parent.addTrkpt(dlat, dlon, parseDouble(zele))
        } else {
          parent.addTrkpt(dlat, dlon)
        }
      }
      lat = null
      lon = null
      ele = null
    }

    override fun setEle(ele: String) {
      // called from child ...
      this.ele = ele
    }
  }

  private inner class ElementELE : Element() {
    private var ele: String? = null

    fun setEle(value: String?) {
      ele = value
    }

    override fun start(): Element = ElementELE()

    @Throws(SAXException::class)
    override fun startElement(
      namespaceURI: String,
      localName: String,
      qName: String,
      atts: Attributes
    ) {
      ele = null
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
      ele = String(ch, start, length)
    }

    @Throws(SAXException::class)
    override fun endElement(namespaceURI: String, localName: String, qName: String) {
      if (null != ele) {
        parent.setEle(ele!!)
      }
      ele = null
    }

  }

}

