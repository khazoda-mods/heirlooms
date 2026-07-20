package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import net.minecraft.advancements.criterion.EnchantedItemTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantedItemTrigger.class)
public class MixinEnchantedItemTrigger {
  @Inject(method = "trigger", at = @At("RETURN"))
  private void heirlooms$onEnchanted(ServerPlayer player, ItemStack stack, int levels, CallbackInfo ci) {
    SlotResultModifier.handleEnchantedItem(player, stack);
  }
}