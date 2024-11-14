package org.cryptomator.ipc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class RevealRunningAppMessageTest {

	@Test
	public void testSendAndReceive(@TempDir Path tmpDir) throws IOException {
		var message = new RevealRunningAppMessage();

		var file = tmpDir.resolve("tmp.file");
		try (var ch = FileChannel.open(file, StandardOpenOption.CREATE_NEW, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
			message.send(ch);
			ch.position(0);
			if (IpcMessage.receive(ch) instanceof RevealRunningAppMessage received) {
				Assertions.assertNotNull(received);
			} else {
				Assertions.fail("Received message of unexpected class");
			}
		}
	}

	// Test pour décoder un message malformé
	@Test
	public void testDecodeMalformedMessage() {
		// Arrange: Crée un ByteBuffer contenant des données aléatoires
		ByteBuffer buffer = ByteBuffer.wrap("randomData".getBytes());

		// Act: Décode le message à partir du buffer
		RevealRunningAppMessage message = RevealRunningAppMessage.decode(buffer);

		// Assert
		Assertions.assertNotNull(message, "Decoded message should not be null"); // Vérifie que le message décodé n'est pas nul
		Assertions.assertEquals(IpcMessage.MessageType.REVEAL_RUNNING_APP, message.getMessageType(),
				"Message type should be REVEAL_RUNNING_APP even with malformed data"); // Vérifie que le type de message est correct même avec des données malformées
	}
}