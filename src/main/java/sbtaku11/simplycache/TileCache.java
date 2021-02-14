package sbtaku11.simplycache;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileCache extends TileEntity
{
	public static final String HandlerKey = "Handler";

	private final ItemHandlerCache handler;

	public TileCache ()
	{
		this.handler = new ItemHandlerCache(this);
	}

	// Level
	public int getLevel () { return handler.getLevel(); }

	public void setLevel (int level) { handler.setLevel(level); }

	// Lock
	public boolean isLocked () { return handler.getLocked(); }

	public void setLocked (boolean lock) { handler.setLocked(lock); }

	// Storage
	public ItemStack getStoredInstance () { return handler.getStoredInstance(); }

	public int getCount () { return handler.getCount(); }

	public int getCapacity () { return handler.getCapacity(); }

	public ItemStack insertItem (ItemStack stack, boolean simulate) { return handler.insertItem(0, stack, simulate); }

	public ItemStack extractItem (int maxExtract, boolean simulate) { return handler.extractItem(0, maxExtract, simulate); }

	// Serialize Handler
	public NBTTagCompound writeToNBTHandler (NBTTagCompound compound)
	{
		NBTTagCompound handlerCompound = handler.serializeNBT();
		if (handlerCompound != null && ! handlerCompound.isEmpty()) compound.setTag(HandlerKey, handlerCompound);
		return compound;
	}

	public void readFromNBTHandler (NBTTagCompound compound)
	{
		if (compound.hasKey(HandlerKey, Constants.NBT.TAG_COMPOUND)) handler.deserializeNBT(compound.getCompoundTag(HandlerKey));
	}

	// Serialize
	@Override
	public NBTTagCompound writeToNBT (NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		writeToNBTHandler(compound);
		return compound;
	}

	@Override
	public void readFromNBT (NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		readFromNBTHandler(compound);
	}

	// Capability
	@Override
	public boolean hasCapability (Capability<?> capability, EnumFacing from)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, from);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability (Capability<T> capability, EnumFacing facing)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) handler;
		return super.getCapability(capability, facing);
	}
}