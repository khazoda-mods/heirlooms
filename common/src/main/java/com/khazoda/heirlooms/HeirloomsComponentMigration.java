package com.khazoda.heirlooms;

import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.world.item.ItemStack;

// Todo: Remove some day once it's unlikely anyone will need to migrate any more
// search for usages in other classes as there are a few that will need nixing
public final class HeirloomsComponentMigration {
  private HeirloomsComponentMigration() {
  }

  public static boolean hasLegacyComponents(ItemStack stack) {
    return stack != null
        && !stack.isEmpty()
        && (stack.has(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get()) || stack.has(MainRegistry.LEGACY_CRAFTED_BY.get()));
  }

  public static boolean migrateLegacyAcquisition(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return false;

    // crafted_* became acquired_* when acquisition types expanded beyond crafting.
    String legacyTimestamp = stack.get(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get());
    String legacyBy = stack.get(MainRegistry.LEGACY_CRAFTED_BY.get());
    if (legacyTimestamp == null && legacyBy == null) return false;

    if (legacyTimestamp != null && !stack.has(MainRegistry.ACQUIRED_TIMESTAMP.get())) {
      stack.set(MainRegistry.ACQUIRED_TIMESTAMP.get(), legacyTimestamp);
    }
    if (legacyBy != null && !stack.has(MainRegistry.ACQUIRED_BY.get())) {
      stack.set(MainRegistry.ACQUIRED_BY.get(), legacyBy);
    }

    stack.remove(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get());
    stack.remove(MainRegistry.LEGACY_CRAFTED_BY.get());
    return true;
  }
}