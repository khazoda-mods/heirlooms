package com.khazoda.heirlooms;

import com.khazoda.heirlooms.block.renderer.DisplayCaseRenderer;
import com.khazoda.heirlooms.block.renderer.DisplayRackRenderer;
import com.khazoda.heirlooms.registry.MainRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;


public class HeirloomsFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    TooltipHandler.register();
    BlockEntityRenderers.register(MainRegistry.DISPLAY_CASE_BE.get(), DisplayCaseRenderer::new);
    BlockEntityRenderers.register(MainRegistry.DISPLAY_RACK_BE.get(), DisplayRackRenderer::new);
  }
}