package sbtaku11.simplycache;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = SimplyCache.MOD_ID, name = SimplyCache.MOD_NAME, version = SimplyCache.VERSION)
public class SimplyCache
{
	public static final String MOD_ID = "simplycache";
	public static final String MOD_NAME = "SimplyCache";
	public static final String VERSION = "1.0";

	@GameRegistry.ObjectHolder(MOD_ID)
	public static class Blocks
	{
		public static final BlockCache cache = null;
	}

	@GameRegistry.ObjectHolder(MOD_ID)
	public static class Items
	{
		public static final ItemBlock cache = null;
		public static final ItemCacheUpgrade cache_upgrade = null;
	}

	@Mod.EventBusSubscriber
	public static class ObjectRegistryHandler
	{
		@SubscribeEvent
		public static void addItems (final RegistryEvent.Register<Item> event)
		{
			final IForgeRegistry<Item> registry = event.getRegistry();

			registry.register(RegistryHelper.SetRegistryKey(new ItemBlockCache(Blocks.cache), "cache"));
			registry.register(RegistryHelper.SetRegistryKey(new ItemCacheUpgrade("basic", "advanced", "elite", "ultimate"), "cache_upgrade"));
		}

		@SubscribeEvent
		public static void addBlocks (final RegistryEvent.Register<Block> event)
		{
			final IForgeRegistry<Block> registry = event.getRegistry();

			registry.register(RegistryHelper.SetRegistryKey(new BlockCache(), "cache"));

			RegistryHelper.RegisterTile(TileCache.class, "cache");
		}

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void addRegisterModels (final ModelRegistryEvent event)
		{
			RegistryHelper.RegisterModel(Items.cache, "cache");
			RegistryHelper.RegisterModel(Items.cache_upgrade, "cache_upgrade_basic", "cache_upgrade_advanced", "cache_upgrade_elite", "cache_upgrade_ultimate");
		}
	}
}
