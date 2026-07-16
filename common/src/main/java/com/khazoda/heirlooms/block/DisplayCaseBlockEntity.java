package com.khazoda.heirlooms.block;

import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayCaseBlockEntity extends DisplayBlockEntity {
  public DisplayCaseBlockEntity(BlockPos pos, BlockState state) {
    super(MainRegistry.DISPLAY_CASE_BE.get(), pos, state);
  }
}