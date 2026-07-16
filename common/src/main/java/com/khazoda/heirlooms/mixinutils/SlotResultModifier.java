package com.khazoda.heirlooms.mixinutils;

import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.time.Instant;

public class SlotResultModifier {
  public static void handleCraftedItem(Player player, ItemStack stack) {
    if (player.level().isClientSide()) return;
    if (stack.isEmpty()) return;
    if (stack.getMaxStackSize() > 1) return;
    if (stack.has(MainRegistry.CRAFTED_TIMESTAMP.get()) || stack.has(MainRegistry.CRAFTED_BY.get())) return;

    stack.set(MainRegistry.CRAFTED_TIMESTAMP.get(), Instant.now().toString());
    stack.set(MainRegistry.CRAFTED_BY.get(), player.getGameProfile().name());
  }

  public static void handleEnchantedItem(Player player, ItemStack stack) {
    if (player.level().isClientSide()) return;
    if (stack.isEmpty()) return;
    if (stack.getMaxStackSize() > 1) return;
    if (stack.has(MainRegistry.ENCHANTED_TIMESTAMP.get()) || stack.has(MainRegistry.ENCHANTED_BY.get())) return;

    stack.set(MainRegistry.ENCHANTED_TIMESTAMP.get(), Instant.now().toString());
    stack.set(MainRegistry.ENCHANTED_BY.get(), player.getGameProfile().name());
  }
}