package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.HeirloomsComponentMigration;
import com.khazoda.heirlooms.mixinutils.HeirloomsState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class MixinInventory {
  @Final
  @Shadow
  public Player player;

  @Unique
  private HeirloomsState.CaptureResult heirlooms$capturedAddAtIndex = HeirloomsState.CaptureResult.NONE;

  @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
  private void heirlooms$captureAddAtIndex(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
    heirlooms$migrateStack(itemStack);
    this.heirlooms$capturedAddAtIndex = HeirloomsState.captureOutput(this.player, itemStack);
  }

  @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
  private void heirlooms$rollbackAddAtIndex(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) this.heirlooms$capturedAddAtIndex.rollback(itemStack);
    this.heirlooms$capturedAddAtIndex = HeirloomsState.CaptureResult.NONE;
  }

  @Inject(method = "setItem", at = @At("RETURN"))
  private void heirlooms$captureSetItem(int slot, ItemStack itemStack, CallbackInfo ci) {
    heirlooms$migrateStack(itemStack);
    HeirloomsState.captureOutput(this.player, itemStack);
  }

  @Unique
  private void heirlooms$migrateStack(ItemStack stack) {
    if (this.player.level().isClientSide()) return;
    HeirloomsComponentMigration.migrateLegacyAcquisition(stack);
  }
}