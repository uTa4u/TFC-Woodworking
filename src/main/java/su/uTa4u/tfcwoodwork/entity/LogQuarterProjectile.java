package su.uTa4u.tfcwoodwork.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LogQuarterProjectile extends AbstractWoodProjectile {


    protected LogQuarterProjectile(EntityType<? extends AbstractWoodProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public LogQuarterProjectile(BlockPos pos, BlockState state, double offsetX, double offsetY, double offsetZ, Level level, Direction dir, boolean isMirrored) {
        super(ModEntities.LOG_QUARTER_PROJ.get(), pos, state, offsetX, offsetY, offsetZ, level, dir, isMirrored);
    }
}
