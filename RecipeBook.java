import java.io.*;
import java.util.*;

// ---------------- Abstract Base Class for Recipe ----------------
abstract class RecipeBase {
    String name, section, ingredients, steps, rating, note, cookTime, difficulty, nutrition, tags;

    RecipeBase(String n, String s, String i, String st, String r, String nt, String ct, String diff, String nut, String tgs) {
        name = n;
        section = s;
        ingredients = i;
        steps = st;
        rating = r;
        note = nt;
        cookTime = ct;
        difficulty = diff;
        nutrition = nut;
        tags = tgs;
    }

    abstract void show();
}

// ---------------- Recipe Class ----------------
class Recipe extends RecipeBase {
    Recipe(String n, String s, String i, String st, String r, String nt, String ct, String diff, String nut, String tgs) {
        super(n, s, i, st, r, nt, ct, diff, nut, tgs);
    }

    void show() {
        System.out.println("---------------------------------------------------");
        System.out.println("Name: " + name);
        System.out.println("Section: " + section);
        System.out.println("Ingredients: " + ingredients);
        System.out.println("Cooking Time: " + cookTime + " min");
        System.out.println("Difficulty: " + difficulty);
        System.out.println("Steps:\n" + steps);
        System.out.println("Rating: " + rating + "/5");
        System.out.println("Note: " + note);
        System.out.println("Nutrition: " + nutrition);
        System.out.println("Tags: " + tags);
        System.out.println("---------------------------------------------------");
    }
}

// ---------------- File Handler Class ----------------
class FileHandler {
    String feedbackFile = "feedback.txt";

    // Get file name for section
    String getFileName(String section) {
        return section.toLowerCase().replace(" ", "_") + "_recipes.txt";
    }

    // Create files if missing
    void checkFiles(String[] sections) {
        try {
            File fb = new File(feedbackFile);
            if (!fb.exists()) {
                fb.createNewFile();
            }
            for (String sec : sections) {
                File f = new File(getFileName(sec));
                if (!f.exists()) {
                    f.createNewFile();
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating files: " + e.getMessage());
        }
    }

    // Backup file
    void backupFile(String section) {
        try {
            File input = new File(getFileName(section));
            File backup = new File("backup_" + getFileName(section));
            BufferedReader br = new BufferedReader(new FileReader(input));
            BufferedWriter bw = new BufferedWriter(new FileWriter(backup));
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            br.close();
            bw.close();
        } catch (IOException e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }

    // Add Recipe
    void addRecipe(Recipe r) {
        try {
            FileWriter fw = new FileWriter(getFileName(r.section), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(r.name + "|" + r.section + "|" + r.ingredients + "|" + r.steps + "|" + r.rating + "|"
                    + r.note + "|" + r.cookTime + "|" + r.difficulty + "|" + r.nutrition + "|" + r.tags);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error adding recipe: " + e.getMessage());
        }
    }

    // Update recipe rating
    void updateRecipeRating(String name, String rating) {
        try {
            for (String section : RecipeBook.sections) {
                File input = new File(getFileName(section));
                File temp = new File("temp.txt");
                BufferedReader br = new BufferedReader(new FileReader(input));
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
                String line;
                boolean found = false;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(name + "|")) {
                        String[] p = line.split("\\|", -1);
                        p[4] = rating; // rating is at index 4
                        bw.write(String.join("|", p));
                        bw.newLine();
                        found = true;
                    } else {
                        bw.write(line);
                        bw.newLine();
                    }
                }
                br.close();
                bw.close();
                if (!input.delete()) {
                    System.out.println("Error: cannot delete original file.");
                }
                temp.renameTo(input);
                if (found) {
                    System.out.println("Recipe rating updated successfully!");
                    return;
                }
            }
            System.out.println("Recipe not found to rate.");
        } catch (IOException e) {
            System.out.println("Error updating rating: " + e.getMessage());
        }
    }

    // Show recipes by section
    void showBySection(String section) {
        boolean found = false;
        try {
            BufferedReader br = new BufferedReader(new FileReader(getFileName(section)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length == 10) {
                    Recipe r = new Recipe(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9]);
                    r.show();
                    found = true;
                }
            }
            br.close();
            if (!found) {
                System.out.println("No recipes found in section: " + section);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Search recipe by name
    void searchByName(String key, String[] sections) {
        boolean found = false;
        for (String sec : sections) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(getFileName(sec)));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.toLowerCase().contains(key.toLowerCase())) {
                        String[] p = line.split("\\|", -1);
                        if (p.length == 10) {
                            Recipe r = new Recipe(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9]);
                            r.show();
                            found = true;
                        }
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
        if (!found) {
            System.out.println("No recipe found for: " + key);
        }
    }

    // Delete recipe by name
    void deleteRecipe(String name, String section) {
        try {
            backupFile(section);
            File input = new File(getFileName(section));
            File temp = new File("temp.txt");
            BufferedReader br = new BufferedReader(new FileReader(input));
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(name + "|")) {
                    bw.write(line);
                    bw.newLine();
                } else {
                    found = true;
                }
            }
            br.close();
            bw.close();
            if (!input.delete()) {
                System.out.println("Error: cannot delete original file.");
            }
            temp.renameTo(input);
            if (found) {
                System.out.println("Recipe deleted successfully!");
            } else {
                System.out.println("Recipe not found!");
            }
        } catch (IOException e) {
            System.out.println("Error deleting recipe: " + e.getMessage());
        }
    }

