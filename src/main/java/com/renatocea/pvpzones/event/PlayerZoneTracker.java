package com.renatocea.pvpzones.event;

import com.renatocea.pvpzones.zone.Zone;
import com.renatocea.pvpzones.zone.ZoneManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class PlayerZoneTracker {

    private final Map<UUID, Vec3> lastPositions = new WeakHashMap<>();
    private final Map<UUID, Boolean> playerZoneState = new WeakHashMap<>();

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Pre event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player == null || player.getCommandSenderWorld().isClientSide) continue;

            UUID uuid = player.getUUID();
            Vec3 currentPos = player.position();
            Vec3 lastPos = lastPositions.get(uuid);

            if (lastPos != null && lastPos.equals(currentPos)) continue;

            lastPositions.put(uuid, currentPos);

            double px = currentPos.x;
            double pz = currentPos.z;

            Zone zone = ZoneManager.getInstance().getZoneAtPosition(px, pz);
            boolean wasInZone = playerZoneState.getOrDefault(uuid, false);
            boolean isInZone = zone != null;

            if (isInZone && !wasInZone) {
                playerZoneState.put(uuid, true);
                player.displayClientMessage(Component.literal("§c§l[!] §aYou entered a §cPvP Zone§a. PvP is enabled!"), true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.NOTE_BLOCK_BANJO, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
            } else if (!isInZone && wasInZone) {
                playerZoneState.put(uuid, false);
                player.displayClientMessage(Component.literal("§b§l[!] §fYou left the §cPvP Zone§f. PvP is now §adisabled§f."), true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.NOTE_BLOCK_BANJO, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 0.5f);
            }
        }
    }
}
