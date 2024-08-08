package su.uTa4u.tfcwoodwork.entity;

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
import su.uTa4u.tfcwoodwork.blocks.BlockType;
import su.uTa4u.tfcwoodwork.blocks.ModBlocks;
import su.uTa4u.tfcwoodwork.util;

public abstract class AbstractWoodProjectile extends AbstractArrow {
    //TODO: make these into regular variables?
    protected static final EntityDataAccessor<Boolean> MIRRORED = SynchedEntityData.defineId(AbstractWoodProjectile.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(AbstractWoodProjectile.class, EntityDataSerializers.DIRECTION);
    protected static final EntityDataAccessor<BlockPos> START_BLOCKPOS = SynchedEntityData.defineId(AbstractWoodProjectile.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<BlockState> BLOCKSTATE = SynchedEntityData.defineId(AbstractWoodProjectile.class, EntityDataSerializers.BLOCK_STATE);

    private static final EntityDimensions DIMENSIONS = new EntityDimensions(0.375f, 0.375f, true);
    private static final int HOR_ROT_PERIOD = 30; // Ticks for 360.0f degree rotation
    private float hRot0 = 0.0f;
    private float hRot = 0.0f;

    protected AbstractWoodProjectile(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractWoodProjectile(EntityType<? extends AbstractArrow> entityType, BlockPos pos, BlockState state, double offsetX, double offsetY, double offsetZ, Level level, Direction dir, boolean isMirrored) {
        super(entityType, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, level);
        this.setMirrored(isMirrored);
        this.setDirection(dir);
        this.setStartBlockpos(pos);
        this.setBlockState(state);
    }

    public float getHRot() {
        this.hRot0 = this.hRot;
        this.hRot = (this.tickCount % HOR_ROT_PERIOD) * (360.0f / HOR_ROT_PERIOD);
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

    //TODO: do damage?
    @Override
    protected void onHitEntity(EntityHitResult pResult) {
    }

    //TODO: if woodpile block is hit, place wood in there immediately
    @Override
    protected void onHitBlock(BlockHitResult result) {
        //TODO: play sound?
        util.spawnDropsPrecise(this.level(), BlockPos.ZERO, result.getLocation(), this.getBlockState().getBlock().asItem().getDefaultInstance());
        this.discard();
    }

    @Override
    protected AABB makeBoundingBox() {
        return DIMENSIONS.makeBoundingBox(this.position());
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
