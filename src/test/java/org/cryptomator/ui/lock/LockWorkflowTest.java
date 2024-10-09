package org.cryptomator.ui.lock;

import javafx.scene.Scene;
import javafx.stage.Stage;

import dagger.Lazy;
import org.cryptomator.common.vaults.Vault;
import org.cryptomator.ui.common.FxmlFile;
import org.cryptomator.ui.common.FxmlScene;
import org.cryptomator.ui.fxapp.FxApplicationWindows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("LockWorkflow Tests")
class LockWorkflowTest {

	@Mock
    private Vault vault;
    @Mock
    private AtomicReference<CompletableFuture<Boolean>> forceRetryDecision;

	@InjectMocks
    private LockWorkflow lockWorkflow;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test lock without force")
    void testLockWithoutForce() throws Exception {
        doNothing().when(vault).lock(false);
        lockWorkflow.call();
        verify(vault).lock(false);
    }

    @Test
    @DisplayName("Test lock failure")
    void testLockFailure() throws Exception {
        doThrow(IllegalStateException.class).when(vault).lock(false);
        when(forceRetryDecision.get()).thenReturn(CompletableFuture.completedFuture(false));

        assertThrows(IllegalStateException.class, () -> lockWorkflow.call());
    }
}