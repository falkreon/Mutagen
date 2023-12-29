package blue.endless.mutagen;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SalmonEntity extends AbstractMutatedEntity {
	
	protected SalmonEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		
		this.moveControl = new MutatedSalmonMoveControl(this);
		this.maxMountStamina = 4;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(2, new SwimAroundGoal(this, 1, 40));
		
	}
	
	public static DefaultAttributeContainer.Builder createAttributes() {
		return LivingEntity.createLivingAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 2.0)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0)
				.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
	}
	
	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}
	
	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.55f;
	}
	
	@Override
	public void tickMovement() {
		if (!this.hasControllingPassenger()) this.mountStamina = 0;
		
		if (!this.isTouchingWater() && this.isOnGround() && this.verticalCollision) {
			this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.05f, 0.4f, (this.random.nextFloat() * 2.0f - 1.0f) * 0.05f));
			this.setOnGround(false);
			this.velocityDirty = true;
			//TODO: Flop sound
			//this.playSound(flopSound, this.getSoundVolume(), this.getSoundPitch());
		}
		super.tickMovement();
	}
	
	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		//Stop that. We don't have feet.
	}
	
	@Override
	public void travel(Vec3d movementInput) {
		if (this.isLogicalSideForUpdatingMovement()) {
			if (this.canMoveVoluntarily()) {
				this.updateVelocity(0.04f, movementInput);
				//this.move(MovementType.SELF, this.getVelocity());
			}
			
			this.setVelocity(this.getVelocity().multiply(0.9)); //drag
			
			if (!this.isTouchingWater() && this.mountStamina == 0) this.setVelocity(this.getVelocity().add(0, -LivingEntity.GRAVITY, 0));
		}
		
		this.move(MovementType.SELF, this.getVelocity());
		/*
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.updateVelocity(0.01f, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			if (this.getTarget() == null) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(movementInput);
		}*/
	}
	
	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		super.tickControlled(controllingPlayer, movementInput);
		
		//Vec2f vec2f = this.getControlledRotation(controllingPlayer);
		//this.setRotation(vec2f.y, vec2f.x);
		//this.bodyYaw = this.headYaw = this.prevYaw = this.getYaw();
		//this.serverPitch = this.prevPitch = controllingPlayer.getPitch();
		
		if (Math.abs(movementInput.getZ()) > 0) {
			
			Vec3d lookVec = Vec3d.fromPolar(controllingPlayer.getPitch(), controllingPlayer.getYaw());
			double movementSpeed = movementInput.getZ() * 0.125;
			//TODO: Manage stamina
			if (!this.isSubmergedInWater()) {
				
				tickMountStamina(MountStaminaCondition.LOSS);
				if (mountStamina == 0) {
					movementSpeed *= 0.125;
				}
			} else {
				tickMountStamina((movementSpeed > 0) ? MountStaminaCondition.GAIN : MountStaminaCondition.LOSS);
				this.fallDistance = 0;
			}
			
			this.addVelocity(lookVec.multiply(movementSpeed));
		} else {
			if (this.isTouchingWater()) {
				tickMountStamina(MountStaminaCondition.LOSS);
			} else {
				mountStamina = 0;
			}
			
		}
	}
	
	
	@Override
	protected boolean shouldSwimInFluids() {
		return true;
	}
	
	static class MutatedSalmonMoveControl extends MoveControl {
		private final SalmonEntity fish;

		MutatedSalmonMoveControl(SalmonEntity owner) {
			super(owner);
			this.fish = owner;
		}

		@Override
		public void tick() {
			
			if (this.fish.isSubmergedIn(FluidTags.WATER)) {
				this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0)); //Counteract the small fluid gravity
			}
			if (this.state != MoveControl.State.MOVE_TO || this.fish.getNavigation().isIdle()) {
				//this.fish.setMovementSpeed(0.2f); // Let's keep those gills moving
				return;
			}
			float baseMoveSpeed = (float) (this.speed * this.fish.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
			this.fish.setMovementSpeed(baseMoveSpeed);
			double dx = this.targetX - this.fish.getX();
			//double dy = this.targetY - this.fish.getY();
			double dz = this.targetZ - this.fish.getZ();
			
			//double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
			//double hdist = Math.sqrt(dx * dx + dz * dz);
			float newYaw = (float) (MathHelper.atan2(dz, dx) * 57.2957763671875) - 90.0f;
			this.fish.setYaw(this.wrapDegrees(this.fish.getYaw(), newYaw, 90.0f));
			this.fish.bodyYaw = this.fish.getYaw();
		}
	}
}
