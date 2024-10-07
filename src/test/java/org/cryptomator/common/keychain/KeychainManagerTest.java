package org.cryptomator.common.keychain;

import org.cryptomator.integrations.keychain.KeychainAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.Duration;
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

    //ajout
    // This test verifies that a passphrase can be deleted from the KeychainManager.
// It checks that after deletion, the passphrase is no longer accessible and is not marked as stored.
    @Test
    public void testDeletePassphrase() throws KeychainAccessException {
        // Arrange
        // Create an instance of KeychainManager and store a passphrase.
        KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
        keychainManager.storePassphrase("test", "Test", "password");

        // Act
        // Delete the stored passphrase.
        keychainManager.deletePassphrase("test");

        // Assert
        // Check that the passphrase is null after deletion.
        Assertions.assertNull(keychainManager.loadPassphrase("test"));
        // Check that the passphrase is marked as not stored.
        Assertions.assertFalse(keychainManager.isPassphraseStored("test"), "Expected passphrase to NOT be stored for testKey after deletion.");
    }
    //ajout
// This test verifies that the passphrase can be changed in the KeychainManager.
// It checks that after changing the passphrase, the new passphrase is accessible.
    @Test
    public void testChangePassphrase() throws KeychainAccessException {
        // Arrange
        // Create an instance of KeychainManager and store a passphrase.
        KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
        keychainManager.storePassphrase("test", "Test", "oldPass");

        // Act
        // Change the stored passphrase to a new value.
        keychainManager.changePassphrase("test", "Test", "newPass");

        // Assert
        // Check that the new passphrase is correctly stored and accessible.
        Assertions.assertArrayEquals("newPass".toCharArray(), keychainManager.loadPassphrase("test"));
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

            keychainManager.storePassphrase("test", null, "bar");

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

}
