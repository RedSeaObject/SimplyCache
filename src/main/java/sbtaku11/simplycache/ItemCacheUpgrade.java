package sbtaku11.simplycache;

import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class ItemCacheUpgrade extends ItemVariants
{
	private final Property[] properties;

	public ItemCacheUpgrade (Property... properties)
	{
		super(Arrays.stream(properties).map(x -> x.variants).toArray(String[]::new));
		this.properties = properties;
	}

	public Integer getLevel (ItemStack stack)
	{
		if (stack.getItem() == this)
		{
			int index = stack.getMetadata();
			if (index < properties.length)
			{
				return properties[index].level;
			}
		}
		return null;
	}

	public static class Property
	{
		public final String variants;
		public final int level;

		public Property (String variants, int level)
		{
			this.variants = variants;
			this.level = level;
		}
	}
}
