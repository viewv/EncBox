package top.viewv.model.tools;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class PasswordGenerate {

    public static String generatePassword(int numberOflowCase,int numberOfupperCase,
                                         int numberOfdigitalCase,int numberOfspecialCase,int passwordLenght) {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(numberOflowCase);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(numberOfupperCase);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(numberOfdigitalCase);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "Error";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(numberOfspecialCase);

        assert passwordLenght > (numberOfdigitalCase+numberOflowCase+numberOfspecialCase+numberOfupperCase) :
                "Password length is too small";

        return gen.generatePassword(passwordLenght, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }
}
