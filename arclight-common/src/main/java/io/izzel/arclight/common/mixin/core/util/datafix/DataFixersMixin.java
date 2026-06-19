package io.izzel.arclight.common.mixin.core.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V1458;
import net.minecraft.util.filefix.FileFixerUpper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
public class DataFixersMixin {

    @Inject(method = "addFixers", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/datafix/fixes/EntityCustomNameToComponentFix;<init>(Lcom/mojang/datafixers/schemas/Schema;)V"))
    private static void arclight$fixPlayerName(DataFixerBuilder fixerUpper, FileFixerUpper.Builder fileFixerUpper, CallbackInfo ci) {
        // CraftBukkit start
        fixerUpper.addFixer(new com.mojang.datafixers.DataFix(fixerUpper.addSchema(1458, V1458::new), false) {
            @Override
            protected com.mojang.datafixers.TypeRewriteRule makeRule() {
                return this.fixTypeEverywhereTyped("Player CustomName", this.getInputSchema().getType(References.PLAYER), (typed) -> {
                    return typed.update(DSL.remainderFinder(), (dynamic) -> {
                        String s = dynamic.get("CustomName").asString("");

                        return s.isEmpty() ? dynamic.remove("CustomName") : dynamic.set("CustomName", LegacyComponentDataFixUtils.createPlainTextComponent(dynamic.getOps(), s));
                    });
                });
            }
        });
        // CraftBukkit end
    }
}
