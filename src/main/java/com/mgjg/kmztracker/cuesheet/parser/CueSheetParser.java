package com.mgjg.kmztracker.cuesheet.parser;

import com.mgjg.kmztracker.cuesheet.CueSheet;


public interface CueSheetParser
{
  CueSheet parse(CueSheet cueSheet) throws Exception;
}
