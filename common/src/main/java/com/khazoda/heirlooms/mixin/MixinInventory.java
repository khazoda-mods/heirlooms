package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.HeirloomsComponentMigration;
import com.khazoda.heirlooms.mixinutils.HeirloomsState;
import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
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
  private boolean heirlooms$capturedAddStack;

  @Unique
  private boolean heirlooms$capturedAddAtIndex;

  @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
  private void heirlooms$captureAdd(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
    heirlooms$migrateStack(itemStack);
    this.heirlooms$capturedAddStack = heirlooms$capture(itemStack);
  }

  @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
  private void heirlooms$rollbackAdd(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
    if (this.heirlooms$capturedAddStack && !cir.getReturnValue()) {
      SlotResultModifier.removeAcquiredItem(itemStack);
    }
    this.heirlooms$capturedAddStack = false;
  }

  @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"))
  private void heirlooms$captureAddAtIndex(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
    heirlooms$migrateStack(itemStack);
    this.heirlooms$capturedAddAtIndex = heirlooms$capture(itemStack);
  }

  @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
  private void heirlooms$rollbackAddAtIndex(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
    if (this.heirlooms$capturedAddAtIndex && !cir.getReturnValue()) {
      SlotResultModifier.removeAcquiredItem(itemStack);
    }
    this.heirlooms$capturedAddAtIndex = false;
  }

  @Inject(method = "setItem", at = @At("HEAD"))
  private void heirlooms$captureSetItem(int slot, ItemStack itemStack, CallbackInfo ci) {
    heirlooms$migrateStack(itemStack);
    heirlooms$capture(itemStack);
  }

  @Unique
  private boolean heirlooms$capture(ItemStack stack) {
    return HeirloomsState.isCapturing(this.player) && HeirloomsState.captureAcquired(stack);
  }

  @Unique
  private void heirlooms$migrateStack(ItemStack stack) {
    if (this.player.level().isClientSide()) return;
    HeirloomsComponentMigration.migrateLegacyAcquisition(stack);
  }
}