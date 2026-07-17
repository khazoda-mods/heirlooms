package com.khazoda.heirlooms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.BiConsumer;

public abstract class DisplayBlock extends BaseEntityBlock {
  public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

  protected DisplayBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  public static BlockBehaviour.Properties defaultProperties(BlockBehaviour.Properties properties) {
    return properties
        .noOcclusion()
        .isSuffocating((_, _, _) -> false)
        .isViewBlocking((_, _, _) -> false);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
    if (level.getBlockEntity(pos) instanceof DisplayBlockEntity displayBlock) {
      return displayBlock.getItem(0).isEmpty() ? 0 : 15;
    }
    return 0;
  }

  @Override
  protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    if (!(level.getBlockEntity(pos) instanceof DisplayBlockEntity displayBlock)) {
      return InteractionResult.PASS;
    }

    if (level.isClientSide()) {
      return InteractionResult.SUCCESS;
    }

    ItemStack currentItem = displayBlock.getItem(0);
    if (currentItem.isEmpty() && !stack.isEmpty()) {
      displayBlock.setItem(0, player.getAbilities().instabuild ? stack.copyWithCount(1) : stack.split(1));
      level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
      return InteractionResult.SUCCESS;
    }

    if (currentItem.isEmpty()) {
      return InteractionResult.PASS;
    }

    ItemStack handStack = player.getItemInHand(hand);
    if (handStack.isEmpty()) {
      player.setItemInHand(hand, currentItem);
      displayBlock.setItem(0, ItemStack.EMPTY);
      level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
      return InteractionResult.SUCCESS;
    }

    displayBlock.setItem(0, player.getAbilities().instabuild ? handStack.copyWithCount(1) : handStack.split(1));
    if (handStack.isEmpty()) {
      player.setItemInHand(hand, currentItem);
    } else if (!player.getInventory().add(currentItem)) {
      Block.popResource(level, pos, currentItem);
    }
    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
    return InteractionResult.SUCCESS;
  }

  @Override
  protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
    dropBlockContents(level, pos);
    super.onExplosionHit(state, level, pos, explosion, dropConsumer);
  }

  @Override
  public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    dropBlockContents(level, pos);
    super.playerWillDestroy(level, pos, state, player);
    return state;
  }

  private void dropBlockContents(Level level, BlockPos pos) {
    if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DisplayBlockEntity displayBlock) {
      Containers.dropContents(level, pos, displayBlock);
      displayBlock.clearContent();
      level.updateNeighbourForOutputSignal(pos, this);
    }
  }

  @Override
  public boolean skipRendering(BlockState state, BlockState neighborState, Direction side) {
    return neighborState.is(this) || super.skipRendering(state, neighborState, side);
  }

  @Override
  public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
    return 1.0F;
  }
}