package su.uTa4u.tfcwoodwork;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.items.ModItems;
import su.uTa4u.tfcwoodwork.sounds.ModSounds;

import java.util.Map;

public class useOnEventHandler {
    @SubscribeEvent
    public static void useOn(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        BlockState state = level.getBlockState(pos);
        ItemStack inMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (isValidBlock(state)) {
            InteractionHand hand = event.getHand();
            if (hand == InteractionHand.OFF_HAND) {
                event.setCanceled(true);
                return;
            }
            if (checkFourDirections(level, pos)) {
                InteractionResult result;
                if (isValidAxe(inMainHand)) {
                    result = useTool(util.TOOL.AXE, level, player, pos, inMainHand, hand);
                } else if (isValidSaw(inMainHand)) {
                    result = useTool(util.TOOL.SAW, level, player, pos, inMainHand, hand);
                } else {
                    return;
                }
                if (result == InteractionResult.sidedSuccess(level.isClientSide)) {
                    player.swing(InteractionHand.MAIN_HAND, true);
                    player.getCooldowns().addCooldown(inMainHand.getItem(), 20);
                    event.setCanceled(true);
                }
            } else {
                event.setUseItem(Event.Result.DENY);
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

    private static boolean isValidSaw(ItemStack inHand) {
        for (Metal.Default metal : Metal.Default.values()) {
            RegistryObject<Item> saw = TFCItems.METAL_ITEMS.get(metal).get(Metal.ItemType.SAW);
            if (saw != null && inHand.is(saw.get())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidAxe(ItemStack inHand) {
        for (RockCategory rock : RockCategory.values()) {
            if (inHand.is(TFCItems.ROCK_TOOLS.get(rock).get(RockCategory.ItemType.AXE).get())) {
                return true;
            }
        }
        for (Metal.Default metal : Metal.Default.values()) {
            RegistryObject<Item> axe = TFCItems.METAL_ITEMS.get(metal).get(Metal.ItemType.AXE);
            if (axe != null && inHand.is(axe.get())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidBlock(BlockState state) {
        for (Wood wood : Wood.VALUES) {
            Map<Wood.BlockType, RegistryObject<Block>> tfc = TFCBlocks.WOODS.get(wood);
            Map<BlockType, RegistryObject<Block>> tfcww = ModBlocks.WOODS.get(wood);
            if (state.is(tfc.get(Wood.BlockType.LOG).get()) ||
                    state.is(tfc.get(Wood.BlockType.STRIPPED_LOG).get())) {
                return true;
            } else if ((state.is(tfcww.get(BlockType.DEBARKED_LOG).get()) ||
                    state.is(tfcww.get(BlockType.DEBARKED_HALF).get()) ||
                    state.is(tfcww.get(BlockType.DEBARKED_QUARTER).get()))) {
                return true;
            }
        }
        return false;
    }

    //TODO: remove tfc recipes for handled blocks/items
    //TODO: drops should drop in cross/perpendicular way
    //TODO: new textures for blocks and items
    //TODO: quarter -> fence
    //TODO: uses/tags for bark/bast/sawdust
    //TODO: fix tool cooldown
    //TODO: make Wood.BlockType == WOOD stripable
    //TODO: make vanilla wood stripable leave vanilla implementation and just drop bark
    //TODO: if alive tree was debarked it should die after some time and fall, more debarked blocks = faster death
    //TODO: relevant helper methods here from util
    //TODO: make bark pileable
    //TODO: make blocks bigged and heavier (tfc mechanics)
    //TODO: chopped woods fly away when chopped
    //TODO: factor out shared stuff from switches/ifs
    public static InteractionResult useTool(util.TOOL tool, Level level, Player player, BlockPos pos, ItemStack inHand, InteractionHand hand) {
        BlockState state = level.getBlockState(pos);
        util.Pair<Wood, Wood.BlockType> pair1 = util.getWoodWoodTypePair(TFCBlocks.WOODS, state);

        Direction dir = player.getDirection();
        if (pair1 != null) {
            BlockState newState;
            switch (pair1.value()) {
                case LOG -> {
                    newState = util.getStateToPlace(TFCBlocks.WOODS, pair1.key(), Wood.BlockType.STRIPPED_LOG);
                    util.spawnDrops(level, pos, ModItems.getBark(pair1.key()));
                }
                case STRIPPED_LOG -> {
                    newState = util.getStateToPlace(ModBlocks.WOODS, pair1.key(), BlockType.DEBARKED_LOG);
                    util.spawnDrops(level, pos, ModItems.getBast(pair1.key()));
                }
                default -> {
                    return InteractionResult.PASS;
                }
            }
            level.setBlockAndUpdate(pos, newState);
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);
            util.damageTool(player, inHand, hand);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            util.Pair<Wood, BlockType> pair2 = util.getWoodWoodTypePair(ModBlocks.WOODS, state);
            if (pair2 != null) {
                if (tool == util.TOOL.AXE && level.getBlockState(pos.above()) == Blocks.AIR.defaultBlockState()) {
                    switch (pair2.value()) {
                        case DEBARKED_LOG -> {
                            //util.dropBiDirectional(level, pos, dir, util.getItemToDrop(ModBlocks.WOODS, pair2.key(), BlockType.DEBARKED_HALF), 1);
                            util.shootChoppedWood(level, pos, pair2.key(), dir);
                        }
                        case DEBARKED_HALF -> {
                            //util.dropBiDirectional(level, pos, dir, util.getItemToDrop(ModBlocks.WOODS, pair2.key(), BlockType.DEBARKED_QUARTER), 1);
                        }
                        default -> { return InteractionResult.PASS; }
                    }
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    level.playSound(player, pos, ModSounds.LOG_CHOP.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
                    util.damageTool(player, inHand, hand);
                } else if (tool == util.TOOL.SAW) {
                    switch (pair2.value()) {
                        case DEBARKED_HALF -> util.spawnDrops(level, pos, new ItemStack(TFCItems.SUPPORTS.get(pair2.key()).get(), 4));
                        case DEBARKED_QUARTER -> util.spawnDrops(level, pos, new ItemStack(TFCItems.LUMBER.get(pair2.key()).get(), 2));
                        default -> { return InteractionResult.PASS; }
                    }
                    util.spawnDrops(level, pos, new ItemStack(ModItems.SAWDUST.get(), 1));
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    level.playSound(player, pos, ModSounds.LOG_SAWED.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
                    util.damageTool(player, inHand, hand);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        }
    }
}
