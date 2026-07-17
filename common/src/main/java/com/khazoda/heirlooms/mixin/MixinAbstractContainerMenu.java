package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.HeirloomsState;
import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu {
  @Unique
  private static boolean heirlooms$isCraftingOutput(Slot slot, ItemStack stack) {
    return slot instanceof ResultSlot || slot.container instanceof ResultContainer || !slot.mayPlace(stack);
  }

  @Shadow
  public abstract Slot getSlot(int index);

  @Inject(method = "clicked", at = @At("HEAD"))
  private void heirlooms$beginOutputCapture(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
    HeirloomsState.clearCapturingPlayer();
    if (slotIndex < 0 || (Object) this instanceof AnvilMenu || (Object) this instanceof GrindstoneMenu) return;

    try {
      Slot slot = this.getSlot(slotIndex);
      if ((Object) this instanceof MerchantMenu) {
        if (slot instanceof MerchantResultSlot && slot.hasItem()) {
          HeirloomsState.setCapturingPlayer(player, SlotResultModifier.ACQUISITION_BOUGHT);
        }
        return;
      }

      if (slot != null && slot.hasItem() && heirlooms$isCraftingOutput(slot, slot.getItem())) {
        HeirloomsState.setCapturingPlayer(player);
      }
    } catch (RuntimeException ignored) {
    }
  }

  @Inject(method = "clicked", at = @At("RETURN"))
  private void heirlooms$endOutputCapture(int slotIndex, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
    HeirloomsState.clearCapturingPlayer();
  }

  @Inject(method = "setCarried", at = @At("HEAD"))
  private void heirlooms$captureCarried(ItemStack stack, CallbackInfo ci) {
    HeirloomsState.captureAcquired(stack);
  }
}