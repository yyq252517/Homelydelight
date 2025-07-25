package com.catoxide.homelydelight;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import vectorwing.farmersdelight.common.registry.ModBlocks;

@SuppressWarnings("deprecation")
public class SoysaucefermentationbarrelBlock extends Block
{
    public static IntegerProperty COMPOSTING = IntegerProperty.create("composting", 0, 7);

    public SoysaucefermentationbarrelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(super.defaultBlockState().setValue(COMPOSTING, 0));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COMPOSTING);
        super.createBlockStateDefinition(builder);
    }

    public int getMaxCompostingStage() {
        return 7;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) return;

        float chance = 0.05F;
        int maxLight = 0;

        for (BlockPos neighborPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            BlockState neighborState = level.getBlockState(neighborPos);
            int light = level.getBrightness(LightLayer.SKY, neighborPos.above());
            if (light > maxLight) {
                maxLight = light;
            }
        }

        chance += maxLight > 12 ? 0.1F : 0.05F;

        if (level.getRandom().nextFloat() <= chance) {
            if (state.getValue(COMPOSTING) == this.getMaxCompostingStage())
                level.setBlock(pos, Homely_Delight.soy_sauce_barrel.get().defaultBlockState(), 3); // finished
            else
                level.setBlock(pos, state.setValue(COMPOSTING, state.getValue(COMPOSTING) + 1), 3); // next stage
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return (getMaxCompostingStage() + 1 - blockState.getValue(COMPOSTING));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(10) == 0) {
            level.addParticle(ParticleTypes.MYCELIUM, (double) pos.getX() + (double) random.nextFloat(), (double) pos.getY() + 1.1D, (double) pos.getZ() + (double) random.nextFloat(), 0.0D, 0.0D, 0.0D);
        }
    }
}

