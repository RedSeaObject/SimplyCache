package sbtaku11.simplycache;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Helper
{
	public static boolean EqualityItem (ItemStack lhs, ItemStack rhs)
	{
		if (ItemStack.areItemsEqual(lhs, rhs))
		{
			NBTTagCompound lc = lhs.getTagCompound();
			NBTTagCompound rc = rhs.getTagCompound();
			return (lc == null && rc == null) || (lc != null && rc != null && lc.equals(rc));
		}
		return false;
	}

	public static ItemStack CopyWithCount (ItemStack stack, int count)
	{
		ItemStack clone = stack.copy();
		clone.setCount(count);
		return clone;
	}
}
