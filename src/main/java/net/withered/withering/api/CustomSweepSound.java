package net.withered.withering.api;

import net.minecraft.sound.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;

public interface CustomSweepSound {
    SoundEvent getSweepSound(ItemStack stack, PlayerEntity player, Entity target);
}
