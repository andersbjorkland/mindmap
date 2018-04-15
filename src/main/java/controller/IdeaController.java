package controller;

import model.Idea;
import model.IdeaConnectionType;

public class IdeaController {
    public void addThought() {

    }

    /*
     * Example of how a mind can be structured.
     */
    public void mindExample() {

        Idea idea1 = new Idea("Dogs", true);
        Idea idea2 = new Idea("Big");
        Idea idea3 = new Idea("Small");
        Idea idea4 = new Idea("Greyhound");
        Idea idea5 = new Idea("Chihuahua");
        Idea idea6 = new Idea("Type of dog");
        Idea idea7 = new Idea("Terrier");
        Idea idea8 = new Idea("Golden Retriever");

        Idea a = new Idea("Happiness");

        idea1.addChild(a, IdeaConnectionType.POINT);

        idea2.addChild(idea4, IdeaConnectionType.BRANCH);
        idea2.addAcquitance(idea6, IdeaConnectionType.EXPLANATION);
        idea3.addChild(idea5, IdeaConnectionType.BRANCH);
        idea3.addAcquitance(idea6, IdeaConnectionType.EXPLANATION);
        idea1.addChild(idea2, IdeaConnectionType.BRANCH);
        idea1.addChild(idea3, IdeaConnectionType.BRANCH);
        idea2.addChild(idea8, IdeaConnectionType.BRANCH);
        idea3.addChild(idea7, IdeaConnectionType.BRANCH);

        Idea mainIdea = idea1;

        System.out.println(mainIdea);
    }
}
