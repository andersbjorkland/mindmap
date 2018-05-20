package model;


import java.util.*;

/**
 * The thought as a keyword expressing a fraction of a theme/subject.
 * This thought is either the parent of all other thoughts in the mind map,
 * or it is a child to another thought.
 *
 * A map tracks the connection types each child (Idea) has to its parent.
 */
public class Idea {
    private int id;
    private String theme;
    private Idea parent;
    private IdeaConnectionType parentConnection = IdeaConnectionType.NONE;
    private int childLevel;
    private Set<Idea> children;
    private Map<Idea, IdeaConnectionType> acquaintances;
    private boolean isMainIdea;
    private Bubble bubble;

    private static int numberOfIdeas = 0;

    /**
     * Create an Idea with its theme, if it is main idea and instructions for display in a Bubble.
     * @param theme the semantic content of the Idea as a String
     * @param isMainIdea the central Idea of the mindmap
     * @param bubble instructions for displaying the Idea.
     */
    public Idea(String theme, boolean isMainIdea, Bubble bubble) {
        children = new HashSet<>();
        acquaintances = new HashMap<>();
        this.id = numberOfIdeas;
        this.theme = theme;
        this.isMainIdea = isMainIdea;
        if (isMainIdea) {
            childLevel = 0;
        } else {
            childLevel = -1;
        }
        this.bubble = bubble;
        numberOfIdeas++;
    }

    /**
     * Create an Idea with its theme and if it is main idea.
     * @param theme the semantic content of the Idea as a String
     * @param isMainIdea the central Idea of the mindmap
     */
    public Idea(String theme, boolean isMainIdea) {
        this(theme, isMainIdea, new Bubble());
    }

    public Idea(String theme) {
        this(theme, false);

    }


    public void addChild(Idea child, IdeaConnectionType connectionType) {
        child.setParent(this);
        child.getBubble().setColor(this.getBubble().getColor());
        child.childLevel = this.childLevel + 1;
        child.parentConnection = connectionType;
        children.add(child);
    }

    /**
     * Adds  a child to the current Idea
     * @param child as the Idea that branches off the current Idea (parent).
     */
    public void addChild(Idea child) {
        addChild(child, IdeaConnectionType.BRANCH);
    }

    public void addAcquaintance(Idea idea, IdeaConnectionType connectionType) {
        acquaintances.put(idea, connectionType);
    }

    public void setParent(Idea parent) {
        this.parent = parent;
    }

    public Idea getParent() {
        return parent;
    }

    public boolean hasParent() {
        return childLevel > 0;
    }

    public int getChildLevel() {
        return childLevel;
    }

    public boolean getIsMainIdea() {
        return isMainIdea;
    }

    static int getNumberOfIdeas() {
        return numberOfIdeas;
    }

    public int getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public Set<Idea> getChildren() {
        return children;
    }

    /**
     *
     * @return all Idea objects that are offsprings to this Idea object.
     */
    public Set<Idea> getFamily() {
        Set<Idea> family = new HashSet<>();
        family.add(this);
        family.addAll(this.getAcquaintances().keySet());
        if (this.hasChildren()) {
            for (Idea idea : this.children) {
                family.addAll(idea.getFamily());
            }
        }
        return family;
    }

    public Map<Idea, IdeaConnectionType> getAcquaintances() {
        return acquaintances;
    }

    public boolean isMainIdea() {
        return isMainIdea;
    }

    public Bubble getBubble() {
        return bubble;
    }

    public void setBubble(Bubble bubble) {
        this.bubble = bubble;
    }

    public void setParentConnection(IdeaConnectionType connectionType) {
        parentConnection = connectionType;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public String toString() {
        String string = theme + " <" + bubble.getType() + ">\n";

        String spaces = "";
        for (int i = 0; i <= childLevel; i++) {
            spaces += "  ";
        }

        if (!acquaintances.isEmpty()){
            for (Idea idea : acquaintances.keySet()) {
                string += spaces + "(" + acquaintances.get(idea) + ": " + idea.theme + ") <" + idea.bubble.getType() + ">\n";
            }
        }

        if (!children.isEmpty()) {
            for (Idea idea : children) {
                string += spaces + idea; // recursion to get each child and its children in turn.
            }
        }
        return string;
    }
}
