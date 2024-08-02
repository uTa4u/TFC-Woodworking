package su.uTa4u.tfcwoodwork.blocks;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import su.uTa4u.tfcwoodwork.util;

public class DebarkedHalf extends AbstractDebarkedWood {
    private static final VoxelShape[] SHAPE_BY_INDEX = new VoxelShape[4];

    public static final int DROP_COUNT = 2;

    static {
        //South/North + not Chopped
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.3125, 0.9375, 1, 0.6875), BooleanOp.OR);
        SHAPE_BY_INDEX[0] = shape;

        //South/North + Chopped
        shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.3125, 0.4375, 1, 0.6875), BooleanOp.OR);
	    shape = Shapes.join(shape, Shapes.box(0.5625, 0, 0.3125, 0.9375, 1, 0.6875), BooleanOp.OR);
        SHAPE_BY_INDEX[1] = shape;

        //West/East   + not Chopped
        shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.0625, 0.6875, 1, 0.9375), BooleanOp.OR);
        SHAPE_BY_INDEX[2] = shape;

        //West/East   + Chopped
        shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.0625, 0.6875, 1, 0.4375), BooleanOp.OR);
    	shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.5625, 0.6875, 1, 0.9375), BooleanOp.OR);
        SHAPE_BY_INDEX[3] = shape;

    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = ((state.getValue(FACING).get2DDataValue() % 2) << 1) |
                    (state.getValue(CHOPPED) ? 1 : 0);
        return SHAPE_BY_INDEX[index];
    }

    public DebarkedHalf() { super(); }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.OFF_HAND) return InteractionResult.PASS;
        if (!state.getValue(CHOPPED)) return InteractionResult.PASS;

        util.Pair<Wood, BlockType> pair = util.getWoodWoodTypePair(ModBlocks.WOODS, state);
        Item item = util.getItemToDrop(ModBlocks.WOODS, pair.key(), BlockType.DEBARKED_QUARTER);
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
        return InteractionResult.SUCCESS;
    }
}
