package net.normlroyal.descendedangel.events.useful;

import net.minecraft.world.entity.LivingEntity;

public final class WingRenderContext {
    private static final ThreadLocal<LivingEntity> ENTITY = new ThreadLocal<>();
    public static void setEntity(LivingEntity e) { ENTITY.set(e); }
    public static LivingEntity getEntity() { return ENTITY.get(); }
    public static void clear() { ENTITY.remove(); }
}