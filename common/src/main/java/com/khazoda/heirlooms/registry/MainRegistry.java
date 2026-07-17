package com.khazoda.heirlooms.registry;

import com.khazoda.core.reg.KhazReg;
import com.khazoda.core.reg.KhazReg.BlockEntry;
import com.khazoda.core.reg.KhazReg.Entry;
import com.khazoda.heirlooms.Constants;
import com.khazoda.heirlooms.block.DisplayCaseBlock;
import com.khazoda.heirlooms.block.DisplayCaseBlockEntity;
import com.khazoda.heirlooms.block.DisplayRackBlock;
import com.khazoda.heirlooms.block.DisplayRackBlockEntity;
import com.khazoda.heirlooms.platform.Services;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MainRegistry {
  public static final KhazReg reg = new KhazReg(Constants.MOD_ID);
  public static final Entry<DataComponentType<String>> ACQUISITION_KIND = component("acquisition_kind");
  public static final Entry<DataComponentType<String>> ACQUIRED_TIMESTAMP = component("acquired_timestamp");
  public static final Entry<DataComponentType<String>> ACQUIRED_BY = component("acquired_by");
  public static final Entry<DataComponentType<Integer>> ACQUISITION_X = intComponent("acquisition_x");
  public static final Entry<DataComponentType<Integer>> ACQUISITION_Z = intComponent("acquisition_z");
  public static final Entry<DataComponentType<String>> ENCHANTED_TIMESTAMP = component("enchanted_timestamp");
  public static final Entry<DataComponentType<String>> ENCHANTED_BY = component("enchanted_by");
  public static final Entry<DataComponentType<Integer>> ENCHANTED_X = intComponent("enchanted_x");
  public static final Entry<DataComponentType<Integer>> ENCHANTED_Z = intComponent("enchanted_z");

  // Todo: remove alongside other migration code once period is over
  public static final Entry<DataComponentType<String>> LEGACY_CRAFTED_TIMESTAMP = component("crafted_timestamp");
  public static final Entry<DataComponentType<String>> LEGACY_CRAFTED_BY = component("crafted_by");

  private static final List<Supplier<? extends ItemLike>> TAB = new ArrayList<>();
  public static final BlockEntry<DisplayCaseBlock, BlockItem> DISPLAY_CASE = reg.blockWithItem("display_case", (key, props) -> new DisplayCaseBlock(DisplayCaseBlock.defaultProperties(props)), BlockItem::new).addToTab(TAB);
  public static final Entry<BlockEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE_BE = blockEntity("display_case", DisplayCaseBlockEntity::new, DISPLAY_CASE);

  public static final BlockEntry<DisplayRackBlock, BlockItem> DISPLAY_RACK = reg.blockWithItem("display_rack", (key, props) -> new DisplayRackBlock(DisplayRackBlock.defaultProperties(props)), BlockItem::new).addToTab(TAB);
  public static final Entry<BlockEntityType<DisplayRackBlockEntity>> DISPLAY_RACK_BE = blockEntity("display_rack", DisplayRackBlockEntity::new, DISPLAY_RACK);

  public static final Entry<CreativeModeTab> HEIRLOOMS_TAB = reg.tab("main", () -> new ItemStack(DISPLAY_CASE.get()));

  private static boolean initialized;

  private MainRegistry() {
  }

  public static KhazReg init() {
    if (initialized) return reg;
    initialized = true;
    reg.freeze();
    return reg;
  }

  public static void addMainTabItems(Consumer<ItemLike> output) {
    for (Supplier<? extends ItemLike> item : TAB) {
      output.accept(item.get());
    }
  }

  private static Entry<DataComponentType<String>> component(String name) {
    return reg.register(Registries.DATA_COMPONENT_TYPE, name, () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
  }

  private static Entry<DataComponentType<Integer>> intComponent(String name) {
    return reg.register(Registries.DATA_COMPONENT_TYPE, name, () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
  }

  private static <T extends BlockEntity> Entry<BlockEntityType<T>> blockEntity(String name, BiFunction<BlockPos, BlockState, T> factory, Supplier<? extends Block> block) {
    return reg.register(Registries.BLOCK_ENTITY_TYPE, name, key -> Services.PLATFORM.createBlockEntityType(factory, block.get()));
  }
}