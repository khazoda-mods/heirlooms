package com.khazoda.heirlooms.block.renderer;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class DisplayRenderState extends BlockEntityRenderState {
  public final BlockModelRenderState block = new BlockModelRenderState();
  @Nullable
  public ItemStackRenderState item;
  public Direction facing = Direction.NORTH;
}