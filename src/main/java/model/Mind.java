package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of all the thoughts being a part of a mind map.
 */
public class Mind {
    private List<Idea> ideas = new ArrayList<>();
    private Idea mainIdea;

    public Mind() {
        createIdea();
    }

    public void createIdea() {

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

        mainIdea = idea1;

    }

    /* as ideas are added to the mind map, get reference to main idea,
     * and add idea to a list of ideas.
     */
    public void addIdea(Idea... ideas) {
        if (ideas.length > 0) {
            for (Idea idea : ideas) {
                if (idea.getIsMainIdea()) {
                    mainIdea = idea;
                }
                this.ideas.add(idea);
            }
        }
    }

    /*
     * Print the objects in the map
     */
    public void printMind() {
        System.out.println(mainIdea);
    }
}
