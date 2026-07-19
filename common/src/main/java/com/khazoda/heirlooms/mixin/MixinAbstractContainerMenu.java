package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.mixinutils.HeirloomsState;
import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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

  @Unique
  private static boolean heirlooms$addsBookEnchantment(ItemStack input, ItemStack addition, ItemStack output) {
    if (!addition.is(Items.ENCHANTED_BOOK)) return false;
    var inputEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(input);
    for (var enchantment : EnchantmentHelper.getEnchantmentsForCrafting(output).entrySet()) {
      if (enchantment.getIntValue() > inputEnchantments.getLevel(enchantment.getKey())) return true;
    }
    return false;
  }

  @Shadow
  public abstract Slot getSlot(int index);

  @WrapOperation(
      method = "clicked",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;doClick(IILnet/minecraft/world/inventory/ContainerInput;Lnet/minecraft/world/entity/player/Player;)V"
      )
  )
  private void heirlooms$captureOutput(AbstractContainerMenu menu, int slotIndex, int buttonNum, ContainerInput containerInput, Player player, Operation<Void> original) {
    HeirloomsState.clearCapture();
    if (player == null || player.level().isClientSide()) {
      original.call(menu, slotIndex, buttonNum, containerInput, player);
      return;
    }

    try {
      heirlooms$armOutputCapture(slotIndex, player);
      original.call(menu, slotIndex, buttonNum, containerInput, player);
    } finally {
      HeirloomsState.clearCapture();
    }
  }

  @Unique
  private void heirlooms$armOutputCapture(int slotIndex, Player player) {
    if (slotIndex < 0 || (Object) this instanceof GrindstoneMenu) return;

    try {
      Slot slot = this.getSlot(slotIndex);
      ItemStack output = slot.getItem();
      if (output.isEmpty()) return;

      if ((Object) this instanceof AnvilMenu) {
        if (slotIndex != AnvilMenu.RESULT_SLOT || output.getMaxStackSize() != 1) return;
        ItemStack input = this.getSlot(AnvilMenu.INPUT_SLOT).getItem();
        ItemStack addition = this.getSlot(AnvilMenu.ADDITIONAL_SLOT).getItem();
        var customName = output.get(DataComponents.CUSTOM_NAME);
        boolean firstNaming = !input.has(DataComponents.CUSTOM_NAME)
            && customName != null
            && !customName.getString().isBlank();
        HeirloomsState.beginAnvilCapture(player, output, firstNaming,
            heirlooms$addsBookEnchantment(input, addition, output));
      } else if ((Object) this instanceof MerchantMenu) {
        if (slot instanceof MerchantResultSlot)
          HeirloomsState.beginAcquisitionCapture(player, output, SlotResultModifier.ACQUISITION_BOUGHT);
      } else if (heirlooms$isCraftingOutput(slot, output)) {
        HeirloomsState.beginAcquisitionCapture(player, output, SlotResultModifier.ACQUISITION_CRAFTED);
      }
    } catch (RuntimeException ignored) {
    }
  }

  @Inject(method = "setCarried", at = @At("RETURN"))
  private void heirlooms$captureCarried(ItemStack stack, CallbackInfo ci) {
    HeirloomsState.captureOutput(stack);
  }
}