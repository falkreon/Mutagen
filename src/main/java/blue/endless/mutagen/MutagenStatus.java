package blue.endless.mutagen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class MutagenStatus extends StatusEffect {

	protected MutagenStatus() {
		super(StatusEffectCategory.NEUTRAL, 0xFF_66FF66);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void applyInstantEffect(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity) {
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
