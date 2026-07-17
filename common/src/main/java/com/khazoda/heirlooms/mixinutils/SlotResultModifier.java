package com.khazoda.heirlooms.mixinutils;

import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.time.Instant;

public class SlotResultModifier {
  public static boolean handleCraftedItem(Player player, ItemStack stack) {
    if (player == null || stack == null) return false;
    if (player.level().isClientSide()) return false;
    if (stack.isEmpty()) return false;
    if (stack.getMaxStackSize() > 1) return false;
    if (stack.has(MainRegistry.CRAFTED_TIMESTAMP.get()) || stack.has(MainRegistry.CRAFTED_BY.get())) return false;

    stack.set(MainRegistry.CRAFTED_TIMESTAMP.get(), Instant.now().toString());
    stack.set(MainRegistry.CRAFTED_BY.get(), player.getGameProfile().name());
    return true;
  }

  public static void handleEnchantedItem(Player player, ItemStack stack) {
    if (player == null || stack == null) return;
    if (player.level().isClientSide()) return;
    if (stack.isEmpty()) return;
    if (stack.getMaxStackSize() > 1) return;
    if (stack.has(MainRegistry.ENCHANTED_TIMESTAMP.get()) || stack.has(MainRegistry.ENCHANTED_BY.get())) return;

    stack.set(MainRegistry.ENCHANTED_TIMESTAMP.get(), Instant.now().toString());
    stack.set(MainRegistry.ENCHANTED_BY.get(), player.getGameProfile().name());
  }

  public static void removeCraftedItem(ItemStack stack) {
    if (stack == null) return;
    if (stack.isEmpty()) return;
    stack.remove(MainRegistry.CRAFTED_TIMESTAMP.get());
    stack.remove(MainRegistry.CRAFTED_BY.get());
  }

  public static void removeEnchantedItem(ItemStack stack) {
    if (stack == null) return;
    if (stack.isEmpty()) return;
    stack.remove(MainRegistry.ENCHANTED_TIMESTAMP.get());
    stack.remove(MainRegistry.ENCHANTED_BY.get());
  }
}