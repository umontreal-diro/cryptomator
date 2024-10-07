/*******************************************************************************
 * Copyright (c) 2016, 2017 Sebastian Stenzel and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under the terms of the accompanying LICENSE file.
 *
 * Contributors:
 *     Sebastian Stenzel - initial API and implementation
 *******************************************************************************/
package org.cryptomator.common.settings;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VaultSettingsTest {

	@ParameterizedTest(name = "VaultSettings.normalizeDisplayName({0}) = {1}")
	@CsvSource(value = {
			"a\u000Fa,a_a",
			": \\,_ _",
			"汉语,汉语",
			"..,_",
			".,_",
			",_",
			"a\ta,a\u0020a",
			"'\t\n\r',_"
	})
	public void testNormalize(String test, String expected) {
		assertEquals(expected, VaultSettings.normalizeDisplayName(test == null ? "" : test));
	}

	@Test
	public void testEqualsSameId() {
		var json1 = new VaultSettingsJson();
		var json2 = new VaultSettingsJson();
		json1.id = "some_id";
		json2.id = "some_id";

		var vaultSettings1 = new VaultSettings(json1);
		var vaultSettings2 = new VaultSettings(json2);

		// VaultSettings returns true when comparing with
		// another VaultSettings having the same id value in its JSON.
		Assertions.assertEquals(vaultSettings1, vaultSettings2);
	}

	@Test
	public void testNotEqualsSameId() {
		var json1 = new VaultSettingsJson();
		var json2 = new VaultSettingsJson();
		json1.id = "some_id";
		json2.id = "another_id";

		var vaultSettings1 = new VaultSettings(json1);
		var vaultSettings2 = new VaultSettings(json2);

		// VaultSettings returns false when comparing with
		// another VaultSettings having a different id value in its JSON.
		Assertions.assertNotEquals(vaultSettings1, vaultSettings2);
	}

	private class VaultSettings2 extends VaultSettings {
		VaultSettings2(VaultSettingsJson json) {
			super(json);
		}
	}

	@Test
	public void testNotEqualsObjectClass1() {
		var json1 = new VaultSettingsJson();
		var json2 = new VaultSettingsJson();
		json1.id = "some_id";
		json2.id = "some_id";

		var vaultSettings1 = new VaultSettings(json1);
		var vaultSettings2 = new VaultSettings2(json2);

		// VaultSettings returns false when comparing with a subclass of VaultSettings.
		Assertions.assertNotEquals(vaultSettings1, vaultSettings2);
	}

	@Test
	public void testNotEqualsObjectClass2() {
		var json1 = new VaultSettingsJson();
		json1.id = "some_id";

		var vaultSettings1 = new VaultSettings(json1);
		var randomObject = new String("random string");

		// VaultSettings returns false when comparing with another object class.
		Assertions.assertNotEquals(vaultSettings1, randomObject);
	}

	@Test
	public void testMountName1() {
		var json = new VaultSettingsJson();
		json.displayName = "some string";

		var vaultSettings = new VaultSettings(json);

		Assertions.assertEquals("some string", vaultSettings.mountName.get());
	}

	@Test
	public void testMountName2() {
		var json = new VaultSettingsJson();
		json.path = "./somePath";

		var vaultSettings = new VaultSettings(json);

		Assertions.assertEquals("somePath", vaultSettings.mountName.get());
	}

}
