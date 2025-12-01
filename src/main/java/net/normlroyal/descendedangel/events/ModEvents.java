package net.normlroyal.descendedangel.events;

import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.normlroyal.descendedangel.DescendedAngel;
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

        float bonusMultiplier = 1.0F + (0.1F * tier);
        event.setAmount(event.getAmount() * bonusMultiplier);
    }
}