package com.ft.emulator.server.game.core.packet.packets.matchplay;

import com.ft.emulator.server.game.core.packet.PacketID;
import com.ft.emulator.server.networking.packet.Packet;

public class S2CMatchplayDamageToPlayer extends Packet {
    public S2CMatchplayDamageToPlayer(short playerHpToSet) {
        super(PacketID.S2CMatchplayDamageToPlayer);

        this.write((short) 0); // Player position
        this.write(playerHpToSet);
        this.write((short) 0); //Unk
        this.write((byte) 0); // Animations
        this.write(0); //Unk
        this.write(0); //Unk
    }
}


// LIST OF ANIMATIONS
// 0 = Damage animation, (1,2,16,17,18,19) = Heal animation, (3,4,6,12) = Nothing
// 5 = Rebirth, 7 = Transform to chicken, 8 = Backwards salto??
// 9 = Thunder (getting small), (10,20) = Shield, 11 = Blind
// 14 = Flying away (because of dmg), 15 = Stone paralysis
// 25 = Confusion, 34 = Color splash on cam (somewhat blind)
// 40 = Buff?, 41 = Buff?, 42 = Buff?, 43 = Buff?, 44 = Buff?
// 46 = Speed ball buff, 46 = Buff?, 47 = Will buff, 48 = Buff?
// 49 = Sta buff, 50-56 (already same buffs), 57 = (Skeleton appears)?
// (59,60) = Firework, (64,65) = Burning (Maybe caused by Tosakkan)