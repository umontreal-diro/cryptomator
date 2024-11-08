package org.cryptomator.ipc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class IpcCommunicatorTest {

	@Test
	public void testSendAndReceive(@TempDir Path tmpDir) throws IOException, InterruptedException {
		var socketPath = tmpDir.resolve("foo.sock");
		try (var server = IpcCommunicator.create(List.of(socketPath));
			 var client = IpcCommunicator.create(List.of(socketPath))) {
			Assertions.assertNotSame(server, client);

			var cdl = new CountDownLatch(1);
			var executor = Executors.newSingleThreadExecutor();
			server.listen(new IpcMessageListener() {
				@Override
				public void revealRunningApp() {
					cdl.countDown();
				}

				@Override
				public void handleLaunchArgs(List<String> args) {

				}
			}, executor);
			client.sendRevealRunningApp();

			Assertions.assertTimeoutPreemptively(Duration.ofMillis(300), (Executable) cdl::await);
			executor.shutdown();
		}
	}

	/**
	 * Teste la création d'un communicateur IPC avec un chemin de socket invalide.
	 * Ce test vérifie que lorsqu'un chemin de socket inexistant est fourni,
	 * le système renvoie un communicateur de type LoopbackCommunicator.
	 */
	@Test
	public void testCreateWithInvalidSocketPath(@TempDir Path tmpDir) throws IOException {
		// Arrange: Définit un chemin de socket invalide dans un répertoire temporaire
		var invalidSocketPath = tmpDir.resolve("invalid.sock");

		// Vérifie que le chemin de socket n'existe pas
		Assertions.assertFalse(Files.exists(invalidSocketPath), "Le chemin du socket ne doit pas exister.");

		// Act: Crée un communicateur IPC avec le chemin de socket invalide
		IpcCommunicator communicator = IpcCommunicator.create(List.of(invalidSocketPath));

		// Assert: Vérifie que le communicateur retourné est une instance de LoopbackCommunicator
		Assertions.assertInstanceOf(LoopbackCommunicator.class, communicator,
				"Un LoopbackCommunicator devait être retourné lorsque le chemin de socket est invalide.");

		// Ferme le communicateur pour libérer les ressources
		communicator.close();
	}

}