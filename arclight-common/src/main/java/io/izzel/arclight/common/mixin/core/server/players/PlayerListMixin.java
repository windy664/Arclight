package io.izzel.arclight.common.mixin.core.server.players;

import com.llamalad7.mixinextras.sugar.Local;
import io.izzel.arclight.common.bridge.core.server.players.PlayerListBridge;
import io.izzel.arclight.common.mod.server.ArclightServer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.notifications.NotificationService;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.bukkit.craftbukkit.CraftServer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin implements PlayerListBridge {

    // @formatter:off
    @Override @Accessor("players") @Mutable public abstract void bridge$setPlayers(List<ServerPlayer> players);
    @Override @Accessor("players") public abstract List<ServerPlayer> bridge$getPlayers();
    @Shadow @Final public PlayerDataStorage playerIo;
    @Shadow @Final private UserBanList bans;
    @Shadow @Final private static SimpleDateFormat BAN_DATE_FORMAT;
    @Shadow public abstract boolean isWhiteListed(NameAndId profile);
    @Shadow @Final private IpBanList ipBans;
    @Shadow @Final public List<ServerPlayer> players;
    @Shadow public abstract boolean canBypassPlayerLimit(NameAndId profile);
    @Shadow protected abstract void save(ServerPlayer playerIn);
    @Shadow @Final private MinecraftServer server;
    @Shadow public abstract UserBanList getBans();
    @Shadow public abstract IpBanList getIpBans();
    @Shadow public abstract void sendLevelInfo(ServerPlayer playerIn, ServerLevel worldIn);
    @Shadow public abstract void sendPlayerPermissionLevel(ServerPlayer player);
    @Shadow @Final private Map<UUID, ServerPlayer> playersByUUID;
    @Shadow public abstract void sendAllPlayerInfo(ServerPlayer playerIn);
    @Shadow @Nullable public abstract ServerPlayer getPlayer(UUID playerUUID);
    @Shadow public abstract void broadcastSystemMessage(Component p_240618_, boolean p_240644_);
    @Shadow public abstract void sendActivePlayerEffects(ServerPlayer serverPlayer);
    @Shadow public abstract ServerPlayer respawn(ServerPlayer serverPlayer, boolean bl, Entity.RemovalReason removalReason);
    // @formatter:on

    private CraftServer cserver;

    @Override
    public CraftServer bridge$getCraftServer() {
        return cserver;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void arclight$loadServer(MinecraftServer server, LayeredRegistryAccess registries, PlayerDataStorage playerIo, NotificationService notificationService, CallbackInfo ci) {
        cserver = ArclightServer.createOrLoad((DedicatedServer) server, (PlayerList) (Object) this);
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/UserNameToIdResolver;add(Lnet/minecraft/server/players/NameAndId;)V"))
    private void arclight$renameDetection(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci, @Local String oldName) {
        // CraftBukkit start - Better rename detection
        String lastKnownName = player.getBukkitEntity().getLastKnownName();
        if (lastKnownName != null) {
            oldName = lastKnownName;
        }
        // CraftBukkit end
    }

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundChangeDifficultyPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
    private void arclight$sendSupportedChannels(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        player.getBukkitEntity().sendSupportedChannels(); // CraftBukkit
    }
}
