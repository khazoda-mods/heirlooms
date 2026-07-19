package com.khazoda.heirlooms;

import com.khazoda.heirlooms.platform.Services;

public class HeirloomsCommon {
  public static void init() {
    if (Services.PLATFORM.isModLoaded("heirlooms")) Constants.LOG.info("- Heirlooms Loaded -");
  }
}