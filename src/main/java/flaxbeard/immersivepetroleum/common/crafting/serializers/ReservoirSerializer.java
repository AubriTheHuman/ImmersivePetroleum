package flaxbeard.immersivepetroleum.common.crafting.serializers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ReservoirSerializer extends IERecipeSerializer<Reservoir>{
	@Override
	public Reservoir readFromJson(ResourceLocation recipeId, JsonObject json){
		String name = JSONUtils.getString(json, "name");
		ResourceLocation fluid = new ResourceLocation(JSONUtils.getString(json, "fluid"));
		int min = JSONUtils.getInt(json, "fluidminimum");
		int max = JSONUtils.getInt(json, "fluidcapacity");
		int trace = JSONUtils.getInt(json, "fluidtrace");
		int weight = JSONUtils.getInt(json, "weight");
		
		Reservoir reservoir = new Reservoir(name, recipeId, fluid, min, max, trace, weight);
		
		ImmersivePetroleum.log.debug(String.format("Loaded reservoir %s as %s, with %smB to %smB of %s and %smB trace, with %s of weight.",
				recipeId, name, min, max, fluid, trace, weight));
		
		if(JSONUtils.hasField(json, "dimension")){
			JsonObject dimension = JSONUtils.getJsonObject(json, "dimension");
			
			List<ResourceLocation> whitelist = new ArrayList<>();
			List<ResourceLocation> blacklist = new ArrayList<>();
			
			if(JSONUtils.hasField(dimension, "whitelist")){
				JsonArray array = JSONUtils.getJsonArray(dimension, "whitelist");
				for(JsonElement obj:array){
					whitelist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(JSONUtils.hasField(dimension, "blacklist")){
				JsonArray array = JSONUtils.getJsonArray(dimension, "blacklist");
				for(JsonElement obj:array){
					blacklist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(whitelist.size() > 0){
				ImmersivePetroleum.log.debug("- Adding these to dimension-whitelist for {} -", name);
				whitelist.forEach(ins -> ImmersivePetroleum.log.debug(ins));
				reservoir.addDimension(false, whitelist);
			}else if(blacklist.size() > 0){
				ImmersivePetroleum.log.debug("- Adding these to dimension-blacklist for {} -", name);
				blacklist.forEach(ins -> ImmersivePetroleum.log.debug(ins));
				reservoir.addDimension(true, blacklist);
			}
		}
		
		if(JSONUtils.hasField(json, "biome")){
			JsonObject biome = JSONUtils.getJsonObject(json, "biome");
			
			List<ResourceLocation> whitelist = new ArrayList<>();
			List<ResourceLocation> blacklist = new ArrayList<>();
			
			if(JSONUtils.hasField(biome, "whitelist")){
				JsonArray array = JSONUtils.getJsonArray(biome, "whitelist");
				for(JsonElement obj:array){
					whitelist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(JSONUtils.hasField(biome, "blacklist")){
				JsonArray array = JSONUtils.getJsonArray(biome, "blacklist");
				for(JsonElement obj:array){
					blacklist.add(new ResourceLocation(obj.getAsString()));
				}
			}
			
			if(whitelist.size() > 0){
				ImmersivePetroleum.log.debug("- Adding these to biome-whitelist for {} -", name);
				whitelist.forEach(ins -> ImmersivePetroleum.log.debug(ins));
				reservoir.addBiome(false, whitelist);
			}else if(blacklist.size() > 0){
				ImmersivePetroleum.log.debug("- Adding these to biome-blacklist for {} -", name);
				blacklist.forEach(ins -> ImmersivePetroleum.log.debug(ins));
				reservoir.addBiome(true, blacklist);
			}
		}
		
		return reservoir;
	}
	
	@Override
	public Reservoir read(ResourceLocation recipeId, PacketBuffer buffer){
		return new Reservoir(buffer.readCompoundTag()); // Very convenient having the NBT stuff already.
	}
	
	@Override
	public void write(PacketBuffer buffer, Reservoir recipe){
		buffer.writeCompoundTag(recipe.writeToNBT());
	}
	
	@Override
	public ItemStack getIcon(){
		return ItemStack.EMPTY;
	}
}
