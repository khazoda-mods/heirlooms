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
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MainRegistry {
  public static final KhazReg reg = new KhazReg(Constants.MOD_ID);
  private static final List<Supplier<? extends ItemLike>> TAB = new ArrayList<>();
  private static boolean initialized;

  public static final BlockEntry<DisplayCaseBlock, BlockItem> DISPLAY_CASE = reg.blockWithItem("display_case",
      (key, props) -> new DisplayCaseBlock(props.strength(2.0F, 6.0F)
          .noOcclusion()
          .mapColor(MapColor.STONE)
          .instrument(NoteBlockInstrument.BASEDRUM)
          .sound(SoundType.STONE)
          .isSuffocating((state, world, pos) -> false)
          .isViewBlocking((state, world, pos) -> false)),
      BlockItem::new).addToTab(TAB);

  public static final BlockEntry<DisplayRackBlock, BlockItem> DISPLAY_RACK = reg.blockWithItem("display_rack",
      (key, props) -> new DisplayRackBlock(props.strength(1.5F, 4.0F)
          .noOcclusion()
          .mapColor(MapColor.WOOD)
          .instrument(NoteBlockInstrument.BASS)
          .sound(SoundType.WOOD)
          .isSuffocating((state, world, pos) -> false)
          .isViewBlocking((state, world, pos) -> false)),
      BlockItem::new).addToTab(TAB);

  public static final Entry<BlockEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE_BE = reg.register(
      Registries.BLOCK_ENTITY_TYPE,
      "display_case",
      key -> Services.PLATFORM.createBlockEntityType(DisplayCaseBlockEntity::new, DISPLAY_CASE.get())
  );

  public static final Entry<BlockEntityType<DisplayRackBlockEntity>> DISPLAY_RACK_BE = reg.register(
      Registries.BLOCK_ENTITY_TYPE,
      "display_rack",
      key -> Services.PLATFORM.createBlockEntityType(DisplayRackBlockEntity::new, DISPLAY_RACK.get())
  );

  public static final Entry<DataComponentType<String>> CRAFTED_TIMESTAMP = component("crafted_timestamp");
  public static final Entry<DataComponentType<String>> CRAFTED_BY = component("crafted_by");
  public static final Entry<DataComponentType<String>> ENCHANTED_TIMESTAMP = component("enchanted_timestamp");
  public static final Entry<DataComponentType<String>> ENCHANTED_BY = component("enchanted_by");

  public static final Entry<CreativeModeTab> HEIRLOOMS_TAB = reg.tab("main", () -> new ItemStack(DISPLAY_CASE.get()));

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
    return reg.register(Registries.DATA_COMPONENT_TYPE, name, () -> DataComponentType.<String>builder()
        .persistent(Codec.STRING)
        .networkSynchronized(ByteBufCodecs.STRING_UTF8)
        .build());
  }
}