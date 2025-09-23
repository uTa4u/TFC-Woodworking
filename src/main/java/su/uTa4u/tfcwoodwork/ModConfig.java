package su.uTa4u.tfcwoodwork;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public final class ModConfig {
    private ModConfig() {
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_PROJECTILE_VS_ITEM = BUILDER
            .comment("Should the custom wood projectile be shot instead of an ItemEntity")
            .comment("Default: true")
            .define("logProjectileVsItem", true);

    // TODO: maybe for higher tier tools -> less cooldown
    public static final ModConfigSpec.ConfigValue<List<Integer>> TOOL_COOLDOWNS = BUILDER
            .comment("Cooldown on tools after log interaction succeeded")
            .comment("Level 0: IGNEOUS_INTRUSIVE, IGNEOUS_EXTRUSIVE, SEDIMENTARY, METAMORPHIC")
            .comment("Level 1: COPPER")
            .comment("Level 2: BRONZE, BISMUTH_BRONZE, BLACK_BRONZE")
            .comment("Level 3: WROUGHT_IRON")
            .comment("Level 4: STEEL")
            .comment("Level 5: BLACK_STEEL")
            .comment("Level 6: BLUE_STEEL, RED_STEEL")
            .comment("Default: [10, 10, 10, 10, 10, 10, 10]")
            .comment("Range: [0, 1200]")
            .define("toolCooldowns", Arrays.asList(10, 10, 10, 10, 10, 10, 10), val -> (val instanceof Integer intVal && (0 <= intVal) && (intVal <= 1200)));

    public static final ModConfigSpec SPEC = BUILDER.build();
}
