package su.uTa4u.tfcwoodwork.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LogHalfProjectile extends AbstractWoodProjectile {

    public LogHalfProjectile(EntityType<LogHalfProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public LogHalfProjectile(BlockPos pos, BlockState state, double offsetX, double offsetY, double offsetZ, Level level, Direction dir, boolean isMirrored) {
        super(ModEntities.LOG_HALF_PROJ.get(), pos, state, offsetX, offsetY, offsetZ, level, dir, isMirrored);
    }


}
