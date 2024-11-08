package org.cryptomator.ipc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class LoopbackCommunicatorTest {

	@Test
	public void testSendAndReceive() {
		try (var communicator = new LoopbackCommunicator()) {
			var cdl = new CountDownLatch(1);
			var executor = Executors.newSingleThreadExecutor();
			communicator.listen(new IpcMessageListener() {
				@Override
				public void revealRunningApp() {
					cdl.countDown();
				}

				@Override
				public void handleLaunchArgs(List<String> args) {

				}
			}, executor);
			communicator.sendRevealRunningApp();

			Assertions.assertTimeoutPreemptively(Duration.ofMillis(300), (Executable) cdl::await);
			executor.shutdown();
		}
	}

	/**
	 * Teste l'envoi de plusieurs messages et la réception de chacun d'eux.
	 * S'assure que le compteur atteint zéro après la réception des trois messages.
	 */
	@Test
	public void testSendMultipleMessages() throws InterruptedException {
		// Arrange: Crée une instance de LoopbackCommunicator pour le test
		try (var communicator = new LoopbackCommunicator()) {
			// Crée un compteur de latch pour attendre la réception de trois messages
			var cdl = new CountDownLatch(3);
			// Crée un exécuteur pour gérer l'écoute dans un thread séparé
			var executor = Executors.newSingleThreadExecutor();

			// Configure le listener pour gérer les messages entrants
			communicator.listen(new IpcMessageListener() {
				@Override
				public void revealRunningApp() {
					// Décrémente le compteur lorsque le message revealRunningApp est reçu
					cdl.countDown();
				}

				@Override
				public void handleLaunchArgs(List<String> args) {
					// Décrémente le compteur lorsque le message handleLaunchArgs est reçu
					cdl.countDown();
				}
			}, executor);

			// Act: Envoie plusieurs messages
			communicator.sendRevealRunningApp();  // Envoie le premier message
			communicator.sendHandleLaunchargs(List.of("arg1", "arg2"));  // Envoie le deuxième message
			communicator.sendRevealRunningApp();  // Envoie le troisième message

			// Assert: Attend que tous les messages soient reçus
			Assertions.assertTimeoutPreemptively(Duration.ofMillis(500), (Executable) cdl::await);

			// Arrête l'exécuteur pour libérer les ressources
			executor.shutdown();
		}
	}




}