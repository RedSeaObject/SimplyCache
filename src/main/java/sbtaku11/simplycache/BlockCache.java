package sbtaku11.simplycache;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCache extends Block implements ITileEntityProvider
{
	public BlockCache ()
	{
		super(Material.IRON);
		this.setHardness(15.0f);
		this.setResistance(25.0f);
		this.setSoundType(SoundType.METAL);

		this.setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity (World world, int meta) { return new TileCache(); }

	// Block
	@Override
	public void onBlockPlacedBy (World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack)
	{
		if (! world.isRemote)
		{
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileCache && ! tile.isInvalid())
			{
				TileCache tileCache = (TileCache) tile;
				if (stack.hasTagCompound())
				{
					NBTTagCompound compound = stack.getTagCompound();
					tileCache.readFromNBTHandler(compound);
				}
			}
		}
	}

	@Override
	public void onBlockClicked (World world, BlockPos pos, EntityPlayer player)
	{
		if (! world.isRemote)
		{
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileCache && ! tile.isInvalid())
			{
				TileCache tileCache = (TileCache) tile;
				int extractAmount = ! player.isSneaking() ? 1 : tileCache.getStoredInstance().getMaxStackSize();
				ItemStack extract = tileCache.extractItem(extractAmount, true);
				if (extract.isEmpty()) return;
				if (! player.inventory.addItemStackToInventory(extract))
				{
					if (extract.getCount() == extractAmount) return;
					extractAmount -= extract.getCount();
				}
				tileCache.extractItem(extractAmount, false);
				world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.4F, 0.8F);
			}
		}
	}

	@Override
	public boolean onBlockActivated (World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (! world.isRemote)
		{
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TileCache && ! tile.isInvalid())
			{
				TileCache tileCache = (TileCache) tile;
				ItemStack stack = player.getHeldItem(hand);
				if (stack.isEmpty())
				{
					if (player.isSneaking())
					{
						tileCache.setLocked(! tileCache.isLocked());
						if (tileCache.isLocked())
						{
							String msg = I18n.format("msg.locked");
							player.sendStatusMessage(new TextComponentString(msg), true);
							world.playSound(player, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.2F, 0.8F);
						}
						else
						{
							String msg = I18n.format("msg.unlocked");
							player.sendStatusMessage(new TextComponentString(msg), true);
							world.playSound(player, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.3F, 0.5F);
						}
					}
					else
					{
						String locked = tileCache.isLocked() ? I18n.format("msg.locked") : I18n.format("msg.unlocked");
						String name = ! tileCache.getStoredInstance().isEmpty() ? tileCache.getStoredInstance().getDisplayName() : I18n.format("msg.empty");
						int count = tileCache.getCount();
						int capacity = tileCache.getCapacity();
						String msg = I18n.format("msg.check", locked, name, count, capacity);
						player.sendStatusMessage(new TextComponentString(msg), true);
						world.playSound(player, pos, SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 0.2F, 0.7F);
					}
				}
				else if (stack.getItem() == SimplyCache.Items.cache_upgrade)
				{
					int currentLevel = tileCache.getLevel();
					int level = stack.getMetadata();
					if (currentLevel == level)
					{
						tileCache.setLevel(level + 1);
						stack.shrink(1);

						int preLevel = currentLevel;
						currentLevel = tileCache.getLevel();
						int capacity = tileCache.getCapacity();
						String msg = I18n.format("msg.upgrade_success", preLevel, currentLevel, capacity);
						player.sendStatusMessage(new TextComponentString(msg), true);
						world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.1F, 1.0F);
					}
					else
					{
						String msg = I18n.format("msg.upgrade_fail", currentLevel, level);
						player.sendStatusMessage(new TextComponentString(msg), true);
					}
				}
				else
				{
					boolean playSound = false;
					ItemStack heldItem = player.getHeldItem(hand);
					ItemStack ret = tileCache.insertItem(heldItem, false);
					if (ret != heldItem)
					{
						player.inventory.setInventorySlotContents(player.inventory.currentItem, ret);
						playSound = true;
					}
					if (playSound) world.playSound(player, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.1F, 0.7F);
				}
			}
		}
		return true;
	}

	@Override
	public void harvestBlock (World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool)
	{
		super.harvestBlock(world, player, pos, state, te, tool);
		world.setBlockToAir(pos);
	}

	@Override
	public boolean removedByPlayer (IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		if (willHarvest) return true;
		return super.removedByPlayer(state, world, pos, player, false);
	}

	@Override
	public void getDrops (NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileCache && ! tile.isInvalid())
		{
			TileCache tileCache = (TileCache) tile;
			NBTTagCompound compound = tileCache.writeToNBTHandler(new NBTTagCompound());
			if (! compound.isEmpty()) stack.setTagCompound(compound);
		}
		drops.add(stack);
	}

	@Override
	public void breakBlock (World world, BlockPos pos, IBlockState state)
	{
		world.removeTileEntity(pos);
	}
}
