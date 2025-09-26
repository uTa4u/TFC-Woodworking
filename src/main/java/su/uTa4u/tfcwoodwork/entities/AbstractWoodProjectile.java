package su.uTa4u.tfcwoodwork.entities;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.sounds.ModSounds;

public abstract class AbstractWoodProjectile extends AbstractArrow {
    private static final String KEY_MIRRORED = "Mirrored";
    private static final String KEY_DIRECTION = "Direction";
    private static final String KEY_START_BLOCKPOS = "StartBlockpos";
    private static final String KEY_BLOCKSTATE = "Blockstate";
    private static final String KEY_HROT = "Hrot";
    private static final String KEY_HROT0 = "Hrot0";
    protected static final EntityDataAccessor<Boolean> MIRRORED = SynchedEntityData.defineId(AbstractWoodProjectile.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(AbstractWoodProjectile.class, EntityDataSerializers.DIRECTION);
    // TODO: this is probably unnecessary
    protected static final EntityDataAccessor<BlockPos> START_BLOCKPOS = SynchedEntityData.defineId(AbstractWoodProjectile.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<BlockState> BLOCKSTATE = SynchedEntityData.defineId(AbstractWoodProjectile.class, EntityDataSerializers.BLOCK_STATE);

    private static final float DIM_SIZE = 0.375f;
    private static final EntityDimensions DIMENSIONS = new EntityDimensions(DIM_SIZE, DIM_SIZE, DIM_SIZE * 0.5f, EntityAttachments.createDefault(DIM_SIZE, DIM_SIZE), true);
    private static final int HOR_ROT_PERIOD = 30; // Ticks for 360.0f degree rotation
    // TODO: this doesn't seem to save correctly...
    //  or rather it doesn't work because arrow doesn't save it's angle, only the position
    private float hRot0 = 0.0f;
    private float hRot = 0.0f;

    protected AbstractWoodProjectile(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractWoodProjectile(EntityType<? extends AbstractArrow> entityType, BlockPos pos, BlockState state, double offsetX, double offsetY, double offsetZ, Level level, Direction dir, boolean isMirrored) {
        super(entityType, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, level, ItemStack.EMPTY, null);
        this.setMirrored(isMirrored);
        this.setDirection(dir);
        this.setStartBlockpos(pos);
        this.setBlockState(state);
        this.pickup = Pickup.ALLOWED;
        this.setPickupItemStack(this.getBlockState().getBlock().asItem().getDefaultInstance());
    }

    public float getHRot() {
        if (this.inGround) return this.hRot;
        this.hRot0 = this.hRot;
        this.hRot = (this.tickCount % HOR_ROT_PERIOD) * (360.0f / HOR_ROT_PERIOD);
        return this.hRot;
    }

    public float getHRot0() {
        return this.hRot0;
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MIRRORED, Boolean.FALSE);
        builder.define(DIRECTION, Direction.NORTH);
        builder.define(START_BLOCKPOS, BlockPos.ZERO);
        builder.define(BLOCKSTATE, ModBlocks.WOODS.get(Wood.ACACIA).get(BlockType.DEBARKED_HALF).get().defaultBlockState());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putBoolean(KEY_MIRRORED, this.getMirrored());
        nbt.putString(KEY_DIRECTION, this.getDirection().getName());
        nbt.put(KEY_START_BLOCKPOS, NbtUtils.writeBlockPos(this.getStartBlockpos()));
        nbt.putString(KEY_BLOCKSTATE, BuiltInRegistries.BLOCK.getKey(this.getBlockState().getBlock()).toString());
        nbt.putFloat(KEY_HROT, this.hRot);
        nbt.putFloat(KEY_HROT0, this.hRot0);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        this.setMirrored(nbt.getBoolean(KEY_MIRRORED));
        final var dir = Direction.byName(nbt.getString(KEY_DIRECTION));
        if (dir != null) {
            this.setDirection(dir);
        }
        NbtUtils.readBlockPos(nbt, KEY_START_BLOCKPOS).ifPresent(this::setStartBlockpos);
        final var state = nbt.getString(KEY_BLOCKSTATE);
        if (!state.isEmpty()) {
            this.setBlockState(BuiltInRegistries.BLOCK.get(ResourceLocation.parse(state)).defaultBlockState());
        }
        this.hRot = nbt.getFloat(KEY_HROT);
        this.hRot0 = nbt.getFloat(KEY_HROT0);
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

    @NotNull
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

    @Override
    @NotNull
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return ModSounds.LOG_HIT_GROUND.get();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (result.getEntity() instanceof LivingEntity entity) {
            entity.hurt(this.damageSources().generic(), 1.0f);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);

        final var level = this.level();
        final var blockEntity1 = level.getBlockEntity(result.getBlockPos());
        final var blockEntity2 = level.getBlockEntity(this.blockPosition());
        LogPileExBlockEntity logPileExBlockEntity;

        if (blockEntity1 instanceof LogPileExBlockEntity) {
            logPileExBlockEntity = (LogPileExBlockEntity) blockEntity1;
        } else if (blockEntity2 instanceof LogPileExBlockEntity) {
            logPileExBlockEntity = (LogPileExBlockEntity) blockEntity2;
        } else {
            return;
        }

        if (logPileExBlockEntity.insertItemStack(this.getPickupItem()).isEmpty()) {
            this.discard();
        }
    }

    @Override
    @NotNull
    protected AABB makeBoundingBox() {
        return DIMENSIONS.makeBoundingBox(this.position());
    }

    @Override
    @NotNull
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public float getPickRadius() {
        return 0.5f;
    }

    @Override
    protected void tickDespawn() {
        if (this.pickup != Pickup.ALLOWED || this.getPickupItemStackOrigin().isEmpty()) {
            super.tickDespawn();
        }
    }
}
