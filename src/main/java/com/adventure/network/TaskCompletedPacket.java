package com.adventure.network;

import com.adventure.AdventureMod;
import com.adventure.reward.RewardGiver;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Packet sent when a task is completed
 * itemsData format: "itemId:count,itemId:count,..." e.g. "minecraft:iron_ingot:3,minecraft:diamond:1"
 */
public record TaskCompletedPacket(int taskId, String translationKey, int xpReward, String itemsData) implements CustomPayload {
    
    public static final CustomPayload.Id<TaskCompletedPacket> ID = 
        new CustomPayload.Id<>(Identifier.of(AdventureMod.MOD_ID, "task_completed"));
    
    public static final PacketCodec<RegistryByteBuf, TaskCompletedPacket> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, TaskCompletedPacket::taskId,
        PacketCodecs.STRING, TaskCompletedPacket::translationKey,
        PacketCodecs.INTEGER, TaskCompletedPacket::xpReward,
        PacketCodecs.STRING, TaskCompletedPacket::itemsData,
        TaskCompletedPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
    
    public static void sendToPlayer(ServerPlayerEntity player, int taskId, String translationKey, 
                                    RewardGiver.RewardResult reward) {
        // Serialize items to string format
        StringBuilder itemsBuilder = new StringBuilder();
        for (int i = 0; i < reward.items().size(); i++) {
            var item = reward.items().get(i);
            if (i > 0) itemsBuilder.append(",");
            itemsBuilder.append(item.itemId()).append(":").append(item.count());
        }
        
        ServerPlayNetworking.send(player, new TaskCompletedPacket(
            taskId, translationKey, reward.xp(), itemsBuilder.toString()));
    }
}

