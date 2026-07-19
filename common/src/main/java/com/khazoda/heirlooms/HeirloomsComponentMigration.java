package com.khazoda.heirlooms;

import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.world.item.ItemStack;

// Automatic crafted_* migration. Keep this and the legacy component registrations
// together until releases containing the old component are old enough to stop supporting.
public final class HeirloomsComponentMigration {
  private HeirloomsComponentMigration() {
  }

  public static boolean isLegacyAcquisition(ItemStack stack) {
    return stack != null
        && !stack.isEmpty()
        && stack.has(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get())
        && stack.has(MainRegistry.LEGACY_CRAFTED_BY.get())
        && !stack.has(MainRegistry.ACQUIRED_TIMESTAMP.get())
        && !stack.has(MainRegistry.ACQUIRED_BY.get())
        && !stack.has(MainRegistry.ACQUISITION_KIND.get())
        && !stack.has(MainRegistry.ACQUISITION_X.get())
        && !stack.has(MainRegistry.ACQUISITION_Z.get())
        && !stack.has(MainRegistry.ACQUISITION_DIMENSION.get());
  }

  public static boolean migrateLegacyAcquisition(ItemStack stack) {
    if (!isLegacyAcquisition(stack)) return false;

    String legacyTimestamp = stack.get(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get());
    String legacyBy = stack.get(MainRegistry.LEGACY_CRAFTED_BY.get());
    if (legacyTimestamp == null || legacyBy == null) return false;

    stack.set(MainRegistry.ACQUIRED_TIMESTAMP.get(), legacyTimestamp);
    stack.set(MainRegistry.ACQUIRED_BY.get(), legacyBy);
    stack.set(MainRegistry.ACQUISITION_KIND.get(), SlotResultModifier.ACQUISITION_CRAFTED);
    stack.remove(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get());
    stack.remove(MainRegistry.LEGACY_CRAFTED_BY.get());
    return true;
  }
}