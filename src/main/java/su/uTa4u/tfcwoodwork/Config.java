package su.uTa4u.tfcwoodwork;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = TFCWoodworking.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue BARK_DROP = BUILDER
            .comment("How many bark pieces to drop per side")
            .defineInRange("barkDropCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue BAST_DROP = BUILDER
            .comment("How many bast pieces to drop per side")
            .defineInRange("bastDropCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue SAWDUST_DROP = BUILDER
            .comment("How much sawdust should drop from each saw interaction")
            .defineInRange("sawdustDropCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue SUPPORT_PER_HALF = BUILDER
            .comment("How many support should be crafted from one debarked half")
            .defineInRange("supportPerLogHalf", 4, 0, 64);

    private static final ForgeConfigSpec.IntValue LUMBER_PER_QUARTER = BUILDER
            .comment("How many lumber should be crafted from one debarked quarter")
            .defineInRange("lumberPerLogQuarter", 2, 0, 64);

    private static final ForgeConfigSpec.IntValue FENCE_FROM_PLANK = BUILDER
            .comment("How many fences should drop from each plank interaction")
            .defineInRange("fenceFromPlank", 2, 0, 64);

    private static final ForgeConfigSpec.IntValue FENCE_FROM_STAIR = BUILDER
            .comment("How many fences should drop from each stair interaction")
            .defineInRange("fenceFromStair", 2, 0, 64);

    private static final ForgeConfigSpec.IntValue FENCE_FROM_LOG = BUILDER
            .comment("How many log fences should drop from each log interaction")
            .defineInRange("fenceFromLog", 4, 0, 64);

    private static final ForgeConfigSpec.IntValue TRAPDOOR_FROM_SLAB = BUILDER
            .comment("How many trapdoors should drop from each slab interaction")
            .defineInRange("trapdoorFromSlab", 2, 0, 64);

    private static final ForgeConfigSpec.IntValue LOG_PILE_LOG_CAP = BUILDER
            .comment("How many logs can custom log pile store per slot")
            .defineInRange("logPileLogCapacity", 4, 1, 64);

    private static final ForgeConfigSpec.IntValue LOG_PILE_HALF_CAP = BUILDER
            .comment("How many log halves can custom log pile store per slot")
            .defineInRange("logPileLogHalfCapacity", 8, 1, 64);

    private static final ForgeConfigSpec.IntValue LOG_PILE_QUAR_CAP = BUILDER
            .comment("How many log quarters can custom log pile store per slot")
            .defineInRange("logPileLogQuarterCapacity", 16, 1, 64);

    private static final ForgeConfigSpec.IntValue LOG_PILE_LIMIT = BUILDER
            .comment("How many log quarters can custom log pile store in total across all slots")
            .comment("Maximum of 1792 is achieved when all slots are holding 64 items")
            .defineInRange("logPileLimit", 64, 1, 1792);

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
    }
}
