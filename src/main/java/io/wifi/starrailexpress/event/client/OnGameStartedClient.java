package io.wifi.starrailexpress.event.client;

import io.wifi.starrailexpress.game.GameUtils.WinStatus;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.server.level.ServerLevel;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;


public interface OnGameStartedClient {



    Event<OnGameStartedClient> EVENT = createArrayBacked(OnGameStartedClient.class,
            listeners -> () -> {


            });



    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    void gameStarted();
}