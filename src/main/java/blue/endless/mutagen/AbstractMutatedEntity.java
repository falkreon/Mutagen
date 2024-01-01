package blue.endless.mutagen;

import org.joml.Vector3f;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AbstractMutatedEntity extends AnimalEntity {
	public static final int STAMINA_CHANGE_COOLDOWN_MAX = 20;
	protected int staminaChangeCooldown = 0;
	protected int mountStamina;
	protected int maxMountStamina = 4;
	protected int idleTicks = 0;

	protected AbstractMutatedEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		
	}

	@Override
	public PassiveEntity createChild(ServerWorld arg0, PassiveEntity arg1) {
		return null;
	}

	
	@SuppressWarnings("resource")
	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ActionResult result = super.interactMob(player, hand);
		if (result != ActionResult.PASS) return result;
		if (this.hasPassengers()) return ActionResult.PASS;
		
		if (!this.getWorld().isClient) {
			player.setYaw(this.getYaw());
			player.setPitch(this.getPitch());
			player.startRiding(this);
			
			return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
		}
		
		return ActionResult.SUCCESS;
	}
	
	@Override
	public LivingEntity getControllingPassenger() {
		Entity rider = this.getFirstPassenger();
		return (rider instanceof LivingEntity livingRider && rider.shouldControlVehicles()) ? livingRider : null;
	}
	
	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		super.tickControlled(controllingPlayer, movementInput);
		idleTicks = 0;
		Vec2f vec2f = this.getControlledRotation(controllingPlayer);
		this.setRotation(vec2f.y, vec2f.x);
		this.bodyYaw = this.headYaw = this.getYaw();
		this.prevYaw = this.headYaw;
		if (this.isLogicalSideForUpdatingMovement()) {
			//TODO: Tick some other movement things!
		}
	}
	
	@SuppressWarnings("resource")
	public void considerDespawn() {
		idleTicks++;
		
		if (this.getWorld().isClient) return;
		if (this.hasCustomName()) return;
		
		if (Config.INSTANCE.mutants.idleTicksBeforeDespawn > 0 && idleTicks > Config.INSTANCE.mutants.idleTicksBeforeDespawn) {
			this.discard();
		}
	}
	
	@Override
	public void tick() {
		considerDespawn();
		super.tick();
	}
	
	@Override
	protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
		//TODO: Honor a lockout period on movement
		//if (this.isOnGround() && this.jumpStrength == 0.0f && this.isAngry() && !this.jumping) {
		//	return Vec3d.ZERO;
		//}
		float sidewaysSpeed = controllingPlayer.sidewaysSpeed * 0.5f;
		float forwardSpeed = controllingPlayer.forwardSpeed;
		
		if (forwardSpeed <= 0.0f) {
			forwardSpeed *= 0.25f;
		}
		return new Vec3d(sidewaysSpeed, 0 /*controllingPlayer.getPitch()*/, forwardSpeed);
	}

	protected Vec2f getControlledRotation(LivingEntity controllingPassenger) {
		return new Vec2f(controllingPassenger.getPitch() * 0.5f, controllingPassenger.getYaw());
	}
	
	
	protected float getPassengerAttachmentY(EntityDimensions dimensions, float scaleFactor) {
		return dimensions.height + (this.isBaby() ? 0.125f : -0.15625f) * scaleFactor;
	}
	
	@Override
	protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
		return this.getMovementSpeed() * 32.0f;
	}
	
	@Override
	protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		return new Vector3f(0.0f, this.getPassengerAttachmentY(dimensions, scaleFactor) + 0.15f * scaleFactor, -0.7f * scaleFactor);
	}
	
	@Override
	public boolean isPushable() {
		return !this.hasPassengers();
	}
	
	/**
	 * 
	 * @param staminaCondition
	 */
	public void tickMountStamina(MountStaminaCondition staminaCondition) {
		switch(staminaCondition) {
			case GAIN -> {
				
				if (staminaChangeCooldown < STAMINA_CHANGE_COOLDOWN_MAX) {
					staminaChangeCooldown++;
					//System.out.println("Stamina gain cd");
				} else {
					staminaChangeCooldown = 0;
					if (mountStamina < maxMountStamina) {
						mountStamina++;
						if (mountStamina > 1) {
							World world = this.getWorld();
							world.playSound(null, getX(), getY(), getZ(), MutagenMod.WHOOSH, SoundCategory.NEUTRAL, 1.0f, 0.2f + (mountStamina * 0.2f), world.getRandom().nextLong());
						}
					}
				}
			}
			case LOSS -> {
				if (staminaChangeCooldown < STAMINA_CHANGE_COOLDOWN_MAX) {
					staminaChangeCooldown++;
					//System.out.println("Stamina loss cd");
				} else {
					staminaChangeCooldown = 0;
					if (mountStamina > 0) mountStamina--;
				}
			}
			case MAINTAIN -> {
				staminaChangeCooldown = 0;
			}
		}
	}
	
	public static enum MountStaminaCondition {
		/** Mount Stamina is going up */
		GAIN,
		/** Mount Stamina is going down */
		LOSS,
		/** Mount Stamina is holding steady */
		MAINTAIN;
	}
}
