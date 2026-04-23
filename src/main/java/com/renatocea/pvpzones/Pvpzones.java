package com.renatocea.pvpzones;

import com.mojang.logging.LogUtils;
import com.renatocea.pvpzones.command.ZoneCommands;
import com.renatocea.pvpzones.event.PlayerZoneTracker;
import com.renatocea.pvpzones.zone.ZoneManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(Pvpzones.MODID)
public class Pvpzones {
    public static final String MODID = "pvpzones";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Pvpzones(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ZoneCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ZoneManager.getInstance().setServer(event.getServer());
        NeoForge.EVENT_BUS.register(new PlayerZoneTracker());
    }
}
