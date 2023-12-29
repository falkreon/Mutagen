package blue.endless.mutagen.client;

import blue.endless.mutagen.SalmonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SalmonEntityRenderer extends MobEntityRenderer<SalmonEntity, SalmonModel> {

	public SalmonEntityRenderer(Context context) {
		super(context, new SalmonModel(context.getPart(MutagenClient.LAYER_BASE_SALMON)), 0.5f);
	}

	@Override
	public Identifier getTexture(SalmonEntity entity) {
		return new Identifier("mutagen", "textures/entity/salmon/salmon.png");
	}

}
