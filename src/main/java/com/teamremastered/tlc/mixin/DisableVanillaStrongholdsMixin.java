package com.teamremastered.tlc.mixin;

import com.teamremastered.tlc.config.TLCConfig;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.core.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//Class took from Yung's Better Stronghold to disable the generation of the stronghold.
//https://github.com/YUNG-GANG/YUNGs-Better-Strongholds/blob/1.20/Common/src/main/java/com/yungnickyoung/minecraft/betterstrongholds/mixin/DisableVanillaStrongholdsMixin.java
@Mixin(ChunkGenerator.class)
public class DisableVanillaStrongholdsMixin {

      //Prevents vanilla strongholds from generating.
    @Inject(method = "tryGenerateStructure", at = @At(value = "HEAD"), cancellable = true)
    void disableVanillaStrongholds(
            StructureSet.StructureSelectionEntry structureSetEntry,
            StructureManager structureManager,
            RegistryAccess registryAccess,
            RandomState randomState,
            StructureTemplateManager structureTemplateManager,
            long seed,
            ChunkAccess chunkAccess,
            ChunkPos chunkPos,
            SectionPos sectionPos,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!TLCConfig.GENERATE_STRONGHOLD.getRaw() && structureSetEntry.structure().value().type() == StructureType.STRONGHOLD) {
            cir.setReturnValue(false);
        }
    }
}