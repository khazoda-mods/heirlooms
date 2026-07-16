package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* FOR GRINDSTONE */
/* Targets the method that's called to disenchant an item */
@Mixin(GrindstoneMenu.class)
public class MixinGrindstoneMenu {
  @Inject(method = "removeNonCursesFrom", at = @At(value = "RETURN"))
  private void heirlooms$onEnchantmentRemoved(ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
    if (item.has(MainRegistry.ENCHANTED_TIMESTAMP.get()) || item.has(MainRegistry.ENCHANTED_BY.get())) {
      item.remove(MainRegistry.ENCHANTED_TIMESTAMP.get());
      item.remove(MainRegistry.ENCHANTED_BY.get());
    }
  }
}