package io.izzel.arclight.common.mixin.core.world.level.storage;

import com.mojang.serialization.Lifecycle;
import io.izzel.arclight.common.bridge.core.world.level.storage.PrimaryLevelDataBridge;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements PrimaryLevelDataBridge {

    @Shadow
    public LevelSettings settings;

    @Shadow
    public abstract boolean isDifficultyLocked();

    @Shadow
    public abstract Difficulty getDifficulty();

    @Shadow
    @Final
    private Lifecycle worldGenSettingsLifecycle;
    // CraftBukkit start - Add world and pdc
    public Registry<LevelStem> customDimensions;
    private ServerLevel world;
    protected Tag pdc;

    @Override
    public void setWorld(ServerLevel world) {
        if (this.world != null) {
            return;
        }
        this.world = world;
        world.getWorld().readBukkitValues(pdc);
        pdc = null;
    }
    // CraftBukkit end

    @Inject(method = "setTagData", at = @At("TAIL"))
    private void arclight$putBukkitValues(CompoundTag tag, UUID singlePlayerUUID, CallbackInfo ci) {
        tag.putString("Bukkit.Version", Bukkit.getName() + "/" + Bukkit.getVersion() + "/" + Bukkit.getBukkitVersion()); // CraftBukkit
        world.getWorld().storeBukkitValues(tag); // CraftBukkit - add pdc
    }

    @Inject(method = "setDifficulty", at = @At("RETURN"))
    private void arclight$sendDiffChange(Difficulty difficulty, CallbackInfo ci) {
        ClientboundChangeDifficultyPacket packet = new ClientboundChangeDifficultyPacket(this.getDifficulty(), this.isDifficultyLocked());
        for (Player player : this.world.players()) {
            ((ServerPlayer) player).connection.send(packet);
        }
    }

    // CraftBukkit start - Check if the name stored in NBT is the correct one
    @Override
    public void checkName(String name) {
        if (!this.settings.levelName.equals(name)) {
            this.settings.levelName = name;
        }
    }
    // CraftBukkit end

    @Override
    public ServerLevel bridge$getWorld() {
        return world;
    }

    @Override
    public void arclight$offerCustomDimensions(Registry<LevelStem> registry) {
        this.customDimensions = registry;
    }

    @Override
    public LevelSettings bridge$getWorldSettings() {
        return this.settings;
    }

    @Override
    public Lifecycle bridge$getLifecycle() {
        return this.worldGenSettingsLifecycle;
    }
}
