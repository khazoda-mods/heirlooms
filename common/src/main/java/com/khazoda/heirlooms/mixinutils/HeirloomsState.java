package com.khazoda.heirlooms.mixinutils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HeirloomsState {
  private static final ThreadLocal<Capture> CAPTURE = new ThreadLocal<>();

  public static void setCapturingPlayer(Player player) {
    setCapturingPlayer(player, SlotResultModifier.ACQUISITION_CRAFTED);
  }

  public static void setCapturingPlayer(Player player, String acquisitionKind) {
    if (player == null) {
      clearCapturingPlayer();
    } else {
      CAPTURE.set(new Capture(player, acquisitionKind));
    }
  }

  public static void clearCapturingPlayer() {
    CAPTURE.remove();
  }

  public static boolean isCapturing(Player player) {
    Capture capture = CAPTURE.get();
    return capture != null && capture.player() == player;
  }

  public static boolean captureAcquired(ItemStack stack) {
    Capture capture = CAPTURE.get();
    return capture != null && SlotResultModifier.handleAcquiredItem(capture.player(), stack, capture.acquisitionKind());
  }

  private record Capture(Player player, String acquisitionKind) {
  }
}