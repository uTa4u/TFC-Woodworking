package su.uTa4u.tfcwoodwork.blocks;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import su.uTa4u.tfcwoodwork.util;

public class DebarkedLog extends AbstractDebarkedWood {
    private static final VoxelShape AABB = Shapes.box(0.0625, 0, 0.0625, 0.9375, 1, 0.9375);
    private static final VoxelShape AABB_CHOPPED_NS = Shapes.join(Shapes.box(0.0625, 0, 0.0625, 0.4375, 1, 0.9375),
            Shapes.box(0.5625, 0, 0.0625, 0.9375, 1, 0.9375), BooleanOp.OR);
    private static final VoxelShape AABB_CHOPPED_WE = Shapes.join(Shapes.box(0.0625, 0, 0.5625, 0.9375, 1, 0.9375),
            Shapes.box(0.0625, 0, 0.0625, 0.9375, 1, 0.4375), BooleanOp.OR);
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int DROP_COUNT = 2;

    public DebarkedLog() {
        super();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(CHOPPED)) {
            if (state.getValue(FACING) == Direction.NORTH || state.getValue(FACING) == Direction.SOUTH) {
                return AABB_CHOPPED_NS;
            } else if (state.getValue(FACING) == Direction.WEST || state.getValue(FACING) == Direction.EAST) {
                return AABB_CHOPPED_WE;
            } else {
                return AABB_CHOPPED_NS; //up and down are unreachable (i think)
            }
        } else {
            return AABB;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below()) || state == level.getBlockState(pos.below());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.OFF_HAND) return InteractionResult.PASS;
        if (!state.getValue(CHOPPED)) return InteractionResult.PASS;

        util.Pair<Wood, BlockType> pair = util.getWoodWoodTypePair(ModBlocks.WOODS, state);
        Item item = util.getItemToDrop(ModBlocks.WOODS, pair.key(), BlockType.DEBARKED_HALF);
        Inventory inv = player.getInventory();

        ItemStack itemStack = new ItemStack(item, DROP_COUNT);
        int slot = inv.getSlotWithRemainingSpace(itemStack);
        slot = slot < 0 ? inv.getFreeSlot() : slot;
        int count = slot < 0 ? 0 : inv.getItem(slot).getCount();
        if (!inv.add(slot, itemStack)) {
            int left = slot < 0 ? DROP_COUNT : DROP_COUNT - (inv.getItem(slot).getCount() - count);
            slot = inv.getSlotWithRemainingSpace(itemStack);
            slot = slot < 0 ? inv.getFreeSlot() : slot;
            if (slot < 0) {
                player.drop(new ItemStack(item, left), false, true);
            } else {
                inv.add(slot, new ItemStack(item, left));
            }
        }
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
