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

    // ajout
    // This test verifies the behavior of the computeRate method in the PasswordStrengthUtil class.
    // It checks that an invalid password returns -1, and that the strength comparison between weak and strong passwords is correct.
    @Test
    public void testComputeRate() {
        // Arrange
        // Create an instance of PasswordStrengthUtil with mocked dependencies.
        PasswordStrengthUtil util = new PasswordStrengthUtil(Mockito.mock(ResourceBundle.class),
                Mockito.mock(Environment.class));

        // Define test password scenarios.
        String weakPassword = "123"; // Weak password example.
        String strongPassword = "VeryStrongPassword123!"; // Strong password example.
        String emptyPassword = null; // Represents an invalid password case.

        // Act & Assert
        // Assert that the rate computed for an empty password is -1, indicating it's invalid.
        Assertions.assertEquals(-1, util.computeRate(emptyPassword)); // Invalid case

        // Assert that the computed rate for the weak password is less than that for the strong password.
        Assertions.assertTrue(util.computeRate(weakPassword) < util.computeRate(strongPassword)); // Strength comparison
    }

}
