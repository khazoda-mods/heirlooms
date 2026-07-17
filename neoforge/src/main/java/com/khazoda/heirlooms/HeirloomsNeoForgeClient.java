package com.khazoda.heirlooms;

import com.khazoda.core.keybind.KhazKeybindNeoForge;
import com.khazoda.heirlooms.block.renderer.DisplayCaseRenderer;
import com.khazoda.heirlooms.block.renderer.DisplayRackRenderer;
import com.khazoda.heirlooms.registry.MainRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class HeirloomsNeoForgeClient {
  public HeirloomsNeoForgeClient(IEventBus eventBus) {
    HeirloomsKeybinds.register();
    KhazKeybindNeoForge.init(eventBus);
  }

  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(MainRegistry.DISPLAY_CASE_BE.get(), DisplayCaseRenderer::new);
    event.registerBlockEntityRenderer(MainRegistry.DISPLAY_RACK_BE.get(), DisplayRackRenderer::new);

  }
}