package model;

/**
 * The thought as a keyword expressing a fraction of a theme/subject.
 * This thought is either the parent of all other thoughts in the mind map,
 * or it is a child to another thought.
 *
 * The child defines its connection to its parent.
 */
public class Idea {
    private int id;
    private String theme;
    private Idea parentIdea;
    private boolean isMainIdea;
    private IdeaConnectionType connectionToParent;
    private static int numberOfIdeas = 0;

    Idea(int id, String theme, Idea parentIdea, boolean isMainIdea, IdeaConnectionType connectionToParent) {
        this.id = id;
        this.theme = theme;
        this.parentIdea = parentIdea;
        this.isMainIdea = isMainIdea;
        this.connectionToParent = connectionToParent;
        numberOfIdeas++;
    }

    public boolean getIsMainIdea() {
        return isMainIdea;
    }

    static int getNumberOfIdeas() {
        return numberOfIdeas;
    }
}
