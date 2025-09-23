package su.uTa4u.tfcwoodwork.mixin;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.BlockItemPlacement;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.InteractionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.uTa4u.tfcwoodwork.blockentities.ModBlockEntities;
import su.uTa4u.tfcwoodwork.blocks.LogPileExBlock;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;

@Mixin(InteractionManager.class)
public abstract class InteractionManagerMixin {

    @Inject(method = "registerDefaultInteractions",
            at = @At(value = "HEAD")
    )
    private static void registerLogPileExInteraction(CallbackInfo ci) {
        BlockItemPlacement logPilePlacement = new BlockItemPlacement(net.minecraft.world.item.Items.AIR, ModBlocks.LOG_PILE_EX);
        InteractionManager.registerBlock(Ingredient.of(TFCTags.Items.LOG_PILE_LOGS), (stack, context) -> {
            final Player player = context.getPlayer();
            if (player != null && player.mayBuild() && player.isShiftKeyDown()) {
                final Level level = context.getLevel();
                final Direction direction = context.getClickedFace();
                final BlockPos posClicked = context.getClickedPos();
                final BlockState stateClicked = level.getBlockState(posClicked);
                final BlockPos relativePos = posClicked.relative(direction);
                final BlockPos belowPos = relativePos.below();
                if (Helpers.isBlock(stateClicked, ModBlocks.LOG_PILE_EX.get())) {
                    return level.getBlockEntity(posClicked, ModBlockEntities.LOG_PILE_EX.get())
                            .map(logPileBlockEntity -> {
                                if (!level.isClientSide()) {
                                    LogPileExBlock.insertAndPushUp(stack, stateClicked, level, posClicked, logPileBlockEntity, true);
                                    return InteractionResult.sidedSuccess(level.isClientSide);
                                }
                                return InteractionResult.sidedSuccess(level.isClientSide);
                            }).orElse(InteractionResult.PASS);
                } else if (level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP)) {
                    final ItemStack stackBefore = stack.copy();
                    final BlockPos actualPlacedPos = new BlockPlaceContext(context).getClickedPos();
                    final InteractionResult result = logPilePlacement.onItemUse(stack, context);
                    if (result.consumesAction()) {
                        level.getBlockEntity(actualPlacedPos, ModBlockEntities.LOG_PILE_EX.get()).ifPresent(logPile -> logPile.insertItemStack(stackBefore.copyWithCount(1)));
                    }
                    return result;
                }
            }
            return InteractionResult.PASS;
        });
    }
}
