package io.izzel.arclight.common.mod.util;

import com.mojang.serialization.Lifecycle;
import net.minecraft.CrashReportCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.*;
import net.minecraft.world.level.storage.*;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("all")
public class DelegateWorldInfo  extends PrimaryLevelData {

    private final ServerLevelData serverLevelData;

    public DelegateWorldInfo(ServerLevelData serverLevelData, LevelSettings levelSettings, SpecialWorldProperty specialWorldProperty, Lifecycle lifecycle) {
        super(levelSettings, specialWorldProperty, lifecycle);
        this.serverLevelData = serverLevelData;
    }

    public static DelegateWorldInfo wrap(ServerLevelData worldInfo) {
        return new DelegateWorldInfo(worldInfo, worldSettings(worldInfo), specialWorldProperty(worldInfo), lifecycle(worldInfo));
    }

    private static LevelSettings worldSettings(ServerLevelData data) {
        data = resolveDelegate(data);

        if (data instanceof PrimaryLevelData bridged) {
            return bridged.getLevelSettings();
        }

        if (data instanceof WorldData p) {
            return p.getLevelSettings();
        }

        return new LevelSettings(data.getLevelName(), data.getGameType(), new LevelSettings.DifficultySettings(data.getDifficulty(), data.isHardcore(), data.isDifficultyLocked()), data.isAllowCommands(), WorldDataConfiguration.DEFAULT);
    }

    private static SpecialWorldProperty specialWorldProperty(ServerLevelData data) {
        data = resolveDelegate(data);

        if (data instanceof WorldData d) {
            return (d.isFlatWorld() ?
                    SpecialWorldProperty.FLAT :
                    (d.isDebugWorld() ?
                     SpecialWorldProperty.DEBUG :
                     SpecialWorldProperty.NONE));
        }

        return SpecialWorldProperty.NONE;
    }

    private static Lifecycle lifecycle(ServerLevelData data) {
        data = resolveDelegate(data);
        if (data instanceof PrimaryLevelData bridged) {
            return bridged.worldGenSettingsLifecycle();
        }

        if (data instanceof WorldData p) {
            return p.worldGenSettingsLifecycle();
        }

        return Lifecycle.stable();
    }

    private static ServerLevelData resolveDelegate(ServerLevelData data) {
        if (data instanceof DerivedLevelData bridged) {
            return resolveDelegate(bridged.wrapped);
        }

        return data;
    }

    @Override
    public RespawnData getRespawnData() {
        return serverLevelData.getRespawnData();
    }

    @Override
    public void setSpawn(RespawnData respawnData) {
        serverLevelData.setSpawn(respawnData);
    }

    @Override
    public boolean isAllowCommands() {
        return serverLevelData.isAllowCommands();
    }

    @Override
    public void setAllowCommands(boolean allowCommands) {
        serverLevelData.setAllowCommands(allowCommands);
    }

    @Override
    public long getGameTime() {
        return serverLevelData.getGameTime();
    }

    @Override
    public void setGameTime(long time) {
        serverLevelData.setGameTime(time);
    }

    @Override
    public @NotNull String getLevelName() {
        return serverLevelData.getLevelName();
    }

    @Override
    public @NotNull GameType getGameType() {
        return serverLevelData.getGameType();
    }

    @Override
    public void setGameType(@NotNull GameType type) {
        serverLevelData.setGameType(type);
    }

    @Override
    public boolean isHardcore() {
        return serverLevelData.isHardcore();
    }

    @Override
    public boolean isInitialized() {
        return serverLevelData.isInitialized();
    }

    @Override
    public void setInitialized(boolean initializedIn) {
        serverLevelData.setInitialized(initializedIn);
    }

    @Override
    public @NotNull Difficulty getDifficulty() {
        return serverLevelData.getDifficulty();
    }

    @Override
    public boolean isDifficultyLocked() {
        return serverLevelData.isDifficultyLocked();
    }

    @Override
    public void fillCrashReportCategory(@NotNull CrashReportCategory crashReportCategory, @NotNull LevelHeightAccessor levelHeightAccessor) {
        serverLevelData.fillCrashReportCategory(crashReportCategory, levelHeightAccessor);
    }
}