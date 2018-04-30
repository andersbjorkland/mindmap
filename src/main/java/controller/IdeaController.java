package controller;

import javafx.scene.paint.Color;
import model.Bubble;
import model.Idea;
import model.IdeaConnectionType;

public class IdeaController {

    /*
     * Example of how a mind can be structured.
     */
    public Idea mindExample() {

        Idea dogs = new Idea("Dogs", true);
        dogs.getBubble().setColor(Color.RED);
        dogs.getBubble().setLineThickness(5);

        Idea big = new Idea("Big");
        dogs.addChild(big, IdeaConnectionType.BRANCH);
        big.getBubble().setColor(Color.BLUE);
        big.getBubble().setLineThickness(3);

        Idea small = new Idea("Small");
        dogs.addChild(small, IdeaConnectionType.BRANCH);
        small.getBubble().setColor(Color.DEEPPINK);

        Idea greyhound = new Idea("Greyhound");
        big.addChild(greyhound, IdeaConnectionType.BRANCH);

        Idea chihuahua = new Idea("Chihuahua");
        small.addChild(chihuahua, IdeaConnectionType.BRANCH);

        Idea cute = new Idea("Cute");
        chihuahua.addAcquitance(cute, IdeaConnectionType.EXPLANATION);

        Idea typeOfDog = new Idea("Type of dog");
        big.addAcquitance(typeOfDog, IdeaConnectionType.EXPLANATION);
        small.addAcquitance(typeOfDog, IdeaConnectionType.EXPLANATION);

        Idea terrier = new Idea("Terrier");
        small.addChild(terrier, IdeaConnectionType.BRANCH);

        Idea goldenRetriever = new Idea("Golden Retriever");
        big.addChild(goldenRetriever, IdeaConnectionType.BRANCH);


        Idea a = new Idea("Happiness");
        dogs.addChild(a, IdeaConnectionType.POINT);

        Idea mainIdea = dogs;
        return mainIdea;
    }
}
