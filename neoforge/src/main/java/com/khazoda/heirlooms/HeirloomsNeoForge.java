package com.khazoda.heirlooms;

import com.khazoda.core.reg.KhazRegNeoForge;
import com.khazoda.heirlooms.registry.MainRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(Constants.MOD_ID)
public class HeirloomsNeoForge {
  public HeirloomsNeoForge(IEventBus eventBus) {
    HeirloomsCommon.init();
    KhazRegNeoForge.init(eventBus, MainRegistry::init);
    eventBus.addListener(this::onBuildCreativeModeTabContents);
  }

  private void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
    if (MainRegistry.HEIRLOOMS_TAB.key().equals(event.getTabKey())) {
      MainRegistry.addMainTabItems(event::accept);
    }
  }
}