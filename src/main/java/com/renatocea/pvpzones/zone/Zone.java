package com.renatocea.pvpzones.zone;

import net.minecraft.nbt.CompoundTag;

public record Zone(int id, String name, int x1, int z1, int x2, int z2) {

    public int minX() { return Math.min(x1, x2); }
    public int maxX() { return Math.max(x1, x2); }
    public int minZ() { return Math.min(z1, z2); }
    public int maxZ() { return Math.max(z1, z2); }

    public boolean contains(int x, int z) {
        return x >= minX() && x <= maxX() && z >= minZ() && z <= maxZ();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("id", id);
        tag.putString("name", name);
        tag.putInt("x1", x1);
        tag.putInt("z1", z1);
        tag.putInt("x2", x2);
        tag.putInt("z2", z2);
        return tag;
    }

    public static Zone load(CompoundTag tag) {
        return new Zone(
            tag.getInt("id"),
            tag.getString("name"),
            tag.getInt("x1"),
            tag.getInt("z1"),
            tag.getInt("x2"),
            tag.getInt("z2")
        );
    }
}
