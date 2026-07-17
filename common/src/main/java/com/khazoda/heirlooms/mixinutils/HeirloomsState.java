package com.khazoda.heirlooms.mixinutils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HeirloomsState {
  private static final ThreadLocal<Player> CAPTURING_PLAYER = new ThreadLocal<>();

  public static void setCapturingPlayer(Player player) {
    if (player == null) {
      clearCapturingPlayer();
    } else {
      CAPTURING_PLAYER.set(player);
    }
  }

  public static void clearCapturingPlayer() {
    CAPTURING_PLAYER.remove();
  }

  public static boolean isCapturing(Player player) {
    return CAPTURING_PLAYER.get() == player;
  }

  public static boolean captureCrafted(ItemStack stack) {
    return SlotResultModifier.handleCraftedItem(CAPTURING_PLAYER.get(), stack);
  }
}