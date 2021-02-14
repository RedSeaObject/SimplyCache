package sbtaku11.simplycache;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ItemHandlerCache implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound>
{
	public static final String LevelKey = "Level";
	public static final String StoredItemKey = "StoredItem";
	public static final String StoredCountKey = "StoredCount";
	public static final String LockedKey = "Locked";

	private static final int[] CapacityPerLevel = new int[] { 20000, 80000, 160000, 320000, 640000 };

	private final TileCache tile;
	private byte level;
	private boolean lock;
	private ItemStack storedInstance = ItemStack.EMPTY;
	private ItemStack storedStack = ItemStack.EMPTY;

	public ItemHandlerCache (TileCache tile)
	{
		this.tile = tile;
	}

	// Level
	public int getLevel () { return level; }

	public void setLevel (int level)
	{
		if (level > this.level)
		{
			this.level = (byte) level;
			tile.markDirty();
		}
	}

	// Lock
	public boolean getLocked () { return lock; }

	public void setLocked (boolean lock)
	{
		boolean mark = false;
		if (lock)
		{
			if (! this.lock && ! storedInstance.isEmpty())
			{
				this.lock = true;
				mark = true;
			}
		}
		else
		{
			if (this.lock)
			{
				this.lock = false;
				if (storedStack.isEmpty()) set(ItemStack.EMPTY);
				mark = true;
			}
		}
		if (mark) tile.markDirty();
	}

	// Storage Changes
	private void set (ItemStack stack)
	{
		storedInstance = Helper.CopyWithCount(stack, 1);
		storedStack = stack.copy();
	}

	private void grow (int quantity)
	{
		storedStack.grow(quantity);
	}

	private void shrink (int quantity)
	{
		storedStack.shrink(quantity);
	}

	// Storage
	public ItemStack getStoredInstance () { return Helper.CopyWithCount(storedInstance, 1); }

	public int getCount () { return storedStack.getCount(); }

	public int getCapacity () { return CapacityPerLevel[level]; }

	public int getSpace () { return Math.max(getCapacity() - storedStack.getCount(), 0); }

	@Override
	public int getSlots () { return 1; }

	@Override
	public ItemStack getStackInSlot (int slot) { return storedStack; }

	@Override
	public ItemStack insertItem (int slot, ItemStack stack, boolean simulate)
	{
		boolean mark = false;
		ItemStack result;
		if (stack.isEmpty()) result = stack;
		else
		{
			int toInsert = stack.getCount();
			if (getSpace() < toInsert) toInsert = getSpace();
			ItemStack sup = Helper.CopyWithCount(stack, stack.getCount() - toInsert);
			if (storedInstance.isEmpty())
			{
				if (! simulate)
				{
					set(stack);
					mark = true;
				}
				result = sup;
			}
			else if (! Helper.EqualityItem(storedInstance, stack)) result = stack;
			else
			{
				if (! simulate)
				{
					grow(toInsert);
					mark = true;
				}
				result = sup;
			}
		}
		if (mark) tile.markDirty();
		return result;
	}

	@Nonnull
	@Override
	public ItemStack extractItem (int slot, int amount, boolean simulate)
	{
		boolean mark = false;
		ItemStack result;
		if (storedStack.isEmpty()) result = ItemStack.EMPTY;
		else
		{
			int toExtract = Math.min(amount, storedStack.getMaxStackSize());
			if (storedStack.getCount() < toExtract) toExtract = storedStack.getCount();
			ItemStack sup = Helper.CopyWithCount(storedStack, toExtract);
			if (! simulate)
			{
				shrink(toExtract);
				if (! lock && storedStack.isEmpty()) set(ItemStack.EMPTY);
				mark = true;
			}
			result = sup;
		}
		if (mark) tile.markDirty();
		return result;
	}

	@Override
	public int getSlotLimit (int slot) { return getCapacity(); }

	// IItemHandlerModifiable
	@Override
	public void setStackInSlot (int slot, ItemStack stack) { insertItem(slot, stack, false); }

	// INBTSerializable
	@Override
	public NBTTagCompound serializeNBT ()
	{
		NBTTagCompound compound = new NBTTagCompound();
		if (level != 0) compound.setByte(LevelKey, level);
		if (! storedInstance.isEmpty())
		{
			compound.setTag(StoredItemKey, storedInstance.writeToNBT(new NBTTagCompound()));
			compound.setInteger(StoredCountKey, storedStack.getCount());
			compound.setBoolean(LockedKey, lock);
		}
		return compound;
	}

	@Override
	public void deserializeNBT (NBTTagCompound compound)
	{
		if (compound.hasKey(LevelKey, Constants.NBT.TAG_BYTE)) level = compound.getByte(LevelKey);
		if (compound.hasKey(StoredItemKey, Constants.NBT.TAG_COMPOUND) && compound.hasKey(StoredCountKey, Constants.NBT.TAG_INT) && compound.hasKey(LockedKey, Constants.NBT.TAG_BYTE))
		{
			storedInstance = new ItemStack(compound.getCompoundTag(StoredItemKey));
			storedStack = Helper.CopyWithCount(storedInstance, compound.getInteger(StoredCountKey));
			lock = compound.getBoolean(LockedKey);
		}
	}
}