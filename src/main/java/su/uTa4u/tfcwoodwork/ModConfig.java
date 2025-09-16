package su.uTa4u.tfcwoodwork;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ModConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue BARK_DROP = BUILDER
            .comment("How many bark pieces to drop per side")
            .comment("Default: 1")
            .defineInRange("barkDropCount", 1, 0, 64);

    public static final ModConfigSpec.IntValue BAST_DROP = BUILDER
            .comment("How many bast pieces to drop per side")
            .comment("Default: 1")
            .defineInRange("bastDropCount", 1, 0, 64);

    public static final ModConfigSpec.IntValue SAWDUST_DROP = BUILDER
            .comment("How much sawdust should drop from each saw interaction")
            .comment("Default: 1")
            .defineInRange("sawdustDropCount", 1, 0, 64);

    public static final ModConfigSpec.IntValue SUPPORT_PER_HALF = BUILDER
            .comment("How many support should be crafted from one debarked half")
            .comment("Default: 4")
            .defineInRange("supportPerLogHalf", 4, 0, 64);

    public static final ModConfigSpec.IntValue LUMBER_PER_QUARTER = BUILDER
            .comment("How many lumber should be crafted from one debarked quarter")
            .comment("Default: 2")
            .defineInRange("lumberPerLogQuarter", 2, 0, 64);

    public static final ModConfigSpec.IntValue FENCE_FROM_PLANK = BUILDER
            .comment("How many fences should drop from each plank interaction")
            .comment("Default: 2")
            .defineInRange("fenceFromPlank", 2, 0, 64);

    public static final ModConfigSpec.IntValue FENCE_FROM_STAIR = BUILDER
            .comment("How many fences should drop from each stair interaction")
            .comment("Default: 2")
            .defineInRange("fenceFromStair", 2, 0, 64);

    public static final ModConfigSpec.IntValue FENCE_FROM_LOG = BUILDER
            .comment("How many log fences should drop from each log interaction")
            .comment("Default: 4")
            .defineInRange("fenceFromLog", 4, 0, 64);

    public static final ModConfigSpec.IntValue TRAPDOOR_FROM_SLAB = BUILDER
            .comment("How many trapdoors should drop from each slab interaction")
            .comment("Default: 2")
            .defineInRange("trapdoorFromSlab", 2, 0, 64);

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
            .define("toolCooldowns", Arrays.asList(10, 10, 10, 10, 10, 10, 10), val -> ( val instanceof Integer intVal && (0 <= intVal) && ( intVal <= 1200)));

    public static final ModConfigSpec SPEC = BUILDER.build();
}
