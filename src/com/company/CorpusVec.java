package com.company;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;

public class CorpusVec implements Serializable {

    private List<PersonDataVec> personDataVecList;


    private Map<String, Integer> wordToIndex;
    private String indexToWord[];


    PersonalityType classVec[];
    float featuresVec[][]; //Feature matrix of documents and tfidf in each

    int[] sumOfDims;




    //Corpus vec is used to migrate our corpus to a vector representation of tfidf features
    public CorpusVec(Corpus corpus, Tfidf tfidf){

        sumOfDims = new int[4];
        Arrays.fill(sumOfDims, 0);



        personDataVecList = new LinkedList<>();

        for (PersonData personData : corpus.getPersonDataList()){
            personDataVecList.add(new PersonDataVec(personData,tfidf));

            if(personData.getPersonalityType().isDim(1))
                sumOfDims[0]++;

            if(personData.getPersonalityType().isDim(2))
                sumOfDims[1]++;

            if(personData.getPersonalityType().isDim(3))
                sumOfDims[2]++;

            if(personData.getPersonalityType().isDim(4))
                sumOfDims[3]++;

        }



        classVec = new PersonalityType[personDataVecList.size()];
        featuresVec = new float[personDataVecList.size()][corpus.getNumberOfUniqueWords()];

        wordToIndex = new HashMap<>();
        indexToWord = new String[corpus.getNumberOfUniqueWords()];


        //Initalizes the dictionery in order to link a word to index in vector\matrix
        int i=0;
        for(String uniqueWord : corpus.getDocumentFreqOfWords().getWordCount().keySet()){
            wordToIndex.put(uniqueWord, i);
            indexToWord[i] = uniqueWord;
            i++;
        }

        i=0;
        for(PersonDataVec personDataVec : personDataVecList){
            classVec[i] = personDataVec.getPersonalityType();
            for(Map.Entry<String, Float> tfidfWord : personDataVec.getTfidfWords().entrySet()){
                featuresVec[i][wordToIndex.get(tfidfWord.getKey())] = tfidfWord.getValue();
            }
            i++;
        }

    }




    public void getSumTFIDFvec(){

        float sumI[] = new float[indexToWord.length];
        float sumE[] = new float[indexToWord.length];

        int numOfI = 0;
        int numOfE = 0;

        List<DifferenceTFIDF> printList = new LinkedList<>();

        for (int i=0; i<classVec.length; i++){
            if(classVec[i].getDim1()==Dim1.EXTRAVERSION){
                numOfE++;
                for(int j=0; j<indexToWord.length; j++){
                    sumE[j] += featuresVec[i][j];
                }

            }else {
                numOfI++;
                for(int j=0; j<indexToWord.length; j++){
                    sumI[j] += featuresVec[i][j];
                }
            }

        }

        for(int j=0; j<indexToWord.length; j++){
            sumI[j] /= ((double)numOfI);
            sumE[j] /= ((double) numOfE);

            printList.add(new DifferenceTFIDF((sumI[j]-sumE[j])/ ((float) sumE[j]+(float) sumI[j]),indexToWord[j] ));
        }

        Collections.sort(printList);

        for(DifferenceTFIDF differenceTFIDF : printList){
            differenceTFIDF.printDiff();
        }



    }

    public void writeVecToCsv(String fileName){
        PrintWriter writer;

        try {
            writer = new PrintWriter(fileName);

            StringBuilder sb = new StringBuilder();

            sb.append("class");


            for(int i=0; i<indexToWord.length; i++){
                sb.append(',');
                sb.append(indexToWord[i]);
            }
            sb.append('\n');


            for(int i=0; i<classVec.length; i++){
                if(classVec[i].getDim1()==Dim1.EXTRAVERSION)
                    sb.append('E');
                else
                    sb.append('I');
//                sb.append(classVec[i].getPersonalityTypeString());

                for(int j=0; j<indexToWord.length; j++){
                    sb.append(',');
                    sb.append(featuresVec[i][j]);
                }

                sb.append('\n');
            }


            sb.append('\n');

            writer.write(sb.toString());

            writer.close();





        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
