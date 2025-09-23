package su.uTa4u.tfcwoodwork.compat.jade;

import net.dries007.tfc.util.Helpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import su.uTa4u.tfcwoodwork.TFCWoodworking;
import su.uTa4u.tfcwoodwork.blockentities.LogPileExBlockEntity;
import su.uTa4u.tfcwoodwork.blocks.LogPileExBlock;

@WailaPlugin
public final class JadeIntegration implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registry) {
        registry.registerBlockDataProvider(LogPileComponentProvider.INSTANCE, LogPileExBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registry) {
        registry.registerBlockComponent(LogPileComponentProvider.INSTANCE, LogPileExBlock.class);
    }

    // Using enum for Singleton pattern is kinda sus, but that's what Jade docs show
    private enum LogPileComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
        INSTANCE;

        private static final String KEY_ITEM = "Item";
        private static final String KEY_ITEMSTACK_COUNT = "Count";
        private static final String KEY_ITEMSTACK_NAME = "Name";
        private static final String KEY_ITEMSTACK_LIST = "Itemstacks";
        private static final ResourceLocation LOG_PILE_TOOLTIP_UID = TFCWoodworking.getResource("log_pile_ex");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
            if (accessor.getBlockEntity() instanceof LogPileExBlockEntity) {
                final var listTag = accessor.getServerData().getList(KEY_ITEMSTACK_LIST, Tag.TAG_COMPOUND);
                for (int i = 0; i < listTag.size(); ++i) {
                    final var stackTag = listTag.getCompound(i);
                    final var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(stackTag.getString(KEY_ITEM)));
                    final var icon = IElementHelper.get().item(new ItemStack(item), 0.5f);
                    tooltip.add(icon);
                    tooltip.append(Component.empty().append(stackTag.getInt(KEY_ITEMSTACK_COUNT) + "x ").append(stackTag.getString(KEY_ITEMSTACK_NAME)));
                }
            }
        }

        @Override
        public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof LogPileExBlockEntity pile) {
                ListTag listTag = new ListTag();
                for (ItemStack stack : Helpers.iterate(pile.getInventory())) {
                    if (!stack.isEmpty()) {
                        CompoundTag stackTag = new CompoundTag();
                        stackTag.putString(KEY_ITEM, stack.getItem().toString());
                        stackTag.putString(KEY_ITEMSTACK_NAME, stack.getHoverName().getString());
                        stackTag.putInt(KEY_ITEMSTACK_COUNT, stack.getCount());
                        listTag.add(stackTag);
                    }
                }
                tag.put(KEY_ITEMSTACK_LIST, listTag);
            }
        }

        @Override
        public ResourceLocation getUid() {
            return LOG_PILE_TOOLTIP_UID;
        }
    }
}