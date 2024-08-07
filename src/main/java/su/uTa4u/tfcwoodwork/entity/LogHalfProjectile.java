package su.uTa4u.tfcwoodwork.entity;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.util;

public class LogHalfProjectile extends AbstractArrow {
    //TODO: make these into regular variables?
    protected static final EntityDataAccessor<Boolean> MIRRORED = SynchedEntityData.defineId(LogHalfProjectile.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(LogHalfProjectile.class, EntityDataSerializers.DIRECTION);
    protected static final EntityDataAccessor<BlockPos> START_BLOCKPOS = SynchedEntityData.defineId(LogHalfProjectile.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<BlockState> BLOCKSTATE = SynchedEntityData.defineId(LogHalfProjectile.class, EntityDataSerializers.BLOCK_STATE);

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int HOR_ROT_PERIOD = 120; // Ticks per 360.0f rotation
    private BlockPos stuckPos;
    private int life = 0;
    private float hRot0 = 0.0f;
    private float hRot = 0.0f;

    public LogHalfProjectile(EntityType<LogHalfProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public LogHalfProjectile(BlockPos pos, BlockState state, double offsetX, double offsetY, double offsetZ, Level level, Direction dir, boolean isMirrored) {
        super(ModEntities.LOG_HALF_PROJ.get(), pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, level);
        this.setMirrored(isMirrored);
        this.setDirection(dir);
        this.setStartBlockpos(pos);
        this.setBlockState(state);
    }

    public float getHRot() {
        this.hRot0 = this.hRot;
        if (!this.inGround) {
            this.hRot = (this.tickCount % HOR_ROT_PERIOD) * (360.0f / HOR_ROT_PERIOD);
        }
        return this.hRot;
    }

    public float getHRot0() {
        return this.hRot0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MIRRORED, Boolean.FALSE);
        this.entityData.define(DIRECTION, Direction.NORTH);
        this.entityData.define(START_BLOCKPOS, BlockPos.ZERO);
        this.entityData.define(BLOCKSTATE, util.getStateToPlace(ModBlocks.WOODS, Wood.ACACIA, BlockType.DEBARKED_HALF));
    }

    public BlockState getBlockState() {
        return this.entityData.get(BLOCKSTATE);
    }

    private void setBlockState(BlockState state) {
        this.entityData.set(BLOCKSTATE, state);
    }

    public boolean getMirrored() {
        return this.entityData.get(MIRRORED);
    }

    private void setMirrored(boolean isMirrored) {
        this.entityData.set(MIRRORED, isMirrored);
    }

    public Direction getDirection() {
        return this.entityData.get(DIRECTION);
    }

    private void setDirection(Direction dir) {
        this.entityData.set(DIRECTION, dir);
    }

    public BlockPos getStartBlockpos() {
        return this.entityData.get(START_BLOCKPOS);
    }

    private void setStartBlockpos(BlockPos pos) {
        this.entityData.set(START_BLOCKPOS, pos);
    }

    //TODO: onHitEntity do damage

    @Override
    protected void onHitBlock(BlockHitResult result) {
        //TODO: play sound?
        this.stuckPos = result.getBlockPos();
        this.inGround = true;
    }

    @Override
    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 10) {
            util.spawnDrops(this.level(), stuckPos, this.getBlockState().getBlock().asItem().getDefaultInstance());
            this.discard();
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
