package com.khazoda.heirlooms;

import com.khazoda.core.keybind.KhazKeybind;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.Locale;

public final class TooltipKeybindPrompt {
  private static final String SMALL_CAPS = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ";

  private TooltipKeybindPrompt() {
  }

  public static Component create(KhazKeybind keybind, int color) {
    return Component.literal("[" + compactKeyLabel(keybind.boundInputLabel()) + "]").withColor(color);
  }

  private static String compactKeyLabel(Component keyLabel) {
    if (keyLabel.getContents() instanceof TranslatableContents translatable) {
      String arrow = switch (translatable.getKey()) {
        case "key.keyboard.left" -> "←";
        case "key.keyboard.up" -> "↑";
        case "key.keyboard.right" -> "→";
        case "key.keyboard.down" -> "↓";
        default -> null;
      };
      if (arrow != null) return arrow;
    }
    return compactKeyLabel(keyLabel.getString());
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
    String uppercase = text.toUpperCase(Locale.ROOT);
    for (int i = 0; i < uppercase.length(); i++)
      result.append(smallCap(uppercase.charAt(i)));
    return result.toString();
  }

  private static char smallCap(char character) {
    return character >= 'A' && character <= 'Z' ? SMALL_CAPS.charAt(character - 'A') : character;
  }
}