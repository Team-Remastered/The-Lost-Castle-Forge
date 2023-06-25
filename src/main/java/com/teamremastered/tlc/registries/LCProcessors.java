package com.teamremastered.tlc.registries;

import com.teamremastered.tlc.TheLostCastle;
import com.teamremastered.tlc.processors.FoundationProcessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class LCProcessors {

    public static StructureProcessorType<FoundationProcessor> FOUNDATION_PROCESSOR = () -> FoundationProcessor.CODEC;

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(LCProcessors::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, new ResourceLocation(TheLostCastle.MODID, "foundation_processor"), FOUNDATION_PROCESSOR);
        });
    }
}
