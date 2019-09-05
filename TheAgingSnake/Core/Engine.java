package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;




public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 70;
    public static final int HEIGHT = 40;

    private TETile[][] finalWorldFrame;
    WorldCreator wc;
    private long seed;
    private String saveMove = "";

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    private void renew() {
        File f = new File("saved.txt");
        if (f.exists()) {
            try {
                BufferedReader bf = new BufferedReader(new FileReader(f));
                while (bf.readLine() != null) {
                    return;
                }
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    private String load() {
        File f = new File("saved.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                String ans = (String) os.readObject();
                return ans;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
                System.exit(0);
            }
        }
        return "";
    }
    private void save() {
        File f = new File("saved.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(saveMove);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public void interactWithKeyboard() {
        drawMenu();
        buffer();
        char choice = StdDraw.nextKeyTyped();

        if (choice == 'n') {
            String input = "";
            renew();
            saveMove += 'n';
            while (true) {
                StdDraw.clear();
                StdDraw.text(35, 30, "Please Enter A Number and click (s) to start");
                StdDraw.text(35, 20, input);
                StdDraw.show();
                buffer();
                char c = StdDraw.nextKeyTyped();
                if (c == 's') {
                    saveMove += 's';
                    break;
                } else if (c < '0' || c > '9') {
                    System.out.println("You need a number.");
                } else {
                    saveMove += c;
                    input += c;
                }
            }
            seed = seed("#" + input);
            wc = new WorldCreator(seed, 20);         //CHANGE for difficulty
            finalWorldFrame = wc.create();
            ter.initialize(WIDTH, HEIGHT + 10, 0, 0);
            ter.renderFrame(finalWorldFrame);

        }

        if (choice == 'l') {
            finalWorldFrame = interactWithInputString(load());
            ter.renderFrame(finalWorldFrame);
        }
        if (choice == 'q') {
            System.exit(0);
        }

        interactions();
    }

    public void hud() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 47, "Press (g) to give up");
        StdDraw.text(35, 45, "Round: " + wc.round());
        StdDraw.text(10, 45, wc.type());
        StdDraw.text(60, 45, "Torches: " + wc.torch());
        StdDraw.show();
    }

    public void endscreen() {
        StdDraw.clear(Color.WHITE);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(35, 45, "Game Over! Your Score : " + wc.round());
        StdDraw.text(35, 25, "Please press (:q) to exit");
        StdDraw.show();
    }

    public void interactions() {
        boolean on = true;
        while (on) {
            if (wc.proceed()) {
                wc.createBase();
                finalWorldFrame = wc.create();
                ter.renderFrame(finalWorldFrame);
            }
            while (!StdDraw.hasNextKeyTyped()) {
                if (StdDraw.isMousePressed()) {
                    wc.brighten();
                    finalWorldFrame = wc.create();
                }
                ter.renderFrame(finalWorldFrame);
                hud();
                StdDraw.pause(10);
            }
            char c = StdDraw.nextKeyTyped();
            if (c == ':') {
                buffer();
                char e = StdDraw.nextKeyTyped();
                if (e == 'q') {
                    save();
                    break;
                } else {
                    continue;
                }
            }
            if (!wc.winnable() || c == 'g') {
                endscreen();
                buffer();
                char d = StdDraw.nextKeyTyped();
                if (d == ':') {
                    buffer();
                    char e = StdDraw.nextKeyTyped();
                    if (e == 'q') {
                        System.exit(0);
                    } else {
                        continue;
                    }
                }
            }
            if (c == 'w') {
                finalWorldFrame = wc.moveUp();
                ter.renderFrame(finalWorldFrame);
            }
            if (c == 'a') {
                finalWorldFrame = wc.moveLeft();
                ter.renderFrame(finalWorldFrame);
            }
            if (c == 's') {
                finalWorldFrame = wc.moveDown();
                ter.renderFrame(finalWorldFrame);
            }
            if (c == 'd') {
                finalWorldFrame = wc.moveRight();
                ter.renderFrame(finalWorldFrame);
            }
            saveMove += c;
        }
        ter.quit();
    }

    public void buffer() {
        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.pause(10);
        }
    }

    public void bufferWithHUD() { //allows for time to pass for user to put in keystroke
        while (!StdDraw.hasNextKeyTyped() && !StdDraw.isMousePressed()) {
            ter.renderFrame(finalWorldFrame);
            hud();
            StdDraw.pause(10);
        }
    }

    public void drawMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.WHITE);
        StdDraw.enableDoubleBuffering();
        StdDraw.text(35, 25, "New World (n)");
        StdDraw.text(35, 20, "Load (l)");
        StdDraw.text(35, 15, "Quit (q)");
        font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(35, 30, "The Aging Snake");
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {

        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        //ter.initialize(WIDTH, HEIGHT);


        ter.initialize(WIDTH, HEIGHT + 10, 0, 0);

        boolean quit = false;

        if (input.charAt(0) == 'n') {
            saveMove += 'n';
            seed = seed(input);
            System.out.println(seed);
            saveMove += seed;
            saveMove += 's';
            String moves = movements(input);
            wc = new WorldCreator(seed, 20);
            finalWorldFrame = wc.create();



            while (moves.length() > 0) {
                if (wc.proceed()) {
                    wc.createBase();
                    finalWorldFrame = wc.create();
                    ter.renderFrame(finalWorldFrame);
                }
                char c = moves.charAt(0);
                if (c == ':') {
                    quit = true;
                    moves = moves.substring(1);
                    continue;
                }
                if (c == 'q' && quit) {
                    save();
                    break;
                }
                if (c == 'w') {
                    finalWorldFrame = wc.moveUp();
                }
                if (c == 'a') {
                    finalWorldFrame = wc.moveLeft();
                }
                if (c == 's') {
                    finalWorldFrame = wc.moveDown();
                }
                if (c == 'd') {
                    finalWorldFrame = wc.moveRight();
                }
                saveMove += c;
                if (moves.length() == 1) {
                    break;
                }
                moves = moves.substring(1);
            }
        }
        if (input.charAt(0) == 'l') {
            String moves = load() + movementsWithL(input);
            finalWorldFrame = interactWithInputString(moves);


        }
        ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }
    private String movementsWithL(String input) {
        return input.substring(1);
    }
    private String movements(String input) {
        if (input.length() == 1) {
            return "";
        }
        int i = 1;
        while (i < input.length()) {
            if (!Character.isDigit(input.charAt(i)) && input.charAt(i) != 'n') {
                i++;
                break;
            } else {
                i++;
            }
        }
        input = input.substring(i);
        return input;
    }

    private long seed(String input) {
        int i = 1;
        long sd;
        StringBuilder sb = new StringBuilder();
        while (i < input.length() && Character.isDigit(input.charAt(i))) {
            sb = sb.append(input.charAt(i));
            i++;

        }
        String s = sb.toString();
        sd = Long.parseLong(s);
        return sd;
    }
}
