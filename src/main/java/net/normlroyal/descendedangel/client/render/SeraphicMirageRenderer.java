package net.normlroyal.descendedangel.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.normlroyal.descendedangel.entity.SeraphicMirageEntity;

import java.util.UUID;

public class SeraphicMirageRenderer extends MobRenderer<SeraphicMirageEntity, PlayerModel<SeraphicMirageEntity>> {
    public SeraphicMirageRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.25F);
    }

    @Override
    public ResourceLocation getTextureLocation(SeraphicMirageEntity entity) {
        UUID owner = entity.getOwnerUUID();

        if (owner != null && Minecraft.getInstance().getConnection() != null) {
            PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(owner);

            if (info != null) {
                return info.getSkinLocation();
            }

            return DefaultPlayerSkin.getDefaultSkin(owner);
        }

        return DefaultPlayerSkin.getDefaultSkin(entity.getUUID());
    }

    @Override
    protected RenderType getRenderType(
            SeraphicMirageEntity entity,
            boolean bodyVisible,
            boolean translucent,
            boolean glowing
    ) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }
}