package su.uTa4u.tfcwoodwork.entity;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.util;

public class LogHalfProjectile extends AbstractWoodProjectile {

    public LogHalfProjectile(EntityType<LogHalfProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public LogHalfProjectile(BlockPos pos, BlockState state, double offsetX, double offsetY, double offsetZ, Level level, Direction dir, boolean isMirrored) {
        super(ModEntities.LOG_HALF_PROJ.get(), pos, state, offsetX, offsetY, offsetZ, level, dir, isMirrored);
    }


}
