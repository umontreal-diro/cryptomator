/*******************************************************************************
 * Copyright (c) 2017 Skymatic UG (haftungsbeschränkt).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the accompanying LICENSE file.
 *******************************************************************************/
package org.cryptomator.launcher;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.FileSystem;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileOpenRequestHandlerTest {

	private FileOpenRequestHandler inTest;
	private BlockingQueue<AppLaunchEvent> queue;

	@BeforeEach
	public void setup() {
		queue = new ArrayBlockingQueue<>(1);
		inTest = new FileOpenRequestHandler(queue);
	}

	@Test
	@DisplayName("./cryptomator.exe foo bar")
	public void testOpenArgsWithCorrectPaths() {
		inTest.handleLaunchArgs(List.of("foo", "bar"));

		AppLaunchEvent evt = queue.poll();
		Assertions.assertNotNull(evt);
		Collection<Path> paths = evt.pathsToOpen();
		MatcherAssert.assertThat(paths, CoreMatchers.hasItems(Paths.get("foo"), Paths.get("bar")));
	}

	@Test
	@DisplayName("./cryptomator.exe foo (with 'foo' being an invalid path)")
	public void testOpenArgsWithIncorrectPaths() {
		FileSystem fs = Mockito.mock(FileSystem.class);
		Mockito.when(fs.getPath("foo")).thenThrow(new InvalidPathException("foo", "foo is not a path"));
		inTest.handleLaunchArgs(fs, List.of("foo"));

		AppLaunchEvent evt = queue.poll();
		Assertions.assertNull(evt);
	}

	@Test
	@DisplayName("./cryptomator.exe foo (with full event queue)")
	public void testOpenArgsWithFullQueue() {
		queue.add(new AppLaunchEvent(AppLaunchEvent.EventType.OPEN_FILE, Collections.emptyList()));
		Assumptions.assumeTrue(queue.remainingCapacity() == 0);

		inTest.handleLaunchArgs(List.of("foo"));
	}

	/**
	 * Teste le comportement de la méthode handleLaunchArgs lorsque des chemins valides et invalides sont fournis.
	 * On s'attend à ce que seul le chemin valide soit ajouté à l'événement d'ouverture.
	 */
	@Test
	@DisplayName("./cryptomator.exe valid path and invalid path")
	public void testOpenArgsWithMixedPaths() {
		// Arrange: Crée un système de fichiers simulé qui lance une InvalidPathException pour "invalidPath"
		FileSystem fs = Mockito.mock(FileSystem.class);
		Mockito.when(fs.getPath("validPath")).thenReturn(Paths.get("validPath")); // Configuration du chemin valide
		Mockito.when(fs.getPath("invalidPath")).thenThrow(new InvalidPathException("invalidPath", "invalidPath is not a valid path")); // Configuration du chemin invalide

		// Act: Appelle handleLaunchArgs avec un chemin valide et un chemin invalide
		inTest.handleLaunchArgs(fs, List.of("validPath", "invalidPath"));

		// Assert: Vérifie que l'événement a été ajouté à la queue et qu'il ne contient que le chemin valide
		AppLaunchEvent evt = queue.poll();
		Assertions.assertNotNull(evt, "On s'attend à ce qu'un AppLaunchEvent soit présent dans la queue."); // Vérifie que l'événement n'est pas nul

		Collection<Path> paths = evt.pathsToOpen();
		Assertions.assertEquals(1, paths.size(), "On s'attend à ce qu'il n'y ait qu'un seul chemin valide dans l'événement."); // Vérifie qu'il n'y a qu'un chemin
		Assertions.assertTrue(paths.contains(Paths.get("validPath")), "On s'attend à ce que l'événement contienne le chemin valide."); // Vérifie que le chemin valide est présent
		Assertions.assertFalse(paths.contains(Paths.get("invalidPath")), "On s'attend à ce que l'événement ne contienne pas le chemin invalide."); // Vérifie que le chemin invalide n'est pas présent
	}


}
