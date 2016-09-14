package utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Me on 21.07.2016.
 */
public class TestCaseLoader {

    public HashMap<String, ArrayList<File>> readCases(File testCasesFolder, boolean displayLog) {
        HashMap<String, ArrayList<File>> testCases = new HashMap<>();
        File[] listOfFiles = testCasesFolder.listFiles();
        for (File testcase : listOfFiles) {
            testCases.put(testcase.getName(), loadLettersFromTestcase(testcase, displayLog));
        }
        return testCases;
    }

    private ArrayList<File> loadLettersFromTestcase(File testcase, boolean displayLog) {

        ArrayList<File> listOfTestCases = new ArrayList<>();
        int letterInTestCaseCount = 0;
        if (testcase.getName().startsWith("testcase")) {
            for (File letter : testcase.listFiles()) {
                if (letter.getName().endsWith("png") || letter.getName().endsWith("jpg")) {
                    listOfTestCases.add(letter);
                    letterInTestCaseCount++;
                }
            }
        }
        if (displayLog)
        System.out.println(testcase.getName() + " loaded. Letters in testcase : " + letterInTestCaseCount);
        return listOfTestCases;
    }

}
