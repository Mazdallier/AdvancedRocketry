package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.util.ZUtils;

public abstract class SatelliteData extends SatelliteBase {
	DataStorage data;
	long lastActionTime, prevLastActionTime;
	
	@Override
	public String getInfo(World world) {
		//tiles dont update unless ppl reopen
		return "Power: " + satelliteProperties.getPowerStorage() + "\nData Storage: " + ZUtils.formatNumber(data.getMaxData()) +
				"\nData: " + ZUtils.formatNumber((data.getData() + dataCreated(world)));
	}
	
	private int dataCreated(World world) {
		return Math.min(data.getMaxData() - data.getData() , (int)Math.max(0,  (world.getTotalWorldTime() - lastActionTime)/200)); //TODO: change from 10 seconds
	}

	@Override
	public void setProperties(SatelliteProperties satelliteProperties) {
		super.setProperties(satelliteProperties);
		data.setMaxData(satelliteProperties.getMaxDataStorage());
	}
	
	
	@Override
	public boolean performAction(EntityPlayer player, World world, int x,
			int y, int z) {

		//Calculate Data Recieved
		//TODO: pay attn to power
		data.addData(dataCreated(world), data.getDataType());
		lastActionTime = world.getTotalWorldTime();


		TileEntity tile = world.getTileEntity(x, y, z);

		if(tile instanceof IDataHandler) {
			IDataInventory dataInv = (IDataInventory)tile;

			data.removeData(dataInv.addData(data.getData(), data.getDataType()));
		}

		return false;
	}

	@Override
	public void setDimensionId(World world) {
		//TODO: send packet on orbit reached
		super.setDimensionId(world);

		lastActionTime = world.getTotalWorldTime();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.data.readFromNBT(nbt.getCompoundTag("data"));
		lastActionTime = nbt.getLong("lastActionTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagCompound data = new NBTTagCompound();
		this.data.writeToNBT(data);
		nbt.setTag("data", data);

		nbt.setLong("lastActionTime",lastActionTime);
	}

	@Override
	public int numberChangesToSend() {
		return 4;
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		lastActionTime = ( lastActionTime & ( ~(0xffffl << (slot*16) ) ) ) | ( ( long )value << (slot*16) );
	}

	@Override
	public void sendChanges(Container container, ICrafting crafter, int variableId, int localId) {
		crafter.sendProgressBarUpdate(container, variableId, (short)(( lastActionTime >>> (localId*16) ) & 0xffff));
		
		if(localId == 3)
			prevLastActionTime=lastActionTime;
	}

	@Override
	public boolean isUpdateRequired(int localId) {
		return lastActionTime != prevLastActionTime;
	}
}
