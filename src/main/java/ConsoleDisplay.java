import controller.IdeaController;
import model.Idea;

import java.util.Set;

/*
 * Used for simple output on the progress of the project.
 */
public class ConsoleDisplay {
    public static void main(String[] args) {
        Idea idea = IdeaController.mindExample();

        System.out.println("Correct representation: ");
        System.out.println(idea + "\n");

        Set<Idea> ideas = idea.getFamily();

        System.out.println("Extracted ideas: ");
        System.out.println("Parent / Idea / Child level ");
        for (Idea extractedIdea: ideas) {
            if (!extractedIdea.isMainIdea()) {
                if (extractedIdea.hasParent()) {
                    System.out.println(extractedIdea.getParent().getTheme() + ": " + extractedIdea.getTheme() + ": " + extractedIdea.getChildLevel());
                } else {
                    System.out.println("No parent: " + extractedIdea.getTheme() + ": " + extractedIdea.getChildLevel());
                }
            } else {
                System.out.println("Main Idea: " + extractedIdea.getTheme() + ": " + extractedIdea.getChildLevel());
            }
        }
    }
}
