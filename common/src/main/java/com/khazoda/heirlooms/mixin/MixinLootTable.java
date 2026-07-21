package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public class MixinLootTable {
  @Inject(method = "fill", at = @At("HEAD"))
  private void heirlooms$captureEmptySlots(Container container, LootParams params, long seed, CallbackInfo ci,
                                           @Share("emptySlots") LocalRef<boolean[]> emptySlots) {
    if (!(params.contextMap().getOptional(LootContextParams.THIS_ENTITY) instanceof ServerPlayer)) return;
    int size = container.getContainerSize();
    if (size <= 0) return;
    boolean[] slots = new boolean[size];
    for (int slot = 0; slot < size; slot++)
      slots[slot] = container.getItem(slot).isEmpty();
    emptySlots.set(slots);
  }

  @Inject(method = "fill", at = @At("RETURN"))
  private void heirlooms$tagGeneratedLoot(Container container, LootParams params, long seed, CallbackInfo ci,
                                          @Share("emptySlots") LocalRef<boolean[]> emptySlots) {
    boolean[] slots = emptySlots.get();
    if (slots == null
        || !(params.contextMap().getOptional(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player)) return;
    int size = Math.min(slots.length, container.getContainerSize());
    for (int slot = 0; slot < size; slot++) {
      ItemStack stack = container.getItem(slot);
      if (slots[slot] && !stack.isEmpty())
        SlotResultModifier.handleAcquiredItem(player, stack, SlotResultModifier.ACQUISITION_LOOTED);
    }
  }
}