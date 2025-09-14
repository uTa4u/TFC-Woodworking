package su.uTa4u.tfcwoodwork;

import net.dries007.tfc.common.LevelTier;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.items.ModItems;
import su.uTa4u.tfcwoodwork.sounds.ModSounds;

import java.util.Random;

@EventBusSubscriber(modid = TFCWoodworking.MOD_ID)
public final class UseOnEventHandler {
    private static final Random RNG = new Random();

    private static final Block[] DTTFC_LOGS = new Block[Wood.VALUES.length];

    @SubscribeEvent
    public static void useOn(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        BlockState state = level.getBlockState(pos);
        ItemStack inMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (player.getCooldowns().isOnCooldown(inMainHand.getItem())) {
            event.setUseItem(TriState.FALSE);
            return;
        }
        if (!isValidBlock(state)) return;
        if (DTTFC_LOGS.length != 0 && isBlockFromDTTFC(state)) {
            event.setUseItem(TriState.FALSE);
            return;
        }
        InteractionHand hand = event.getHand();
        if (inMainHand.is(ItemTags.AXES)) {
            if (checkFourDirections(level, pos)) {
                InteractionResult result = useTool(Util.Tool.AXE, level, player, pos);
                if (result == InteractionResult.sidedSuccess(level.isClientSide)) {
                    player.swing(InteractionHand.MAIN_HAND, true);
                    damageTool(player, inMainHand, EquipmentSlot.MAINHAND);
                    setCooldownForItems(player, ItemTags.AXES);
                    event.setCanceled(true);
                }
            } else {
                if (hand == InteractionHand.MAIN_HAND) {
                    event.setUseItem(TriState.FALSE);
                }
            }
        } else if (inMainHand.is(TFCTags.Items.TOOLS_SAW)) {
            if (checkFourDirections(level, pos)) {
                InteractionResult result = useTool(Util.Tool.SAW, level, player, pos);
                if (result == InteractionResult.sidedSuccess(level.isClientSide)) {
                    player.swing(InteractionHand.MAIN_HAND, true);
                    damageTool(player, inMainHand, EquipmentSlot.MAINHAND);
                    setCooldownForItems(player, TFCTags.Items.TOOLS_SAW);
                    event.setCanceled(true);
                }
            } else {
                if (hand == InteractionHand.MAIN_HAND) {
                    event.setUseItem(TriState.FALSE);
                }
            }
        }
    }

    private static void setCooldownForItems(Player player, TagKey<Item> tag) {
        ItemCooldowns cds = player.getCooldowns();
        for (var axe : BuiltInRegistries.ITEM.getOrCreateTag(tag)) {
            if (axe.value() instanceof TieredItem tieredItem) {
                int level = 6; // default to maximum cooldown for vanilla tools
                if (tieredItem.getTier() instanceof LevelTier levelTier) {
                    level = levelTier.level();
                }
                cds.addCooldown(tieredItem, Config.TOOL_COOLDOWNS.get().get(level));
            }
        }
    }

    private static boolean checkFourDirections(Level level, BlockPos pos) {
        for (int i = 0; i < 4; ++i) {
            Direction dir = Direction.from2DDataValue(i);
            BlockPos nbour = pos.relative(dir);
            if (level.getBlockState(nbour).isFaceSturdy(level, nbour, dir.getOpposite(), SupportType.FULL)) {
                return false;
            }
        }
        return true;
    }

