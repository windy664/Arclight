package io.izzel.arclight.fabric.mod.permission;

import io.izzel.arclight.i18n.ArclightConfig;
import net.fabricmc.fabric.api.permission.v1.PermissionContext;
import net.fabricmc.fabric.api.permission.v1.PermissionEvents;
import net.fabricmc.fabric.api.permission.v1.PermissionNode;

public class ArclightPermissionImpl {
    public static void init() {
        if (!ArclightConfig.spec().getCompat().isForwardPermission()) {
            return;
        }

        PermissionEvents.ON_REQUEST.register(ArclightPermissionImpl::handlePermission);

        // Fixme: Bukkit didn't support offline player's permission.
//        OfflinePermissionCheckEvent.EVENT.register((uuid, permission) -> {
//            return CompletableFuture.completedFuture(TriState.FALSE);
//        });
    }

    private static <T> T handlePermission(PermissionContext ctx, PermissionNode<T> perm) {
        final Boolean result;
        query: {
            final var stack = ctx.get(PermissionContext.COMMAND_SOURCE_STACK);
            if (stack != null) {
                final var sender = stack.getBukkitSender();
                if (sender != null) {
                    result = sender.hasPermission(perm.toString());
                    break query;
                }
            }
            final var entity = ctx.get(PermissionContext.ENTITY);
            if (entity != null) {
                result = entity.getBukkitEntity().hasPermission(perm.toString());
            } else {
                result = null;
            }
        }
        try {
            return perm.cast(result);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
