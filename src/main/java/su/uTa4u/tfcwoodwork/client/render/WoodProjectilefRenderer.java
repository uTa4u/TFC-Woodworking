package su.uTa4u.tfcwoodwork.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.entity.AbstractWoodProjectile;
import su.uTa4u.tfcwoodwork.entity.LogHalfProjectile;
import su.uTa4u.tfcwoodwork.entity.LogQuarterProjectile;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class WoodProjectilefRenderer extends EntityRenderer<AbstractWoodProjectile> {
    private static final Map<Wood, ResourceLocation> TEXTURE_BY_WOOD;
    private final BlockRenderDispatcher dispatcher;

    static {
        TEXTURE_BY_WOOD = Helpers.mapOfKeys(Wood.class, (wood) -> new ResourceLocation(TFCWoodworking.MOD_ID, "textures/entity/log_half/" + wood.getSerializedName() + ".png"));
    }

    public WoodProjectilefRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowRadius = 0.5F;
        this.dispatcher = pContext.getBlockRenderDispatcher();
    }

    public void render(AbstractWoodProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState blockstate = entity.getBlockState();
        if (blockstate.getRenderShape() == RenderShape.MODEL) {
            Level level = entity.level();
            if (blockstate != level.getBlockState(entity.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                poseStack.pushPose();

                Direction chopperDir = entity.getDirection();
                if (chopperDir == Direction.SOUTH || chopperDir == Direction.NORTH) poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));

                if (entity.getMirrored()) poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
                poseStack.translate(0, -0.3125, 0);

                poseStack.translate(0, 0.5, 0);
                poseStack.mulPose(Axis.XN.rotationDegrees(Mth.rotLerp(partialTicks, entity.getHRot0(), entity.getHRot())));
                poseStack.translate(0, -0.5, 0);

                poseStack.translate(-0.5, 0, -0.5);

                BlockPos blockpos = entity.blockPosition();
                var model = this.dispatcher.getBlockModel(blockstate);
                for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(entity.getStartBlockpos())), net.minecraftforge.client.model.data.ModelData.EMPTY)) {
                    renderType = net.minecraftforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType);
                    this.dispatcher.getModelRenderer().tesselateBlock(level, model, blockstate, blockpos, poseStack, buffer.getBuffer(renderType), false, RandomSource.create(), blockstate.getSeed(entity.getStartBlockpos()), OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.ModelData.EMPTY, renderType);
                }
                poseStack.popPose();
                super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            }
        }
    }

    //Is this even used by the renderer? Idk, but didn't want to return null
    //If it is used, need to handle log_quarter
    @Override
    public ResourceLocation getTextureLocation(AbstractWoodProjectile pEntity) {
        return TEXTURE_BY_WOOD.get(Wood.ACACIA);
    }
}
