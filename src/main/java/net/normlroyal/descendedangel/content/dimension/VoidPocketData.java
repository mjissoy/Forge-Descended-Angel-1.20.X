package net.normlroyal.descendedangel.content.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VoidPocketData extends SavedData {
    public static final String DATA_NAME = "descendedangel_void_pockets";

    public static final int HORIZONTAL_RADIUS = 32;
    public static final int HALF_HEIGHT = 24;
    public static final int DEFAULT_REQUIRED_KILLS = 12;

    private final Map<UUID, Pocket> pockets = new HashMap<>();
    private final Map<String, Anchor> anchors = new HashMap<>();
    private int nextSlot = 1;

    public static VoidPocketData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                VoidPocketData::load,
                VoidPocketData::new,
                DATA_NAME
        );
    }

    public static VoidPocketData load(CompoundTag tag) {
        VoidPocketData data = new VoidPocketData();
        data.nextSlot = Math.max(1, tag.getInt("NextSlot"));

        ListTag pocketTags = tag.getList("Pockets", Tag.TAG_COMPOUND);
        for (int i = 0; i < pocketTags.size(); i++) {
            Pocket pocket = Pocket.load(pocketTags.getCompound(i));
            if (pocket != null) {
                data.pockets.put(pocket.id, pocket);
            }
        }

        ListTag anchorTags = tag.getList("Anchors", Tag.TAG_COMPOUND);
        for (int i = 0; i < anchorTags.size(); i++) {
            Anchor anchor = Anchor.load(anchorTags.getCompound(i));
            if (anchor != null) {
                data.anchors.put(anchor.key(), anchor);
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("NextSlot", nextSlot);

        ListTag pocketTags = new ListTag();
        for (Pocket pocket : pockets.values()) {
            pocketTags.add(pocket.save());
        }
        tag.put("Pockets", pocketTags);

        ListTag anchorTags = new ListTag();
        for (Anchor anchor : anchors.values()) {
            anchorTags.add(anchor.save());
        }
        tag.put("Anchors", anchorTags);

        return tag;
    }

    public Pocket createPocket(ServerPlayer player, @Nullable Anchor returnAnchor) {
        UUID id = UUID.randomUUID();
        int slot = nextSlot++;
        BlockPos center = new BlockPos(slot * 256, 64, 0);

        double returnX = player.getX();
        double returnY = player.getY();
        double returnZ = player.getZ();
        @Nullable BlockPos returnAnchorPos = null;

        if (returnAnchor != null) {
            returnAnchorPos = returnAnchor.pos;
            returnX = returnAnchor.pos.getX() + 0.5D;
            returnY = returnAnchor.pos.getY() + 1.0D;
            returnZ = returnAnchor.pos.getZ() + 0.5D;
        }

        Pocket pocket = new Pocket(
                id,
                slot,
                center,
                player.level().dimension(),
                returnX,
                returnY,
                returnZ,
                player.getYRot(),
                player.getXRot(),
                returnAnchorPos,
                false,
                false,
                false,
                0,
                DEFAULT_REQUIRED_KILLS,
                player.level().getGameTime(),
                player.getUUID()
        );

        pockets.put(id, pocket);
        setDirty();
        return pocket;
    }

    public Optional<Pocket> getPocket(UUID id) {
        return Optional.ofNullable(pockets.get(id));
    }

    public Optional<Pocket> findPocketAt(BlockPos pos) {
        return pockets.values().stream()
                .filter(pocket -> pocket.contains(pos))
                .findFirst();
    }

    public Collection<Pocket> pockets() {
        return pockets.values();
    }

    public List<Anchor> anchorsFor(UUID playerId) {
        List<Anchor> visible = new ArrayList<>();
        for (Anchor anchor : anchors.values()) {
            if (anchor.owner == null || anchor.owner.equals(playerId)) {
                visible.add(anchor);
            }
        }
        visible.sort(Comparator
                .comparing((Anchor anchor) -> anchor.dimension.location().toString())
                .thenComparingInt(anchor -> anchor.pos.getX())
                .thenComparingInt(anchor -> anchor.pos.getY())
                .thenComparingInt(anchor -> anchor.pos.getZ()));
        return visible;
    }

    public boolean isSameAnchor(Anchor anchor, ResourceKey<Level> dimension, BlockPos pos) {
        return anchor.dimension.equals(dimension) && anchor.pos.equals(pos);
    }

    public List<Anchor> anchorsInPocket(Pocket pocket) {
        List<Anchor> result = new ArrayList<>();
        for (Anchor anchor : anchors.values()) {
            if (anchor.dimension.equals(ModDimensions.VOID_POCKET_LEVEL) && pocket.contains(anchor.pos)) {
                result.add(anchor);
            }
        }
        return result;
    }

    public void removePocket(UUID id) {
        pockets.remove(id);
        anchors.values().forEach(anchor -> {
            if (id.equals(anchor.pocketId)) {
                anchor.pocketId = null;
            }
        });
        setDirty();
    }

    public Anchor registerAnchor(ResourceKey<Level> dimension, BlockPos pos, @Nullable UUID owner) {
        String key = anchorKey(dimension, pos);
        Anchor anchor = anchors.get(key);
        if (anchor == null) {
            anchor = new Anchor(dimension, pos.immutable(), owner, null, defaultAnchorName(dimension, pos));
            anchors.put(key, anchor);
        } else {
            if (owner != null && anchor.owner == null) {
                anchor.owner = owner;
            }
            if (anchor.name == null || anchor.name.isBlank()) {
                anchor.name = defaultAnchorName(dimension, pos);
            }
        }
        setDirty();
        return anchor;
    }

    public void removeAnchor(ResourceKey<Level> dimension, BlockPos pos) {
        Anchor removed = anchors.remove(anchorKey(dimension, pos));
        if (removed != null && removed.pocketId != null) {
            pockets.values().forEach(pocket -> {
                if (removed.pocketId.equals(pocket.id) && pos.equals(pocket.returnAnchorPos)) {
                    pocket.returnAnchorPos = null;
                }
            });
        }
        setDirty();
    }

    public Optional<Anchor> getAnchor(ResourceKey<Level> dimension, BlockPos pos) {
        return Optional.ofNullable(anchors.get(anchorKey(dimension, pos)));
    }

    public Optional<Anchor> findNearestAnchor(ResourceKey<Level> dimension, BlockPos center, int radius, UUID playerId) {
        int radiusSqr = radius * radius;
        return anchors.values().stream()
                .filter(anchor -> anchor.dimension.equals(dimension))
                .filter(anchor -> anchor.owner == null || anchor.owner.equals(playerId))
                .filter(anchor -> anchor.pos.distSqr(center) <= radiusSqr)
                .min((a, b) -> Double.compare(a.pos.distSqr(center), b.pos.distSqr(center)));
    }

    public void linkReturnAnchor(Pocket pocket) {
        if (pocket.returnAnchorPos == null) {
            return;
        }

        getAnchor(pocket.returnDimension, pocket.returnAnchorPos).ifPresent(anchor -> {
            anchor.pocketId = pocket.id;
            setDirty();
        });
    }

    private static String anchorKey(ResourceKey<Level> dimension, BlockPos pos) {
        return dimension.location() + ";" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static class Pocket {
        public final UUID id;
        public final int slot;
        public final BlockPos center;
        public final ResourceKey<Level> returnDimension;
        public final double returnX;
        public final double returnY;
        public final double returnZ;
        public final float returnYaw;
        public final float returnPitch;
        @Nullable
        public BlockPos returnAnchorPos;
        public boolean preserved;
        public boolean completed;
        public boolean generated;
        public int kills;
        public final int requiredKills;
        public final long createdGameTime;
        public final UUID owner;

        public Pocket(
                UUID id,
                int slot,
                BlockPos center,
                ResourceKey<Level> returnDimension,
                double returnX,
                double returnY,
                double returnZ,
                float returnYaw,
                float returnPitch,
                @Nullable BlockPos returnAnchorPos,
                boolean preserved,
                boolean completed,
                boolean generated,
                int kills,
                int requiredKills,
                long createdGameTime,
                UUID owner
        ) {
            this.id = id;
            this.slot = slot;
            this.center = center;
            this.returnDimension = returnDimension;
            this.returnX = returnX;
            this.returnY = returnY;
            this.returnZ = returnZ;
            this.returnYaw = returnYaw;
            this.returnPitch = returnPitch;
            this.returnAnchorPos = returnAnchorPos;
            this.preserved = preserved;
            this.completed = completed;
            this.generated = generated;
            this.kills = kills;
            this.requiredKills = requiredKills;
            this.createdGameTime = createdGameTime;
            this.owner = owner;
        }

        public BlockPos entryPos() {
            return center.offset(0, 7, 0);
        }

        public AABB bounds() {
            return new AABB(
                    center.getX() - HORIZONTAL_RADIUS,
                    center.getY() - HALF_HEIGHT,
                    center.getZ() - HORIZONTAL_RADIUS,
                    center.getX() + HORIZONTAL_RADIUS + 1,
                    center.getY() + HALF_HEIGHT + 1,
                    center.getZ() + HORIZONTAL_RADIUS + 1
            );
        }

        public boolean contains(BlockPos pos) {
            return pos.getX() >= center.getX() - HORIZONTAL_RADIUS
                    && pos.getX() <= center.getX() + HORIZONTAL_RADIUS
                    && pos.getY() >= center.getY() - HALF_HEIGHT
                    && pos.getY() <= center.getY() + HALF_HEIGHT
                    && pos.getZ() >= center.getZ() - HORIZONTAL_RADIUS
                    && pos.getZ() <= center.getZ() + HORIZONTAL_RADIUS;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("Id", id);
            tag.putInt("Slot", slot);
            writeBlockPos(tag, "Center", center);
            tag.putString("ReturnDimension", returnDimension.location().toString());
            tag.putDouble("ReturnX", returnX);
            tag.putDouble("ReturnY", returnY);
            tag.putDouble("ReturnZ", returnZ);
            tag.putFloat("ReturnYaw", returnYaw);
            tag.putFloat("ReturnPitch", returnPitch);
            tag.putBoolean("Preserved", preserved);
            tag.putBoolean("Completed", completed);
            tag.putBoolean("Generated", generated);
            tag.putInt("Kills", kills);
            tag.putInt("RequiredKills", requiredKills);
            tag.putLong("CreatedGameTime", createdGameTime);
            tag.putUUID("Owner", owner);
            if (returnAnchorPos != null) {
                writeBlockPos(tag, "ReturnAnchor", returnAnchorPos);
            }
            return tag;
        }

        @Nullable
        public static Pocket load(CompoundTag tag) {
            ResourceLocation returnDimensionId = ResourceLocation.tryParse(tag.getString("ReturnDimension"));
            if (returnDimensionId == null || !tag.hasUUID("Id") || !tag.hasUUID("Owner")) {
                return null;
            }

            BlockPos returnAnchor = tag.contains("ReturnAnchorX") ? readBlockPos(tag, "ReturnAnchor") : null;

            return new Pocket(
                    tag.getUUID("Id"),
                    tag.getInt("Slot"),
                    readBlockPos(tag, "Center"),
                    ResourceKey.create(Registries.DIMENSION, returnDimensionId),
                    tag.getDouble("ReturnX"),
                    tag.getDouble("ReturnY"),
                    tag.getDouble("ReturnZ"),
                    tag.getFloat("ReturnYaw"),
                    tag.getFloat("ReturnPitch"),
                    returnAnchor,
                    tag.getBoolean("Preserved"),
                    tag.getBoolean("Completed"),
                    tag.getBoolean("Generated"),
                    tag.getInt("Kills"),
                    Math.max(1, tag.getInt("RequiredKills")),
                    tag.getLong("CreatedGameTime"),
                    tag.getUUID("Owner")
            );
        }
    }

    public static class Anchor {
        public final ResourceKey<Level> dimension;
        public final BlockPos pos;
        @Nullable
        public UUID owner;
        @Nullable
        public UUID pocketId;
        public String name;

        public Anchor(ResourceKey<Level> dimension, BlockPos pos, @Nullable UUID owner, @Nullable UUID pocketId, String name) {
            this.dimension = dimension;
            this.pos = pos;
            this.owner = owner;
            this.pocketId = pocketId;
            this.name = name;
        }

        public String key() {
            return anchorKey(dimension, pos);
        }

        public String displayName() {
            if (name != null && !name.isBlank()) {
                return name;
            }
            return defaultAnchorName(dimension, pos);
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Dimension", dimension.location().toString());
            writeBlockPos(tag, "Pos", pos);
            tag.putString("Name", displayName());
            if (owner != null) {
                tag.putUUID("Owner", owner);
            }
            if (pocketId != null) {
                tag.putUUID("PocketId", pocketId);
            }
            return tag;
        }

        @Nullable
        public static Anchor load(CompoundTag tag) {
            ResourceLocation dimensionId = ResourceLocation.tryParse(tag.getString("Dimension"));
            if (dimensionId == null) {
                return null;
            }

            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, dimensionId);
            BlockPos pos = readBlockPos(tag, "Pos");
            UUID owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
            UUID pocketId = tag.hasUUID("PocketId") ? tag.getUUID("PocketId") : null;
            String name = tag.contains("Name", Tag.TAG_STRING) ? tag.getString("Name") : defaultAnchorName(dimension, pos);

            return new Anchor(dimension, pos, owner, pocketId, name);
        }
    }

    private static String defaultAnchorName(ResourceKey<Level> dimension, BlockPos pos) {
        String path = dimension.location().getPath();
        String prettyDimension = switch (path) {
            case "overworld" -> "Overworld";
            case "the_nether" -> "Nether";
            case "the_end" -> "End";
            case "void_pocket" -> "Void Pocket";
            default -> path.replace('_', ' ');
        };
        return prettyDimension + " Anchor " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }

    private static void writeBlockPos(CompoundTag tag, String prefix, BlockPos pos) {
        tag.putInt(prefix + "X", pos.getX());
        tag.putInt(prefix + "Y", pos.getY());
        tag.putInt(prefix + "Z", pos.getZ());
    }

    private static BlockPos readBlockPos(CompoundTag tag, String prefix) {
        return new BlockPos(
                tag.getInt(prefix + "X"),
                tag.getInt(prefix + "Y"),
                tag.getInt(prefix + "Z")
        );
    }
}
