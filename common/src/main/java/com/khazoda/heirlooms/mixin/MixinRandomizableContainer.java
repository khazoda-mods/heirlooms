package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomizableContainer.class)
public interface MixinRandomizableContainer {
  @Inject(
      method = "unpackLootTable",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/level/storage/loot/LootTable;fill(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/storage/loot/LootParams;J)V",
          shift = Shift.AFTER
      )
  )
  private void heirlooms$onLootGenerated(Player player, CallbackInfo ci) {
    Container container = (Container) this;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      ItemStack stack = container.getItem(slot);
      SlotResultModifier.handleAcquiredItem(player, stack, SlotResultModifier.ACQUISITION_LOOTED);
    }
  }
}