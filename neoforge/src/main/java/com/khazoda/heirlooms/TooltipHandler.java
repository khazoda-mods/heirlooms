package com.khazoda.heirlooms;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class TooltipHandler {

  @SubscribeEvent
  public static void onItemTooltip(ItemTooltipEvent event) {
    CommonTooltipHandler.handleTooltip(event.getItemStack(), event.getToolTip());
  }
}