package io.izzel.arclight.common.mixin.core.world.level.chunk.storage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Mixin(RegionFile.class)
public abstract class RegionFileMixin {

    // @formatter:off
    @Shadow private static int getSectorNumber(int i) { return 0; }
    @Shadow @Final private FileChannel file;
    // @formatter:on

    @WrapOperation(method = "<init>(Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Ljava/nio/file/Path;Ljava/nio/file/Path;Lnet/minecraft/world/level/chunk/storage/RegionFileVersion;Z)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/storage/RegionFile;getNumSectors(I)I"))
    private int arclight$extendMaxLen(int offset, Operation<Integer> original) throws IOException {
        int len = original.call(offset);
        if (len == 255) {
            // We're maxed out, so we need to read the proper length from the section
            ByteBuffer realLen = ByteBuffer.allocate(4);
            this.file.read(realLen, getSectorNumber(offset) * 4096L);
            len = (realLen.getInt(0) + 4) / 4096 + 1;
        }
        return len;
    }

    @WrapOperation(method = "getChunkDataInputStream",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/storage/RegionFile;getNumSectors(I)I"))
    private int arclight$extendMaxLen2(int offset, Operation<Integer> original) throws IOException {
        int len = original.call(offset);
        if (len == 255) {
            // We're maxed out, so we need to read the proper length from the section
            ByteBuffer realLen = ByteBuffer.allocate(4);
            this.file.read(realLen, getSectorNumber(offset) * 4096L);
            len = (realLen.getInt(0) + 4) / 4096 + 1;
        }
        return len;
    }
}
