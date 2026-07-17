package com.khazoda.heirlooms.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

public class DisplayCaseBlock extends DisplayBlock {
  public static final MapCodec<DisplayCaseBlock> CODEC = simpleCodec(DisplayCaseBlock::new);

  public DisplayCaseBlock(BlockBehaviour.Properties properties) {
    super(properties);
  }

  public static BlockBehaviour.Properties defaultProperties(BlockBehaviour.Properties properties) {
    return DisplayBlock.defaultProperties(properties)
        .strength(2.0F, 6.0F)
        .mapColor(MapColor.STONE)
        .instrument(NoteBlockInstrument.BASEDRUM)
        .sound(SoundType.STONE);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new DisplayCaseBlockEntity(pos, state);
  }
}