package com.teamremastered.tlc.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamremastered.tlc.registries.LCStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public class LostCastle extends Structure {

    // A custom codec that changes the size limit for our code_structure_sky_fan.json's config to not be capped at 7.
    // With this, we can have a structure with a size limit up to 30 if we want to have extremely long branches of pieces in the structure.
    public static final Codec<LostCastle> CODEC = RecordCodecBuilder.<LostCastle>mapCodec(instance ->
            instance.group(LostCastle.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, LostCastle::new)).codec();

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;

    public LostCastle(Structure.StructureSettings config,
                         Holder<StructureTemplatePool> startPool,
                         Optional<ResourceLocation> startJigsawName,
                         int size,
                         HeightProvider startHeight,
                         Optional<Heightmap.Types> projectStartToHeightmap,
                         int maxDistanceFromCenter)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    private static boolean extraSpawningChecks(Structure.GenerationContext context) {

        // Grabs the chunk position we are at
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMinBlockX();
        int z = chunkPos.getMinBlockZ();
        int startHeight = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState());

        //Get Height at 78 blocks from castle spawn point (around the castle)
        int height1 = context.chunkGenerator().getFirstOccupiedHeight(x + 78, z, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState());
        int height2 = context.chunkGenerator().getFirstOccupiedHeight(x -78, z, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState());
        int height3 = context.chunkGenerator().getFirstOccupiedHeight(x, z + 78, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState());
        int height4 = context.chunkGenerator().getFirstOccupiedHeight(x, z - 78, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState());

        //Check if the height difference around the castle is bigger than 10. If not spawn the castle.
        return (Math.abs(startHeight - height1) < 10 && Math.abs(startHeight - height2) < 10 && Math.abs(startHeight - height3) < 10 && Math.abs(startHeight - height4) < 10);

    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!LostCastle.extraSpawningChecks(context)) {
            return Optional.empty();
        }

        // Set's our spawning blockpos's y
        // Since we are going to have heightmap/terrain height spawning set to true further down, this will make it so we spawn at terrain height.
        // If we wanted to spawn on ocean floor, we would set heightmap/terrain height spawning to false and the grab the y value of the terrain with OCEAN_FLOOR_WG heightmap.
        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMinBlockX();
        int z = chunkPos.getMinBlockZ();

        BlockPos blockPos = new BlockPos(x, startY, z);

        Optional<Structure.GenerationStub> structurePiecesGenerator =
                JigsawPlacement.addPieces(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        this.startPool, // The starting pool to use to create the structure layout from
                        this.startJigsawName, // Can be used to only spawn from one Jigsaw block. But we don't need to worry about this.
                        this.size, // How deep a branch of pieces can go away from center piece. (5 means branches cannot be longer than 5 pieces from center piece)
                        blockPos, // Where to spawn the structure.
                        false, // "useExpansionHack" This is for legacy villages to generate properly. You should keep this false always.
                        this.projectStartToHeightmap, // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
                        // Here, blockpos's y value is 0 which means the structure spawn 0 blocks above terrain height.
                        // Set this to false for structure to be place only at the passed in blockpos's Y value instead.
                        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
                        this.maxDistanceFromCenter); // Maximum limit for how far pieces can spawn from center. You cannot set this bigger than 128 or else pieces gets cutoff.

        /*
         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
         */

        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
    }

    @Override
    public StructureType<?> type() {
        return LCStructures.LOST_CASTLE.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}
