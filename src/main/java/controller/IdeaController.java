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
        Idea small = new Idea("Small");
        Idea greyhound = new Idea("Greyhound");
        Idea chihuahua = new Idea("Chihuahua");
        Idea typeOfDog = new Idea("Type of dog");
        Idea terrier = new Idea("Terrier");
        Idea goldenRetriever = new Idea("Golden Retriever");
        Idea cute = new Idea("Cute");

        Idea a = new Idea("Happiness");

        dogs.addChild(a, IdeaConnectionType.POINT);
        dogs.addChild(big, IdeaConnectionType.BRANCH);
        dogs.addChild(small, IdeaConnectionType.BRANCH);

        big.addChild(greyhound, IdeaConnectionType.BRANCH);
        big.addChild(goldenRetriever, IdeaConnectionType.BRANCH);
        small.addChild(chihuahua, IdeaConnectionType.BRANCH);
        small.addChild(terrier, IdeaConnectionType.BRANCH);

        big.addAcquitance(typeOfDog, IdeaConnectionType.EXPLANATION);
        small.addAcquitance(typeOfDog, IdeaConnectionType.EXPLANATION);
        chihuahua.addAcquitance(cute, IdeaConnectionType.EXPLANATION);

        Idea mainIdea = dogs;

        return mainIdea;
    }
}
