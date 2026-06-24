package net.normlroyal.descendedangel.content.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.content.potions.custom.BlessedFavourEffect;
import net.normlroyal.descendedangel.content.potions.custom.DivineGraceEffect;
import net.normlroyal.descendedangel.content.potions.custom.LocustSwarmEffect;
import net.normlroyal.descendedangel.content.potions.custom.ProvidenceEffect;


public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, DescendedAngel.MOD_ID);

    public static final RegistryObject<MobEffect> DIVINE_GRACE =
            MOB_EFFECTS.register("divine_grace", DivineGraceEffect::new);

    public static final RegistryObject<MobEffect> PROVIDENCE =
            MOB_EFFECTS.register("providence", ProvidenceEffect::new);

    public static final RegistryObject<MobEffect> LOCUST_SWARM =
            MOB_EFFECTS.register("locust_swarm", LocustSwarmEffect::new);

    public static final RegistryObject<MobEffect> BLESSED_FAVOUR =
            MOB_EFFECTS.register("blessed_favour", BlessedFavourEffect::new);
}