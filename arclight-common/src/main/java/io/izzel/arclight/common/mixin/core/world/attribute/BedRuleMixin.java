package io.izzel.arclight.common.mixin.core.world.attribute;

import io.izzel.arclight.common.bridge.core.world.attribute.BedRuleBridge;
import io.izzel.arclight.common.mod.mixins.annotation.CreateConstructor;
import io.izzel.arclight.common.mod.mixins.annotation.ShadowConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.attribute.BedRule;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(BedRule.class)
public class BedRuleMixin implements BedRuleBridge {

    @ShadowConstructor
    public void arclight$constructor(BedRule.Rule canSleep, BedRule.Rule canSetSpawn, boolean explodes, Optional<Component> errorMessage) {
        throw new RuntimeException();
    }

    private PlayerBedEnterEvent.BedEnterResult bukkit;

    @CreateConstructor
    public void arclight$constructor(BedRule.Rule canSleep, BedRule.Rule canSetSpawn, boolean explodes, Optional<Component> errorMessage, PlayerBedEnterEvent.BedEnterResult bukkit) {
        arclight$constructor(canSleep, canSetSpawn, explodes, errorMessage);
        bridge$pushBedEnterResult(bukkit);
        if (((BedRule) (Object) this) == BedRule.CAN_SLEEP_WHEN_DARK) {
            bridge$pushBedEnterResult(PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_NOW);
        } else if (((BedRule) (Object) this) == BedRule.EXPLODES) {
            bridge$pushBedEnterResult(PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_HERE);
        }
    }

    @Override
    public void bridge$pushBedEnterResult(PlayerBedEnterEvent.BedEnterResult bukkit) {
        this.bukkit = bukkit;
    }

    @Override
    public PlayerBedEnterEvent.BedEnterResult bukkit() {
        return bukkit;
    }
}
