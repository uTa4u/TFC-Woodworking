package su.uTa4u.tfcwoodwork.items;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SawdustSoupBowl extends Item {
    public SawdustSoupBowl(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entityLiving) {
      ItemStack itemstack = super.finishUsingItem(itemStack, level, entityLiving);
      return entityLiving instanceof Player && ((Player)entityLiving).getAbilities().instabuild ? itemstack : new ItemStack(TFCBlocks.CERAMIC_BOWL.get().asItem());
   }
}
