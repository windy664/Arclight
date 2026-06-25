package io.izzel.arclight.common.mixin.core.server.level;

import io.izzel.arclight.common.mod.server.ArclightServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.server.level.ServerChunkCache$MainThreadExecutor")
public abstract class ServerChunkCache_MainThreadExecutorMixin extends BlockableEventLoop<Runnable> {


    @Shadow
    @Final
    private ServerChunkCache this$0;

    protected ServerChunkCache_MainThreadExecutorMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public boolean pollTask() {
        try {
            if (this$0.bridge$tickDistanceManager()) {
                return true;
            } else {
                this$0.bridge$getLightManager().tryScheduleUpdate();
                return super.pollTask();
            }
        } finally {
            this$0.chunkMap.bridge$getCallbackExecutor().run();
            // InitAuther97: drain queued tasks when the server thread is waiting for chunks
            // This carries the AsyncCatcher to ensure no timeout unexpectedly.
            ArclightServer.getMinecraftServer().bridge$drainQueuedTasks();
        }
    }
}
