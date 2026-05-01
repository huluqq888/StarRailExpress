package io.wifi.events.day_night_fight.block;

import dev.doctor4t.ratatouille.util.registrar.BlockRegistrar;
import io.wifi.starrailexpress.SRE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

public interface DNFBlocks {
    BlockRegistrar registrar = new BlockRegistrar(SRE.TMM_MOD_ID);

    Block CLUE_POINT = registrar.create("clue_point",
            new CluePointBlock(Block.Properties.ofFullCopy(Blocks.END_ROD)
                    .noCollission()
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(state -> 14)
                    .sound(SoundType.WOOD)
                    .randomTicks()
            ));

    static void initialize() {
        registrar.registerEntries();
    }
}
