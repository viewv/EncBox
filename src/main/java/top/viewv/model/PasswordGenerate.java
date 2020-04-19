package top.viewv.model;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class PasswordGenerate {

    public static String generatePassayPassword(int numberOflowCase,int numberOfupperCase,
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

        return gen.generatePassword(passwordLenght, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }
}