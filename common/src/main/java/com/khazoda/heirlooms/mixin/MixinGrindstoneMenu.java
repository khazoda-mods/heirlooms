package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public class MixinGrindstoneMenu {
  @Inject(method = "removeNonCursesFrom", at = @At(value = "RETURN"))
  private void heirlooms$onEnchantmentRemoved(ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
    SlotResultModifier.removeEnchantedItem(cir.getReturnValue());
  }
}