package net.withered.withering.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;

public interface CustomSweepParticle {
    ParticleEffect getSweepParticle(ItemStack stack, PlayerEntity player);
}