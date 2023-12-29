package blue.endless.mutagen.client;

import blue.endless.mutagen.SalmonEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class SalmonModel extends EntityModel<SalmonEntity> {
	public static final String BODY = EntityModelPartNames.BODY;
	public static final String HEAD = EntityModelPartNames.HEAD;
	
	public static final String JAW = EntityModelPartNames.JAW;
	
	public static final String LEFT_FIN = EntityModelPartNames.LEFT_FIN;
	public static final String RIGHT_FIN = EntityModelPartNames.RIGHT_FIN;
	public static final String DORSAL_FIN = EntityModelPartNames.TOP_FIN;
	public static final String PELVIC_FIN = EntityModelPartNames.BOTTOM_FIN;
	//We've skipped two fins to keep model complexity down
	
	public static final String TAIL1 = EntityModelPartNames.TAIL + "_base";
	public static final String TAIL2 = EntityModelPartNames.TAIL;
	public static final String TAIL3 = EntityModelPartNames.TAIL_FIN;
	
	private final ModelPart body;
	//private final ModelPart head;
	
	private final ModelPart leftFin;
	private final ModelPart rightFin;
	
	private final ModelPart tail1;
	private final ModelPart tail2;

	public SalmonModel(ModelPart root) {
		this.body = root.getChild(BODY);
		//this.head = body.getChild(HEAD);
		this.leftFin = body.getChild(LEFT_FIN);
		this.rightFin = body.getChild(RIGHT_FIN);
		this.tail1 = body.getChild(TAIL1);
		this.tail2 = tail1.getChild(TAIL2);
	}

	public static TexturedModelData createBodyLayer() {
		ModelData meshdefinition = new ModelData();
		ModelPartData partdefinition = meshdefinition.getRoot();

		ModelPartData body = partdefinition.addChild(BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -10.0F, -4.0F, 16.0F, 12.0F, 31.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, -4.0F));

		body.addChild(RIGHT_FIN, ModelPartBuilder.create().uv(39, 28).cuboid(0.0F, -5.0F, 0.0F, 0.0F, 10.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -4.0F, -3.0F, 0.0F, -0.2182F, 0.0F));
		body.addChild(LEFT_FIN, ModelPartBuilder.create().uv(39, 28).cuboid(0.0F, -5.0F, 0.0F, 0.0F, 10.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(8.0F, -4.0F, -3.0F, 0.0F, 0.2182F, 0.0F));

		body.addChild(DORSAL_FIN, ModelPartBuilder.create().uv(30, 65).cuboid(0.0F, -7.0F, -4.0F, 0.0F, 9.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.0F, 1.0F, 0.1745F, 0.0F, 0.0F));
		body.addChild(PELVIC_FIN, ModelPartBuilder.create().uv(0, 57).cuboid(-0.5F, -1.0F, 8.0F, 0.0F, 8.0F, 15.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, -1.0F, 3.0F, -0.1309F, 0.0F, 0.0F));

		ModelPartData head = body.addChild(
				HEAD,
				ModelPartBuilder.create()
					.uv(58, 43).cuboid(-6.0F, -6.0F, -12.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F))
					.uv(50, 67).cuboid(-6.0F, -2.0F, -20.0F, 12.0F, 4.0F, 8.0F, new Dilation(0.0F)),
				ModelTransform.pivot(0.0F, -4.0F, -4.0F));
		
		head.addChild(JAW, ModelPartBuilder.create().uv(58, 79).cuboid(-5.0F, 0.0F, -8.0F, 10.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, -12.0F, 0.3491F, 0.0F, 0.0F));
		

		ModelPartData tail1 = body.addChild(TAIL1, ModelPartBuilder.create().uv(0, 43).cuboid(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 19.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -4.0F, 27.0F));
		ModelPartData tail2 = tail1.addChild(TAIL2, ModelPartBuilder.create().uv(63, 0).cuboid(-3.0F, -4.0F, 0.0F, 6.0F, 8.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 19.0F));
		tail2.addChild(TAIL3, ModelPartBuilder.create().uv(0, 69).cuboid(0.0F, -6.0F, 0.0F, 0.0F, 13.0F, 11.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 16.0F));

		return TexturedModelData.of(meshdefinition, 128, 128);
	}

	@Override
	public void setAngles(SalmonEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		//TODO: Implement
	}
	
	@Override
	public void animateModel(SalmonEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		float age = entity.age + tickDelta;
		
		float finSwingRadians = (float) Math.sin(0.1 * age) * 0.3f + (float) (Math.PI / 5);
		float tailSwingRadians = (float) Math.sin(0.1 * age - (2.5 * (Math.PI/4))) * 0.5f;
		float tail2SwingRadians = (float) Math.sin(0.1 * age - (3.7 * (Math.PI/4))) * 0.35f;
		
		leftFin.setAngles(0, finSwingRadians, 0);
		rightFin.setAngles(0, -finSwingRadians, 0);
		
		tail1.setAngles(0, tailSwingRadians, 0);
		tail2.setAngles(0, tail2SwingRadians, 0);
		
		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
	}

	@Override
	public void render(MatrixStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
