package controller;

import model.Idea;
import model.IdeaConnectionType;

import java.util.HashSet;
import java.util.Set;

public class IdeaController {

    public Set<Idea> extractUniqueIdeas(Idea masterIdea) {
        Set<Idea> ideas = new HashSet<>();
        ideas.add(masterIdea);
        ideas.addAll(masterIdea.getAcquaintances().keySet());
        Set<Idea> ideaChildren = new HashSet<>(masterIdea.getChildren());
        if (masterIdea.hasChildren()){
            for (Idea idea: ideaChildren) {
                ideas.addAll(extractUniqueIdeas(idea));
            }
        }
        return ideas;
    }

    public void addThought() {

    }

    /*
     * Example of how a mind can be structured.
     */
    public Idea mindExample() {

        Idea dogs = new Idea("Dogs", true);
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
