package com.khazoda.heirlooms.mixinutils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class HeirloomsState {
  private static final ThreadLocal<Capture> CAPTURE = new ThreadLocal<>();

  private HeirloomsState() {
  }

  public static void beginAcquisitionCapture(Player player, ItemStack expectedStack, String acquisitionKind) {
    CAPTURE.set(new Capture(player, expectedStack.getItem(), acquisitionKind, false, false));
  }

  public static void beginAnvilCapture(Player player, ItemStack expectedStack, boolean firstNaming, boolean enchantment) {
    if (firstNaming || enchantment)
      CAPTURE.set(new Capture(player, expectedStack.getItem(), null, firstNaming, enchantment));
  }

  public static void clearCapture() {
    CAPTURE.remove();
  }

  public static CaptureResult captureOutput(Player player, ItemStack stack) {
    return captureOutput(player, stack, true);
  }

  public static void captureOutput(ItemStack stack) {
    captureOutput(null, stack, false);
  }

  private static CaptureResult captureOutput(Player player, ItemStack stack, boolean checkPlayer) {
    Capture capture = CAPTURE.get();
    if (capture == null || stack == null || stack.isEmpty()) return CaptureResult.NONE;
    if ((checkPlayer && capture.player() != player) || !stack.is(capture.expectedItem())) return CaptureResult.NONE;

    CAPTURE.remove();
    boolean acquisition = capture.acquisitionKind() != null
        && SlotResultModifier.handleAcquiredItem(capture.player(), stack, capture.acquisitionKind());
    boolean firstNaming = capture.firstNaming()
        && SlotResultModifier.handleFirstNamedItem(capture.player(), stack);
    boolean enchantment = capture.enchantment()
        && SlotResultModifier.handleEnchantedItem(capture.player(), stack);
    return acquisition || firstNaming || enchantment
        ? new CaptureResult(acquisition, firstNaming, enchantment)
        : CaptureResult.NONE;
  }

  public record CaptureResult(boolean acquisition, boolean firstNaming, boolean enchantment) {
    public static final CaptureResult NONE = new CaptureResult(false, false, false);

    public void rollback(ItemStack stack) {
      if (this.acquisition) SlotResultModifier.removeAcquiredItem(stack);
      if (this.firstNaming) SlotResultModifier.removeFirstNamedItem(stack);
      if (this.enchantment) SlotResultModifier.removeEnchantedItem(stack);
    }
  }

  private record Capture(Player player, Item expectedItem, String acquisitionKind, boolean firstNaming,
                         boolean enchantment) {
  }
}