package io.izzel.arclight.common.bridge.core.commands.arguments.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;

public interface EntitySelectorParserBridge {

    default EntitySelector parse(boolean overridePermissions) throws CommandSyntaxException {
       return null;
   }
}
