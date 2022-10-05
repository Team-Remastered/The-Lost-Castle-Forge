package com.teamremastered.lostcastle;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TheLostCastle.MODID)
public class TheLostCastle
{
    public static final String MODID = "lostcastle";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TheLostCastle()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LCRegistery.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
    }

}
