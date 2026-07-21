package com.khazoda.heirlooms;

import com.khazoda.core.keybind.KhazKeybind;
import com.khazoda.heirlooms.mixinutils.SlotResultModifier;
import com.khazoda.heirlooms.registry.MainRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public final class CommonTooltipHandler {

  private static final int CRAFTED_TEXT_COLOR = 0x8A603F;
  private static final int NAMED_TEXT_COLOR = 0x3F8A55;
  private static final int ENCHANTED_TEXT_COLOR = 0x3F718A;
  private static final int HUD_CRAFTED_TEXT_COLOR = 0xFFD27D;
  private static final int HUD_NAMED_TEXT_COLOR = 0x79D98A;
  private static final int HUD_ENCHANTED_TEXT_COLOR = 0x8ACEEB;

  private static String cachedLanguageCode;
  private static Locale cachedLocale;

  private CommonTooltipHandler() {
  }

  public static void handleTooltip(ItemStack stack, List<Component> tooltip) {
    if (stack == null || stack.isEmpty()) return;
    TooltipData data = TooltipData.from(stack);
    boolean hasAcquisition = data.hasValidAcquisition();
    boolean hasFirstNaming = data.hasValidFirstNaming();
    boolean hasEnchantment = data.hasValidEnchantment();
    if (!data.legacyAcquisition() && !hasAcquisition && !hasFirstNaming && !hasEnchantment) return;

    if (data.legacyAcquisition()) {
      tooltip.add(Component.translatable("tooltip.heirlooms.legacy_components").withColor(16755200));
      tooltip.add(Component.translatable("tooltip.heirlooms.legacy_components_instructions").withColor(16755200));
    }

    boolean showAcquisition = HeirloomsKeybinds.SHOW_ACQUISITION_DETAILS.isBoundInputHeldInUi();
    boolean showFirstNaming = HeirloomsKeybinds.SHOW_FIRST_NAMING_DETAILS.isBoundInputHeldInUi();
    boolean showEnchantment = HeirloomsKeybinds.SHOW_ENCHANTMENT_DETAILS.isBoundInputHeldInUi();
    addButtonPrompts(tooltip, hasAcquisition, showAcquisition, hasFirstNaming, showFirstNaming, hasEnchantment, showEnchantment);

    if (hasAcquisition && showAcquisition)
      addTimestampTooltip(tooltip, data.acquiredAt(), data.acquiredBy(), acquisitionTranslationKey(data.acquisitionKind()), CRAFTED_TEXT_COLOR);

    if (hasFirstNaming && showFirstNaming)
      addTimestampTooltip(tooltip, data.namedFirstAt(), data.namedFirstBy(), "tooltip.heirlooms.first_named_by", NAMED_TEXT_COLOR);

    if (hasEnchantment && showEnchantment)
      addTimestampTooltip(tooltip, data.enchantedAt(), data.enchantedBy(), "tooltip.heirlooms.enchanted_by", ENCHANTED_TEXT_COLOR);
  }

  public static void handleTooltipExpanded(ItemStack stack, List<Component> acquisitionTooltip, List<Component> namingTooltip,
                                           List<Component> enchantmentTooltip, boolean showLocation) {
    if (stack == null || stack.isEmpty()) return;
    TooltipData data = TooltipData.from(stack);

    if (data.hasValidAcquisition()) {
      addExpandedTooltip(acquisitionTooltip, data.acquiredAt(), data.acquiredBy(), acquisitionIcon(data.acquisitionKind()), HUD_CRAFTED_TEXT_COLOR);
      if (showLocation)
        addLocationTooltip(acquisitionTooltip, data.acquisitionX(), data.acquisitionZ(), data.acquisitionDimension(), HUD_CRAFTED_TEXT_COLOR);
    }

    if (data.hasValidFirstNaming()) {
      addExpandedTooltip(namingTooltip, data.namedFirstAt(), data.namedFirstBy(), "first_named", HUD_NAMED_TEXT_COLOR);
      if (showLocation)
        addLocationTooltip(namingTooltip, data.namedFirstX(), data.namedFirstZ(), data.namedFirstDimension(), HUD_NAMED_TEXT_COLOR);
    }

    if (data.hasValidEnchantment()) {
      addExpandedTooltip(enchantmentTooltip, data.enchantedAt(), data.enchantedBy(), "enchanted", HUD_ENCHANTED_TEXT_COLOR);
      if (showLocation)
        addLocationTooltip(enchantmentTooltip, data.enchantedX(), data.enchantedZ(), data.enchantedDimension(), HUD_ENCHANTED_TEXT_COLOR);
    }
  }

  private static void addButtonPrompts(List<Component> tooltip, boolean hasAcquisition, boolean showAcquisition,
                                       boolean hasFirstNaming, boolean showFirstNaming,
                                       boolean hasEnchantment, boolean showEnchantment) {
    MutableComponent buttonPrompt = Component.empty();
    boolean added = appendButtonPrompt(buttonPrompt, false, hasAcquisition, showAcquisition, HeirloomsKeybinds.SHOW_ACQUISITION_DETAILS, CRAFTED_TEXT_COLOR);
    added = appendButtonPrompt(buttonPrompt, added, hasFirstNaming, showFirstNaming, HeirloomsKeybinds.SHOW_FIRST_NAMING_DETAILS, NAMED_TEXT_COLOR);
    added = appendButtonPrompt(buttonPrompt, added, hasEnchantment, showEnchantment, HeirloomsKeybinds.SHOW_ENCHANTMENT_DETAILS, ENCHANTED_TEXT_COLOR);
    if (added) tooltip.add(buttonPrompt);
  }

  private static boolean appendButtonPrompt(MutableComponent prompt, boolean added, boolean hasData, boolean shown, KhazKeybind keybind, int color) {
    if (!hasData || shown || !keybind.hasBoundInput()) return added;
    if (added) prompt.append(Component.literal(" "));
    prompt.append(TooltipKeybindPrompt.create(keybind, color));
    return true;
  }

  private static String acquisitionTranslationKey(String acquisitionKind) {
    if (SlotResultModifier.ACQUISITION_BOUGHT.equals(acquisitionKind)) return "tooltip.heirlooms.bought_by";
    if (SlotResultModifier.ACQUISITION_LOOTED.equals(acquisitionKind)) return "tooltip.heirlooms.looted_by";
    return SlotResultModifier.ACQUISITION_CRAFTED.equals(acquisitionKind) ? "tooltip.heirlooms.crafted_by" : "tooltip.heirlooms.acquired_by";
  }

  private static String acquisitionIcon(String acquisitionKind) {
    if (SlotResultModifier.ACQUISITION_CRAFTED.equals(acquisitionKind)) return "crafted";
    if (SlotResultModifier.ACQUISITION_LOOTED.equals(acquisitionKind)) return "looted";
    return SlotResultModifier.ACQUISITION_BOUGHT.equals(acquisitionKind) ? "bought" : "acquired";
  }

  private static Locale getCachedLocale() {
    try {
      String languageCode = net.minecraft.client.Minecraft.getInstance().getLanguageManager().getSelected();
      if (!languageCode.equals(cachedLanguageCode)) {
        cachedLanguageCode = languageCode;
        cachedLocale = Locale.forLanguageTag(languageCode.replace("_", "-"));
      }
    } catch (Exception ignored) {
      cachedLanguageCode = "en_us";
      cachedLocale = Locale.ENGLISH;
    }
    return cachedLocale;
  }

  private static void addTimestampTooltip(List<Component> tooltip, ZonedDateTime timestamp, String creator, String translationKey, int color) {
    tooltip.add(Component.translatable(translationKey, creator).withColor(color));
    addDateTooltip(tooltip, timestamp, color);
  }

  private static void addExpandedTooltip(List<Component> tooltip, ZonedDateTime timestamp, String playerName, String icon, int color) {
    tooltip.add(Component.literal(playerName).withStyle(style -> style.withColor(color).withInsertion(icon)));
    addDateTooltip(tooltip, timestamp, color);
  }

  private static void addDateTooltip(List<Component> tooltip, ZonedDateTime timestamp, int color) {
    Locale locale = getCachedLocale();
    String monthName = timestamp.getMonth().getDisplayName(TextStyle.FULL, locale);
    tooltip.add(Component.translatable("tooltip.heirlooms.date", getFormattedDay(timestamp.getDayOfMonth(), locale), timestamp.getMonthValue(), monthName, timestamp.getYear()).withColor(color));
  }

  private static void addLocationTooltip(List<Component> tooltip, Integer x, Integer z, String dimension, int color) {
    if (x != null && z != null)
      tooltip.add(Component.translatable("tooltip.heirlooms.location", x, z)
          .withStyle(style -> style.withColor(color).withInsertion(dimension)));
  }

  private static ZonedDateTime parseTimestamp(String timestamp) {
    if (timestamp == null) return null;
    try {
      return Instant.parse(timestamp).atZone(ZoneId.systemDefault());
    } catch (DateTimeException ignored) {
      return null;
    }
  }

  // Handle en_us and en_gb ordinal suffixes
  private static String getFormattedDay(int day, Locale locale) {
    if (!locale.getLanguage().equals("en")) return String.valueOf(day);
    return day + switch (day % 100) {
      case 11, 12, 13 -> "th";
      default -> switch (day % 10) {
        case 1 -> "st";
        case 2 -> "nd";
        case 3 -> "rd";
        default -> "th";
      };
    };
  }

  private record TooltipData(
      boolean legacyAcquisition,
      ZonedDateTime acquiredAt,
      String acquiredBy,
      String acquisitionKind,
      Integer acquisitionX,
      Integer acquisitionZ,
      String acquisitionDimension,
      String namedFirst,
      ZonedDateTime namedFirstAt,
      String namedFirstBy,
      Integer namedFirstX,
      Integer namedFirstZ,
      String namedFirstDimension,
      ZonedDateTime enchantedAt,
      String enchantedBy,
      Integer enchantedX,
      Integer enchantedZ,
      String enchantedDimension
  ) {
    private static TooltipData from(ItemStack stack) {
      return new TooltipData(
          HeirloomsComponentMigration.isLegacyAcquisition(stack),
          parseTimestamp(stack.get(MainRegistry.ACQUIRED_TIMESTAMP.get())),
          stack.get(MainRegistry.ACQUIRED_BY.get()),
          stack.get(MainRegistry.ACQUISITION_KIND.get()),
          stack.get(MainRegistry.ACQUISITION_X.get()),
          stack.get(MainRegistry.ACQUISITION_Z.get()),
          stack.get(MainRegistry.ACQUISITION_DIMENSION.get()),
          stack.get(MainRegistry.NAMED_FIRST.get()),
          parseTimestamp(stack.get(MainRegistry.NAMED_FIRST_TIMESTAMP.get())),
          stack.get(MainRegistry.NAMED_FIRST_BY.get()),
          stack.get(MainRegistry.NAMED_FIRST_X.get()),
          stack.get(MainRegistry.NAMED_FIRST_Z.get()),
          stack.get(MainRegistry.NAMED_FIRST_DIMENSION.get()),
          parseTimestamp(stack.get(MainRegistry.ENCHANTED_TIMESTAMP.get())),
          stack.get(MainRegistry.ENCHANTED_BY.get()),
          stack.get(MainRegistry.ENCHANTED_X.get()),
          stack.get(MainRegistry.ENCHANTED_Z.get()),
          stack.get(MainRegistry.ENCHANTED_DIMENSION.get())
      );
    }

    private boolean hasValidAcquisition() {
      return acquiredAt != null && acquiredBy != null && !acquiredBy.isBlank();
    }

    private boolean hasValidFirstNaming() {
      return namedFirst != null && !namedFirst.isBlank()
          && namedFirstAt != null && namedFirstBy != null && !namedFirstBy.isBlank();
    }

    private boolean hasValidEnchantment() {
      return enchantedAt != null && enchantedBy != null && !enchantedBy.isBlank();
    }
  }
}