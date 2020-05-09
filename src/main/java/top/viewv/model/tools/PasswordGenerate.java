package top.viewv.model.tools;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGenerate {

    public static String generatePassword( int numberOfupperCase, int numberOfspecialCase, int passwordLenght) {

        List<CharacterRule> characterRules = new ArrayList<>();

        PasswordGenerator gen = new PasswordGenerator();

        SecureRandom secureRandom = new SecureRandom();

        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);

        int numberOflowCase = passwordLenght - numberOfupperCase - numberOfspecialCase;

        if (numberOflowCase > 0){
            numberOflowCase = secureRandom.nextInt(numberOflowCase) + 1;
            lowerCaseRule.setNumberOfCharacters(numberOflowCase);
            characterRules.add(lowerCaseRule);
        }else {
            numberOflowCase = 0;
        }

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);

        if (numberOfupperCase != 0){
            upperCaseRule.setNumberOfCharacters(numberOfupperCase);
            characterRules.add(upperCaseRule);
        }

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);

        int numberOfdigitalCase = passwordLenght - numberOflowCase - numberOfupperCase -numberOfspecialCase;

        if (numberOfdigitalCase > 0){
            numberOfdigitalCase = secureRandom.nextInt(numberOfdigitalCase) + 1;
            digitRule.setNumberOfCharacters(numberOfdigitalCase);
            characterRules.add(digitRule);
        }

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "Error";
            }
            public String getCharacters() {
                return "!@#$%^&*()_+=";
            }
        };

        CharacterRule splCharRule = new CharacterRule(specialChars);
        if (numberOfspecialCase != 0){
            splCharRule.setNumberOfCharacters(numberOfspecialCase);
            characterRules.add(splCharRule);
        }

        return gen.generatePassword(passwordLenght,characterRules);
    }
}
