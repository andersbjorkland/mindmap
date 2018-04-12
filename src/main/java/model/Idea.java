package model;

/**
 * The thought as a keyword expressing a fraction of a theme/subject.
 * This thought is either the parent of all other thoughts in the mind map,
 * or it is a child to another thought.
 *
 * The child defines its connection to its parent.
 */
public class Idea {
    private String theme;
    private Idea parentIdea;
    private boolean isMainIdea;
    private IdeaConnectionType connectionToParent;

}
