package sbtaku11.simplycache;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemVariants extends Item
{
	private final String[] variants;

	public ItemVariants (String... variants)
	{
		super();
		this.setHasSubtypes(true);
		this.setMaxDamage(0);

		this.variants = variants;
	}

	@Override
	public String getTranslationKey (ItemStack stack)
	{
		return super.getTranslationKey(stack) + "." + variants[stack.getMetadata()];
	}

	@Override
	public void getSubItems (CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			for (int i = 0; i < variants.length; ++ i)
			{
				items.add(new ItemStack(this, 1, i));
			}
		}

	}
}
