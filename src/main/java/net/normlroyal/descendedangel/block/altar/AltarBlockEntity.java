package net.normlroyal.descendedangel.block.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.normlroyal.descendedangel.block.ModBlockEntities;
import net.normlroyal.descendedangel.item.custom.TieredHaloItem;
import net.normlroyal.descendedangel.menu.AltarMenu;
import net.normlroyal.descendedangel.recipe.AltarRiteRecipe;
import net.normlroyal.descendedangel.recipe.ModRecipeTypes;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public class AltarBlockEntity extends BlockEntity implements MenuProvider, GeoAnimatable {

    public static final int RING_START = 0;
    public static final int RING_COUNT = 8;
    public static final int CORE_SLOT  = 8;
    public static final int HALO_SLOT  = 9;
    public static final int SLOT_COUNT = 10;

    private final ItemStackHandler items = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (level != null && !level.isClientSide) {
                updateRiteUiFromInputs();
            }
        }
    };

    private void updateRiteUiFromInputs() {
        if (level == null) return;

        if (crafting) {
            setRiteUiIfChanged(STATE_NONE, Component.literal("Crafting..."));
            return;
        }

        SimpleContainer c = buildRecipeContainer();

        var recipeOpt = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.ALTAR_RITE.get(), c, level);

        if (recipeOpt.isEmpty()) {
            if (riteState != STATE_NONE ||
                    !startButtonText.getString().equals("Insert Items")) {
                setRiteUiIfChanged(STATE_NONE, Component.literal("Insert Items"));
            }
            return;
        }

        AltarRiteRecipe recipe = recipeOpt.get();

        int required = recipe.requiredHaloTier();
        if (required > 0) {
            int authorityTier = getAuthorityTierForRite();
            if (authorityTier < required) {
                setRiteUiIfChanged(STATE_HALO_BAD, Component.literal("Halo Rank Insufficient"));
                return;
            }
        }
        setRiteUiIfChanged(STATE_CAN_START, Component.literal("Start ").append(recipe.displayComponent()));
    }

    private SimpleContainer buildRecipeContainer() {
        SimpleContainer c = new SimpleContainer(9);
        for (int i = 0; i < 8; i++) {
            c.setItem(i, items.getStackInSlot(i));
        }
        c.setItem(8, items.getStackInSlot(CORE_SLOT));
        return c;
    }

    private LazyOptional<IItemHandler> itemCap = LazyOptional.empty();

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALTAR.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.descendedangel.altar");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AltarMenu(id, inv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", items.serializeNBT());
        tag.putBoolean("Crafting", crafting);
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        if (activeRecipeId != null) tag.putString("ActiveRecipe", activeRecipeId.toString());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Items")) {
            items.deserializeNBT(tag.getCompound("Items"));
        }
        crafting = tag.getBoolean("Crafting");
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        activeRecipeId = tag.contains("ActiveRecipe") ? new ResourceLocation(tag.getString("ActiveRecipe")) : null;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        itemCap = LazyOptional.of(() -> items);
        if (level != null && !level.isClientSide) {
            updateRiteUiFromInputs();}
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(
            net.minecraftforge.common.capabilities.Capability<T> cap,
            @Nullable Direction side
    ) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    private boolean crafting = false;
    private int progress = 0;
    private int maxProgress = 0;

    @Nullable private ResourceLocation activeRecipeId = null;

    public boolean isCrafting() { return crafting; }
    public int getProgress() { return progress; }
    public int getMaxProgress() { return maxProgress; }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("RiteState", riteState);
        tag.putString("StartBtn", Component.Serializer.toJson(startButtonText));
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);

        this.clientRiteState = tag.getInt("RiteState");

        if (tag.contains("StartBtn")) {
            Component c = Component.Serializer.fromJson(tag.getString("StartBtn"));
            this.startButtonText = (c == null) ? Component.empty() : c;
        } else {
            this.startButtonText = Component.empty();
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }

    public static final int STATE_NONE = 0;
    public static final int STATE_CAN_START = 1;
    public static final int STATE_HALO_BAD = 2;

    private int riteState = STATE_NONE;
    private int clientRiteState = STATE_NONE;
    private Component startButtonText = Component.empty();

    public int getRiteState() {
        if (level != null && level.isClientSide) return clientRiteState;
        return riteState;
    }
    public void setRiteState(int state) {
        this.riteState = state;
    }
    public Component getStartButtonText() { return startButtonText; }

    private void setRiteUiIfChanged(int state, Component text) {
        Component safe = (text == null) ? Component.empty() : text;

        if (this.riteState == state && Component.Serializer.toJson(this.startButtonText)
                .equals(Component.Serializer.toJson(safe))) {
            return;
        }

        this.riteState = state;
        this.startButtonText = safe;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }


    public void tryStartRite(ServerPlayer player) {
        if (level == null || level.isClientSide) return;
        if (crafting) return;

        SimpleContainer c = buildRecipeContainer();

        var recipeOpt = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.ALTAR_RITE.get(), c, level);

        if (recipeOpt.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cNo matching altar recipe"));
            return;
        }

        AltarRiteRecipe recipe = recipeOpt.get();

        int required = recipe.requiredHaloTier();
        if (required > 0) {
            int authorityTier = getAuthorityTierForRite();
            if (authorityTier < required) {
                setRiteUiIfChanged(STATE_HALO_BAD, Component.literal("Halo Rank Insufficient"));
                return;
            }
        }

        this.crafting = true;
        this.progress = 0;
        this.maxProgress = Math.max(1, durationTicksForRequiredTier(required));
        this.activeRecipeId = recipe.getId();

        updateRiteUiFromInputs();
        setChanged();
    }

    private final AnimatableInstanceCache geoCache =
            GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }

    private static int durationTicksForRequiredTier(int requiredTier) {
        if (requiredTier <= 3) return 20 * 5;
        if (requiredTier <= 6) return 20 * 10;
        return 20 * 15;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AltarBlockEntity be) {
        if (level.isClientSide) return;
        if (!be.crafting) return;

        be.progress++;

        if (be.progress >= be.maxProgress) {
            be.finishRite();
        } else {
            be.setChanged();
        }
    }

    private void finishRite() {
        if (level == null) return;

        if (activeRecipeId == null) {
            crafting = false;
            updateRiteUiFromInputs();
            return;
        }

        AltarRiteRecipe recipe = level.getRecipeManager()
                .byKey(activeRecipeId)
                .filter(r -> r instanceof AltarRiteRecipe)
                .map(r -> (AltarRiteRecipe) r)
                .orElse(null);

        if (recipe == null) {
            crafting = false;
            updateRiteUiFromInputs();
            return;
        }

        for (int i = 0; i < 8; i++) {
            ItemStack stack = items.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            ItemStack remainder = stack.getCraftingRemainingItem();
            stack.shrink(1);

            if (stack.isEmpty()) {
                items.setStackInSlot(i, remainder.isEmpty() ? ItemStack.EMPTY : remainder.copy());
            }
        }

        ItemStack core = items.getStackInSlot(CORE_SLOT);
        if (!core.isEmpty()) {
            core.shrink(1);
            if (core.isEmpty()) {
                items.setStackInSlot(CORE_SLOT, ItemStack.EMPTY);
            }
        }
        ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
        items.setStackInSlot(CORE_SLOT, result);

        crafting = false;
        progress = 0;
        maxProgress = 0;
        activeRecipeId = null;

        updateRiteUiFromInputs();

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }


    public void setClientRiteState(int state) {
        this.clientRiteState = state;
    }

    private static int haloTierOf(ItemStack stack) {
        if (stack.getItem() instanceof TieredHaloItem h) return h.getTier();
        return 0;
    }

    private int getAuthorityTierForRite() {
        int slotTier = haloTierOf(items.getStackInSlot(HALO_SLOT));
        int coreTier = haloTierOf(items.getStackInSlot(CORE_SLOT));
        return Math.max(slotTier, coreTier);
    }

}
