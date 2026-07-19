package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RandomizableContainer.class)
public interface MixinRandomizableContainer {
  @WrapOperation(
      method = "unpackLootTable",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V"
      )
  )
  private void heirlooms$onLootGenerated(LootTable table, Container container, LootParams params, long seed, Operation<Void> original, Player player) {
    if (player == null) {
      original.call(table, container, params, seed);
      return;
    }

    int size = container.getContainerSize();
    ItemStack[] previousContents = new ItemStack[size];
    for (int slot = 0; slot < size; slot++)
      previousContents[slot] = container.getItem(slot).copy();

    original.call(table, container, params, seed);
    for (int slot = 0; slot < size; slot++) {
      ItemStack stack = container.getItem(slot);
      if (!ItemStack.matches(previousContents[slot], stack))
        SlotResultModifier.handleAcquiredItem(player, stack, SlotResultModifier.ACQUISITION_LOOTED);
    }
  }
}