    public static void damageTool(Player player, ItemStack inHand, EquipmentSlot slot) {
        int uses = ((TieredItem) inHand.getItem()).getTier().getUses();
        //when bismuth bronze axe is used chance to damage the tool is 1/3
        if (RNG.nextDouble() < 400.0 / uses) {
            //maybe break even harder when the chance value is bigger than 1
            inHand.hurtAndBreak(1, player, slot);
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

    private static boolean isValidBlock(BlockState state) {
        return (state.is(BlockTags.LOGS) && state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y)
                || state.is(ModTags.Blocks.LOGS)
                || state.is(BlockTags.WOODEN_STAIRS)
                || state.is(BlockTags.WOODEN_SLABS)
                || state.is(BlockTags.PLANKS);
    }

    // TODO: remove chisel recipes for handled items

    // TODO: if alive tree was debarked it should die after some time and fall, more debarked blocks = faster death
    // TODO: make bark/bast pileable

    // TODO: fix projectile rotation reseting after world exit
    // TODO: check if player is looking in the same axis as wood is placed
    // TODO: mixin into tfc log pile
    // TODO: add interactions like these https://discord.com/channels/432522930610765835/1415675635489181716
    // TODO: actual data driven recipe system
    // TODO: make this not suck, refactor!
    public static InteractionResult useTool(Util.Tool tool, Level level, Player player, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Direction dir = player.getDirection();
        return Util.getWoodWoodTypePair(TFCBlocks.WOODS, state).map(pair1 -> {
            BlockState newState;
            if (tool == Util.Tool.AXE) {
                switch (pair1.value()) {
                    case LOG -> {
                        newState = Util.getStateToPlace(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STRIPPED_LOG);
                        Util.spawnDropsCardinal(level, pos, new ItemStack(ModItems.getBark(pair1.key()), Config.BARK_DROP.get()));
                    }
                    case STRIPPED_LOG -> {
                        newState = Util.getStateToPlace(ModBlocks.WOODS, pair1.key(), BlockType.DEBARKED_LOG);
                        Util.spawnDropsCardinal(level, pos, new ItemStack(ModItems.getBast(pair1.key()), Config.BAST_DROP.get()));
                    }
                    case WOOD -> {
                        newState = Util.getStateToPlace(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STRIPPED_WOOD);
                        Item bark = ModItems.getBark(pair1.key());
                        Util.spawnDropsCardinal(level, pos, new ItemStack(bark, Config.BARK_DROP.get()));
                        Util.spawnDropsAbove(level, pos, new ItemStack(bark, Config.BARK_DROP.get() * 2));
                    }
                    case STRIPPED_WOOD -> {
                        newState = Util.getStateToPlace(ModBlocks.WOODS, pair1.key(), BlockType.DEBARKED_LOG);
                        Item bast = ModItems.getBast(pair1.key());
                        Util.spawnDropsCardinal(level, pos, new ItemStack(bast, Config.BAST_DROP.get()));
                        Util.spawnDropsAbove(level, pos, new ItemStack(bast, Config.BAST_DROP.get() * 2));
                    }
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                level.setBlockAndUpdate(pos, newState);
                level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            } else if (tool == Util.Tool.SAW) {
                switch (pair1.value()) {
                    case LOG -> {
                        newState = Blocks.AIR.defaultBlockState();
                        Util.spawnDrops(level, pos, new ItemStack(Util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.LOG_FENCE), Config.FENCE_FROM_LOG.get()));
                    }
                    case PLANKS -> {
                        newState = Blocks.AIR.defaultBlockState();
                        Util.spawnDrops(level, pos, new ItemStack(Util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.FENCE), Config.FENCE_FROM_PLANK.get()));
                        Util.spawnDrops(level, pos, new ItemStack(Util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STAIRS), 1));
                    }
                    case STAIRS -> {
                        newState = Blocks.AIR.defaultBlockState();
                        Util.spawnDrops(level, pos, new ItemStack(Util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.FENCE), Config.FENCE_FROM_STAIR.get()));
                        Util.spawnDrops(level, pos, new ItemStack(Util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.SLAB), 1));
                    }
                    case SLAB -> {
                        newState = Blocks.AIR.defaultBlockState();
                        Util.spawnDrops(level, pos, new ItemStack(Util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.TRAPDOOR), Config.TRAPDOOR_FROM_SLAB.get()));
                    }
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                Util.spawnDropsAbove(level, pos, new ItemStack(ModItems.SAWDUST.get(), Config.SAWDUST_DROP.get()));
                level.setBlockAndUpdate(pos, newState);
                level.playSound(player, pos, ModSounds.LOG_SAWED.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElseGet(() -> Util.getWoodWoodTypePair(ModBlocks.WOODS, state).map(pair2 -> {
            if (tool == Util.Tool.AXE && level.getBlockState(pos.above()) == Blocks.AIR.defaultBlockState()) {
                switch (pair2.value()) {
                    case DEBARKED_LOG -> Util.shootLogHalves(level, pos, pair2.key(), dir);
                    case DEBARKED_HALF -> Util.shootLogQuarters(level, pos, pair2.key(), dir);
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(player, pos, ModSounds.LOG_CHOP.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            } else if (tool == Util.Tool.SAW) {
                switch (pair2.value()) {
                    case DEBARKED_HALF -> Util.spawnDrops(level, pos, new ItemStack(TFCItems.SUPPORTS.get(pair2.key()).get(), Config.SUPPORT_PER_HALF.get()));
                    case DEBARKED_QUARTER -> Util.spawnDrops(level, pos, new ItemStack(TFCItems.LUMBER.get(pair2.key()).get(), Config.LUMBER_PER_QUARTER.get()));
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                Util.spawnDrops(level, pos, new ItemStack(ModItems.SAWDUST.get(), Config.SAWDUST_DROP.get()));
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(player, pos, ModSounds.LOG_SAWED.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS));
    }
}
