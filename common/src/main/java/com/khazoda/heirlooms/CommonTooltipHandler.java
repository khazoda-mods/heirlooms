package com.khazoda.heirlooms;

import com.khazoda.heirlooms.registry.MainRegistry;
import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CommonTooltipHandler {

  public static final int CRAFTED_TEXT_COLOR = 9068607;
  public static final int ENCHANTED_TEXT_COLOR = 4157834;

  private static String cachedLanguageCode = null;
  private static Locale cachedLocale = null;

  public static void handleTooltip(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
    handleTooltip(stack, tooltip, HeirloomsKeybinds.SHOW_ACQUISITION_DETAILS.isBoundInputHeldInUi(), HeirloomsKeybinds.SHOW_ENCHANTMENT_DETAILS.isBoundInputHeldInUi(), true, false);
  }

  public static void handleTooltipExpanded(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
    handleTooltip(stack, tooltip, true, true, false, true);
  }

  private static void handleTooltip(ItemStack stack, List<Component> tooltip, boolean showAcquisition, boolean showEnchantment, boolean showPrompts, boolean showLocation) {
    TooltipData data = TooltipData.from(stack);
    if (!data.hasAnyData()) return;

    if (data.hasLegacyComponents()) {
      tooltip.add(Component.translatable("tooltip.heirlooms.legacy_components").withColor(16755200));
      tooltip.add(Component.translatable("tooltip.heirlooms.legacy_components_instructions").withColor(16755200));
    }

    if (showPrompts)
      addButtonPrompts(tooltip, data.hasAcquisitionData(), data.hasEnchantmentData(), showAcquisition, showEnchantment);

    if (data.hasFullAcquisitionData() && showAcquisition)
      addTimestampTooltip(tooltip, data.acquiredTimestamp(), data.acquiredBy(), acquisitionTranslationKey(data.acquisitionKind()), data.acquisitionX(), data.acquisitionZ(), showLocation, CRAFTED_TEXT_COLOR);

    if (data.hasFullEnchantmentData() && showEnchantment)
      addTimestampTooltip(tooltip, data.enchantedTimestamp(), data.enchantedBy(), "tooltip.heirlooms.enchanted_by", data.enchantedX(), data.enchantedZ(), showLocation, ENCHANTED_TEXT_COLOR);
  }

  private static void addButtonPrompts(List<Component> tooltip, boolean hasAcquisitionData, boolean hasEnchantData, boolean showAcquisition, boolean showEnchantment) {
    MutableComponent buttonPrompt = Component.empty();

    if (hasAcquisitionData && !showAcquisition && HeirloomsKeybinds.SHOW_ACQUISITION_DETAILS.hasBoundInput()) {
      buttonPrompt.append(TooltipKeybindPrompt.create(HeirloomsKeybinds.SHOW_ACQUISITION_DETAILS, CRAFTED_TEXT_COLOR));
    }
    if (hasAcquisitionData && !showAcquisition && hasEnchantData && !showEnchantment)
      buttonPrompt.append(Component.literal(" "));
    if (hasEnchantData && !showEnchantment && HeirloomsKeybinds.SHOW_ENCHANTMENT_DETAILS.hasBoundInput()) {
      buttonPrompt.append(TooltipKeybindPrompt.create(HeirloomsKeybinds.SHOW_ENCHANTMENT_DETAILS, ENCHANTED_TEXT_COLOR));
    }
    if (!buttonPrompt.getString().isEmpty()) tooltip.add(buttonPrompt);
  }

  private static String acquisitionTranslationKey(String acquisitionKind) {
    return switch (acquisitionKind) {
      case SlotResultModifier.ACQUISITION_BOUGHT -> "tooltip.heirlooms.bought_by";
      case SlotResultModifier.ACQUISITION_LOOTED -> "tooltip.heirlooms.looted_by";
      default -> "tooltip.heirlooms.crafted_by";
    };
  }

  public static void updateCachedLocale() {
    try {
      String languageCode = net.minecraft.client.Minecraft.getInstance().getLanguageManager().getSelected();
      if (!languageCode.equals(cachedLanguageCode)) {
        cachedLanguageCode = languageCode;
        cachedLocale = Locale.forLanguageTag(languageCode.replace("_", "-"));
      }
    } catch (Exception e) {
      cachedLanguageCode = "en_us";
      cachedLocale = Locale.ENGLISH;
    }
  }

  private static Locale getCachedLocale() {
    if (cachedLocale == null) {
      updateCachedLocale();
    }
    return cachedLocale;
  }

  private static void addTimestampTooltip(List<Component> tooltip, String timestamp, String creator, String translationKey, Integer x, Integer z, boolean showLocation, int color) {
    try {
      Instant instant = Instant.parse(timestamp);
      ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

      int year = zonedDateTime.getYear();
      int month = zonedDateTime.getMonthValue();
      int day = zonedDateTime.getDayOfMonth();

      /* Get localized month name */
      Locale locale = getCachedLocale();
      String monthName = zonedDateTime.getMonth().getDisplayName(TextStyle.FULL, locale);

      tooltip.add(Component.translatable(translationKey, creator).withColor(color));
      tooltip.add(Component.translatable("tooltip.heirlooms.date", getFormattedDay(day, locale), month, monthName, year).withColor(color));
      if (showLocation && x != null && z != null)
        tooltip.add(Component.translatable("tooltip.heirlooms.location", x, z).withColor(color));

    } catch (Exception e) {
      Constants.LOG.error("Heirlooms mod error [1] - please report on the issue tracker", e);
    }
  }

  // Handle en_us and en_gb ordinal suffixes
  private static String getFormattedDay(int day, Locale locale) {
    if (locale.getLanguage().equals("en")) {
      String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
      return switch (day % 100) {
        case 11, 12, 13 -> day + "th";
        default -> day + suffixes[day % 10];
      };
    } else {
      return String.valueOf(day);
    }
  }

  private record TooltipData(
      boolean hasLegacyComponents,
      String acquiredTimestamp,
      String acquiredBy,
      String acquisitionKind,
      Integer acquisitionX,
      Integer acquisitionZ,
      String enchantedTimestamp,
      String enchantedBy,
      Integer enchantedX,
      Integer enchantedZ
  ) {
    private static TooltipData from(ItemStack stack) {
      boolean hasLegacyComponents = HeirloomsComponentMigration.hasLegacyComponents(stack);
      String acquiredTimestamp = stack.get(MainRegistry.ACQUIRED_TIMESTAMP.get());
      String acquiredBy = stack.get(MainRegistry.ACQUIRED_BY.get());
      if (acquiredTimestamp == null) acquiredTimestamp = stack.get(MainRegistry.LEGACY_CRAFTED_TIMESTAMP.get());
      if (acquiredBy == null) acquiredBy = stack.get(MainRegistry.LEGACY_CRAFTED_BY.get());

      return new TooltipData(
          hasLegacyComponents,
          acquiredTimestamp,
          acquiredBy,
          stack.get(MainRegistry.ACQUISITION_KIND.get()),
          stack.get(MainRegistry.ACQUISITION_X.get()),
          stack.get(MainRegistry.ACQUISITION_Z.get()),
          stack.get(MainRegistry.ENCHANTED_TIMESTAMP.get()),
          stack.get(MainRegistry.ENCHANTED_BY.get()),
          stack.get(MainRegistry.ENCHANTED_X.get()),
          stack.get(MainRegistry.ENCHANTED_Z.get())
      );
    }

    private boolean hasAnyData() {
      return hasAcquisitionData() || hasEnchantmentData();
    }

    private boolean hasAcquisitionData() {
      return acquiredTimestamp != null;
    }

    private boolean hasEnchantmentData() {
      return enchantedTimestamp != null;
    }

    private boolean hasFullAcquisitionData() {
      return acquiredTimestamp != null && acquiredBy != null;
    }

    private boolean hasFullEnchantmentData() {
      return enchantedTimestamp != null && enchantedBy != null;
    }
  }
}