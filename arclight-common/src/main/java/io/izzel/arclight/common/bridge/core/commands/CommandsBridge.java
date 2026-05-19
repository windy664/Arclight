package io.izzel.arclight.common.bridge.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;

import java.util.Map;
import java.util.function.Function;

public interface CommandsBridge {

    default void dispatchServerCommand(CommandSourceStack sender, String command) {

    }

    default void performPrefixedCommand(CommandSourceStack sender, String command, String label) {

    }

    default void performCommand(ParseResults<CommandSourceStack> command, String commandString, String label) {

    }

    default <S, T> void bridge$forge$mergeNode(CommandNode<S> sourceNode, CommandNode<T> resultNode,
                                       Map<CommandNode<S>, CommandNode<T>> sourceToResult,
                                       S canUse, Command<T> execute,
                                       Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion) {

    }
}
