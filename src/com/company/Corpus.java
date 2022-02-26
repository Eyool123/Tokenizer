package com.company;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Corpus implements Serializable {

    private List<PersonData> personDataList;
    private WordFreq documentFreqOfWords; // word occur (1) in each document (person)
    private int totalNumberOfWords;

    private static final long serialVersionUID = 1L;


    public Corpus(){
        this.personDataList = new LinkedList<>();
        documentFreqOfWords = new WordFreq();
        this.totalNumberOfWords = 0;
    }




    //A copy constructor in order to filter words with minimum Document Frequency in corpus documents
    public Corpus(Corpus corpus1,Corpus corpus2, float filterMinimumDF, float filterMaximumDF){
        this();

        for(PersonData data: corpus1.getPersonDataList()){
            WordFreq filteredWordFreq = new WordFreq();


            //we iterate over each word freqMapCount and create a new word freqMap of filtered words only
            for (Map.Entry<String, Integer> word : data.getPersonWordFreq().getWordCount().entrySet()){

                //if word df is more than min than we add it to word count
//                float df = corpus.getDocumentFrequencyOfWord(word.getKey());
                float df1 = corpus1.getWordFreqInCorpus(word.getKey());
                float df2 = corpus2.getWordFreqInCorpus(word.getKey());

                if( (df1>=filterMinimumDF&&df1<=filterMaximumDF) ||  (df2>=filterMinimumDF&&df2<=filterMaximumDF) )
                    filteredWordFreq.addWordMul(word.getKey(), word.getValue());

            }

            addPersonToCorpus(new PersonData(data.getPersonalityType(), filteredWordFreq));
        }


        for(PersonData data: corpus2.getPersonDataList()){
            WordFreq filteredWordFreq = new WordFreq();


            //we iterate over each word freqMapCount and create a new word freqMap of filtered words only
            for (Map.Entry<String, Integer> word : data.getPersonWordFreq().getWordCount().entrySet()){

                //if word df is more than min than we add it to word count
//                float df = corpus.getDocumentFrequencyOfWord(word.getKey());
                float df1 = corpus1.getWordFreqInCorpus(word.getKey());
                float df2 = corpus2.getWordFreqInCorpus(word.getKey());

                if( (df1>=filterMinimumDF&&df1<=filterMaximumDF) ||  (df2>=filterMinimumDF&&df2<=filterMaximumDF)  )
                    filteredWordFreq.addWordMul(word.getKey(), word.getValue());

            }

            addPersonToCorpus(new PersonData(data.getPersonalityType(), filteredWordFreq));
        }


    }





    public Corpus(Corpus corpus, int dimension, Enum type){
        this();

        for(PersonData data: corpus.getPersonDataList()){
            if(data.getPersonalityType().getDim(dimension)==type){
                WordFreq filteredWordFreq = new WordFreq();

                //we iterate over each word freqMapCount and create a new word freqMap of filtered types only
                for (Map.Entry<String, Integer> word : data.getPersonWordFreq().getWordCount().entrySet()){


                    filteredWordFreq.addWordMul(word.getKey(), word.getValue());

                }

                addPersonToCorpus(new PersonData(data.getPersonalityType(), filteredWordFreq));
            }

        }

    }

    public void addPersonToCorpus(PersonData personData){
        personDataList.add(personData);

        for(String word : personData.getPersonWordFreq().getWordCount().keySet()){
            documentFreqOfWords.addWord(word);
            totalNumberOfWords++;
        }
    }
    public int getNumberOfPeople(){
        return this.personDataList.size();
    }

    public List<PersonData> getPersonDataList() {
        return personDataList;
    }

    public WordFreq getDocumentFreqOfWords() {
        return documentFreqOfWords;
    }

    public int getDocumentFrequencyOfWord(String word){
        if(documentFreqOfWords.getWordCount().containsKey(word))
            return documentFreqOfWords.getWordCount().get(word);
        else
            return 0;
    }


    public int getNumberOfUniqueWords(){
        return documentFreqOfWords.getWordCount().size();
    }
    void printCorpus(){
        this.documentFreqOfWords.printWordCount();
        System.out.println();
        System.out.println("Number of people: "+this.getNumberOfPeople());
        System.out.println("Number of different words: "+this.documentFreqOfWords.getWordCount().size());
        System.out.println("Total number of words: "+totalNumberOfWords);
    }


    //get in how many documents in the corpus the word appears
    int getNumberOfDocumentWordAppearsIn(String word){

        if(documentFreqOfWords.getWordCount().containsKey(word)){
            return documentFreqOfWords.getWordCount().get(word);
        }else
            return 0;
    }

    //return 0-1 of word freq in corpus wordAppearInNDocuments / numberOfDocuments
    float getWordFreqInCorpus(String word){
        return (float) getNumberOfDocumentWordAppearsIn(word)/this.getNumberOfPeople();

    }
}
