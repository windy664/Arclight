package io.izzel.arclight.common.mixin.core.server.dedicated;

import com.mojang.datafixers.DataFixer;
import io.izzel.arclight.common.bridge.core.server.dedicated.DedicatedServerBridge;
import io.izzel.arclight.common.mod.server.ArclightServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin extends MinecraftServer implements DedicatedServerBridge {

    // @formatter:off
    @Shadow @Final public RconConsoleSource rconConsoleSource;
    // @formatter:on

    public DedicatedServerMixin(Thread serverThread, LevelStorageSource.LevelStorageAccess storageSource, PackRepository packRepository, WorldStem worldStem, Optional<GameRules> gameRules, Proxy proxy, DataFixer fixerUpper, Services services, LevelLoadListener levelLoadListener, boolean propagatesCrashes) {
        super(serverThread, storageSource, packRepository, worldStem, gameRules, proxy, fixerUpper, services, levelLoadListener, propagatesCrashes);
    }

    @Inject(method = "initServer", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/dedicated/DedicatedServer;setPlayerList(Lnet/minecraft/server/players/PlayerList;)V"))
    public void arclight$loadPlugins(CallbackInfoReturnable<Boolean> cir) {
        this.bridge$forge$unlockRegistries();
        ((CraftServer) Bukkit.getServer()).loadPlugins();
        ((CraftServer) Bukkit.getServer()).enablePlugins(PluginLoadOrder.STARTUP);
        this.bridge$forge$lockRegistries();
    }

    @Redirect(method = "handleConsoleInputs", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V"))
    private void arclight$serverCommandEvent(Commands commands, CommandSourceStack source, String command) {
        if (command.isEmpty()) {
            return;
        }
        ServerCommandEvent event = new ServerCommandEvent(this.bridge$getConsole(), command);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.bridge$getServer().dispatchServerCommand(this.bridge$getConsole(), new ConsoleInput(event.getCommand(), source));
        }
    }

    @Inject(method = "onServerExit", at = @At("RETURN"))
    public void arclight$exitNow(CallbackInfo ci) {
        bridge$platform$exitNow();
        Thread exitThread = new Thread(this::arclight$exit, "Exit Thread");
        exitThread.setDaemon(true);
        exitThread.start();
    }

    private void arclight$exit() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> threads = new ArrayList<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (!thread.isDaemon() && !thread.getName().equals("DestroyJavaVM")) {
                threads.add(thread.getName());
            }
        }
        if (!threads.isEmpty()) {
            ArclightServer.LOGGER.debug("Threads {} not shutting down", String.join(", ", threads));
            ArclightServer.LOGGER.info("{} threads not shutting down correctly, force exiting", threads.size());
        }
        System.exit(0);
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public String getPluginNames() {
        StringBuilder result = new StringBuilder();
        org.bukkit.plugin.Plugin[] plugins = this.bridge$getServer().getPluginManager().getPlugins();

        result.append(this.bridge$getServer().getName());
        result.append(" on Bukkit ");
        result.append(this.bridge$getServer().getBukkitVersion());

        if (plugins.length > 0 && this.bridge$getServer().getQueryPlugins()) {
            result.append(": ");

            for (int i = 0; i < plugins.length; i++) {
                if (i > 0) {
                    result.append("; ");
                }

                result.append(plugins[i].getDescription().getName());
                result.append(" ");
                result.append(plugins[i].getDescription().getVersion().replaceAll(";", ","));
            }
        }

        return result.toString();
    }

    @Override
    public WorldLoader.DataLoadContext arclight$dataLoadContext() {
        return this.bridge$getWorldLoader();
    }

    @Override
    public void arclight$forceUpgradeIfNeeded(LevelStorageSource.LevelStorageAccess worldSession, RegistryAccess.Frozen dimensions) {
        if (this.bridge$getOptions().has("forceUpgrade")) {
            net.minecraft.server.Main.forceUpgrade(worldSession, DataFixers.getDataFixer(), this.bridge$getOptions().has("eraseCache"), () -> true, dimensions, this.bridge$getOptions().has("recreateRegionFiles"));
        }
    }
}
