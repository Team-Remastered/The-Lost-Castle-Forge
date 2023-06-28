package com.teamremastered.tlc;

import com.mojang.logging.LogUtils;
import com.teamremastered.tlc.config.TLCConfig;
import com.teamremastered.tlc.registries.LCProcessors;
import com.teamremastered.tlc.registries.LCTags;
import com.teamremastered.tlc.registries.LCStructures;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TheLostCastle.MODID)
public class TheLostCastle
{
    public static final String MODID = "tlc";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String CONFIG_FILE = String.format("%s.toml", TheLostCastle.MODID);

    public TheLostCastle()
    {
        // Register ourselves for server and other game events we are interested in. Idk if I can delete this, I leave it there in just in case.
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the stuff
        LCStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
        LCProcessors.init();
        LCTags.init();

        TLCConfig.load();

    }
}