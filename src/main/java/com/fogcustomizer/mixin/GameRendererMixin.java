package com.fogcustomizer.mixin;

import com.fogcustomizer.FogConfig;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Both hooks run at the end of the vanilla methods and only overwrite what vanilla just set, so blindness
 * and the void/cloud fog keep their vanilla behaviour.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique private static final float FOG_DENSITY_WATER = 0.1F;
    @Unique private static final float FOG_DENSITY_LAVA = 2.0F;
    /** Pushes the fog far past the render distance, so nothing is fogged. */
    @Unique private static final float NO_FOG = 1.0E6F;

    @Shadow private Minecraft minecraft;
    @Shadow private float renderDistance;
    @Shadow private boolean thiccFog;
    @Shadow private float fogRed;
    @Shadow private float fogGreen;
    @Shadow private float fogBlue;

    /** Vanilla leaves fog alone in these cases; so do we. */
    @Unique
    private boolean fogcustomizer$vanillaOnly(Entity camera) {
        return !FogConfig.enabled
                || camera == null
                || this.thiccFog
                || camera instanceof LivingEntity && ((LivingEntity) camera).hasStatusEffect(StatusEffect.BLINDNESS);
    }

    @Inject(method = "setupClearColor", at = @At("TAIL"))
    private void fogcustomizer$color(float tickDelta, CallbackInfo ci) {
        Entity camera = this.minecraft.getCamera();
        if (!FogConfig.customColor || fogcustomizer$vanillaOnly(camera)) return;
        Material material = Camera.getBlockInside(this.minecraft.world, camera, tickDelta).getMaterial();
        if (material == Material.WATER || material == Material.LAVA) return;

        this.fogRed = FogConfig.color.getRedF();
        this.fogGreen = FogConfig.color.getGreenF();
        this.fogBlue = FogConfig.color.getBlueF();
        GlStateManager.clearColor(this.fogRed, this.fogGreen, this.fogBlue, 0.0F);
    }

    /** @param mode -1 for the sky pass, which vanilla fogs from 0 instead of 75% */
    @Inject(method = "setupFog", at = @At("TAIL"))
    private void fogcustomizer$distance(int mode, float tickDelta, CallbackInfo ci) {
        Entity camera = this.minecraft.getCamera();
        if (fogcustomizer$vanillaOnly(camera)) return;
        Material material = Camera.getBlockInside(this.minecraft.world, camera, tickDelta).getMaterial();

        if (material == Material.WATER) {
            // vanilla's density also depends on Respiration and Water Breathing; scale the common case
            if (FogConfig.water < 100.0F) GlStateManager.fogDensity(FOG_DENSITY_WATER * FogConfig.water / 100.0F);
            return;
        }
        if (material == Material.LAVA) {
            if (FogConfig.lava < 100.0F) GlStateManager.fogDensity(FOG_DENSITY_LAVA * FogConfig.lava / 100.0F);
            return;
        }

        boolean thickNether = this.minecraft.world.dimension.isFogThick((int) camera.x, (int) camera.z);
        if (thickNether && FogConfig.netherFog) return;

        if (!FogConfig.fog) {
            GlStateManager.fogStart(NO_FOG);
            GlStateManager.fogEnd(NO_FOG);
            return;
        }
        float end = this.renderDistance * FogConfig.end / 100.0F;
        float start = mode == -1 ? 0.0F : Math.min(this.renderDistance * FogConfig.start / 100.0F, end);
        GlStateManager.fogStart(start);
        GlStateManager.fogEnd(end);
    }
}
