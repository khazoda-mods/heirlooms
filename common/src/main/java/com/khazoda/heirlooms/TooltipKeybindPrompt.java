package com.khazoda.heirlooms;

import com.khazoda.core.keybind.KhazKeybind;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class TooltipKeybindPrompt {
  private TooltipKeybindPrompt() {
  }

  public static Component create(KhazKeybind keybind, int color) {
    return Component.literal("[" + compactKeyLabel(keybind.boundInputLabel().getString()) + "]").withColor(color);
  }

  private static String compactKeyLabel(String keyLabel) {
    String[] parts = keyLabel.split("\\s*\\+\\s*");
    StringBuilder result = new StringBuilder(keyLabel.length());
    for (String part : parts) {
      if (!result.isEmpty()) result.append("+");
      result.append(compactKeyPart(part));
    }
    return result.toString();
  }

  private static String compactKeyPart(String keyPart) {
    String lowerKeyPart = keyPart.toLowerCase(Locale.ROOT);
    if (lowerKeyPart.contains("control") || lowerKeyPart.equals("ctrl")) return "ᴄᴛʀʟ";
    if (lowerKeyPart.contains("shift")) return "sʜɪғᴛ";
    if (lowerKeyPart.contains("alt")) return "ᴀʟᴛ";
    if (lowerKeyPart.contains("space")) return "sᴘᴀᴄᴇ";
    if (lowerKeyPart.startsWith("mouse button ")) return "ᴍ" + keyPart.substring("mouse button ".length());
    if (lowerKeyPart.startsWith("button ")) return "ᴍ" + keyPart.substring("button ".length());
    return toSmallCaps(keyPart);
  }

  private static String toSmallCaps(String text) {
    StringBuilder result = new StringBuilder(text.length());
    for (char character : text.toUpperCase(Locale.ROOT).toCharArray()) {
      result.append(smallCap(character));
    }
    return result.toString();
  }

  private static char smallCap(char character) {
    return switch (character) {
      case 'A' -> 'ᴀ';
      case 'B' -> 'ʙ';
      case 'C' -> 'ᴄ';
      case 'D' -> 'ᴅ';
      case 'E' -> 'ᴇ';
      case 'F' -> 'ғ';
      case 'G' -> 'ɢ';
      case 'H' -> 'ʜ';
      case 'I' -> 'ɪ';
      case 'J' -> 'ᴊ';
      case 'K' -> 'ᴋ';
      case 'L' -> 'ʟ';
      case 'M' -> 'ᴍ';
      case 'N' -> 'ɴ';
      case 'O' -> 'ᴏ';
      case 'P' -> 'ᴘ';
      case 'Q' -> 'ǫ';
      case 'R' -> 'ʀ';
      case 'S' -> 's';
      case 'T' -> 'ᴛ';
      case 'U' -> 'ᴜ';
      case 'V' -> 'ᴠ';
      case 'W' -> 'ᴡ';
      case 'X' -> 'x';
      case 'Y' -> 'ʏ';
      case 'Z' -> 'ᴢ';
      default -> character;
    };
  }
}