package blue.endless.mutagen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class MutagenStatus extends InstantStatusEffect {

	protected MutagenStatus() {
		super(StatusEffectCategory.NEUTRAL, 0xFF_66FF66);
	}
	
	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		applyEffect(entity);
	}
	
	@Override
	public void applyInstantEffect(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity) {
		applyEffect(target);
	}
	
	public void applyEffect(LivingEntity target) {
		System.out.println("Applying effect to " + target.getClass().getSimpleName());
		
		if (target instanceof net.minecraft.entity.passive.SalmonEntity) {
			World world = target.getWorld();
			SalmonEntity replacement = MutagenMod.SALMON.create(world);
			
			//Copy important properties over to the new mob
			replacement.refreshPositionAndAngles(target.getBlockPos(), target.getYaw(), target.getPitch());
			if (target.hasCustomName()) {
				replacement.setCustomName(target.getCustomName());
			}
			
			world.playSound(null, target.getX(), target.getY(), target.getZ(), MutagenMod.MUTATE, SoundCategory.NEUTRAL, 0.7f, 1.0f, world.getRandom().nextLong());
			target.discard();
			world.spawnEntity(replacement);
		} else {
			target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(), MutagenMod.FAIL_TO_MUTATE, SoundCategory.NEUTRAL, 0.7f, 1.0f, target.getWorld().getRandom().nextLong());
			
		}
	}
}
