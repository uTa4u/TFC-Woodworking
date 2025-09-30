package su.uTa4u.tfcwoodwork.items;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class SawdustSoupBowl extends Item {
    public SawdustSoupBowl(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entityLiving) {
        if (entityLiving instanceof Player player) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(TFCBlocks.CERAMIC_BOWL.get().asItem()));
        }
        return super.finishUsingItem(itemStack, level, entityLiving);
    }
}
