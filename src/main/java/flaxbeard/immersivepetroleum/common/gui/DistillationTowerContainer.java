package flaxbeard.immersivepetroleum.common.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_0;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_1;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_2;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.INV_3;
import static flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity.TANK_INPUT;

import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.gui.IESlot;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class DistillationTowerContainer extends IEBaseContainer<DistillationTowerTileEntity>{
	public DistillationTowerContainer(int windowId, PlayerInventory inventoryPlayer, final DistillationTowerTileEntity tile){
		super(inventoryPlayer, tile, windowId);
		
		this.addSlot(new IESlot.FluidContainer(this, this.inv, INV_0, 12, 17, 2){
			@Override
			public boolean isItemValid(ItemStack itemStack){
				return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).map(h -> {
					if(h.getTanks() <= 0)
						return false;
					FluidStack fs = h.getFluidInTank(0);
					if(fs.isEmpty())
						return false;
					if(tile.tanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tile.tanks[TANK_INPUT].getFluid()))
						return false;
					
					DistillationRecipe incomplete = DistillationRecipe.findRecipe(fs);
					return incomplete != null;
				}).orElse(false);
			}
		});
		this.addSlot(new IESlot.Output(this, this.inv, INV_1, 12, 53));
		
		this.addSlot(new IESlot.FluidContainer(this, this.inv, INV_2, 134, 17, 0));
		this.addSlot(new IESlot.Output(this, this.inv, INV_3, 134, 53));
		
		slotCount = 4;
		
		for(int i = 0;i < 3;i++){
			for(int j = 0;j < 9;j++){
				addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 85 + i * 18));
			}
		}
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 143));
		}
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity player){
		return inv != null;
	}
}
