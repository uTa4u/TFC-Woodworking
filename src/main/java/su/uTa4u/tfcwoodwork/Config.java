package su.uTa4u.tfcwoodwork;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = TFCWoodworking.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue BARK_DROP = BUILDER
            .comment("How many bark pieces to drop per side")
            .comment("Default: 1")
            .defineInRange("barkDropCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue BAST_DROP = BUILDER
            .comment("How many bast pieces to drop per side")
            .comment("Default: 1")
            .defineInRange("bastDropCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue SAWDUST_DROP = BUILDER
            .comment("How much sawdust should drop from each saw interaction")
            .comment("Default: 1")
            .defineInRange("sawdustDropCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue SUPPORT_PER_HALF = BUILDER
            .comment("How many support should be crafted from one debarked half")
            .comment("Default: 4")
            .defineInRange("supportPerLogHalf", 4, 0, 64);

    private static final ForgeConfigSpec.IntValue LUMBER_PER_QUARTER = BUILDER
            .comment("How many lumber should be crafted from one debarked quarter")
            .comment("Default: 2")
            .defineInRange("lumberPerLogQuarter", 2, 0, 64);

    private static final ForgeConfigSpec.IntValue FENCE_FROM_PLANK = BUILDER
            .comment("How many fences should drop from each plank interaction")
            .comment("Default: 2")
            .defineInRange("fenceFromPlank", 2, 0, 64);

    private static final ForgeConfigSpec.IntValue FENCE_FROM_STAIR = BUILDER
            .comment("How many fences should drop from each stair interaction")
            .comment("Default: 2")
            .defineInRange("fenceFromStair", 2, 0, 64);

    private static final ForgeConfigSpec.IntValue FENCE_FROM_LOG = BUILDER
            .comment("How many log fences should drop from each log interaction")
            .comment("Default: 4")
            .defineInRange("fenceFromLog", 4, 0, 64);

    private static final ForgeConfigSpec.IntValue TRAPDOOR_FROM_SLAB = BUILDER
            .comment("How many trapdoors should drop from each slab interaction")
            .comment("Default: 2")
            .defineInRange("trapdoorFromSlab", 2, 0, 64);

    private static final ForgeConfigSpec.IntValue LOG_PILE_LOG_CAP = BUILDER
            .comment("How many logs can custom log pile store per slot")
            .comment("Default: 4")
            .defineInRange("logPileLogCapacity", 4, 1, 64);

    private static final ForgeConfigSpec.IntValue LOG_PILE_HALF_CAP = BUILDER
            .comment("How many log halves can custom log pile store per slot")
            .comment("Default: 8")
            .defineInRange("logPileLogHalfCapacity", 8, 1, 64);

    private static final ForgeConfigSpec.IntValue LOG_PILE_QUAR_CAP = BUILDER
            .comment("How many log quarters can custom log pile store per slot")
            .comment("Default: 16")
            .defineInRange("logPileLogQuarterCapacity", 16, 1, 64);

    private static final ForgeConfigSpec.IntValue LOG_PILE_LIMIT = BUILDER
            .comment("How many log quarters can custom log pile store in total across all slots")
            .comment("Maximum of 1792 is achieved when all slots are configured to hold 64 items")
            .comment("Default: 64")
            .defineInRange("logPileLimit", 64, 1, 1792);

    private static final ForgeConfigSpec.BooleanValue LOG_PROJECTILE_VS_ITEM = BUILDER
            .comment("Should the custom wood projectile be shot instead of a ItemEntity")
            .comment("Default: true")
            .define("logProjectileVsItem", true);

    //maybe for higher tier tools -> less cooldown
    private static final ForgeConfigSpec.ConfigValue<List<Integer>> TOOL_COOLDOWNS = BUILDER
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

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int barkDropCount;
    public static int bastDropCount;
    public static int supportPerLogHalf;
    public static int lumberPerLogQuarter;
    public static int sawdustDropCount;
    public static int fenceFromPlank;
    public static int fenceFromStair;
    public static int fenceFromLog;
    public static int trapdoorFromSlab;
    public static int logPileLogCapacity;
    public static int logPileLogHalfCapacity;
    public static int logPileLogQuarterCapacity;
    public static int logPileLimit;

    public static boolean logProjectileVsItem;

    public static List<Integer> toolCooldowns;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        barkDropCount = BARK_DROP.get();
        bastDropCount = BAST_DROP.get();
        supportPerLogHalf = SUPPORT_PER_HALF.get();
        lumberPerLogQuarter = LUMBER_PER_QUARTER.get();
        sawdustDropCount = SAWDUST_DROP.get();
        fenceFromPlank = FENCE_FROM_PLANK.get();
        fenceFromStair = FENCE_FROM_STAIR.get();
        fenceFromLog = FENCE_FROM_LOG.get();
        trapdoorFromSlab = TRAPDOOR_FROM_SLAB.get();
        logPileLogCapacity = LOG_PILE_LOG_CAP.get();
        logPileLogHalfCapacity = LOG_PILE_HALF_CAP.get();
        logPileLogQuarterCapacity = LOG_PILE_QUAR_CAP.get();
        logPileLimit = LOG_PILE_LIMIT.get();
        logProjectileVsItem = LOG_PROJECTILE_VS_ITEM.get();
        toolCooldowns = TOOL_COOLDOWNS.get();
    }
}
