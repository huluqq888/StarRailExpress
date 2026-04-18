package org.agmas.noellesroles.game.roles.neutral.panda;

import io.wifi.starrailexpress.event.client.OnGameFinishedClient;
import io.wifi.starrailexpress.event.client.OnGameStartedClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PandaClientHandle {
    public static Map<UUID, Panda> pandaMap = new HashMap<>();
    public static void getOrCreatePanda(Player player, ClientLevel clientLevel) {
        UUID uuid = player.getUUID();
        if (!pandaMap.containsKey(uuid)){
            Panda value = new Panda(EntityType.PANDA, clientLevel);
            value.setPos(player.getX(),player.getY(),player.getZ());
            value.setUUID(uuid);
            value.setYHeadRot(player.getYHeadRot());
            pandaMap.put(uuid, value);
            clientLevel.addEntity(pandaMap.get(uuid));
        }else {
            pandaMap.get(uuid);
        }
    }
    static {
        OnGameStartedClient.EVENT.register(() -> {
            pandaMap.clear();
        });
        OnGameFinishedClient.EVENT.register(() -> {
            pandaMap.clear();
        });
//        ClientTickEvents.END_CLIENT_TICK.register(
//                client -> {
//                    ClientLevel level = client.level;
//                    if (level.getGameTime()%20==0){
//
//                    }
//
//                }
//        );
    }
}
