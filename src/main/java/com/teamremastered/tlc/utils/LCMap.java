package com.teamremastered.tlc.utils;

import com.teamremastered.tlc.TheLostCastle;
import com.teamremastered.tlc.registries.LCTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class LCMap {

    public static boolean nullCheck = false;

    public static ItemStack createMap(ServerLevel serverLevel, BlockPos playerPosition) {
        // Get position of marker
        BlockPos structurePos = serverLevel.findNearestMapStructure(LCTags.LOST_CASTLE_MAP, playerPosition, 100, false);
        ItemStack stack;

        // Create map
        if (structurePos == null) {
            stack = MapItem.create(serverLevel, 0, 0, (byte) 2 , true, true);
            nullCheck = true;
        }
        else {
            stack = MapItem.create(serverLevel, structurePos.getX(), structurePos.getZ(), (byte) 2 , true, true);
        }

        MapItem.renderBiomePreviewMap(serverLevel, stack);

        if (structurePos == null) {
            MapItemSavedData.addTargetDecoration(stack, BlockPos.ZERO, "+", MapDecoration.Type.TARGET_X);
            TheLostCastle.LOGGER.error("Something went wrong with The Lost Castle");
        } else {
            MapItemSavedData.addTargetDecoration(stack, structurePos, "+", MapDecoration.Type.TARGET_X);
        }

        // Set the name of the map
        stack.setHoverName(Component.nullToEmpty("Lost Castle Map"));

        return stack;
    }
        //Create The Trade
    public static class LCMapTrade implements VillagerTrades.ItemListing {

        @Override
        public MerchantOffer getOffer(@Nonnull Entity entity, @NotNull RandomSource random){
            int xp = 10;
            int min = 15;
            int max = 25;
            int priceEmeralds = ThreadLocalRandom.current().nextInt(min, max + 1);
            if (!entity.level.isClientSide()) {
                ItemStack map = createMap((ServerLevel) entity.level, entity.blockPosition());
                return new MerchantOffer(new ItemStack(Items.EMERALD, priceEmeralds), new ItemStack(Items.COMPASS), map, 12, xp, 0.2F);
            }
            return null;
        }
    }

    // Add Map to Cartographers
    @SubscribeEvent
    public static void onVillagerTradesEvent(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.CARTOGRAPHER) {
            event.getTrades().get(3).add(new LCMap.LCMapTrade());
        }
    }
}
