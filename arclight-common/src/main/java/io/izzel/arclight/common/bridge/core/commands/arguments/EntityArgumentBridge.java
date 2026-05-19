package io.izzel.arclight.common.bridge.core.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;

public interface EntityArgumentBridge {

    default EntitySelector parse(StringReader reader, boolean allowSelectors, boolean overridePermissions) throws CommandSyntaxException {
        return null;
    }
}
