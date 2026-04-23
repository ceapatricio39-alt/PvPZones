package com.renatocea.pvpzones.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.renatocea.pvpzones.zone.Zone;
import com.renatocea.pvpzones.zone.ZoneManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ZoneCommands {
    //crear zonita

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("pvpzone")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("x1", IntegerArgumentType.integer())
                    .then(Commands.argument("z1", IntegerArgumentType.integer())
                        .then(Commands.argument("x2", IntegerArgumentType.integer())
                            .then(Commands.argument("z2", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    int x1 = IntegerArgumentType.getInteger(ctx, "x1");
                                    int z1 = IntegerArgumentType.getInteger(ctx, "z1");
                                    int x2 = IntegerArgumentType.getInteger(ctx, "x2");
                                    int z2 = IntegerArgumentType.getInteger(ctx, "z2");
                                    String name = "PvP Zone " + ZoneManager.getInstance().getZones().size() + 1;
                                    ZoneManager.getInstance().addZone(x1, z1, x2, z2, name);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Created " + name + ": (" + x1 + "," + z1 + ") to (" + x2 + "," + z2 + ")"), true);
                                    return 1;
                                })
                            )
                        )
                    )
                )
        );

        //remover zonilla

        dispatcher.register(
            Commands.literal("pvpzonesremove")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("id", IntegerArgumentType.integer())
                    .suggests((ctx, builder) -> {
                        for (Zone zone : ZoneManager.getInstance().getZones()) {
                            builder.suggest(zone.id());
                        }
                        return CompletableFuture.completedFuture(builder.build());
                    })
                    .executes(ctx -> {
                        int id = IntegerArgumentType.getInteger(ctx, "id");
                        if (ZoneManager.getInstance().removeZone(id)) {
                            ctx.getSource().sendSuccess(() -> Component.literal("Removed zone " + id), true);
                        } else {
                            ctx.getSource().sendFailure(Component.literal("Zone " + id + " not found"));
                        }
                        return 1;
                    })
                )
        );

        //Ver zonas

        dispatcher.register(
            Commands.literal("pvpzones")
                .requires(source -> source.hasPermission(4))
                .executes(ctx -> {
                    List<Zone> zoneList = ZoneManager.getInstance().getZones();
                    if (zoneList.isEmpty()) {
                        ctx.getSource().sendSuccess(() -> Component.literal("No zones defined"), false);
                    } else {
                        ctx.getSource().sendSuccess(() -> Component.literal("PvP Zones:"), false);
                        for (Zone zone : zoneList) {
                            ctx.getSource().sendSuccess(() -> Component.literal("  [" + zone.id() + "] " + zone.name() +
                                " (" + zone.minX() + "," + zone.minZ() + ") to (" + zone.maxX() + "," + zone.maxZ() + ")"), false);
                        }
                    }
                    return zoneList.size();
                })
        );
    }
}
