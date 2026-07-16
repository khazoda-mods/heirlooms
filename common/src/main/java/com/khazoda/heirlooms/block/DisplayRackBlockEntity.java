package com.khazoda.heirlooms.block;

import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayRackBlockEntity extends DisplayBlockEntity {
  public DisplayRackBlockEntity(BlockPos pos, BlockState state) {
    super(MainRegistry.DISPLAY_RACK_BE.get(), pos, state);
  }
}