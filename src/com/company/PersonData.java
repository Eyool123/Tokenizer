package com.company;

import java.io.Serializable;

public class PersonData implements Serializable {

    private WordFreq personWordFreq;
    private PersonalityType personalityType;

    public PersonData(String personalityType, String[] words){

        this.personalityType = new PersonalityType(personalityType);
        this.personWordFreq= new WordFreq(words);

    }
    public PersonData(PersonalityType personalityType, WordFreq wordFreq){
        this.personalityType = personalityType;
        this.personWordFreq = wordFreq;
    }

    public PersonalityType getPersonalityType() {
        return personalityType;
    }

    public WordFreq getPersonWordFreq() {
        return personWordFreq;
    }
}
