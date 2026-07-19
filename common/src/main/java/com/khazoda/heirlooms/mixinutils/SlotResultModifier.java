package com.khazoda.heirlooms.mixinutils;

import com.khazoda.heirlooms.HeirloomsComponentMigration;
import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.time.Instant;

public final class SlotResultModifier {
  public static final String ACQUISITION_CRAFTED = "crafted";
  public static final String ACQUISITION_BOUGHT = "bought";
  public static final String ACQUISITION_LOOTED = "looted";

  private SlotResultModifier() {
  }

  public static boolean handleAcquiredItem(Player player, ItemStack stack, String acquisitionKind) {
    if (!canRecord(player, stack) || acquisitionKind == null || acquisitionKind.isBlank()) return false;
    String playerName = player.getGameProfile().name();
    if (playerName == null || playerName.isBlank()) return false;

    HeirloomsComponentMigration.migrateLegacyAcquisition(stack);
    if (hasAcquisitionData(stack))
      return false;

    stack.set(MainRegistry.ACQUIRED_TIMESTAMP.get(), Instant.now().toString());
    stack.set(MainRegistry.ACQUIRED_BY.get(), playerName);
    stack.set(MainRegistry.ACQUISITION_KIND.get(), acquisitionKind);
    stack.set(MainRegistry.ACQUISITION_X.get(), player.blockPosition().getX());
    stack.set(MainRegistry.ACQUISITION_Z.get(), player.blockPosition().getZ());
    stack.set(MainRegistry.ACQUISITION_DIMENSION.get(), dimensionId(player));
    return true;
  }

  public static boolean handleEnchantedItem(Player player, ItemStack stack) {
    if (!canRecord(player, stack)) return false;
    String playerName = player.getGameProfile().name();
    if (playerName == null || playerName.isBlank()) return false;
    if (stack.has(MainRegistry.ENCHANTED_TIMESTAMP.get())
        || stack.has(MainRegistry.ENCHANTED_BY.get())
        || stack.has(MainRegistry.ENCHANTED_X.get())
        || stack.has(MainRegistry.ENCHANTED_Z.get())
        || stack.has(MainRegistry.ENCHANTED_DIMENSION.get())) return false;

    stack.set(MainRegistry.ENCHANTED_TIMESTAMP.get(), Instant.now().toString());
    stack.set(MainRegistry.ENCHANTED_BY.get(), playerName);
    stack.set(MainRegistry.ENCHANTED_X.get(), player.blockPosition().getX());
    stack.set(MainRegistry.ENCHANTED_Z.get(), player.blockPosition().getZ());
    stack.set(MainRegistry.ENCHANTED_DIMENSION.get(), dimensionId(player));
    return true;
  }

  public static boolean handleFirstNamedItem(Player player, ItemStack stack) {
    if (!canRecord(player, stack) || hasFirstNamingData(stack)) return false;
    Component customName = stack.get(DataComponents.CUSTOM_NAME);
    if (customName == null) return false;
    String firstName = customName.getString();
    if (firstName.isBlank()) return false;
    String playerName = player.getGameProfile().name();
    if (playerName == null || playerName.isBlank()) return false;

    stack.set(MainRegistry.NAMED_FIRST.get(), firstName);
    stack.set(MainRegistry.NAMED_FIRST_TIMESTAMP.get(), Instant.now().toString());
    stack.set(MainRegistry.NAMED_FIRST_BY.get(), playerName);
    stack.set(MainRegistry.NAMED_FIRST_X.get(), player.blockPosition().getX());
    stack.set(MainRegistry.NAMED_FIRST_Z.get(), player.blockPosition().getZ());
    stack.set(MainRegistry.NAMED_FIRST_DIMENSION.get(), dimensionId(player));
    return true;
  }

  public static void removeAcquiredItem(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return;
    stack.remove(MainRegistry.ACQUIRED_TIMESTAMP.get());
    stack.remove(MainRegistry.ACQUIRED_BY.get());
    stack.remove(MainRegistry.ACQUISITION_KIND.get());
    stack.remove(MainRegistry.ACQUISITION_X.get());
    stack.remove(MainRegistry.ACQUISITION_Z.get());
    stack.remove(MainRegistry.ACQUISITION_DIMENSION.get());
  }

  public static void removeEnchantedItem(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return;
    stack.remove(MainRegistry.ENCHANTED_TIMESTAMP.get());
    stack.remove(MainRegistry.ENCHANTED_BY.get());
    stack.remove(MainRegistry.ENCHANTED_X.get());
    stack.remove(MainRegistry.ENCHANTED_Z.get());
    stack.remove(MainRegistry.ENCHANTED_DIMENSION.get());
  }

  public static void removeFirstNamedItem(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return;
    stack.remove(MainRegistry.NAMED_FIRST.get());
    stack.remove(MainRegistry.NAMED_FIRST_TIMESTAMP.get());
    stack.remove(MainRegistry.NAMED_FIRST_BY.get());
    stack.remove(MainRegistry.NAMED_FIRST_X.get());
    stack.remove(MainRegistry.NAMED_FIRST_Z.get());
    stack.remove(MainRegistry.NAMED_FIRST_DIMENSION.get());
  }

  private static boolean canRecord(Player player, ItemStack stack) {
    return player != null
        && stack != null
        && !player.level().isClientSide()
        && !stack.isEmpty()
        && stack.getMaxStackSize() == 1;
  }

  private static String dimensionId(Player player) {
    return player.level().dimension().identifier().toString();
  }

  private static boolean hasAcquisitionData(ItemStack stack) {
    return stack.has(MainRegistry.ACQUISITION_KIND.get())
        || stack.has(MainRegistry.ACQUIRED_TIMESTAMP.get())
        || stack.has(MainRegistry.ACQUIRED_BY.get())
        || stack.has(MainRegistry.ACQUISITION_X.get())
        || stack.has(MainRegistry.ACQUISITION_Z.get())
        || stack.has(MainRegistry.ACQUISITION_DIMENSION.get())
        || stack.has(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get())
        || stack.has(MainRegistry.LEGACY_CRAFTED_BY.get());
  }

  private static boolean hasFirstNamingData(ItemStack stack) {
    return stack.has(MainRegistry.NAMED_FIRST.get())
        || stack.has(MainRegistry.NAMED_FIRST_TIMESTAMP.get())
        || stack.has(MainRegistry.NAMED_FIRST_BY.get())
        || stack.has(MainRegistry.NAMED_FIRST_X.get())
        || stack.has(MainRegistry.NAMED_FIRST_Z.get())
        || stack.has(MainRegistry.NAMED_FIRST_DIMENSION.get());
  }
}