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

public class AltarBlockEntity extends BlockEntity implements MenuProvider, GeoAnimatable {

    public static final int RING_START = 0;
    public static final int RING_COUNT = 8;
    public static final int CORE_SLOT  = 8;
    public static final int HALO_SLOT  = 9;
    public static final int SLOT_COUNT = 10;

    // Inventory

    private final ItemStackHandler items = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (level != null && !level.isClientSide && !crafting) {
                updateRiteUiFromInputs();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private void updateRiteUiFromInputs() {
        if (crafting) {
            if (riteState != STATE_NONE || !startButtonText.getString().equals("Crafting...")) {
                setRiteUi(STATE_NONE, Component.literal("Crafting..."));
            }
            return;
        }

        if (level == null) return;

        SimpleContainer c = buildRecipeContainer();

        System.out.println("---- ALTAR INPUTS ----");
        for (int i = 0; i < 8; i++) {
            System.out.println("ring[" + i + "] = " + items.getStackInSlot(i));
        }
        System.out.println("core = " + items.getStackInSlot(CORE_SLOT));
        System.out.println("----------------------");

        var recipeOpt = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.ALTAR_RITE.get(), c, level);

        if (recipeOpt.isEmpty()) {
            if (riteState != STATE_NONE ||
                    !startButtonText.getString().equals("Insert Items")) {
                setRiteUi(STATE_NONE, Component.literal("Insert Items"));
            }
            return;
        }

        AltarRiteRecipe recipe = recipeOpt.get();

        int required = recipe.requiredHaloTier();
        if (required > 0) {
            int authorityTier = getAuthorityTierForRite();
            if (authorityTier < required) {
                setRiteUi(STATE_HALO_BAD, Component.literal("Halo Rank Insufficient"));
                return;
            }
        }
        setRiteUi(STATE_CAN_START, Component.literal("Start ").append(recipe.displayComponent()));
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
        tag.putBoolean("Crafting", crafting);
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        if (activeRecipeId != null) tag.putString("ActiveRecipe", activeRecipeId.toString());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);

        this.riteState = tag.getInt("RiteState");
        this.crafting = tag.getBoolean("Crafting");
        this.progress = tag.getInt("Progress");
        this.maxProgress = tag.getInt("MaxProgress");
        this.activeRecipeId = tag.contains("ActiveRecipe")
                ? new ResourceLocation(tag.getString("ActiveRecipe"))
                : null;

        if (tag.contains("StartBtn")) {
            Component c = Component.Serializer.fromJson(tag.getString("StartBtn"));
            this.startButtonText = (c == null) ? Component.empty() : c;
        } else {
            this.startButtonText = Component.empty();
        }
    }

    public static final int STATE_NONE = 0;
    public static final int STATE_CAN_START = 1;
    public static final int STATE_HALO_BAD = 2;

    private int riteState = STATE_NONE;
    private Component startButtonText = Component.empty();

    public int getRiteState() { return riteState; }
    public Component getStartButtonText() { return startButtonText; }

    public void setRiteUi(int state, Component text) {
        this.riteState = state;
        this.startButtonText = (text == null) ? Component.empty() : text;

        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void tryStartRite(ServerPlayer player) {
        if (level == null || level.isClientSide) return;
        if (crafting) return; // already running

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
                setRiteUi(STATE_HALO_BAD, Component.literal("Halo Rank Insufficient"));
                return;
            }
        }

        this.crafting = true;
        this.progress = 0;
        this.maxProgress = Math.max(1, durationTicksForRequiredTier(required));
        this.activeRecipeId = recipe.getId();

        updateRiteUiFromInputs();
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
            if (!stack.isEmpty()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    items.setStackInSlot(i, ItemStack.EMPTY);
                }
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

    public void setClientProgress(int p) { this.progress = p; }
    public void setClientMaxProgress(int m) { this.maxProgress = m; }
    public void setClientCrafting(boolean c) { this.crafting = c; }

    private boolean hasRequiredHalo(int requiredTier) {
        if (requiredTier <= 0) return true;

        ItemStack halo = items.getStackInSlot(HALO_SLOT);
        if (!(halo.getItem() instanceof TieredHaloItem h)) return false;

        return h.getTier() >= requiredTier;
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
