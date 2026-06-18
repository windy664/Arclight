package io.izzel.arclight.common.mixin.core.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EggItem.class)
public class EggItemMixin {

    /**
     * @author wdog5734
     * @reason Bukkit patch
     */
    @Overwrite
    public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        // level.playSound((Entity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (level instanceof ServerLevel serverLevel) {
            // CraftBukkit start
            if (Projectile.spawnProjectileFromRotation(ThrownEgg::new, serverLevel, itemStack, player, 0.0F, 1.5F, 1.0F).isRemoved()) {
                if (player instanceof net.minecraft.server.level.ServerPlayer) {
                    ((net.minecraft.server.level.ServerPlayer) player).getBukkitEntity().updateInventory();
                }
                return InteractionResult.FAIL;
            }
            // CraftBukkit end
        }
        player.awardStat(Stats.ITEM_USED.get(((EggItem) (Object) this)));
        itemStack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

}
