package com.khazoda.heirlooms.mixinutils;

import com.khazoda.heirlooms.HeirloomsComponentMigration;
import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.time.Instant;

public class SlotResultModifier {
  public static final String ACQUISITION_CRAFTED = "crafted";
  public static final String ACQUISITION_BOUGHT = "bought";
  public static final String ACQUISITION_LOOTED = "looted";

  public static boolean handleCraftedItem(Player player, ItemStack stack) {
    return handleAcquiredItem(player, stack, ACQUISITION_CRAFTED);
  }

  public static boolean handleAcquiredItem(Player player, ItemStack stack, String acquisitionKind) {
    if (player == null || stack == null) return false;
    if (player.level().isClientSide()) return false;
    if (stack.isEmpty()) return false;
    if (stack.getMaxStackSize() > 1) return false;
    HeirloomsComponentMigration.migrateLegacyAcquisition(stack);
    if (stack.has(MainRegistry.ACQUIRED_TIMESTAMP.get()) || stack.has(MainRegistry.ACQUIRED_BY.get()) || stack.has(MainRegistry.ACQUISITION_KIND.get()))
      return false;

    stack.set(MainRegistry.ACQUIRED_TIMESTAMP.get(), Instant.now().toString());
    stack.set(MainRegistry.ACQUIRED_BY.get(), player.getGameProfile().name());
    stack.set(MainRegistry.ACQUISITION_KIND.get(), acquisitionKind);
    stack.set(MainRegistry.ACQUISITION_X.get(), player.blockPosition().getX());
    stack.set(MainRegistry.ACQUISITION_Z.get(), player.blockPosition().getZ());
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
    stack.set(MainRegistry.ENCHANTED_X.get(), player.blockPosition().getX());
    stack.set(MainRegistry.ENCHANTED_Z.get(), player.blockPosition().getZ());
  }

  public static void removeAcquiredItem(ItemStack stack) {
    if (stack == null) return;
    if (stack.isEmpty()) return;
    HeirloomsComponentMigration.migrateLegacyAcquisition(stack);
    stack.remove(MainRegistry.ACQUIRED_TIMESTAMP.get());
    stack.remove(MainRegistry.ACQUIRED_BY.get());
    stack.remove(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get());
    stack.remove(MainRegistry.LEGACY_CRAFTED_BY.get());
    stack.remove(MainRegistry.ACQUISITION_KIND.get());
    stack.remove(MainRegistry.ACQUISITION_X.get());
    stack.remove(MainRegistry.ACQUISITION_Z.get());
  }

  public static void removeEnchantedItem(ItemStack stack) {
    if (stack == null) return;
    if (stack.isEmpty()) return;
    stack.remove(MainRegistry.ENCHANTED_TIMESTAMP.get());
    stack.remove(MainRegistry.ENCHANTED_BY.get());
    stack.remove(MainRegistry.ENCHANTED_X.get());
    stack.remove(MainRegistry.ENCHANTED_Z.get());
  }
}