package org.cryptomator.common.keychain;


import org.cryptomator.common.settings.Settings;
import org.cryptomator.integrations.keychain.KeychainAccessException;
import org.cryptomator.integrations.keychain.KeychainAccessProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javafx.application.Platform;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class KeychainManagerTest {

	@Test
	public void testStoreAndLoad() throws KeychainAccessException {
		KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
		keychainManager.storePassphrase("test", "Test", "asd");
		Assertions.assertArrayEquals("asd".toCharArray(), keychainManager.loadPassphrase("test"));
	}

	@Nested
	public static class WhenObservingProperties {

		@BeforeAll
		public static void startup() throws InterruptedException {
			CountDownLatch latch = new CountDownLatch(1);
			Platform.startup(latch::countDown);
			var javafxStarted = latch.await(5, TimeUnit.SECONDS);
			Assumptions.assumeTrue(javafxStarted);
		}

		@Test
		public void testPropertyChangesWhenStoringPassword() throws KeychainAccessException, InterruptedException {
			KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
			ReadOnlyBooleanProperty property = keychainManager.getPassphraseStoredProperty("test");
			Assertions.assertFalse(property.get());

			keychainManager.storePassphrase("test", null,"bar");

			AtomicBoolean result = new AtomicBoolean(false);
			CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {
				result.set(property.get());
				latch.countDown();
			});
			Assertions.assertTimeoutPreemptively(Duration.ofSeconds(1), () -> latch.await());
			Assertions.assertTrue(result.get());
		}

	}


	@Test
	public void testDeletePassphrase() throws KeychainAccessException {
		KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
		keychainManager.storePassphrase("test-key", "test-display", "test-pass");
		keychainManager.deletePassphrase("test-key");
		Assertions.assertFalse(keychainManager.isPassphraseStored("test-key"));
	}
	/**
	 Le but de ce test est de vérifier que la méthode changePassphrase fonctionne correctement.
	 Ça vérifie lorsqu'une passphrase est changé, la nouvelle passphrase doit être correctement stocké et récupérée.
	 */
	@Test
	public void testChangePassphrase() throws KeychainAccessException {
		// Arrange: Crée une instance de KeychainManager avec une implémentation de MapKeychainAccess pour la gestion des mots de passe
		KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));

		// Définit une clé pour stocker la phrase de passe
		String key = "test-key";

		// Définit l'ancienne phrase de passe qui sera stockée
		String oldPassphrase = "old-pass";

		// Définit la nouvelle phrase de passe qui sera utilisée pour remplacer l'ancienne
		String newPassphrase = "new-pass";

		// Act: Stocke l'ancienne phrase de passe sous la clé spécifiée
		keychainManager.storePassphrase(key, "Test Display", oldPassphrase);

		// Change la phrase de passe pour la clé spécifiée, en remplaçant l'ancienne par la nouvelle
		keychainManager.changePassphrase(key, "Test Display", newPassphrase);

		// Assert: Vérifie que la nouvelle phrase de passe a été correctement stockée et peut être récupérée
		Assertions.assertArrayEquals(newPassphrase.toCharArray(), keychainManager.loadPassphrase(key));
	}

}