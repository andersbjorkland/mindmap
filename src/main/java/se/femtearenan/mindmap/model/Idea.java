package se.femtearenan.mindmap.model;


import java.io.Serializable;
import java.util.*;

/**
 * The thought as a keyword expressing a fraction of a theme/subject.
 * This thought is either the parent of all other thoughts in the mind map,
 * or it is a child to another thought.
 *
 * A map tracks the connection types each child (Idea) has to its parent.
 */
public class Idea implements Serializable {
    private static final long serialVersionUID = 1L;
    private String theme;
    private Idea parent;
    private int childLevel;
    private Set<Idea> children;
    private Map<Idea, IdeaConnectionType> acquaintances;
    private Bubble bubble;

    /**
     * Create an Idea with its theme, if it is main idea and instructions for display in a Bubble.
     * @param theme the semantic content of the Idea as a String
     * @param isMainIdea the central Idea of the mindmap
     * @param bubble instructions for displaying the Idea.
     */
    private Idea(String theme, boolean isMainIdea, Bubble bubble) {
        children = new HashSet<>();
        acquaintances = new HashMap<>();
        this.theme = theme;
        if (isMainIdea) {
            childLevel = 0;
        } else {
            childLevel = -1;
        }
        this.bubble = bubble;
    }

    /**
     * Create an Idea with its theme and if it is main idea.
     * @param theme the semantic content of the Idea as a String
     * @param isMainIdea the central Idea of the mindmap
     */
    private Idea(String theme, boolean isMainIdea) {
        this(theme, isMainIdea, new Bubble());
    }

    public Idea(String theme) {
        this(theme, false);
    }

    public void addChild(Idea child) {
        child.setParent(this);
        child.childLevel = this.childLevel + 1;
        children.add(child);
    }

    public void addAcquaintance(Idea idea, IdeaConnectionType connectionType) {
        acquaintances.put(idea, connectionType);
    }

    private void setParent(Idea parent) {
        this.parent = parent;
    }

    public Idea getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasThisChild(Idea idea) {
        boolean hasThisChild = false;
        for (Idea child : children) {
            if (child == idea) {
                hasThisChild = true;
                break;
            }
        }
        return hasThisChild;
    }

    public boolean hasThisAcquaintance(Idea idea) {
        boolean hasThisAcquaintance = false;
        for (Idea acquaintance : acquaintances.keySet()) {
            if (acquaintance == idea) {
                hasThisAcquaintance = true;
                break;
            }
        }
        return hasThisAcquaintance;
    }

    public int getChildLevel() {
        return childLevel;
    }

    public String getTheme() {
        return theme;
    }

    public Set<Idea> getChildren() {
        return children;
    }

    public Map<Idea, IdeaConnectionType> getAcquaintances() {
        return acquaintances;
    }

    public Bubble getBubble() {
        return bubble;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void removeChild(Idea child) {
        child.setParent(null);
        children.remove(child);
    }

    public void removeAcquaintance(Idea acquaintance) {
        acquaintances.remove(acquaintance);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(theme + " <" + bubble.getType() + ">\n");

        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i <= childLevel; i++) {
            spaces.append("  ");
        }

        if (!acquaintances.isEmpty()){
            for (Idea idea : acquaintances.keySet()) {
                string.append(spaces).append("(").append(acquaintances.get(idea)).append(": ").append(idea.theme).append(") <").append(idea.bubble.getType()).append(">\n");
            }
        }

        if (!children.isEmpty()) {
            for (Idea idea : children) {
                string.append(spaces.toString()).append(idea); // recursion to get each child and its children in turn.
            }
        }
        return string.toString();
    }
}
