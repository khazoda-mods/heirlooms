package com.khazoda.heirlooms;

import com.khazoda.core.keybind.KhazClientKeybinds;
import com.khazoda.core.keybind.KhazKeybind;
import com.mojang.blaze3d.platform.InputConstants;

public final class HeirloomsKeybinds {
  public static final KhazKeybind SHOW_ACQUISITION_DETAILS = KhazKeybind.builder(Constants.ID("show_acquisition_details")).defaultKey(InputConstants.KEY_LCONTROL).build();
  public static final KhazKeybind SHOW_ENCHANTMENT_DETAILS = KhazKeybind.builder(Constants.ID("show_enchantment_details")).defaultKey(InputConstants.KEY_LALT).build();

  private HeirloomsKeybinds() {
  }

  public static void register() {
    KhazClientKeybinds.register(SHOW_ACQUISITION_DETAILS, SHOW_ENCHANTMENT_DETAILS);
  }
}