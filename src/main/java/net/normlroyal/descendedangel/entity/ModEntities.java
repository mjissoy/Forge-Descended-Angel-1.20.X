package net.normlroyal.descendedangel.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "descendedangel");

    public static final RegistryObject<EntityType<VoidAnomalyEntity>> VOID_ANOMALY =
            ENTITY_TYPES.register("void_anomaly",
                    () -> EntityType.Builder
                            .of(VoidAnomalyEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("descendedangel:void_anomaly"));

    public static final RegistryObject<EntityType<ImpEntity>> IMP =
            ENTITY_TYPES.register("imp",
                    () -> EntityType.Builder
                            .of(ImpEntity::new, MobCategory.MONSTER)
                            .sized(0.375F, 0.8125F)
                            .clientTrackingRange(16)
                            .build("descendedangel:imp"));
}
