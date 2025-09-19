package su.uTa4u.tfcwoodwork.recipes.inworld;

import net.dries007.tfc.common.LevelTier;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import su.uTa4u.tfcwoodwork.ModConfig;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.recipes.ModRecipeTypes;

import java.util.Random;

@EventBusSubscriber(modid = TFCWoodworking.MOD_ID)
public final class InWorldRecipeHandler {
    private static final Random RNG = new Random();

    // TODO: Rework compat with dynamic trees tfc
    // TODO: Add compat with arbor firma craft
    private static final Block[] DTTFC_LOGS = new Block[Wood.VALUES.length];

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        final var player = event.getEntity();
        final var itemStack = player.getMainHandItem();

        if (player.getCooldowns().isOnCooldown(itemStack.getItem())) {
            event.setUseItem(TriState.FALSE);
            return;
        }

        final var level = event.getLevel();
        final var pos = event.getPos();
        final var blockState = level.getBlockState(pos);
        final var recipes = level.getRecipeManager();

        final var input = new InWorldRecipeInput(blockState, itemStack);
        final var resultOpt = recipes.getRecipeFor(ModRecipeTypes.IN_WORLD.get(), input, level);

        resultOpt.map(RecipeHolder::value).ifPresent((recipe) -> {
            if (!checkFiveDirections(level, pos)) return;

            final var resultItems = recipe.resultItems();
            if (!resultItems.isEmpty()) {
                final var dir = player.getDirection();
                for (var pair : resultItems) {
                    pair.getSecond().accept(level, pos, pair.getFirst(), dir);
                }
            }

            level.setBlockAndUpdate(pos, recipe.resultState());
            damageTool(player, itemStack);
            setCooldownForItems(player, recipe.tool().getToolTagKey());

            recipe.soundInstance().ifPresent((soundInstance -> soundInstance.play(level, player, pos)));
            player.swing(InteractionHand.MAIN_HAND, true);

            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            event.setCanceled(true);
        });
    }


    private static void setCooldownForItems(Player player, TagKey<Item> tag) {
        ItemCooldowns cds = player.getCooldowns();
        for (var axe : BuiltInRegistries.ITEM.getOrCreateTag(tag)) {
            if (axe.value() instanceof TieredItem tieredItem) {
                int level = 6; // default to maximum cooldown for vanilla tools
                if (tieredItem.getTier() instanceof LevelTier levelTier) {
                    level = levelTier.level();
                }
                cds.addCooldown(tieredItem, ModConfig.TOOL_COOLDOWNS.get().get(level));
            }
        }
    }

    private static boolean checkFiveDirections(Level level, BlockPos pos) {
        for (int i = 1; i < 5; ++i) {
            Direction dir = Direction.from3DDataValue(i);
            BlockPos nbour = pos.relative(dir);
            if (level.getBlockState(nbour).isFaceSturdy(level, nbour, dir.getOpposite(), SupportType.FULL)) {
                return false;
            }
        }
        return true;
    }

    public static void damageTool(Player player, ItemStack inHand) {
        int uses = ((TieredItem) inHand.getItem()).getTier().getUses();
        // when bismuth bronze axe is used chance to damage the tool is 1/3
        if (RNG.nextDouble() < 400.0 / uses) {
            // maybe break even harder when the chance value is bigger than 1
            inHand.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }

    public static void initDTTFCBlocks() {
        for (int i = 0; i < Wood.VALUES.length; ++i) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("dttfc", Wood.VALUES[i].getSerializedName() + "_branch"));
            DTTFC_LOGS[i] = block;
        }
    }

    private static boolean isBlockFromDTTFC(BlockState state) {
        for (int i = 0; i < Wood.VALUES.length; ++i) {
            if (state.is(DTTFC_LOGS[i])) return true;
        }
        return false;
    }

    // TODO: remove chisel recipes for handled items

    // TODO: if alive tree was debarked it should die after some time and fall, more debarked blocks = faster death
    // TODO: make bark/bast pileable

    // TODO: fix projectile rotation reseting after world exit
    // TODO: check if player is looking in the same axis as wood is placed
    // TODO: add interactions like these https://discord.com/channels/432522930610765835/1415675635489181716
}
