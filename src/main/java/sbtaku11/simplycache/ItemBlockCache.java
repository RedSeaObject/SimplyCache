package sbtaku11.simplycache;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemBlockCache extends ItemBlock
{
	public ItemBlockCache (Block block)
	{
		super(block);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation (ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey(TileCache.HandlerKey, Constants.NBT.TAG_COMPOUND))
			{
				NBTTagCompound handlerCompound = compound.getCompoundTag(TileCache.HandlerKey);
				if (handlerCompound.hasKey(ItemHandlerCache.LevelKey, Constants.NBT.TAG_BYTE))
				{
					int level = handlerCompound.getByte(ItemHandlerCache.LevelKey);
					tooltip.add(I18n.format("gui.tooltip.cache.level", level));
				}
				if (handlerCompound.hasKey(ItemHandlerCache.StoredItemKey, Constants.NBT.TAG_COMPOUND) && handlerCompound.hasKey(ItemHandlerCache.StoredCountKey, Constants.NBT.TAG_INT) && handlerCompound.hasKey(ItemHandlerCache.LockedKey, Constants.NBT.TAG_BYTE))
				{
					String storedItem = new ItemStack(handlerCompound.getCompoundTag(ItemHandlerCache.StoredItemKey)).getDisplayName();
					tooltip.add(I18n.format("gui.tooltip.cache.stored_item", storedItem));
					int storedCount = handlerCompound.getInteger(ItemHandlerCache.StoredCountKey);
					tooltip.add(I18n.format("gui.tooltip.cache.stored_count", storedCount));
					String locked = handlerCompound.getBoolean(ItemHandlerCache.LockedKey) ? I18n.format("msg.locked") : I18n.format("msg.unlocked");
					tooltip.add(I18n.format("gui.tooltip.cache.locked", locked));
				}
			}
		}
	}
}
