package su.uTa4u.tfcwoodwork.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import su.uTa4u.tfcwoodwork.ModTags;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.entities.AbstractWoodProjectile;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class WoodProjectilefRenderer extends EntityRenderer<AbstractWoodProjectile> {
    private final BlockRenderDispatcher dispatcher;

    public WoodProjectilefRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.25F;
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    public void render(AbstractWoodProjectile entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        final var blockstate = entity.getBlockState();

        if (blockstate.getRenderShape() != RenderShape.MODEL) return;

        poseStack.pushPose();

        poseStack.translate(0, -0.3125, 0);
        poseStack.translate(0, 0.5, 0);

        final var isMirrored = entity.getMirrored();
        final var dir = entity.getDirection();
        final var axis = dir.getAxis();
        final var angle = Mth.rotLerp(partialTicks, entity.getHRot0(), entity.getHRot());

        if (blockstate.is(ModTags.Blocks.LOGS_QUARTER)) {
            if (axis == Direction.Axis.X) {
                poseStack.mulPose(mojandAxisFromDir(dir, isMirrored).rotationDegrees(angle));
                if (isMirrored) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
                }
            }
            if (axis == Direction.Axis.Z) {
                poseStack.mulPose(mojandAxisFromDir(dir, !isMirrored).rotationDegrees(angle));
                if (isMirrored) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
                }
            }
            if (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            }
        } else if (blockstate.is(ModTags.Blocks.LOGS_HALF)) {
            if (axis == Direction.Axis.X) {
                poseStack.mulPose(mojandAxisFromDir(dir, isMirrored).rotationDegrees(angle));
            }
            if (axis == Direction.Axis.Z) {
                poseStack.mulPose(mojandAxisFromDir(dir, !isMirrored).rotationDegrees(angle));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
            }
            if (isMirrored) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            }
            if (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            }
        }

        poseStack.translate(0, -0.5, 0);
        poseStack.translate(-0.5, 0, -0.5);

        final var model = this.dispatcher.getBlockModel(blockstate);
        final var startPos = entity.getStartBlockpos();

         for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(startPos)), ModelData.EMPTY)) {
             this.dispatcher.getModelRenderer().tesselateBlock(
                     entity.level(),
                     model,
                     blockstate,
                     entity.blockPosition().above(),
                     poseStack,
                     buffer.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType)),
                     false,
                     RandomSource.create(),
                     blockstate.getSeed(startPos),
                     OverlayTexture.NO_OVERLAY,
                     ModelData.EMPTY,
                     renderType
             );
         }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull AbstractWoodProjectile entity) {
        return TFCWoodworking.getResource("this_is_not_used_so_dont_care");
    }

    private static Axis mojandAxisFromDir(Direction dir, boolean isMirrored) {
        return switch (dir) {
            case DOWN -> isMirrored ? Axis.YP : Axis.YN;
            case UP -> isMirrored ? Axis.YN : Axis.YP;
            case NORTH -> isMirrored ? Axis.ZP : Axis.ZN;
            case SOUTH -> isMirrored ? Axis.ZN : Axis.ZP;
            case WEST -> isMirrored ? Axis.XP : Axis.XN;
            case EAST -> isMirrored ? Axis.XN : Axis.XP;
        };
    }
}
