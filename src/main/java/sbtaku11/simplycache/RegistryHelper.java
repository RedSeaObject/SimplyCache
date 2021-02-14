package sbtaku11.simplycache;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RegistryHelper
{
	public static void RegisterTile (Class<? extends TileEntity> tile, String registryKey)
	{
		GameRegistry.registerTileEntity(tile, SimplyCache.MOD_ID + ":" + registryKey);
	}

	public static Block SetRegistryKey (Block block, String registryKey)
	{
		block.setRegistryName(registryKey);
		block.setTranslationKey(registryKey);
		return block;
	}

	public static Item SetRegistryKey (Item item, String registryKey)
	{
		item.setRegistryName(registryKey);
		item.setTranslationKey(registryKey);
		return item;
	}

	@SideOnly(Side.CLIENT)
	public static void RegisterModel (Item item, String... modelKey)
	{
		for (int i = 0; i < modelKey.length; ++ i)
		{
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(SimplyCache.MOD_ID + ":" + modelKey[i], "inventory"));
		}
	}
}
