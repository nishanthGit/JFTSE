package com.jftse.emulator.server.game.core.packet.packets.guild;

import com.jftse.emulator.server.networking.packet.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SGuildChangeLogoRequestPacket extends Packet {
    private int pocketIdLogoBackground;
    private int logoBackgroundColor;
    private int pocketIdLogoPattern;
    private int logoPatternColor;
    private int pocketIdLogoMark;
    private int logoMarkColor;

    public C2SGuildChangeLogoRequestPacket(Packet packet) {
        super(packet);

        this.pocketIdLogoBackground = this.readInt();
        this.logoBackgroundColor = this.readInt();
        this.pocketIdLogoPattern = this.readInt();
        this.logoPatternColor = this.readInt();
        this.pocketIdLogoMark = this.readInt();
        this.logoMarkColor = this.readInt();
    }
}
