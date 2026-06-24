package net.normlroyal.descendedangel.potions;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, DescendedAngel.MOD_ID);

    public static final RegistryObject<Potion> DIVINE_GRACE =
            POTIONS.register("divine_grace",
                    () -> new Potion(new MobEffectInstance(ModEffects.DIVINE_GRACE.get(), 1, 0)));

    public static final RegistryObject<Potion> STRONG_DIVINE_GRACE =
            POTIONS.register("strong_divine_grace",
                    () -> new Potion("divine_grace",
                            new MobEffectInstance(ModEffects.DIVINE_GRACE.get(), 1, 1)));

    public static final RegistryObject<Potion> PROVIDENCE =
            POTIONS.register("providence",
                    () -> new Potion(new MobEffectInstance(ModEffects.PROVIDENCE.get(), 20 * 180, 0)));

    public static final RegistryObject<Potion> LONG_PROVIDENCE =
            POTIONS.register("long_providence",
                    () -> new Potion("providence",
                            new MobEffectInstance(ModEffects.PROVIDENCE.get(), 20 * 480, 0)));

    public static final RegistryObject<Potion> STRONG_PROVIDENCE =
            POTIONS.register("strong_providence",
                    () -> new Potion("providence",
                            new MobEffectInstance(ModEffects.PROVIDENCE.get(), 20 * 120, 1)));

    public static final RegistryObject<Potion> LOCUST_SWARM =
            POTIONS.register("locust_swarm",
                    () -> new Potion(new MobEffectInstance(ModEffects.LOCUST_SWARM.get(), 20 * 30, 0)));

    public static final RegistryObject<Potion> LONG_LOCUST_SWARM =
            POTIONS.register("long_locust_swarm",
                    () -> new Potion("locust_swarm",
                            new MobEffectInstance(ModEffects.LOCUST_SWARM.get(), 20 * 60, 0)));

    public static final RegistryObject<Potion> STRONG_LOCUST_SWARM =
            POTIONS.register("strong_locust_swarm",
                    () -> new Potion("locust_swarm",
                            new MobEffectInstance(ModEffects.LOCUST_SWARM.get(), 20 * 22, 1)));

    public static final RegistryObject<Potion> BLESSED_FAVOUR =
            POTIONS.register("blessed_favour",
                    () -> new Potion(new MobEffectInstance(ModEffects.BLESSED_FAVOUR.get(), 20 * 60, 0)));

    public static final RegistryObject<Potion> LONG_BLESSED_FAVOUR =
            POTIONS.register("long_blessed_favour",
                    () -> new Potion("blessed_favour",
                            new MobEffectInstance(ModEffects.BLESSED_FAVOUR.get(), 20 * 120, 0)));
}