package com.ft.emulator.server.game.core.matchplay.room;

import com.ft.emulator.server.database.model.player.Player;
import com.ft.emulator.server.shared.module.Client;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameSession {
    public GameSession() {
        clients = new ArrayList<>();
    }

    private int sessionId;
    private int lastBallHitByTeam = -1;
    private long timeLastBallWasHit = -1;
    private List<Client> clients;
    private Room room;
}