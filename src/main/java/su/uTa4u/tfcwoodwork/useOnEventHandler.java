package su.uTa4u.tfcwoodwork;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.items.ModItems;
import su.uTa4u.tfcwoodwork.sounds.ModSounds;

import java.util.Random;

public class useOnEventHandler {
    private static final Random rng = new Random();

    private static final Block[] DTTFC_LOGS = new Block[Wood.VALUES.length];

    @SubscribeEvent
    public static void useOn(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        BlockState state = level.getBlockState(pos);
        ItemStack inMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (player.getCooldowns().isOnCooldown(inMainHand.getItem())) {
            event.setUseItem(Event.Result.DENY);
            return;
        }
        if (!isValidBlock(state)) return;
        if (DTTFC_LOGS.length != 0 && isBlockFromDTTFC(state)) {
            event.setUseItem(Event.Result.DENY);
            return;
        }
        InteractionHand hand = event.getHand();
        if (isValidAxe(inMainHand)) {
            if (checkFourDirections(level, pos)) {
                InteractionResult result = useTool(util.TOOL.AXE, level, player, pos);
                if (result == InteractionResult.sidedSuccess(level.isClientSide)) {
                    player.swing(InteractionHand.MAIN_HAND, true);
                    damageTool(player, inMainHand, InteractionHand.MAIN_HAND);
                    setCooldownForAxes(player);
                    event.setCanceled(true);
                }
            } else {
                if (hand == InteractionHand.MAIN_HAND) event.setUseItem(Event.Result.DENY);
            }
        } else if (isValidSaw(inMainHand)) {
            if (checkFourDirections(level, pos)) {
                InteractionResult result = useTool(util.TOOL.SAW, level, player, pos);
                if (result == InteractionResult.sidedSuccess(level.isClientSide)) {
                    player.swing(InteractionHand.MAIN_HAND, true);
                    damageTool(player, inMainHand, InteractionHand.MAIN_HAND);
                    setCooldownForSaws(player);
                    event.setCanceled(true);
                }
            } else {
                if (hand == InteractionHand.MAIN_HAND) event.setUseItem(Event.Result.DENY);
            }
        }
    }

    private static void setCooldownForAxes(Player player) {
        ItemCooldowns cds = player.getCooldowns();
        Helpers.allItems(ModTags.Items.TFC_AXES).forEach((axe) -> cds.addCooldown(axe.asItem(), Config.toolCooldowns.get(((TieredItem) axe).getTier().getLevel())));
    }

    private static void setCooldownForSaws(Player player) {
        ItemCooldowns cds = player.getCooldowns();
        Helpers.allItems(ModTags.Items.TFC_SAWS).forEach((saw) -> cds.addCooldown(saw.asItem(), Config.toolCooldowns.get(((TieredItem) saw).getTier().getLevel())));
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

    public static void damageTool(Player player, ItemStack inHand, InteractionHand hand) {
        int uses = ((TieredItem) inHand.getItem()).getTier().getUses();
        //when bismuth bronze axe is used chance to damage the tool is 1/3
        if (rng.nextDouble() < 400.0 / uses) {
            //maybe break even harder when the chance value is bigger than 1
            inHand.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }
    }

    public static void initDTTFCBlocks() {
        for (int i = 0; i < Wood.VALUES.length; ++i) {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("dttfc", Wood.VALUES[i].getSerializedName() + "_branch"));
            if (block != null) {
                DTTFC_LOGS[i] = block;
            }
        }
    }

    private static boolean isBlockFromDTTFC(BlockState state) {
        for (int i = 0; i < Wood.VALUES.length; ++i) {
            if (state.is(DTTFC_LOGS[i])) return true;
        }
        return false;
    }

    private static boolean isValidSaw(ItemStack inHand) {
        return Helpers.isItem(inHand, ModTags.Items.TFC_SAWS);
    }

    private static boolean isValidAxe(ItemStack inHand) {
        return Helpers.isItem(inHand, ModTags.Items.TFC_AXES);
    }

    private static boolean isValidBlock(BlockState state) {
        return (Helpers.isBlock(state, BlockTags.LOGS) && state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y)
                || Helpers.isBlock(state, ModTags.Blocks.LOGS)
                || Helpers.isBlock(state, BlockTags.WOODEN_STAIRS)
                || Helpers.isBlock(state, BlockTags.WOODEN_SLABS)
                || Helpers.isBlock(state, BlockTags.PLANKS);
    }

    //TODO: remove chisel recipes for handled items

    //TODO: if alive tree was debarked it should die after some time and fall, more debarked blocks = faster death
    //TODO: make bark/bast pileable

