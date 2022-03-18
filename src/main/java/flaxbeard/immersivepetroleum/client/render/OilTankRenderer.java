package flaxbeard.immersivepetroleum.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import blusunrize.immersiveengineering.client.utils.GuiHelper;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity.Port;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity.PortState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class OilTankRenderer implements BlockEntityRenderer<OilTankTileEntity>{
	@Override
	public boolean shouldRenderOffScreen(OilTankTileEntity te){
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(OilTankTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		if(te != null && te.formed && te.getLevelNonnull().hasChunkAt(te.getBlockPos())){
			combinedOverlay = OverlayTexture.NO_OVERLAY;
			
			matrix.pushPose();
			{
				switch(te.getFacing()){
					case EAST:{
						matrix.mulPose(new Quaternion(0, 270F, 0, true));
						matrix.translate(0, 0, -1);
						break;
					}
					case SOUTH:{
						matrix.mulPose(new Quaternion(0F, 180F, 0F, true));
						matrix.translate(-1, 0, -1);
						break;
					}
					case WEST:{
						matrix.mulPose(new Quaternion(0, 90F, 0, true));
						matrix.translate(-1, 0, 0);
						break;
					}
					default:break;
				}
				
				matrix.pushPose();
				{
					if(te.getIsMirrored()){
						// Tank Display
						if(te.posInMultiblock.equals(new BlockPos(1, 2, 5))){
							// Background
							Matrix4f mat = matrix.last().pose();
							VertexConsumer builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_POSITION_COLOR);
							builder.vertex(mat, 1.5F, -0.5F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.vertex(mat, 1.5F, 1F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.vertex(mat, 0F, 1F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.vertex(mat, 0F, -0.5F, 0.995F).color(34, 34, 34, 255).endVertex();
							
							OilTankTileEntity master = te.master();
							if(master != null){
								FluidStack fs = master.tank.getFluid();
								if(!fs.isEmpty()){
									matrix.pushPose();
									{
										matrix.translate(0.25, 0.875, 0.9975F);
										matrix.scale(0.0625F, -0.0625F, 0.0625F);
										
										float h = fs.getAmount() / (float) master.tank.getCapacity();
										GuiHelper.drawRepeatedFluidSprite(buffer.getBuffer(RenderType.solid()), matrix, fs, 0, 0 + (1 - h) * 16, 16, h * 16);
									}
									matrix.popPose();
								}
							}
						}
						
						// Dynamic Fluid IO Ports
						OilTankTileEntity master = te.master();
						if(master != null){
							for(Port port:Port.DYNAMIC_PORTS){
								if(port.matches(te.posInMultiblock)){
									matrix.pushPose();
									matrix.mulPose(new Quaternion(0, 180F, 0, true));
									matrix.translate(-1, 0, -1);
									quad(matrix, buffer, master.getPortStateFor(port), port.posInMultiblock.getX() == 4, combinedLight, combinedOverlay);
									matrix.popPose();
									break;
								}
							}
						}
					}else{
						// Tank Display
						if(te.posInMultiblock.equals(new BlockPos(3, 2, 5))){
							// Background
							Matrix4f mat = matrix.last().pose();
							VertexConsumer builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_POSITION_COLOR);
							builder.vertex(mat, 1.5F, -0.5F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.vertex(mat, 1.5F, 1F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.vertex(mat, 0F, 1F, 0.995F).color(34, 34, 34, 255).endVertex();
							builder.vertex(mat, 0F, -0.5F, 0.995F).color(34, 34, 34, 255).endVertex();
							
							OilTankTileEntity master = te.master();
							if(master != null){
								FluidStack fs = master.tank.getFluid();
								if(!fs.isEmpty()){
									matrix.pushPose();
									{
										matrix.translate(0.25, 0.875, 0.9975F);
										matrix.scale(0.0625F, -0.0625F, 0.0625F);
										
										float h = fs.getAmount() / (float) master.tank.getCapacity();
										GuiHelper.drawRepeatedFluidSprite(buffer.getBuffer(RenderType.solid()), matrix, fs, 0, 0 + (1 - h) * 16, 16, h * 16);
									}
									matrix.popPose();
								}
							}
						}
						
						// Dynamic Fluid IO Ports
						OilTankTileEntity master = te.master();
						if(master != null){
							for(Port port:Port.DYNAMIC_PORTS){
								if(port.matches(te.posInMultiblock)){
									matrix.pushPose();
									quad(matrix, buffer, master.getPortStateFor(port), port.posInMultiblock.getX() == 4, combinedLight, combinedOverlay);
									matrix.popPose();
									break;
								}
							}
						}
					}
				}
				matrix.popPose();
			}
			matrix.popPose();
		}
	}
	
	public void quad(PoseStack matrix, MultiBufferSource buffer, PortState portState, boolean flip, int combinedLight, int combinedOverlay){
		Matrix4f mat = matrix.last().pose();
		VertexConsumer builder = buffer.getBuffer(IPRenderTypes.OIL_TANK);
		
		boolean input = portState == PortState.INPUT;
		float u0 = input ? 0.0F : 0.1F, v0 = 0.5F;
		float u1 = u0 + 0.1F, v1 = v0 + 0.1F;
		if(flip){
			builder.vertex(mat, 1.001F, 0F, 0F).color(1F, 1F, 1F, 1F).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, 1.001F, 1F, 0F).color(1F, 1F, 1F, 1F).uv(u1, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, 1.001F, 1F, 1F).color(1F, 1F, 1F, 1F).uv(u0, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, 1.001F, 0F, 1F).color(1F, 1F, 1F, 1F).uv(u0, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
		}else{
			builder.vertex(mat, -0.001F, 0F, 0F).color(1F, 1F, 1F, 1F).uv(u0, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, -0.001F, 0F, 1F).color(1F, 1F, 1F, 1F).uv(u1, v1).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, -0.001F, 1F, 1F).color(1F, 1F, 1F, 1F).uv(u1, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
			builder.vertex(mat, -0.001F, 1F, 0F).color(1F, 1F, 1F, 1F).uv(u0, v0).overlayCoords(combinedOverlay).uv2(combinedLight).normal(1, 1, 1).endVertex();
		}
	}
}
