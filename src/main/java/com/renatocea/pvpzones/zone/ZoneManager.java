package com.renatocea.pvpzones.zone;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ZoneManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ZoneManager INSTANCE;

    private final List<Zone> zones = new CopyOnWriteArrayList<>();
    private MinecraftServer server;
    private int nextId = 1;

    private static final String ZONES_FILENAME = "pvpzones.dat";

    private ZoneManager() {}

    public static ZoneManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ZoneManager();
        }
        return INSTANCE;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        load();
    }

    public void addZone(int x1, int z1, int x2, int z2, String name) {
        Zone zone = new Zone(nextId++, name, x1, z1, x2, z2);
        zones.add(zone);
        save();
        LOGGER.info("Added PvP zone: {} ({}:{})", name, zone.id(), zone.minX() + "," + zone.minZ() + " to " + zone.maxX() + "," + zone.maxZ());
    }

    public boolean removeZone(int id) {
        boolean removed = zones.removeIf(z -> z.id() == id);
        if (removed) {
            save();
            LOGGER.info("Removed PvP zone with id: {}", id);
        }
        return removed;
    }

    public List<Zone> getZones() {
        return List.copyOf(zones);
    }

    public Zone getZoneAtPosition(double x, double z) {
        return zones.stream()
            .filter(zone -> zone.contains((int) x, (int) z))
            .findFirst()
            .orElse(null);
    }

    public void save() {
        if (server == null) return;
        try {
            Path path = server.getWorldPath(LevelResource.ROOT).resolve(ZONES_FILENAME);
            Files.createDirectories(path.getParent());

            CompoundTag tag = new CompoundTag();
            tag.putInt("nextId", nextId);
            CompoundTag zonesTag = new CompoundTag();
            for (Zone zone : zones) {
                zonesTag.put(String.valueOf(zone.id()), zone.save());
            }
            tag.put("zones", zonesTag);

            NbtIo.write(tag, path);
            LOGGER.debug("Saved {} zones to {}", zones.size(), path);
        } catch (IOException e) {
            LOGGER.error("Failed to save zones", e);
        }
    }

    private void load() {
        if (server == null) return;
        try {
            Path path = server.getWorldPath(LevelResource.ROOT).resolve(ZONES_FILENAME);
            if (!Files.exists(path)) {
                LOGGER.info("No zones file found, starting fresh");
                return;
            }

            CompoundTag tag = NbtIo.read(path);
            nextId = tag.getInt("nextId");
            CompoundTag zonesTag = tag.getCompound("zones");

            zones.clear();
            for (String key : zonesTag.getAllKeys()) {
                Zone zone = Zone.load(zonesTag.getCompound(key));
                zones.add(zone);
            }

            LOGGER.info("Loaded {} zones", zones.size());
        } catch (Exception e) {
            LOGGER.error("Failed to load zones", e);
            zones.clear();
            nextId = 1;
        }
    }
}
