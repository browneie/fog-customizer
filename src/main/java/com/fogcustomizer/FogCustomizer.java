package com.fogcustomizer;

import net.fabricmc.api.ClientModInitializer;

public class FogCustomizer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FogConfig.INSTANCE.preload();
    }
}
