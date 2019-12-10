package com.ft.emulator.server.game.server.packets.chat;

import com.ft.emulator.server.game.server.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SChatRoomReqPacket extends Packet {

    private byte type;
    private String message;

    public C2SChatRoomReqPacket(Packet packet) {

        super(packet);

        this.type = this.readByte();
        this.message = this.readUnicodeString().trim().replaceAll("[^a-zA-Z0-9\\s+]", "");
    }
}