package flaxbeard.immersivepetroleum.common.data.loot;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataProvider;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.resources.ResourceLocation;

public abstract class LootGenerator implements DataProvider{
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	
	private final DataGenerator gen;
	protected final Map<ResourceLocation, LootTable> tables = new HashMap<>();
	
	public LootGenerator(DataGenerator gen){
		this.gen = gen;
	}
	
	@Override
	public void act(HashCache cache) throws IOException{
		this.tables.clear();
		Path outFolder = this.gen.getOutputFolder();
		
		registerTables();
		
		ValidationContext validator = new ValidationContext(LootParameterSets.GENERIC, (rl) -> null, this.tables::get);
		this.tables.forEach((name, table) -> {
			LootTableManager.validateLootTable(validator, name, table);
		});
		
		Multimap<String, String> problems = validator.getProblems();
		if(!problems.isEmpty()){
			problems.forEach((name, table) -> {
				ImmersivePetroleum.log.warn("Found validation problem in " + name + ": " + table);
			});
			throw new IllegalStateException("Failed to validate loot tables, see logs");
		}else{
			this.tables.forEach((name, table) -> {
				Path out = getPath(outFolder, name);
				
				try{
					IDataProvider.save(GSON, cache, LootTableManager.toJson(table), out);
				}catch(IOException e){
					ImmersivePetroleum.log.error("Couldnt save loot table {}", out, e);
				}
			});
		}
	}
	
	private static Path getPath(Path path, ResourceLocation rl){
		return path.resolve("data/" + rl.getNamespace() + "/loot_tables/" + rl.getPath() + ".json");
	}
	
	protected abstract void registerTables();
}
