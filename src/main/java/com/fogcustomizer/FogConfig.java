package com.fogcustomizer;

import org.polyfrost.compose.render.PolyColor;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Color;
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class FogConfig extends Config {

    public static final FogConfig INSTANCE = new FogConfig();

    @Switch(title = "Enabled")
    public static boolean enabled = true;

    @Switch(title = "Fog", description = "Turn off to remove distance fog completely.", subcategory = "Distance")
    public static boolean fog = true;

    @Slider(
            title = "Fog Start",
            description = "Where fog begins, as a percent of your render distance. Vanilla is 75%.",
            subcategory = "Distance",
            min = 0.0F,
            max = 100.0F,
            step = 1.0F
    )
    public static float start = 75.0F;

    @Slider(
            title = "Fog End",
            description = "Where fog is fully opaque, as a percent of your render distance. Vanilla is 100%.",
            subcategory = "Distance",
            min = 10.0F,
            max = 200.0F,
            step = 1.0F
    )
    public static float end = 100.0F;

    @Switch(title = "Nether Fog", description = "The Nether's thick fog. Turn off to use the distances above instead.", subcategory = "Distance")
    public static boolean netherFog = true;

    @Slider(title = "Water Fog", description = "Fog density underwater, as a percent of vanilla.", subcategory = "Liquids",
            min = 0.0F, max = 100.0F, step = 1.0F)
    public static float water = 100.0F;

    @Slider(title = "Lava Fog", description = "Fog density inside lava, as a percent of vanilla.", subcategory = "Liquids",
            min = 0.0F, max = 100.0F, step = 1.0F)
    public static float lava = 100.0F;

    @Switch(title = "Custom Fog Color", description = "Replace the sky-matched fog color (not used underwater or in lava).", subcategory = "Color")
    public static boolean customColor = false;

    // Fog is a single GL color, so a gradient's second color could never be used.
    @Color(title = "Fog Color", subcategory = "Color", gradient = false)
    public static PolyColor color = new PolyColor(0xFFB8D4FF, false, 1.0F);

    private FogConfig() {
        super("fogcustomizer.json", "Fog Customizer", Category.QOL);
        for (String option : new String[]{"fog", "start", "end", "netherFog", "water", "lava", "customColor", "color"}) {
            addDependency(option, "enabled");
        }
        addDependency("start", "fog");
        addDependency("end", "fog");
        addDependency("color", "customColor");
    }
}
