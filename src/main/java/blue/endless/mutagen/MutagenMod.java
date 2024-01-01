package blue.endless.mutagen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
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
		
		//Grab the config if it exists
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();
		Path configFile = FabricLoader.getInstance().getConfigDir().resolve("mutagen.json");
		if (Files.exists(configFile)) {
			try {
				Config.INSTANCE = gson.fromJson(Files.readString(configFile), Config.class);
			} catch (JsonSyntaxException | IOException e) {
				LOGGER.error("Could not read config file.", e);
			}
		} else {
			try {
				Config.INSTANCE = new Config();
				Files.writeString(configFile, gson.toJson(Config.INSTANCE, Config.class));
			} catch (IOException e) {
				LOGGER.error("Could not write default config file.", e);
			}
		}
	}
}