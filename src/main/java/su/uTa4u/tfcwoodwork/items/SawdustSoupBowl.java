package su.uTa4u.tfcwoodwork.items;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SawdustSoupBowl extends Item {
    public SawdustSoupBowl(Properties properties) {
        super(properties);
    }

    @Override
    @NotNull
    public ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entityLiving) {
      ItemStack itemstack = super.finishUsingItem(itemStack, level, entityLiving);
      return entityLiving instanceof Player player && player.getAbilities().instabuild ? itemstack : new ItemStack(TFCBlocks.CERAMIC_BOWL.get().asItem());
   }
}
