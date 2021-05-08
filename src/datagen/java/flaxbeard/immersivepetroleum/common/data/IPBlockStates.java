package flaxbeard.immersivepetroleum.common.data;

import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.blockstates.ConnectorBlockBuilder;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.metal.AutoLubricatorBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.FlarestackBlock;
import flaxbeard.immersivepetroleum.common.blocks.metal.GasGeneratorBlock;
import flaxbeard.immersivepetroleum.common.multiblocks.CokerUnitMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.DistillationTowerMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.HydroTreaterMultiblock;
import flaxbeard.immersivepetroleum.common.multiblocks.PumpjackMultiblock;
import flaxbeard.immersivepetroleum.common.util.fluids.IPFluid;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IPBlockStates extends BlockStateProvider{
	/** ResourceLocation("forge","obj") */
//	private static final ResourceLocation FORGE_LOADER = new ResourceLocation("forge", "obj");
	
	final ExistingFileHelper exFileHelper;
	public IPBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper){
		super(gen, ImmersivePetroleum.MODID, exFileHelper);
		this.exFileHelper = exFileHelper;
	}
	
	@Override
	protected void registerStatesAndModels(){
		// Dummy Oil Ore
		ModelFile dummyOilOreModel = cubeAll(IPContent.Blocks.dummyOilOre);
		getVariantBuilder(IPContent.Blocks.dummyOilOre).partialState()
			.setModels(new ConfiguredModel(dummyOilOreModel));
		itemModelWithParent(IPContent.Blocks.dummyOilOre, dummyOilOreModel);
		
		// Dummy Pipe
		ModelFile dummyPipeModel = new ExistingModelFile(modLoc("block/dummy_pipe"), this.exFileHelper);
		getVariantBuilder(IPContent.Blocks.dummyPipe).partialState()
			.setModels(new ConfiguredModel(dummyPipeModel));
		itemModelWithParent(IPContent.Blocks.dummyPipe, dummyPipeModel);
		
		// Dummy Conveyor
		ModelFile dummyConveyorModel = new ExistingModelFile(modLoc("block/dummy_conveyor"), this.exFileHelper);
		getVariantBuilder(IPContent.Blocks.dummyConveyor).partialState()
			.setModels(new ConfiguredModel(dummyConveyorModel));
		getItemBuilder(IPContent.Blocks.dummyConveyor)
			.parent(dummyConveyorModel)
			.texture("particle", new ResourceLocation(ImmersiveEngineering.MODID, "block/conveyor/conveyor"));
		
		// Multiblocks
		distillationtower();
		pumpjack();
		cokerunit();
		hydrotreater();
		
		// "Normal" Blocks
		simpleBlockWithItem(IPContent.Blocks.asphalt);
		simpleBlockWithItem(IPContent.Blocks.petcoke);
		gasGenerator();
		
		autolubricator();
		flarestack();
		
		// Fluids
		for(IPFluid f:IPFluid.FLUIDS){
			ResourceLocation still = f.getAttributes().getStillTexture();
			ModelFile model = this.models().getBuilder("block/fluid/" + f.getRegistryName().getPath()).texture("particle", still);
			
			getVariantBuilder(f.block).partialState().setModels(new ConfiguredModel(model));
		}
	}
	
	private void distillationtower(){
		ResourceLocation idleTexture = modLoc("multiblock/distillation_tower");
		ResourceLocation modelNormal = modLoc("models/multiblock/obj/distillationtower.obj");
		ResourceLocation modelMirrored = modLoc("models/multiblock/obj/distillationtower_mirrored.obj");
		
		BlockModelBuilder normal = multiblockModel(IPContent.Multiblock.distillationtower, modelNormal, idleTexture, "_idle", DistillationTowerMultiblock.INSTANCE, false);
		BlockModelBuilder mirrored = multiblockModel(IPContent.Multiblock.distillationtower, modelMirrored, idleTexture, "_mirrored_idle", DistillationTowerMultiblock.INSTANCE, true);
		
		createMultiblock(IPContent.Multiblock.distillationtower, normal, mirrored, idleTexture);
	}
	
	private void pumpjack(){
		ResourceLocation texture = modLoc("multiblock/pumpjack_base");
		ResourceLocation modelNormal = modLoc("models/multiblock/obj/pumpjack.obj");
		ResourceLocation modelMirrored = modLoc("models/multiblock/obj/pumpjack_mirrored.obj");
		
		BlockModelBuilder normal = multiblockModel(IPContent.Multiblock.pumpjack, modelNormal, texture, "", PumpjackMultiblock.INSTANCE, false);
		BlockModelBuilder mirrored = multiblockModel(IPContent.Multiblock.pumpjack, modelMirrored, texture, "_mirrored", PumpjackMultiblock.INSTANCE, true);
		
		createMultiblock(IPContent.Multiblock.pumpjack, normal, mirrored, texture);
	}
	
	private void cokerunit(){
		ResourceLocation texture = modLoc("multiblock/cokerunit");
		ResourceLocation modelNormal = modLoc("models/multiblock/obj/cokerunit.obj");
		ResourceLocation modelMirrored = modLoc("models/multiblock/obj/cokerunit_mirrored.obj");
		
		BlockModelBuilder normal = multiblockModel(IPContent.Multiblock.cokerunit, modelNormal, texture, "", CokerUnitMultiblock.INSTANCE, false);
		BlockModelBuilder mirrored = multiblockModel(IPContent.Multiblock.cokerunit, modelMirrored, texture, "_mirrored", CokerUnitMultiblock.INSTANCE, true);
		
		createMultiblock(IPContent.Multiblock.cokerunit, normal, mirrored, texture);
	}
	
	private void hydrotreater(){
		ResourceLocation texture = modLoc("multiblock/hydrotreater");
		ResourceLocation modelNormal = modLoc("models/multiblock/obj/hydrotreater.obj");
		ResourceLocation modelMirrored = modLoc("models/multiblock/obj/hydrotreater_mirrored.obj");
		
		BlockModelBuilder normal = multiblockModel(IPContent.Multiblock.hydrotreater, modelNormal, texture, "", HydroTreaterMultiblock.INSTANCE, false);
		BlockModelBuilder mirrored = multiblockModel(IPContent.Multiblock.hydrotreater, modelMirrored, texture, "_mirrored", HydroTreaterMultiblock.INSTANCE, true);
		
		createMultiblock(IPContent.Multiblock.hydrotreater, normal, mirrored, texture);
	}
	
	private BlockModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror){
		UnaryOperator<BlockPos> transform = UnaryOperator.identity();
		if(mirror){
			Vector3i size = mb.getSize(null);
			transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
		}
		final Vector3i offset = mb.getMasterFromOriginOffset();
		@SuppressWarnings("deprecation")
		Stream<Vector3i> partsStream = mb.getStructure(null).stream()
				.filter(info -> !info.state.isAir())
				.map(info -> info.pos)
				.map(transform)
				.map(p -> p.subtract(offset));
		
		String name = getMultiblockPath(block) + add;
		BlockModelBuilder base = this.models().withExistingParent(name, mcLoc("block"))
				.customLoader(OBJLoaderBuilder::begin).modelLocation(model).detectCullableFaces(false).flipV(true).end()
				.texture("texture", texture)
				.texture("particle", texture);
		
		BlockModelBuilder split = this.models().withExistingParent(name + "_split", mcLoc("block"))
				.customLoader(SplitModelBuilder::begin)
				.innerModel(base)
				.parts(partsStream.collect(Collectors.toList()))
				.dynamic(false).end();
		
		return split;
	}
	
	private void autolubricator(){
		ResourceLocation texture = modLoc("models/lubricator");
		
		BlockModelBuilder lube_empty = this.models().withExistingParent("lube_empty", new ResourceLocation(ImmersiveEngineering.MODID, "block/ie_empty")).texture("particle", texture);
		
		BlockModelBuilder lubeModel = this.models().withExistingParent(getPath(IPContent.Blocks.auto_lubricator), mcLoc("block"))
				.customLoader(OBJLoaderBuilder::begin).modelLocation(modLoc("models/block/obj/autolubricator.obj")).flipV(true).end()
				.texture("texture", texture)
				.texture("particle", texture);
		
		VariantBlockStateBuilder lubeBuilder = getVariantBuilder(IPContent.Blocks.auto_lubricator);
		for(Direction dir:AutoLubricatorBlock.FACING.getAllowedValues()){
			int rot = (90 * dir.getHorizontalIndex()) + 90 % 360;
			
			lubeBuilder.partialState()
				.with(AutoLubricatorBlock.SLAVE, false)
				.with(AutoLubricatorBlock.FACING, dir)
				.setModels(new ConfiguredModel(lubeModel, 0, rot, false));
			
			lubeBuilder.partialState()
				.with(AutoLubricatorBlock.SLAVE, true)
				.with(AutoLubricatorBlock.FACING, dir)
				.setModels(new ConfiguredModel(lube_empty));
		}
	}
	
	private void flarestack(){
		ResourceLocation texture = modLoc("block/obj/flarestack");
		ConfiguredModel emptyModel = new ConfiguredModel(this.models().withExistingParent("flare_empty",
				new ResourceLocation(ImmersiveEngineering.MODID, "block/ie_empty"))
				.texture("particle", texture));
		
		BlockModelBuilder flarestackModel = this.models().withExistingParent(getPath(IPContent.Blocks.flarestack), mcLoc("block"))
				.customLoader(OBJLoaderBuilder::begin).modelLocation(modLoc("models/block/obj/flarestack.obj")).flipV(true).end()
				.texture("texture", texture)
				.texture("particle", texture);
		
		VariantBlockStateBuilder flarestackBuilder = getVariantBuilder(IPContent.Blocks.flarestack);
		
		flarestackBuilder.partialState()
			.with(FlarestackBlock.SLAVE, false)
			.setModels(new ConfiguredModel(flarestackModel, 0, 0, false));
		
		flarestackBuilder.partialState()
			.with(FlarestackBlock.SLAVE, true)
			.setModels(emptyModel);
	}
	
	private void gasGenerator(){
		ResourceLocation texture = modLoc("block/obj/generator");
		
		BlockModelBuilder model = this.models().getBuilder(getPath(IPContent.Blocks.gas_generator))
			.customLoader(OBJLoaderBuilder::begin).modelLocation(modLoc("models/block/obj/generator.obj")).flipV(true).end()
			.texture("texture", texture)
			.texture("particle", texture);
		
		VariantBlockStateBuilder builder = getVariantBuilder(IPContent.Blocks.gas_generator);
		ConnectorBlockBuilder.builder(this.models(), builder, (res, mod) -> res.texture("particle", texture))
			.fixedModel(model)
			.layers(RenderType.getSolid(), RenderType.getCutout())
			.rotationData(GasGeneratorBlock.FACING, 0)
			.build();
	}
	
	/**
	 * From {@link blusunrize.immersiveengineering.common.data.BlockStates}
	 * 
	 * @param idleTexture
	 */
	private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, ResourceLocation particleTexture){
		createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, 180, particleTexture);
	}
	
	/** From {@link blusunrize.immersiveengineering.common.data.BlockStates} */
	private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, Property<Boolean> isSlave, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState, int rotationOffset, ResourceLocation particleTex){
		Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
		VariantBlockStateBuilder builder = getVariantBuilder(b);
		
		boolean[] possibleMirrorStates;
		if(mirroredState != null)
			possibleMirrorStates = new boolean[]{false, true};
		else
			possibleMirrorStates = new boolean[1];
		for(boolean mirrored:possibleMirrorStates)
			for(Direction dir:facing.getAllowedValues()){
				final int angleY;
				final int angleX;
				if(facing.getAllowedValues().contains(Direction.UP)){
					angleX = -90 * dir.getYOffset();
					if(dir.getAxis() != Axis.Y)
						angleY = getAngle(dir, rotationOffset);
					else
						angleY = 0;
				}else{
					angleY = getAngle(dir, rotationOffset);
					angleX = 0;
				}
				
				ModelFile model = mirrored ? mirroredModel : masterModel;
				PartialBlockstate partialState = builder.partialState()
//						.with(isSlave, false)
						.with(facing, dir);
				
				if(mirroredState != null)
					partialState = partialState.with(mirroredState, mirrored);
				
				partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
			}
	}
	
	/** From {@link blusunrize.immersiveengineering.common.data.BlockStates} */
	private int getAngle(Direction dir, int offset){
		return (int) ((dir.getHorizontalAngle() + offset) % 360);
	}
	
	private String getMultiblockPath(Block b){
		return "multiblock/" + getPath(b);
	}
	
	private String getPath(Block b){
		return b.getRegistryName().getPath();
	}
	
	private void itemModelWithParent(Block block, ModelFile parent){
		getItemBuilder(block).parent(parent)
			.texture("particle", modLoc("block/" + getPath(block)));
	}
	
	private void simpleBlockWithItem(Block block){
		ModelFile file = cubeAll(block);
		
		getVariantBuilder(block).partialState()
			.setModels(new ConfiguredModel(file));
		itemModelWithParent(block, file);
	}
	
	private ItemModelBuilder getItemBuilder(Block block){
		return itemModels().getBuilder(modLoc("item/" + getPath(block)).toString());
	}
}
