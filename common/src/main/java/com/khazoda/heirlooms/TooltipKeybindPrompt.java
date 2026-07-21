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
    if (keyLabel.equals("+")) return keyLabel;
    String[] parts = keyLabel.split("\\s*\\+\\s*");
    StringBuilder result = new StringBuilder(keyLabel.length());
    for (String part : parts) {
      if (!result.isEmpty()) result.append("+");
      result.append(compactKeyPart(part));
    }
    return result.toString();
  }

  private static String compactKeyPart(String keyPart) {
    return switch (keyPart.toLowerCase(Locale.ROOT)) {
      case "control", "left control", "right control", "ctrl" -> "ᴄᴛʀʟ";
      case "shift", "left shift", "right shift" -> "sʜɪғᴛ";
      case "alt", "left alt", "right alt" -> "ᴀʟᴛ";
      case "space" -> "sᴘᴀᴄᴇ";
      default -> toSmallCaps(keyPart);
    };
  }

  private static String toSmallCaps(String text) {
    StringBuilder result = new StringBuilder(text.length());
    for (int i = 0; i < text.length(); i++)
      result.append(smallCap(text.charAt(i)));
    return result.toString();
  }

  private static char smallCap(char character) {
    if (character >= 'A' && character <= 'Z') return SMALL_CAPS.charAt(character - 'A');
    return character >= 'a' && character <= 'z' ? SMALL_CAPS.charAt(character - 'a') : character;
  }
}