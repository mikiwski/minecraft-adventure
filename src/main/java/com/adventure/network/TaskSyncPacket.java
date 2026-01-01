package com.adventure.network;

import com.adventure.AdventureMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record TaskSyncPacket(int currentLevel, int taskId, int progress, int targetAmount) implements CustomPayload {
    
    public static final CustomPayload.Id<TaskSyncPacket> ID = 
        new CustomPayload.Id<>(Identifier.of(AdventureMod.MOD_ID, "task_sync"));
    
    public static final PacketCodec<PacketByteBuf, TaskSyncPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, TaskSyncPacket::currentLevel,
        PacketCodecs.INTEGER, TaskSyncPacket::taskId,
        PacketCodecs.INTEGER, TaskSyncPacket::progress,
        PacketCodecs.INTEGER, TaskSyncPacket::targetAmount,
        TaskSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }

    public static void sendToPlayer(ServerPlayerEntity player, int currentLevel, int taskId, int progress, int targetAmount) {
        ServerPlayNetworking.send(player, new TaskSyncPacket(currentLevel, taskId, progress, targetAmount));
    }
}

