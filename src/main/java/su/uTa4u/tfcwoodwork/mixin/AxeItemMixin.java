package su.uTa4u.tfcwoodwork.mixin;

import net.minecraft.world.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(value = AxeItem.class)
public abstract class AxeItemMixin {

    @ModifyVariable(method = "useOn",
            at = @At("STORE"),
            ordinal = 0
    )
    private Optional useOn(Optional value) {
        return Optional.empty();
    }
}
