package main;

import exceptions.ImageReadingException;
import utility.ImageReader;

import java.text.DecimalFormat;

/**
 * Created by Me on 02.06.2016.
 */
public class Test {
    public static void main(String[] args) throws ImageReadingException{
       double[]intputs = ImageReader.getInputs("letters/A.png");
        DecimalFormat df = new DecimalFormat("#.#");
        for(int i = 0; i<intputs.length;i++)
        {
            if(i%32==0)
            {
                System.out.println();
            }
            System.out.print(df.format(intputs[i])+"\t");

        }

    }
}

