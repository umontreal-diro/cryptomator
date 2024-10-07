package org.cryptomator.ui.health;

import org.cryptomator.common.vaults.Vault;
import org.cryptomator.cryptofs.VaultConfig;
import org.cryptomator.cryptofs.health.api.HealthCheck;
import org.cryptomator.cryptolib.api.Masterkey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckTest {

	private CheckExecutor checkExecutor;
	private Vault mockVault;
	private AtomicReference<Masterkey> mockMasterkeyRef;
	private AtomicReference<VaultConfig> mockVaultConfigRef;
	private SecureRandom mockCsprng;

	@BeforeEach
	public void setUp() {
		mockVault = mock(Vault.class);
		mockMasterkeyRef = mock(AtomicReference.class);
		mockVaultConfigRef = mock(AtomicReference.class);
		mockCsprng = mock(SecureRandom.class);
		when(mockVault.getPath()).thenReturn(mock(java.nio.file.Path.class));

		checkExecutor = new CheckExecutor(mockVault, mockMasterkeyRef, mockVaultConfigRef, mockCsprng);
	}

	@Test
	public void testExecuteBatch() {
		Check mockCheck = mock(Check.class);
		when(mockCheck.getHealthCheck()).thenReturn(mock(HealthCheck.class));

		checkExecutor.executeBatch(List.of(mockCheck));

		verify(mockCheck).setState(Check.CheckState.SCHEDULED);
	}
}
