package com.khazoda.heirlooms;

import com.khazoda.core.reg.KhazRegFabric;
import com.khazoda.heirlooms.registry.MainRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;

public class HeirloomsFabric implements ModInitializer {

  @Override
  public void onInitialize() {
    HeirloomsCommon.init();
    KhazRegFabric.init(MainRegistry::init);
    CreativeModeTabEvents.modifyOutputEvent(MainRegistry.HEIRLOOMS_TAB.key()).register(output -> MainRegistry.addMainTabItems(output::accept));
  }
}