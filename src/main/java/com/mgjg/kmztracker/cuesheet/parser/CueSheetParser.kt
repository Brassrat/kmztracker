package com.mgjg.kmztracker.cuesheet.parser

import com.mgjg.kmztracker.cuesheet.CueSheet


interface CueSheetParser {
  @Throws(Exception::class)
  fun parse(cueSheet: CueSheet): CueSheet
}
