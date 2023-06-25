package com.teamremastered.tlc.registries;

import com.teamremastered.tlc.TheLostCastle;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class LCTags {

    //useless init but I like knowing in my main class that the tags are registered
    public static void init() {}

    public static TagKey<Structure> LOST_CASTLE_MAP = TagKey.create(Registries.STRUCTURE,
            new ResourceLocation(TheLostCastle.MODID, "lost_castle_map"));
}