    //TODO: make this not suck, refactor!
    public static InteractionResult useTool(util.TOOL tool, Level level, Player player, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Direction dir = player.getDirection();
        return util.getWoodWoodTypePair(TFCBlocks.WOODS, state).map((pair1) -> {
            BlockState newState;
            if (tool == util.TOOL.AXE) {
                switch (pair1.value()) {
                    case LOG -> {
                        newState = util.getStateToPlace(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STRIPPED_LOG);
                        util.spawnDropsCardinal(level, pos, new ItemStack(ModItems.getBark(pair1.key()), Config.barkDropCount));
                    }
                    case STRIPPED_LOG -> {
                        newState = util.getStateToPlace(ModBlocks.WOODS, pair1.key(), BlockType.DEBARKED_LOG);
                        util.spawnDropsCardinal(level, pos, new ItemStack(ModItems.getBast(pair1.key()), Config.bastDropCount));
                    }
                    case WOOD -> {
                        newState = util.getStateToPlace(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STRIPPED_WOOD);
                        Item bark = ModItems.getBark(pair1.key());
                        util.spawnDropsCardinal(level, pos, new ItemStack(bark, Config.barkDropCount));
                        util.spawnDropsAbove(level, pos, new ItemStack(bark, Config.barkDropCount * 2));
                    }
                    case STRIPPED_WOOD -> {
                        newState = util.getStateToPlace(ModBlocks.WOODS, pair1.key(), BlockType.DEBARKED_LOG);
                        Item bast = ModItems.getBast(pair1.key());
                        util.spawnDropsCardinal(level, pos, new ItemStack(bast, Config.bastDropCount));
                        util.spawnDropsAbove(level, pos, new ItemStack(bast, Config.bastDropCount * 2));
                    }
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                level.setBlockAndUpdate(pos, newState);
                level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            } else if (tool == util.TOOL.SAW) {
                switch (pair1.value()) {
                    case LOG -> {
                        newState = Blocks.AIR.defaultBlockState();
                        util.spawnDrops(level, pos, new ItemStack(util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.LOG_FENCE), Config.fenceFromLog));
                    }
                    case PLANKS -> {
                        newState = Blocks.AIR.defaultBlockState();
                        util.spawnDrops(level, pos, new ItemStack(util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.FENCE), Config.fenceFromPlank));
                        util.spawnDrops(level, pos, new ItemStack(util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STAIRS), 1));
                    }
                    case STAIRS -> {
                        newState = Blocks.AIR.defaultBlockState();
                        util.spawnDrops(level, pos, new ItemStack(util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.FENCE), Config.fenceFromStair));
                        util.spawnDrops(level, pos, new ItemStack(util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.SLAB), 1));
                    }
                    case SLAB -> {
                        newState = Blocks.AIR.defaultBlockState();
                        util.spawnDrops(level, pos, new ItemStack(util.getItemToDrop(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.TRAPDOOR), Config.trapdoorFromSlab));
                    }
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                util.spawnDropsAbove(level, pos, new ItemStack(ModItems.SAWDUST.get(), Config.sawdustDropCount));
                level.setBlockAndUpdate(pos, newState);
                level.playSound(player, pos, ModSounds.LOG_SAWED.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElseGet(() -> util.getWoodWoodTypePair(ModBlocks.WOODS, state).map((pair2) -> {
            if (tool == util.TOOL.AXE && level.getBlockState(pos.above()) == Blocks.AIR.defaultBlockState()) {
                switch (pair2.value()) {
                    case DEBARKED_LOG -> util.shootLogHalves(level, pos, pair2.key(), dir);
                    case DEBARKED_HALF -> util.shootLogQuarters(level, pos, pair2.key(), dir);
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(player, pos, ModSounds.LOG_CHOP.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            } else if (tool == util.TOOL.SAW) {
                switch (pair2.value()) {
                    case DEBARKED_HALF -> util.spawnDrops(level, pos, new ItemStack(TFCItems.SUPPORTS.get(pair2.key()).get(), Config.supportPerLogHalf));
                    case DEBARKED_QUARTER -> util.spawnDrops(level, pos, new ItemStack(TFCItems.LUMBER.get(pair2.key()).get(), Config.lumberPerLogQuarter));
                    default -> {
                        return InteractionResult.PASS;
                    }
                }
                util.spawnDrops(level, pos, new ItemStack(ModItems.SAWDUST.get(), Config.sawdustDropCount));
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.playSound(player, pos, ModSounds.LOG_SAWED.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS));
    }
}
