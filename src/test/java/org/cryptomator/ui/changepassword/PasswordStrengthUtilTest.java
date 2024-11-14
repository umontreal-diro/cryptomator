package org.cryptomator.ui.changepassword;

import com.google.common.base.Strings;
import org.cryptomator.common.Environment;
import org.cryptomator.ui.changepassword.PasswordStrengthUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.ResourceBundle;

public class PasswordStrengthUtilTest {

	@Test
	public void testLongPasswords() {
		PasswordStrengthUtil util = new PasswordStrengthUtil(Mockito.mock(ResourceBundle.class), Mockito.mock(Environment.class));
		String longPw = Strings.repeat("x", 10_000);
		Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
			util.computeRate(longPw);
		});
	}

	@Test
	public void testIssue979() {
		PasswordStrengthUtil util = new PasswordStrengthUtil(Mockito.mock(ResourceBundle.class), Mockito.mock(Environment.class));
		int result1 = util.computeRate("backed derrick buckling mountains glove client procedures desire destination sword hidden ram");
		int result2 = util.computeRate("backed derrick buckling mountains glove client procedures desire destination sword hidden ram escalation");
		Assertions.assertEquals(4, result1);
		Assertions.assertEquals(4, result2);
	}

	// Nouveau test jhosim
	@Test
	public void testGetStrengthDescriptionTooShort(){

		String RESSOURCE_PREFIX = "passwordStrength.messageLabel.";

		ResourceBundle fakeBundle = Mockito.mock(ResourceBundle.class);
		Environment fakeEnvironment = Mockito.mock(Environment.class);

		PasswordStrengthUtil util = new PasswordStrengthUtil(fakeBundle, fakeEnvironment);

		Mockito.when(fakeBundle.getString(RESSOURCE_PREFIX + "tooShort")).thenReturn("too short bro");
		Mockito.when(fakeEnvironment.getMinPwLength()).thenReturn(10);

		String test = util.getStrengthDescription(-1);

		Assertions.assertEquals("too short bro", test);

	}
	// Nouveau test jhosim
	@Test
	public void testGetStrengthDescriptionValid(){

		String RESSOURCE_PREFIX = "passwordStrength.messageLabel.";

		ResourceBundle fakeBundle = Mockito.mock(ResourceBundle.class);
		Environment fakeEnvironment = Mockito.mock(Environment.class);

		PasswordStrengthUtil util = new PasswordStrengthUtil(fakeBundle, fakeEnvironment);

		Mockito.when(fakeBundle.containsKey(RESSOURCE_PREFIX + "11")).thenReturn(true);
		Mockito.when(fakeBundle.getString(RESSOURCE_PREFIX+"11")).thenReturn("long enough bro");

		String test = util.getStrengthDescription(11);

		Assertions.assertEquals("long enough bro", test);

	}
	// Nouveau test jhosim
	@Test
	public void testGetStrengthDescriptionNoKey(){

		String RESSOURCE_PREFIX = "passwordStrength.messageLabel.";

		ResourceBundle fakeBundle = Mockito.mock(ResourceBundle.class);
		Environment fakeEnvironment = Mockito.mock(Environment.class);

		PasswordStrengthUtil util = new PasswordStrengthUtil(fakeBundle, fakeEnvironment);

		Mockito.when(fakeBundle.containsKey(RESSOURCE_PREFIX + "4")).thenReturn(false);

		String test = util.getStrengthDescription(4);

		Assertions.assertEquals("", test);
	}


	@Test
	public void fullfillsMinimumrequirementsTest(){

		String RESSOURCE_PREFIX = "passwordStrength.messageLabel.";
		ResourceBundle fakeBundle = Mockito.mock(ResourceBundle.class);
		Environment fakeEnvironment = Mockito.mock(Environment.class);
		Mockito.when(fakeEnvironment.getMinPwLength()).thenReturn(10);
		PasswordStrengthUtil util = new PasswordStrengthUtil(fakeBundle, fakeEnvironment);

		boolean testTrue = util.fulfillsMinimumRequirements("12345678910");
		boolean testFalse = util.fulfillsMinimumRequirements("12345");


		Assertions.assertTrue(testTrue);
		Assertions.assertFalse(testFalse);

	}

	/**
	 * Le test suivant a pour objectif de vérifier que la méthode fulfillsMinimumRequirements()
	 * de la classe PasswordStrengthUtil fonctionne correctement en s'assurant qu'un mot de passe
	 * respecte les exigences minimales de longueur.
	 */
	@Test
	public void testFulfillsMinimumRequirements() {

		// Arrange: Initialiser les conditions du test
		int minPwLength = 8; // Définir la longueur minimale du mot de passe pour ce test
		ResourceBundle mockResourceBundle = Mockito.mock(ResourceBundle.class); // Simuler le bundle de ressources
		Environment mockEnvironment = Mockito.mock(Environment.class); // Simuler l'environnement

		// Simuler le comportement de l'environnement pour retourner la longueur minimale requise
		Mockito.when(mockEnvironment.getMinPwLength()).thenReturn(minPwLength);

		// Créer une instance de PasswordStrengthUtil avec les objets simulés
		PasswordStrengthUtil util = new PasswordStrengthUtil(mockResourceBundle, mockEnvironment);

		// Définir deux mots de passe : un valide et un trop court
		String validPassword = "validPassword"; // Un mot de passe valide
		String shortPassword = "short"; // Un mot de passe trop court

		// Act: Exécuter les actions qui doivent être testées
		boolean isValid = util.fulfillsMinimumRequirements(validPassword); // Vérifier le mot de passe valide
		boolean isShort = util.fulfillsMinimumRequirements(shortPassword); // Vérifier le mot de passe trop court

		// Assert: Vérifier que les résultats sont conformes aux attentes
		Assertions.assertTrue(isValid, "Le mot de passe valide devrait satisfaire les exigences minimales.");
		Assertions.assertFalse(isShort, "Le mot de passe trop court ne devrait pas satisfaire les exigences minimales.");
	}


}
