package net.withered.withering.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.withered.withering.api.CustomCritSound;
import net.withered.withering.api.CustomSweepParticle;
import net.withered.withering.api.CustomSweepSound;
import net.withered.withering.api.PersistentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void preventDrop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (stack != null && stack.getItem() instanceof PersistentItem) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if (player.isAlive()) {
                player.getInventory().insertStack(stack.copy());
            }
            cir.setReturnValue(null);
        }
    }

    @ModifyArg(
            method = "attack(Lnet/minecraft/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"
            ),
            index = 4
    )
    private SoundEvent modifyAttackSounds(SoundEvent originalSound) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack weapon = player.getMainHandStack();

        if (originalSound == SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP) {
            if (!weapon.isEmpty() && weapon.getItem() instanceof CustomSweepSound customWeapon) {
                SoundEvent customSound = customWeapon.getSweepSound(weapon, player, null);
                if (customSound != null) {
                    return customSound;
                }
            }
        }

        if (originalSound == SoundEvents.ENTITY_PLAYER_ATTACK_CRIT) {
            if (!weapon.isEmpty() && weapon.getItem() instanceof CustomCritSound customWeapon) {
                SoundEvent customSound = customWeapon.getCritSound(weapon, player, null);
                if (customSound != null) {
                    return customSound;
                }
            }
        }

        return originalSound;
    }

    @Inject(
            method = "spawnSweepAttackParticles()V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifySweepParticles(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        ItemStack stack = player.getMainHandStack();

        if (!stack.isEmpty() && stack.getItem() instanceof CustomSweepParticle custom) {
            ParticleEffect particle = custom.getSweepParticle(stack, player);

            if (particle != null && player.getWorld() instanceof ServerWorld serverWorld) {
                double d = (double)(-MathHelper.sin(player.getYaw() * 0.017453292F));
                double e = (double)MathHelper.cos(player.getYaw() * 0.017453292F);
                serverWorld.spawnParticles(
                        particle,
                        player.getX() + d,
                        player.getBodyY(0.5),
                        player.getZ() + e,
                        0,
                        d,
                        0.0,
                        e,
                        0.0
                );

                ci.cancel();
            }
        }
    }
}