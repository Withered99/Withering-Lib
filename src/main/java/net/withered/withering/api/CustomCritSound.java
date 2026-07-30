package net.withered.withering.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;

public interface CustomCritSound {
    SoundEvent getCritSound(ItemStack stack, PlayerEntity player, Entity target);
}
