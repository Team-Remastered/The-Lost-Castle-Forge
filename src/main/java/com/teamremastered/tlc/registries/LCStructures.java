package com.teamremastered.tlc.registries;

import com.teamremastered.tlc.TheLostCastle;
import com.teamremastered.tlc.structures.LostCastle;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LCStructures {

    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on Forge.
     * This will handle registering the base structure for us at the correct time so we don't have to handle it ourselves.
     */
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, TheLostCastle.MODID);

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of tlc:lost_castle.
     */
    public static final RegistryObject<StructureType<LostCastle>> LOST_CASTLE = DEFERRED_REGISTRY_STRUCTURE.register("lost_castle", () -> () -> LostCastle.CODEC);
}
