package com.khazoda.heirlooms;

import com.khazoda.heirlooms.platform.Services;
import com.khazoda.heirlooms.registry.MainRegistry;

public class HeirloomsCommon {
  public static void init() {
    MainRegistry.init();
    if (Services.PLATFORM.isModLoaded("heirlooms")) Constants.LOG.info("- Heirlooms Loaded -");
  }
}