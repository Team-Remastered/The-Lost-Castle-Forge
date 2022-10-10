package com.teamremastered.tlc.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamremastered.tlc.registries.LCStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public class LostCastle extends StructureFeature<JigsawConfiguration> {

    // A custom codec that changes the size limit for our code_structure_sky_fan.json's config to not be capped at 7.
    // With this, we can have a structure with a size limit up to 30 if we want to have extremely long branches of pieces in the structure.
    public static final Codec<JigsawConfiguration> CODEC = RecordCodecBuilder.create((codec) -> {
        return codec.group(
                StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(JigsawConfiguration::startPool),
                Codec.intRange(0, 30).fieldOf("size").forGetter(JigsawConfiguration::maxDepth)
        ).apply(codec, JigsawConfiguration::new);
    });

    public LostCastle() {
        // Create the pieces layout of the structure and give it to the game
        super(CODEC, LostCastle::createPiecesGenerator, PostPlacementProcessor.NONE);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }



    private static int distanceFromSpawn(ChunkPos structurePos) {
        //Spawn point is always around 0 ~ 0
        ChunkPos spawnPointPos = new ChunkPos(0, 0);

        //Convert the structure position into blocks and get the structure distance from spawn
        int structurePosX = structurePos.x << 4;
        int structurePosZ = structurePos.z << 4;
        int distanceFromSpawn = (int) Math.sqrt(Math.pow((structurePosX-spawnPointPos.x), 2) + Math.pow((structurePosZ-spawnPointPos.z), 2));

        System.out.println("Distance from spawn is: " + distanceFromSpawn);
        return distanceFromSpawn;
    }

    private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {

        // Grabs the chunk position we are at
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMinBlockX();
        int z = chunkPos.getMinBlockZ();
        int startHeight = context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor());

        //Get Height at 78 blocks from castle spawn point (around the castle)
        int height1 = context.chunkGenerator().getFirstOccupiedHeight(x + 78, z, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor());
        int height2 = context.chunkGenerator().getFirstOccupiedHeight(x -78, z, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor());
        int height3 = context.chunkGenerator().getFirstOccupiedHeight(x, z + 78, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor());
        int height4 = context.chunkGenerator().getFirstOccupiedHeight(x, z - 78, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor());

        //Check if the height difference around the castle is bigger than 10 and castle if the structure is within 5000 blocks from spawn. If not spawn the castle.
        return (Math.abs(startHeight - height1) < 10 && Math.abs(startHeight - height2) < 10 && Math.abs(startHeight - height3) < 10 && Math.abs(startHeight - height4) < 10 && distanceFromSpawn(chunkPos) > 5000);

    }

    public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!LostCastle.isFeatureChunk(context)) {
            return Optional.empty();
        }

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMinBlockX();
        int z = chunkPos.getMinBlockZ();

        BlockPos blockPos = new BlockPos(x, 0, z);

        Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator =
                JigsawPlacement.addPieces(
                        context, // Used for JigsawPlacement to get all the proper behaviors done.
                        PoolElementStructurePiece::new, // Needed in order to create a list of jigsaw pieces when making the structure's layout.
                        blockPos, // Position of the structure. Y value is ignored if last parameter is set to true.
                        false, // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
                        // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
                        true); // Adds the terrain height's y value to the passed in blockpos's y value. (This uses WORLD_SURFACE_WG heightmap which stops at top water too)
        // Here, blockpos's y value is 60 which means the structure spawn 60 blocks above terrain height.
        // Set this to false for structure to be place only at the passed in blockpos's Y value instead.
        // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.

        /*
         * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
         * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
         * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
         */

        // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
        return structurePiecesGenerator;
    }
}
