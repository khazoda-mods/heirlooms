package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.HeirloomsState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class MixinSlot {
  @Inject(method = "safeTake", at = @At("RETURN"))
  private void heirlooms$captureTakenItem(int amount, int maxAmount, Player player, CallbackInfoReturnable<ItemStack> cir) {
    HeirloomsState.captureOutput(player, cir.getReturnValue());
  }
}