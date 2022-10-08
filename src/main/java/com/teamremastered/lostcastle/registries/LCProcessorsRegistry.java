package com.teamremastered.lostcastle.registries;

import com.teamremastered.lostcastle.TheLostCastle;
import com.teamremastered.lostcastle.processors.FoundationProcessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class LCProcessorsRegistry {

    public static StructureProcessorType<FoundationProcessor> FOUNDATION_PROCESSOR = () -> FoundationProcessor.CODEC;

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(LCProcessorsRegistry::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Registry.register(Registry.STRUCTURE_PROCESSOR, new ResourceLocation(TheLostCastle.MODID, "foundation_processor"), FOUNDATION_PROCESSOR);
        });
    }
}
