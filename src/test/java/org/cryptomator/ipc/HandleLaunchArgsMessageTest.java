package org.cryptomator.ipc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class HandleLaunchArgsMessageTest {

	@Test
	public void testSendAndReceive(@TempDir Path tmpDir) throws IOException {
		var message = new HandleLaunchArgsMessage(List.of("hello world", "foo bar"));

		var file = tmpDir.resolve("tmp.file");
		try (var ch = FileChannel.open(file, StandardOpenOption.CREATE_NEW, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
			message.send(ch);
			ch.position(0);
			if (IpcMessage.receive(ch) instanceof HandleLaunchArgsMessage received) {
				Assertions.assertArrayEquals(message.args().toArray(), received.args().toArray());
			} else {
				Assertions.fail("Received message of unexpected class");
			}
		}
	}

	@Test
	public void testSendAndReceiveEmpty(@TempDir Path tmpDir) throws IOException {
		var message = new HandleLaunchArgsMessage(List.of());

		var file = tmpDir.resolve("tmp.file");
		try (var ch = FileChannel.open(file, StandardOpenOption.CREATE_NEW, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
			message.send(ch);
			ch.position(0);
			if (IpcMessage.receive(ch) instanceof HandleLaunchArgsMessage received) {
				Assertions.assertArrayEquals(message.args().toArray(), received.args().toArray());
			} else {
				Assertions.fail("Received message of unexpected class");
			}
		}
	}
	/**
	 * Teste le décodage d'un message malformé pour s'assurer que le système peut gérer
	 * une entrée inattendue de manière appropriée. Ce test vérifie le comportement du décodeur
	 * lorsque l'entrée ne respecte pas le format attendu.
	 */
	@Test
	public void testDecodeMalformedMessage() {
		// Arrange : Prépare une chaîne malformée qui n'utilise pas le délimiteur attendu.
		String malformedData = "hello world foo bar";
		// Encode la chaîne malformée dans un ByteBuffer pour le décodage.
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(malformedData);

		// Act : Décode le ByteBuffer malformé en un HandleLaunchArgsMessage.
		HandleLaunchArgsMessage message = HandleLaunchArgsMessage.decode(buffer);

		// Assert : Vérifie que le décodeur gère correctement l'entrée malformée.
		// Vérifie que le message résultant contient un argument.
		Assertions.assertEquals(1, message.args().size(), "On s'attend à ce qu'il y ait un argument dans le message décodé.");
		// Vérifie que l'argument correspond à la chaîne d'entrée complète.
		Assertions.assertEquals("hello world foo bar", message.args().getFirst(), "On s'attend à ce que le premier argument soit la chaîne d'entrée malformée complète.");
	}


}