    // Update recipe by name
    void updateRecipe(String name, Recipe newR) {
        try {
            backupFile(newR.section);
            File input = new File(getFileName(newR.section));
            File temp = new File("temp.txt");
            BufferedReader br = new BufferedReader(new FileReader(input));
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(name + "|")) {
                    bw.write(newR.name + "|" + newR.section + "|" + newR.ingredients + "|" + newR.steps + "|" +
                            newR.rating + "|" + newR.note + "|" + newR.cookTime + "|" + newR.difficulty + "|" +
                            newR.nutrition + "|" + newR.tags);
                    bw.newLine();
                    found = true;
                } else {
                    bw.write(line);
                    bw.newLine();
                }
            }
            br.close();
            bw.close();
            if (!input.delete()) {
                System.out.println("Error: cannot delete original file.");
            }
            temp.renameTo(input);
            if (found) {
                System.out.println("Recipe updated successfully!");
            } else {
                System.out.println("Recipe not found!");
            }
        } catch (IOException e) {
            System.out.println("Error updating recipe: " + e.getMessage());
        }
    }

    // Add feedback
    void addFeedback(String recipeName, String feedback) {
        try {
            FileWriter fw = new FileWriter(feedbackFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(recipeName + ": " + feedback);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error saving feedback: " + e.getMessage());
        }
    }

    // Show feedback
    void showFeedback() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(feedbackFile));
            String line;
            boolean any = false;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                any = true;
            }
            br.close();
            if (!any) {
                System.out.println("No feedback available.");
            }
        } catch (IOException e) {
            System.out.println("Error reading feedback: " + e.getMessage());
        }
    }

    // Show recently added recipes
    void showRecent(String section, int count) {
        Recipe[] all = new Recipe[1000];
        int idx = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(getFileName(section)));
            String line;
            while ((line = br.readLine()) != null && idx < 1000) {
                String[] p = line.split("\\|", -1);
                if (p.length == 10) {
                    all[idx] = new Recipe(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9]);
                    idx++;
                }
            }
            br.close();
            if (idx == 0) {
                System.out.println("No recipes found in section: " + section);
                return;
            }
            int start = idx - count;
            if (start < 0) start = 0;
            for (int i = start; i < idx; i++) {
                all[i].show();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Show top rated recipes
    void showTopRated(String section) {
        boolean found = false;
        try {
            BufferedReader br = new BufferedReader(new FileReader(getFileName(section)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length == 10) {
                    try {
                        int r = Integer.parseInt(p[4]);
                        if (r >= 4) {
                            Recipe rec = new Recipe(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9]);
                            rec.show();
                            found = true;
                        }
                    } catch (Exception e) {}
                }
            }
            br.close();
            if (!found) {
                System.out.println("No top rated recipes found in " + section);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Show quick recipes
    void showQuickRecipes(String section, int maxMinutes) {
        boolean found = false;
        try {
            BufferedReader br = new BufferedReader(new FileReader(getFileName(section)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length == 10) {
                    try {
                        int t = Integer.parseInt(p[6].replaceAll("[^0-9]", ""));
                        if (t <= maxMinutes) {
                            Recipe rec = new Recipe(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9]);
                            rec.show();
                            found = true;
                        }
                    } catch (Exception e) {}
                }
            }
            br.close();
            if (!found) {
                System.out.println("No quick recipes (<= " + maxMinutes + " min) found in " + section);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Show stats for section
    void showStats(String section) {
        int total = 0;
        int sumRating = 0;
        int minTime = Integer.MAX_VALUE;
        int maxTime = 0;
        int easy = 0, medium = 0, hard = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(getFileName(section)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length == 10) {
                    total++;
                    try { sumRating += Integer.parseInt(p[4]); } catch(Exception e){}
                    String diff = p[7].toLowerCase();
                    if(diff.contains("easy")) easy++;
                    else if(diff.contains("medium")) medium++;
                    else if(diff.contains("hard")) hard++;
                    try {
                        int t = Integer.parseInt(p[6].replaceAll("[^0-9]", ""));
                        if(t < minTime) minTime = t;
                        if(t > maxTime) maxTime = t;
                    } catch(Exception e){}
                }
            }
            br.close();
            System.out.println("=== Statistics for " + section + " ===");
            System.out.println("Total Recipes: " + total);
            System.out.println("Average Rating: " + (total>0 ? (sumRating*1.0/total) : 0));
            System.out.println("Easy: " + easy + ", Medium: " + medium + ", Hard: " + hard);
            System.out.println("Shortest Cooking Time: " + (minTime==Integer.MAX_VALUE ? "N/A":minTime) + " min");
            System.out.println("Longest Cooking Time: " + (maxTime==0?"N/A":maxTime) + " min");
        } catch(IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Export all recipes
    void exportAll(String[] sections) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("All_Recipes_Summary.txt"));
            for(String sec: sections){
                bw.write("=== "+sec+" ===");
                bw.newLine();
                BufferedReader br = new BufferedReader(new FileReader(getFileName(sec)));
                String line;
                while((line = br.readLine()) != null){
                    bw.write(line);
                    bw.newLine();
                }
                br.close();
                bw.newLine();
            }
            bw.close();
            System.out.println("All recipes exported to All_Recipes_Summary.txt");
        } catch(IOException e){
            System.out.println("Error exporting: "+e.getMessage());
        }
    }
}

// ---------------- Main Class ----------------
public class RecipeBook {
    static String[] sections = {"Maharashtrian","Punjabi","South Indian","North Indian","Chinese","Italian"};
    static Scanner sc = new Scanner(System.in);
    static FileHandler fileHandler = new FileHandler();

    public static void main(String[] args){
        fileHandler.checkFiles(sections);
        int ch=0;
        while(ch!=30){
            printMenu();
            try { ch=Integer.parseInt(sc.nextLine()); } catch(Exception e){ch=0;}
            switch(ch){
                case 1:{ addRecipeMenu(); break; }
                case 2:{ showRecipesMenu(); break; }
                case 3:{ searchRecipeMenu(); break; }
                case 4:{ deleteRecipeMenu(); break; }
                case 5:{ updateRecipeMenu(); break; }
                case 6:{ addFeedbackMenu(); break; }
                case 7:{ showFeedbackMenu(); break; }
                case 8:{ showRecentMenu(); break; }
                case 9:{ showTopRatedMenu(); break; }
                case 10:{ showQuickMenu(); break; }
                case 11:{ showStatsMenu(); break; }
                case 12:{ exportAllMenu(); break; }
                case 13:{ rateRecipeMenu(); break; }
                case 14:{ System.out.println("Exiting... Goodbye!"); break; }
                default:{ System.out.println("Invalid choice!"); break; }
            }
        }
    }

    // ---------------- Menu Functions ----------------
    static void printMenu(){
        System.out.println("\n--- RECIPE BOOK MENU ---");
        System.out.println("1. Add Recipe");
        System.out.println("2. Show Recipes");
        System.out.println("3. Search Recipe by Name");
        System.out.println("4. Delete Recipe");
        System.out.println("5. Update Recipe");
        System.out.println("6. Add Feedback");
        System.out.println("7. Show Feedback");
        System.out.println("8. Show Recent Recipes");
        System.out.println("9. Show Top Rated Recipes");
        System.out.println("10. Show Quick Recipes");
        System.out.println("11. Show Stats");
        System.out.println("12. Export All Recipes");
        System.out.println("13. Rate Recipe");
        System.out.println("14. Exit");
        System.out.print("Enter choice: ");
    }

    static int selectSection(){
        System.out.println("Select Section:");
        for(int i=0;i<sections.length;i++){ System.out.println((i+1)+". "+sections[i]); }
        int sec=1;
        try{ sec=Integer.parseInt(sc.nextLine()); if(sec<1||sec>sections.length) sec=1; } catch(Exception e){ sec=1; }
        return sec;
    }

    static void addRecipeMenu(){
        int sec = selectSection(); String section = sections[sec-1];
        System.out.print("Name: "); String name=sc.nextLine();
        System.out.print("Ingredients: "); String ingredients=sc.nextLine();
        System.out.print("Steps: "); String steps=sc.nextLine();
        String rating="0"; // initial rating
        System.out.print("Note: "); String note=sc.nextLine();
        System.out.print("Cooking Time: "); String cookTime=sc.nextLine();
        System.out.print("Difficulty: "); String difficulty=sc.nextLine();
        System.out.print("Nutrition: "); String nutrition=sc.nextLine();
        System.out.print("Tags: "); String tags=sc.nextLine();
        Recipe r = new Recipe(name,section,ingredients,steps,rating,note,cookTime,difficulty,nutrition,tags);
        fileHandler.addRecipe(r); System.out.println("Recipe added successfully!");
    }

    static void showRecipesMenu(){ fileHandler.showBySection(sections[selectSection()-1]); }
    static void searchRecipeMenu(){ System.out.print("Enter recipe name: "); fileHandler.searchByName(sc.nextLine(),sections);}
    static void deleteRecipeMenu(){ int sec=selectSection(); System.out.print("Enter recipe name: "); fileHandler.deleteRecipe(sc.nextLine(),sections[sec-1]); }
    static void updateRecipeMenu(){ int sec=selectSection(); String section = sections[sec-1];
        System.out.print("Enter recipe name to update: "); String oldName=sc.nextLine();
        System.out.print("New Name: "); String name=sc.nextLine();
        System.out.print("Ingredients: "); String ingredients=sc.nextLine();
        System.out.print("Steps: "); String steps=sc.nextLine();
        String rating="0";
        System.out.print("Note: "); String note=sc.nextLine();
        System.out.print("Cooking Time: "); String cookTime=sc.nextLine();
        System.out.print("Difficulty: "); String difficulty=sc.nextLine();
        System.out.print("Nutrition: "); String nutrition=sc.nextLine();
        System.out.print("Tags: "); String tags=sc.nextLine();
        Recipe r = new Recipe(name,section,ingredients,steps,rating,note,cookTime,difficulty,nutrition,tags);
        fileHandler.updateRecipe(oldName,r);
    }

    static void addFeedbackMenu(){ System.out.print("Recipe Name: "); String r=sc.nextLine(); System.out.print("Feedback: "); fileHandler.addFeedback(r,sc.nextLine()); }
    static void showFeedbackMenu(){ fileHandler.showFeedback(); }
    static void showRecentMenu(){ int sec=selectSection(); System.out.print("How many recent recipes? "); int n=Integer.parseInt(sc.nextLine()); fileHandler.showRecent(sections[sec-1],n);}
    static void showTopRatedMenu(){ fileHandler.showTopRated(sections[selectSection()-1]); }
    static void showQuickMenu(){ int sec=selectSection(); System.out.print("Max time (minutes): "); int t=Integer.parseInt(sc.nextLine()); fileHandler.showQuickRecipes(sections[sec-1],t);}
    static void showStatsMenu(){ fileHandler.showStats(sections[selectSection()-1]); }
    static void exportAllMenu(){ fileHandler.exportAll(sections); }
    static void rateRecipeMenu(){ System.out.print("Recipe Name to rate: "); String r=sc.nextLine(); System.out.print("Your rating (1-5): "); String rate=sc.nextLine(); fileHandler.updateRecipeRating(r,rate); }
}
