package com.khazoda.heirlooms.block;

import com.khazoda.heirlooms.block.util.VoxelShapeHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DisplayRackBlock extends DisplayBlock {
  public static final MapCodec<DisplayRackBlock> CODEC = simpleCodec(DisplayRackBlock::new);
  final VoxelShape blockShape = makeShape();
  final VoxelShape[] blockShapes = VoxelShapeHelper.calculateBlockShapes(blockShape); // Cache all shape directions

  public DisplayRackBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new DisplayRackBlockEntity(pos, state);
  }

  @Override
  protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    Direction direction = state.getValue(FACING);
    return VoxelShapeHelper.getSidedOutlineShape(direction, blockShape, blockShapes);
  }

  public VoxelShape makeShape() {
    VoxelShape shape = Shapes.empty();
    shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.4375, 0.75, 0.75, 0.8125), BooleanOp.OR);

    return shape;
  }
}