package com.khazoda.heirlooms.mixin;

import com.khazoda.heirlooms.CommonTooltipHandler;
import com.khazoda.heirlooms.Constants;
import com.khazoda.heirlooms.block.DisplayBlockEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Gui.class)
public class ClientMixinGUI {
  @Unique
  private static final Identifier TEXT_ICONS = Constants.ID("textures/gui/text_icons.png");
  @Unique
  private static final int ICON_SHEET_WIDTH = 32;
  @Unique
  private final Minecraft heirlooms$minecraft = Minecraft.getInstance();

  @Inject(at = @At("TAIL"), method = "extractRenderState")
  public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    if (this.heirlooms$minecraft.options.hideGui || this.heirlooms$minecraft.player == null || this.heirlooms$minecraft.level == null)
      return;

    if (!(this.heirlooms$minecraft.hitResult instanceof BlockHitResult blockHit)) return;
    if (!(this.heirlooms$minecraft.level.getBlockEntity(blockHit.getBlockPos()) instanceof DisplayBlockEntity displayBlock))
      return;

    ItemStack stack = displayBlock.getItem(0);
    if (!stack.isEmpty()) heirlooms$renderHudExtras(graphics, stack);
  }

  @Unique
  private void heirlooms$renderHudExtras(GuiGraphicsExtractor guiGraphics, ItemStack stack) {
    Component name = stack.getHoverName();
    List<Component> acquisitionLines = new ArrayList<>(3);
    List<Component> namingLines = new ArrayList<>(3);
    List<Component> enchantmentLines = new ArrayList<>(3);
    CommonTooltipHandler.handleTooltipExpanded(stack, acquisitionLines, namingLines, enchantmentLines,
        !this.heirlooms$minecraft.player.isReducedDebugInfo());

    int centerX = this.heirlooms$minecraft.getWindow().getGuiScaledWidth() / 2;
    int startY = (this.heirlooms$minecraft.getWindow().getGuiScaledHeight() / 2) + 35 - this.heirlooms$minecraft.font.lineHeight - 2;

    int groupStartX = centerX - (20 + this.heirlooms$minecraft.font.width(name)) / 2;

    guiGraphics.item(stack, groupStartX, startY - 4);
    heirlooms$renderLine(guiGraphics, name, 0, groupStartX + 20, startY);

    int sectionCount = (acquisitionLines.isEmpty() ? 0 : 1) + (namingLines.isEmpty() ? 0 : 1) + (enchantmentLines.isEmpty() ? 0 : 1);
    if (sectionCount == 0) return;

    int baseDataY = startY + 18;
    if (sectionCount == 3) {
      heirlooms$renderThreeSections(guiGraphics, acquisitionLines, namingLines, enchantmentLines, centerX, baseDataY);
    } else if (sectionCount == 2) {
      List<Component> left = acquisitionLines.isEmpty() ? namingLines : acquisitionLines;
      List<Component> right = enchantmentLines.isEmpty() ? namingLines : enchantmentLines;
      heirlooms$renderLines(guiGraphics, left, centerX - 6, baseDataY, 1.0F);
      heirlooms$renderLines(guiGraphics, right, centerX + 6, baseDataY, 0.0F);
    } else {
      List<Component> lines = !acquisitionLines.isEmpty() ? acquisitionLines : !namingLines.isEmpty() ? namingLines : enchantmentLines;
      heirlooms$renderLines(guiGraphics, lines, centerX, baseDataY, 0.5F);
    }
  }

  @Unique
  private void heirlooms$renderThreeSections(GuiGraphicsExtractor guiGraphics, List<Component> acquisitionLines, List<Component> namingLines,
                                             List<Component> enchantmentLines, int centerX, int startY) {
    int gap = 8;
    int acquisitionWidth = heirlooms$maxWidth(acquisitionLines);
    int namingWidth = heirlooms$maxWidth(namingLines);
    int enchantmentWidth = heirlooms$maxWidth(enchantmentLines);
    int totalWidth = acquisitionWidth + namingWidth + enchantmentWidth + gap * 2;
    if (totalWidth > this.heirlooms$minecraft.getWindow().getGuiScaledWidth() - 16) {
      int y = heirlooms$renderLines(guiGraphics, acquisitionLines, centerX, startY, 0.5F) + 2;
      y = heirlooms$renderLines(guiGraphics, namingLines, centerX, y, 0.5F) + 2;
      heirlooms$renderLines(guiGraphics, enchantmentLines, centerX, y, 0.5F);
      return;
    }

    int x = centerX - totalWidth / 2;
    heirlooms$renderLines(guiGraphics, acquisitionLines, x, startY, 0.0F);
    x += acquisitionWidth + gap;
    heirlooms$renderLines(guiGraphics, namingLines, x, startY, 0.0F);
    x += namingWidth + gap;
    heirlooms$renderLines(guiGraphics, enchantmentLines, x, startY, 0.0F);
  }

  @Unique
  private int heirlooms$maxWidth(List<Component> lines) {
    int width = 0;
    for (int i = 0; i < lines.size(); i++)
      width = Math.max(width, heirlooms$lineWidth(lines.get(i), i));
    return width;
  }

  @Unique
  private int heirlooms$renderLines(GuiGraphicsExtractor guiGraphics, List<Component> lines, int x, int startY, float alignment) {
    int y = startY;
    int lineHeight = this.heirlooms$minecraft.font.lineHeight + 1;
    for (int i = 0; i < lines.size(); i++) {
      Component line = lines.get(i);
      int lineX = alignment == 0.0F ? x : x - (int) (heirlooms$lineWidth(line, i) * alignment);
      heirlooms$renderLine(guiGraphics, line, i, lineX, y);
      y += lineHeight;
    }
    return y;
  }

  @Unique
  private int heirlooms$lineWidth(Component line, int index) {
    return this.heirlooms$minecraft.font.width(line) + (heirlooms$iconU(line, index) < 0 ? 0 : 12);
  }

  @Unique
  private static int heirlooms$iconU(Component line, int index) {
    if (index == 1) return 0;
    if (index != 2) return -1;
    String dimension = line.getStyle().getInsertion();
    if ("minecraft:the_nether".equals(dimension)) return 16;
    if ("minecraft:the_end".equals(dimension)) return 24;
    return 8;
  }

  @Unique
  private void heirlooms$renderLine(GuiGraphicsExtractor guiGraphics, Component text, int index, int x, int y) {
    int iconU = heirlooms$iconU(text, index);
    if (iconU >= 0) {
      TextColor textColor = text.getStyle().getColor();
      int color = textColor == null ? -1 : 0xFF000000 | textColor.getValue();
      guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXT_ICONS, x + 1, y + 1, iconU, 0, 8, 8, ICON_SHEET_WIDTH, 8, ARGB.scaleRGB(color, 0.25F));
      guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXT_ICONS, x, y, iconU, 0, 8, 8, ICON_SHEET_WIDTH, 8, color);
      x += 12;
    }

    guiGraphics.text(this.heirlooms$minecraft.font, text, x, y, 0xFFFFFFFF, true);
  }
}