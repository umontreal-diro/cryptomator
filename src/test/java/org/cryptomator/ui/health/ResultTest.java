package org.cryptomator.ui.health;

import org.cryptomator.cryptofs.VaultConfig;
import org.cryptomator.cryptofs.health.api.DiagnosticResult;
import org.cryptomator.cryptolib.api.Cryptor;
import org.cryptomator.cryptolib.api.Masterkey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultTest {

	private DiagnosticResult diagnosis;
	private Path vaultPath;
	private VaultConfig config;
	private Masterkey masterkey;
	private Cryptor cryptor;
	private DiagnosticResult.Fix mockFix;

	@BeforeEach
	void setUp() {
		// Mock dependencies
		diagnosis = mock(DiagnosticResult.class);
		vaultPath = mock(Path.class);
		config = mock(VaultConfig.class);
		masterkey = mock(Masterkey.class);
		cryptor = mock(Cryptor.class);
		mockFix = mock(DiagnosticResult.Fix.class); // Mock the Fix object
	}

	@Test
	void testCreateResultNonFixable() {
		// Simulate the diagnosis fix state to return empty (non-fixable)
		when(diagnosis.getFix(vaultPath, config, masterkey, cryptor)).thenReturn(java.util.Optional.empty());

		// Create result
		Result result = Result.create(diagnosis, vaultPath, config, masterkey, cryptor);

		// Verify that the initial state is NOT_FIXABLE
		assertEquals(Result.FixState.NOT_FIXABLE, result.getState());
	}

	@Test
	void testCreateResultFixable() {
		// Simulate a fixable state
		when(diagnosis.getFix(vaultPath, config, masterkey, cryptor)).thenReturn(java.util.Optional.of(mockFix));

		// Create result
		Result result = Result.create(diagnosis, vaultPath, config, masterkey, cryptor);

		// Verify that the initial state is FIXABLE
		assertEquals(Result.FixState.FIXABLE, result.getState());
	}

	@Test
	void testSetState() {
		// Simulate a fixable state
		when(diagnosis.getFix(vaultPath, config, masterkey, cryptor)).thenReturn(java.util.Optional.of(mockFix));

		// Create result
		Result result = Result.create(diagnosis, vaultPath, config, masterkey, cryptor);

		// Set a new state
		result.setState(Result.FixState.FIXING);

		// Verify the state has been updated
		assertEquals(Result.FixState.FIXING, result.getState());
	}
}
