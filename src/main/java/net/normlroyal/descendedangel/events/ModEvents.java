package net.normlroyal.descendedangel.events;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.normlroyal.descendedangel.DescendedAngel;
import net.normlroyal.descendedangel.config.ModConfigs;
import net.normlroyal.descendedangel.item.ModItems;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = DescendedAngel.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        LivingEntity target = event.getEntity();
        if (target.getMobType() != MobType.UNDEAD) return;

        var opt = CuriosApi.getCuriosHelper()
                .findFirstCurio(player, stack -> stack.getItem() instanceof TieredHaloItem);

        if (opt.isEmpty()) return;

        ItemStack haloStack = opt.get().stack();
        TieredHaloItem haloItem = (TieredHaloItem) haloStack.getItem();
        int tier = haloItem.getTier();

        double dmgPerTier = ModConfigs.COMMON.HALO_UNDEAD_DAMAGE_BONUS_PER_TIER.get();
        float bonusMultiplier = 1.0F + (float)(dmgPerTier * tier);

        event.setAmount(event.getAmount() * bonusMultiplier);
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }
        var opt = CuriosApi.getCuriosHelper()
                .findFirstCurio(player, stack -> stack.getItem() instanceof TieredHaloItem);

        if (opt.isEmpty()) {
            return;
        }

        TieredHaloItem halo = (TieredHaloItem) opt.get().stack().getItem();
        int tier = halo.getTier();


        double healPerTier = ModConfigs.COMMON.HALO_HEAL_BONUS_PER_TIER.get();
        float healMultiplier = 1.0F + (float)(healPerTier * tier);

        float original = event.getAmount();
        event.setAmount(original * healMultiplier);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide()) return;

        if (!(event.getSource().getEntity() instanceof Player)) return;

        if (!(entity instanceof Mob mob)) return;
        if (mob.getType().getCategory() != MobCategory.MONSTER) return;

        double dropChance = ModConfigs.COMMON.VOID_TEAR_DROP_CHANCE.get();
        if (entity.getRandom().nextDouble() < dropChance) {
            ItemStack stack = new ItemStack(ModItems.VOIDTEAR.get());

            ItemEntity drop = new ItemEntity(
                    level,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    stack
            );

            event.getDrops().add(drop);
        }
    }
}