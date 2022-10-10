package com.teamremastered.tlc.processors;

import com.mojang.serialization.Codec;
import com.teamremastered.tlc.registries.LCProcessors;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Dynamically generates a foundation below the Castle like the Mansion.
 * Credit to YungNickYoung since I used his Leg Processor and tweaked it.
 * Repo: https://github.com/YUNG-GANG/YUNGs-Better-Strongholds/blob/multiloader/1.19/Common/src/main/java/com/yungnickyoung/minecraft/betterstrongholds/world/processor/LegProcessor.java
 */

@MethodsReturnNonnullByDefault
public class FoundationProcessor extends StructureProcessor {

    public static final FoundationProcessor INSTANCE = new FoundationProcessor();
    public static final Codec<FoundationProcessor> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader,
                                                             BlockPos jigsawPiecePos,
                                                             BlockPos jigsawPieceBottomCenterPos,
                                                             StructureTemplate.StructureBlockInfo blockInfoLocal,
                                                             StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                             StructurePlaceSettings structurePlacementData) {
        if (blockInfoGlobal.state.is(Blocks.YELLOW_CONCRETE)) {
            if (levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(new ChunkPos(blockInfoGlobal.pos))) {
                return blockInfoGlobal;
            }

            BlockState[] foundationBlocks = {
                    Blocks.STONE.defaultBlockState(),
                    Blocks.STONE_BRICKS.defaultBlockState(),
                    Blocks.CRACKED_STONE_BRICKS.defaultBlockState(),
                    Blocks.CRACKED_STONE_BRICKS.defaultBlockState(),
                    Blocks.MOSSY_STONE_BRICKS.defaultBlockState(),
                    Blocks.POLISHED_ANDESITE.defaultBlockState(),
                    Blocks.POLISHED_ANDESITE.defaultBlockState()
            };

            // Replace the yellow concrete itself
            if (blockInfoGlobal.state.is(Blocks.YELLOW_CONCRETE)) {
                blockInfoGlobal = new StructureTemplate.StructureBlockInfo(blockInfoGlobal.pos, RandomBlocks(foundationBlocks), blockInfoGlobal.nbt);
            }

            // Reusable mutable
            BlockPos.MutableBlockPos mutable = blockInfoGlobal.pos.mutable().move(Direction.DOWN); // Move down since we already processed the first block
            BlockState currBlockState = levelReader.getBlockState(mutable);

            while (mutable.getY() > levelReader.getMinBuildHeight()
                    && mutable.getY() < levelReader.getMaxBuildHeight()
                    && (currBlockState.isAir() || !levelReader.getFluidState(mutable).isEmpty())) {
                // Place block in vertical pillar
                levelReader.getChunk(mutable).setBlockState(mutable, RandomBlocks(foundationBlocks), false);

                // Move down
                mutable.move(Direction.DOWN);
                currBlockState = levelReader.getBlockState(mutable);
            }
        }
        return blockInfoGlobal;
    }

    public BlockState RandomBlocks (BlockState[] randomBlocks) {
        int min = 0;
        int max = randomBlocks.length;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max);

        return randomBlocks[randomNum];
    }

    protected StructureProcessorType<?> getType() {
        return LCProcessors.FOUNDATION_PROCESSOR;
    }
}
