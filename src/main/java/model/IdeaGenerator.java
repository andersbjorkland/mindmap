package model;

/**
 * Keep track of ideas created and increments unique IDs' for each Idea to be created.
 * Creation of a new Idea is handled in this generator.
 */
public class IdeaGenerator {

    public Idea createMainIdea(String theme) {
        return createIdea(theme, null, true, IdeaConnectionType.NONE);
    }

    public Idea createIdea(String theme, Idea parentIdea, IdeaConnectionType type) {
        return createIdea(theme, parentIdea, false, type);
    }

    public Idea createIdea(String theme, Idea parentIdea, boolean isMainIdea, IdeaConnectionType type) {
        int num = Idea.getNumberOfIdeas();
        Idea idea = new Idea(num, theme, parentIdea, false, type);
        return idea;
    }
}
