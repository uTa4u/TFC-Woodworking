package su.uTa4u.tfcwoodwork.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import su.uTa4u.tfcwoodwork.TFCWoodworking;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES;

    public static final RegistryObject<EntityType<LogHalfProjectile>> LOG_HALF_PROJ;
    public static final RegistryObject<EntityType<LogQuarterProjectile>> LOG_QUARTER_PROJ;

    static {
        ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TFCWoodworking.MOD_ID);

        LOG_HALF_PROJ = registerEntityType("log_half_proj", LogHalfProjectile::new);
        LOG_QUARTER_PROJ = registerEntityType("log_quarter_proj", LogQuarterProjectile::new);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String name, EntityType.EntityFactory<T> factory) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, MobCategory.MISC).sized(0.375f, 0.375f).build(name));
    }
}