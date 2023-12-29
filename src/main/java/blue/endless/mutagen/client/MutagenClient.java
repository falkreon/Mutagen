package blue.endless.mutagen.client;

import blue.endless.mutagen.MutagenMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class MutagenClient implements ClientModInitializer {
	public static final EntityModelLayer LAYER_BASE_SALMON = new EntityModelLayer(new Identifier("mutagen", "salmon"), "main");
	
	@Override
	public void onInitializeClient() {
		
		EntityRendererRegistry.register(MutagenMod.SALMON, (context) -> {
			return new SalmonEntityRenderer(context);
		});

		EntityModelLayerRegistry.registerModelLayer(LAYER_BASE_SALMON, SalmonModel::createBodyLayer);
	}
}