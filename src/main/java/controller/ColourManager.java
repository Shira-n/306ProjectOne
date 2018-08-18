package controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 */
public class ColourManager {
    private List<String> _colours = new ArrayList<>();


    List<String> colourChoices = new ArrayList() {
        {
            add("#39add1");
            add("#c25975");
            add("#e15258");
            add("#f9845b");
            add("#838cc7");
            add("#7d669e");
            add("#53bbb4");
            add("#51b46d");
            add("#e0ab18");
            add("#637a91");
            add("#f092b0");
            add("#b7c0c7");
        }
    };

    public ColourManager (int numProcessor) {
        for (int i = 0; i < numProcessor; i++) {
            int randomPickIndex  = (int)(Math.random() * (colourChoices.size()-1));
            _colours.add(colourChoices.get(randomPickIndex));
            System.out.println("Processor " + i + " : " + randomPickIndex + ", "+  _colours.get(i));
            colourChoices.remove(randomPickIndex);
        }
    }

    public String getColor(int processorIndex) {
        //System.out.println("COLOR: " + _colours.get(processorIndex-1));
        return _colours.get(processorIndex-1);
    }

}
