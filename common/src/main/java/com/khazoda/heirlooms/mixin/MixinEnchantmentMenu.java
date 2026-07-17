package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public class MixinEnchantmentMenu {
  @Inject(method = "clickMenuButton", at = @At("RETURN"))
  private void heirlooms$onEnchanted(Player player, int buttonId, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) return;

    Slot slot = ((EnchantmentMenu) (Object) this).getSlot(0);
    SlotResultModifier.handleEnchantedItem(player, slot.getItem());
  }
}