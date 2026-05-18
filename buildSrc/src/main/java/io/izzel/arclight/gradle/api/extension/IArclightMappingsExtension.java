package io.izzel.arclight.gradle.api.extension;

import java.io.File;

public interface IArclightMappingsExtension {
    File getBukkitToNeoForge();
    void setBukkitToNeoForge(File bukkitToNeoForge);

    File getBukkitToFabric();
    void setBukkitToFabric(File bukkitToFabric);

    File getBukkitToFabricInheritance();
    void setBukkitToFabricInheritance(File bukkitToFabricInheritance);

    File getReobfBukkitPackage();
    void setReobfBukkitPackage(File reobfBukkitPackage);

    default boolean areMappingsExist() {
        return getBukkitToNeoForge().exists()
                && getBukkitToFabric().exists()
                && getBukkitToFabricInheritance().exists()
                && getReobfBukkitPackage().exists();
    }
}
