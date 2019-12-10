package com.ft.emulator.server.game.server.packets.inventory;

import com.ft.emulator.server.game.server.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SInventorySellReqPacket extends Packet {

    private int itemPocketId;

    public C2SInventorySellReqPacket(Packet packet) {

        super(packet);

        this.itemPocketId = this.readInt();
    }
}