package com.ft.emulator.server.game.server;

import com.ft.emulator.common.utilities.BitKit;
import com.ft.emulator.server.shared.module.Client;
import com.ft.emulator.server.shared.module.GameHandler;
import com.ft.emulator.server.shared.networking.NetworkThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

public class GameServer extends NetworkThread {

    private final static Logger logger = LoggerFactory.getLogger("gameserver");

    private final PacketHandler packetHandler;
    private final GameHandler gameHandler;

    public GameServer(int port) {

        super(port);

        this.gameHandler = new GameHandler();
        this.packetHandler = new PacketHandler(this.gameHandler);
    }

    @Override
    public void listenerThread() {

	try {

	    this.serverSocket = new ServerSocket(this.port, 600);
	    this.stopped = false;
	}
	catch (Exception e) {
	    logger.error("Couldn't start listener thread!");
	    logger.error(e.getMessage());

	    this.stop();

	}

	while (!this.stopped) {

	    try {

		Client client = new Client(this.serverSocket.accept());
		this.gameHandler.addClient(client);

		new Thread(() -> receivingThread(client)).start();
	    }
	    catch (Exception e) {

		logger.error(e.getMessage());
		break;
	    }
	}
    }

    @Override
    public void receivingThread(Client client) {

	PacketStream clientStream = client.getPacketStream();

	byte[] clientBuffer = new byte[4096];

	packetHandler.sendWelcomePacket(client);

	while ((!this.stopped) && (clientStream.read(clientBuffer, 0, 8) != -1)) {

	    if (BitKit.bytesToShort(clientBuffer, 6) > -1)
		clientStream.read(clientBuffer, 8, BitKit.bytesToShort(clientBuffer, 6));

	    Packet packet = new Packet(clientBuffer);
	    logger.info((this.port == 5896 ? "RELAY " : "") + "RECV [" + String.format("0x%x", (int)packet.getPacketId()) + "] " + BitKit.toString(packet.getRawPacket(), 0, packet.getDataLength() + 8));

	    packetHandler.handlePacket(client, packet);
	}
	packetHandler.handleDisconnect(client);
    }
}