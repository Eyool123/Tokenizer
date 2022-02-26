package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Thread.sleep;


public class Main {

    private static final String FILE = "src\\mbti.csv";
    private static final String FILE1 = "C:\\Users\\eyalg\\Desktop\\ML PROJ\\mbti9k_comments.csv";
    private static final String FILEPATH = "C:\\Users\\eyalg\\Desktop\\ML PROJ\\File";
    private static final String FILEPATHFILTER = "C:\\Users\\eyalg\\Desktop\\ML PROJ\\FileFilter";

    private static final String STOPWORDSFILE = "C:\\Users\\eyalg\\Desktop\\ML PROJ\\mbti9k_comments.csv";
    private static final String EICSV = "C:\\Users\\eyalg\\Desktop\\ML PROJ\\EIfile1.csv";


    public static void main(String[] args) {
//
//        Corpus newCorp = makeCorpusOfFile();
//        corpus.printCorpus();


        Corpus newCorp = makeCorpusOfFile1(CSVFIL);
//        corpus1.printCorpus();
        System.out.println("Corpus read");

//        WriteCorpusToFile(newCorp, FILEPATH);




//        Corpus newCorp = readCorpusFromFile(FILEPATH);
        /*
         *  first we read the corpus from csv file
         *  then we save it to a serialize file of all words
         *   then we filter minimum df and save make a new corpuse and save it to filterFile
         *
         *
         * */
//        newCorp.printCorpus();
        Corpus onlyEcorp = new Corpus(newCorp, 1, Dim1.EXTRAVERSION);
        System.out.println("Corpus wrote");




        Corpus onlyICorp = new Corpus(newCorp, 1, Dim1.INTROVERSION);
        System.out.println("Corpus wrote");



        Corpus filterCorp = new Corpus(onlyEcorp, onlyICorp, 0.065f, 1f);
        System.out.println("Corpus wrote");




//        Corpus filterCorp = readCorpusFromFile(FILEPATHFILTER);

//        filterCorp.printCorpus();

//        WriteCorpusToFile(filterCorp, FILEPATHFILTER);



        Tfidf tfidf = new Tfidf(filterCorp);
//        tfidf.print();

        CorpusVec corpusVec = new CorpusVec(filterCorp, tfidf);

//        corpusVec.getSumTFIDFvec();


        corpusVec.writeVecToCsv(EICSV);


    }

    private static Corpus readCorpusFromFile(String filePath) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Corpus corpus = (Corpus) ois.readObject();
            ois.close();
            fis.close();
            return corpus;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static void WriteCorpusToFile(Corpus corpus1, String filePath) {

        try {

            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(corpus1);
            objectOut.close();
            objectOut.flush();
            System.out.println("The Corpus  was succesfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static CorpusVec readCorpusVecFromFile(String filePath) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            CorpusVec corpus = (CorpusVec) ois.readObject();
            ois.close();
            fis.close();
            return corpus;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void WriteCorpusVecToFile(CorpusVec corpus1, String filePath) {

        try {

            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(corpus1);
            objectOut.close();
            objectOut.flush();
            System.out.println("The CorpusVec  was succesfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static Corpus makeCorpusOfFile() {

        Corpus corpus = new Corpus();

        BufferedReader reader = null;
        String line = "";

        try {
            reader = new BufferedReader(new FileReader(FILE));
            line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",", 2);

                String[] words = formatStr(row[1]).split(" +");

                PersonData newPerson = new PersonData(row[0],words);
                corpus.addPersonToCorpus(newPerson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return corpus;

    }

    private static Corpus makeCorpusOfFile1(String file) {

        Corpus corpus = new Corpus();

        BufferedReader reader = null;
        String line = "";

        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
            int count = 0;
            while ((line = reader.readLine()) != null ) {

                String[] row = parseString(line); //used to split between csv columns

                String formattedString = formatStr(row[1]); //inital clear for the text
                String wordTokens[] = tokensProcessor(formattedString); //tokenizes the text and stemms the word

                PersonData newPerson = new PersonData(row[2].replaceAll("[^a-zA-Z ]", "").toUpperCase(Locale.ROOT),wordTokens);
                corpus.addPersonToCorpus(newPerson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return corpus;

    }

    private static Set<String> stopWords;

    private static String[] tokensProcessor(String words) {


        String allWords[] = words.split(" ");


        List<String> tokens = new ArrayList<>();


        if(stopWords==null)
            initializeStopWords();

        String last = "";

        for(String word: allWords){

            word = word.trim();

            if(word.length()>1 && !stopWords.contains(word)) // if not a symbol such as HAPPY etc
                if(Character.isLowerCase(word.charAt(0))){
                    word = PorterStemmer.stemWord(word);
                }

            if(!stopWords.contains(word)&&word.length()>1){
                tokens.add(word);
                if(!last.isEmpty())
                    tokens.add(last + " " + word);
                last = word;

            }
        }


        return tokens.toArray(new String[0]);

    }

    private static void initializeStopWords()  {

        stopWords = new HashSet<>();
        String words[] = new String[]{
                "i","im", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his",
                "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which",
                "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have","ive", "has", "had", "having",
                "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with",
                "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on",
                "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few",
                "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very","dont", "can", "will", "just",
                "don", "should", "now","doesnt"


        };
        stopWords.addAll(List.of(words));

    }


    //used to parse the json to a new format without quatation in order to process it further
    static String[] parseString(String str){

        StringBuilder builder = new StringBuilder(str);
        boolean inQuotes = false;
        for (int currentIndex = 0; currentIndex < builder.length(); currentIndex++) {
            char currentChar = builder.charAt(currentIndex);
            if (currentChar == '\"') inQuotes = !inQuotes; // toggle state
            if (currentChar == ',' && inQuotes) {
                builder.setCharAt(currentIndex, ';'); // or '♡', and replace later
            }
        }


        return (builder.toString().split(","));
    }




    private static String formatStr(String str) {
//                String clearStr = str.trim().toLowerCase().replaceAll("https?://\\S+\\s?", " LINK ").replaceAll("[^a-zA-Z ]", " ");

        String clearStr = str.trim().toLowerCase()
                .replaceAll("https?://\\S+\\s?", " LINK ")
                .replaceAll("[;:][~-]?[)D]", " HAPPY ")
                .replaceAll("[;:][~-]?[(]", " SAD ")
                .replaceAll("'", "")
                .replaceAll("\\.\\.\\.", " THREEDOTS ")
                .replaceAll("\\.\\.\\.", " TYPE ")
                .replaceAll("\\?\\?+", " DOUBLEQUESTIONMARK ")
                .replaceAll("!!+", " DOUBLEEXCLAMETIONMARK ")
                .replaceAll("!", " EXCLAMETIONMARK ")
                .replaceAll("[^a-zA-Z ]", " ");
        return clearStr;
    }

}
