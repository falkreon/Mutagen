package blue.endless.mutagen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class MutagenMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Mutagen");
	
	public static SoundEvent WHOOSH;
	public static SoundEvent MUTATE;
	public static SoundEvent FAIL_TO_MUTATE;
	public static EntityType<SalmonEntity> SALMON;
	public static MutagenStatus MUTAGEN_STATUS;
	public static Potion MUTAGEN_POTION;
	
	@Override
	public void onInitialize() {
		
		WHOOSH = Registry.register(Registries.SOUND_EVENT, new Identifier("mutagen", "whoosh"), SoundEvent.of(new Identifier("mutagen", "whoosh")));
		MUTATE = Registry.register(Registries.SOUND_EVENT, new Identifier("mutagen", "mutate"), SoundEvent.of(new Identifier("mutagen", "mutate")));
		FAIL_TO_MUTATE = Registry.register(Registries.SOUND_EVENT, new Identifier("mutagen", "fail_to_mutate"), SoundEvent.of(new Identifier("mutagen", "fail_to_mutate")));
		
		SALMON = Registry.register(
					Registries.ENTITY_TYPE,
					new Identifier("mutagen", "salmon"),
					FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, SalmonEntity::new)
						.dimensions(EntityDimensions.fixed(2.0f, 0.75f)).build()
			);
		FabricDefaultAttributeRegistry.register(SALMON, AbstractMutatedEntity.createMobAttributes());
		
		MUTAGEN_STATUS = Registry.register(Registries.STATUS_EFFECT, new Identifier("mutagen", "mutagen"), new MutagenStatus());
		MUTAGEN_POTION = Registry.register(Registries.POTION, new Identifier("mutagen", "mutagen"), new Potion(new StatusEffectInstance(MUTAGEN_STATUS)));
		
		BrewingRecipeRegistry.registerPotionRecipe(Potions.THICK, Items.SPIDER_EYE, MUTAGEN_POTION);
	}
}