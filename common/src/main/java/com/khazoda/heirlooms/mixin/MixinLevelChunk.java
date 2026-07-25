package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.HeirloomsComponentMigration;
import com.khazoda.heirlooms.block.DisplayBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public class MixinLevelChunk {
  @Inject(method = "setBlockEntity", at = @At("TAIL"))
  private void heirlooms$migrateDisplayItem(BlockEntity blockEntity, CallbackInfo ci) {
    if (!(blockEntity instanceof DisplayBlockEntity display)) return;

    LevelChunk chunk = (LevelChunk) (Object) this;
    if (chunk.getBlockEntities().get(blockEntity.getBlockPos()) != blockEntity) return;
    if (!HeirloomsComponentMigration.migrateLegacyAcquisition(display.getItem(0))) return;
    if (!chunk.getLevel().isClientSide()) chunk.markUnsaved();
  }
}