package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;


//@source RANDOM WALKER ALGORITHM

public class WorldCreator {

    /*****Constants*****/
    private static final int WIDTH = 70;
    private static final int HEIGHT = 40;
    private static final int AREA = WIDTH * HEIGHT;
    /*******************/

    private Random rand;
    private int tunnelCount;
    private int maxTunnelLength;
    private TETile[][] world;
    private LinkedList<Integer> fire;
    private int[] avatarPos;        //int[0] is x, int[1] is y
    private int[] portalPos;
    private long seed;
    private int vision;
    private int round = 1;
    private int torches = 7;


    public WorldCreator(long seed, int v) {
        vision = v;
        this.seed = seed;
        rand = new Random(seed);
        world = createBase();
        fire = new LinkedList<>();
    }

    public boolean winnable() {
        int x = avatarPos[0];
        int y = avatarPos[1];
        return (world[x + 1][y].equals(Tileset.FLOOR) || world[x - 1][y].equals(Tileset.FLOOR)
            || world[x][y + 1].equals(Tileset.FLOOR) || world[x][y - 1].equals(Tileset.FLOOR)
            || world[x + 1][y].equals(Tileset.WATER) || world[x - 1][y].equals(Tileset.WATER)
                || world[x][y + 1].equals(Tileset.WATER) || world[x][y - 1].equals(Tileset.WATER));
    }

    public int round() {
        return round;
    }

    public void brighten() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (y > world[0].length - 1) {
            y = world[0].length - 1;
        }
        if (torches > 0) {
            if (world[x][y] != Tileset.WATER) {
                world[x][y] = Tileset.FIRE;
            }
            if (!fire.contains(x * 1000 + y)) {
                fire.addFirst(x * 1000 + y);
            }
        }
        torches = 7 - fire.size();
    }
    public String torch() {
        return Integer.toString(torches);
    }
    public String type() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (y > world[0].length - 1) {
            return "";
        }
        if (world[x][y].equals(Tileset.WALL) || world[x][y].equals(Tileset.FLOOR)) {
            return world[x][y].description();
        }
        if (world[x][y].equals((Tileset.FLOWER))) {
            return "You";
        }
        if (world[x][y].equals(Tileset.WATER)) {
            return "Portal";
        } else {
            return "";
        }
    }

    public TETile[][] create() {
        TETile[][] darkWorld = initializeWorld();

        for (int i = 0; i < world.length; i++) {        //creates the walls around
            for (int j = 0; j < world[0].length; j++) {
                if (world[i][j] == Tileset.WATER) {
                    darkWorld[i][j] = world[i][j];
                } else if (Math.sqrt(Math.pow(i - avatarPos[0], 2)
                        + Math.pow(j - avatarPos[1], 2)) > vision) {
                    darkWorld[i][j] = Tileset.NOTHING;
                } else {
                    darkWorld[i][j] = world[i][j];
                }
                if (fire.size() > 0) {
                    for (int loc : fire) {
                        if (Math.sqrt(Math.pow(i - loc / 1000, 2)
                                + Math.pow(j - loc % 1000, 2)) < 5) {
                            darkWorld[i][j] = world[i][j];
                        }
                    }
                }
            }
        }
        return darkWorld;
    }

    public TETile[][] createBase() {
        //makes the empty world
        world = initializeWorld();
        tunnelCount = (int) (RandomUtils.uniform(rand, 0.25, 0.35) * AREA);
        maxTunnelLength = (int) (0.0055 * AREA);     //max tunnel_length

        makeFloors();
        makeWalls();
        fillers();
        createAvatar();
        createPortal();
        return world;
    }


    public boolean proceed() {
        if (avatarPos[0] == portalPos[0] && avatarPos[1] == portalPos[1]) {
            vision -= 4;
            if (vision == 1) {
                vision = 0;
            } else {
                vision = Math.max(1, vision);
            }
            round++;
            fire.clear();
            return true;
        } else {
            return false;
        }
    }

    public TETile[][] moveUp() {
        int x = avatarPos[0];
        int y = avatarPos[1];
        if (world[x][y + 1].equals(Tileset.FLOOR) || world[x][y + 1].equals(Tileset.WATER)) {
            //world[x][y] = Tileset.FLOOR;
            world[x][y + 1] = Tileset.FLOWER;
            avatarPos = new int[] {x, y + 1};
        }
        return create();
    }

    public TETile[][] moveDown() {
        int x = avatarPos[0];
        int y = avatarPos[1];
        if (world[x][y - 1].equals(Tileset.FLOOR) || world[x][y - 1].equals(Tileset.WATER)) {
            //world[x][y] = Tileset.FLOOR;
            world[x][y - 1] = Tileset.FLOWER;
            avatarPos = new int[] {x, y - 1};
        }
        return create();
    }

    public TETile[][] moveRight() {
        int x = avatarPos[0];
        int y = avatarPos[1];
        if (world[x + 1][y].equals(Tileset.FLOOR) || world[x + 1][y].equals(Tileset.WATER)) {
            //world[x][y] = Tileset.FLOOR;
            world[x + 1][y] = Tileset.FLOWER;
            avatarPos = new int[] {x + 1, y};
        }
        return create();
    }

    public TETile[][] moveLeft() {
        int x = avatarPos[0];
        int y = avatarPos[1];
        if (world[x - 1][y].equals(Tileset.FLOOR) || world[x - 1][y].equals(Tileset.WATER)) {
            //world[x][y] = Tileset.FLOOR;
            world[x - 1][y] = Tileset.FLOWER;
            avatarPos = new int[] {x - 1, y};
        }
        return create();
    }

    private void createAvatar() {
        int x = 0;
        int y = 0;
        while (world[x][y] != Tileset.FLOOR) {
            x++;
            if (x > world.length - 1) {
                x = 0;
                y++;
            }
        }
        world[x][y] = Tileset.FLOWER;
        avatarPos = new int[] {x, y};

    }

    private void createPortal() {
        int x = world.length - 1;
        int y = world[0].length - 1;
        while (world[x][y] != Tileset.FLOOR) {
            x--;
            if (x < 0) {
                x = world.length - 1;
                y--;
            }
        }
        world[x][y] = Tileset.WATER;
        portalPos = new int[] {x, y};
    }


    private void makeWalls() {
        for (int i = 1; i < world.length - 1; i++) {        //creates the walls around
            for (int j = 1; j < world[0].length - 1; j++) {
                if (world[i][j].equals(Tileset.FLOOR)) {
                    if (world[i - 1][j].equals((Tileset.NOTHING))) {    //check left
                        world[i - 1][j] = Tileset.WALL;
                    }
                    if (world[i + 1][j].equals(Tileset.NOTHING)) {      //check right
                        world[i + 1][j] = Tileset.WALL;
                    }
                    if (world[i][j - 1].equals(Tileset.NOTHING)) {      //check down
                        world[i][j - 1] = Tileset.WALL;
                    }
                    if (world[i][j + 1].equals(Tileset.NOTHING)) {      //check up
                        world[i][j + 1] = Tileset.WALL;
                    }
                    if (world[i - 1][j - 1].equals((Tileset.NOTHING))) {
                        world[i - 1][j - 1] = Tileset.WALL;
                    }
                    if (world[i + 1][j + 1].equals(Tileset.NOTHING)) {
                        world[i + 1][j + 1] = Tileset.WALL;
                    }
                    if (world[i - 1][j + 1].equals(Tileset.NOTHING)) {
                        world[i - 1][j + 1] = Tileset.WALL;
                    }
                    if (world[i + 1][j - 1].equals(Tileset.NOTHING)) {
                        world[i + 1][j - 1] = Tileset.WALL;
                    }
                }
            }
        }
    }

    private void makeFloors() {
        int[] randStartArray = randomRC();
        int currRow = randStartArray[0];
        int currCol = randStartArray[1];
        ArrayList<int[]> directions = directionGen();
        int[] lastDirection = {-1, -1};
        int[] randDirection;


        while (tunnelCount > 0 && maxTunnelLength > 0) {        //find next direction
            do {
                randDirection = directions.get(RandomUtils.uniform(rand, 4));
            } while ((randDirection[0] == -lastDirection[0]
                    && randDirection[1] == -lastDirection[1])
                    || (randDirection[0] == lastDirection[0]
                    && randDirection[1] == lastDirection[1]));

            int randLength = RandomUtils.uniform(rand, maxTunnelLength);
            int currTunnelLength = 0;

            while (currTunnelLength < randLength) {
                if ((currRow == 1 && randDirection[0] == -1)
                        || (currCol == 1 && randDirection[1] == -1)
                        || (currRow == world.length - 2 && randDirection[0] == 1)
                        || (currCol == world[0].length - 2 && randDirection[1] == 1)) {
                    break;
                } else {                    //creates the floors
                    world[currRow][currCol] = Tileset.FLOOR;
                    currRow += randDirection[0];
                    currCol += randDirection[1];
                    currTunnelLength++;
                }
                if (currTunnelLength > 0) {
                    lastDirection = randDirection;
                    tunnelCount--;
                }
            }
        }
    }

    private void fillers() {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                if (world[x][y].equals(Tileset.WALL)) {
                    int counter = 0;
                    if (world[x + 1][y].equals(Tileset.FLOOR)) {
                        counter++;
                    }
                    if (world[x - 1][y].equals(Tileset.FLOOR)) {
                        counter++;
                    }
                    if (world[x][y + 1].equals(Tileset.FLOOR)) {
                        counter++;
                    }
                    if (world[x][y - 1].equals(Tileset.FLOOR)) {
                        counter++;
                    }
                    if (counter >= 3) {
                        world[x][y] = Tileset.FLOOR;
                    }
                }
            }
        }
    }

    private ArrayList<int[]> directionGen() {
        ArrayList<int[]> temp = new ArrayList<>();
        int[] left = {-1, 0};
        temp.add(left);
        int[] right = {1, 0};
        temp.add(right);
        int[] down = {0, -1};
        temp.add(down);
        int[] up = {0, 1};
        temp.add(up);

        return temp;
    }

    private int[] randomRC() {              //array for returning random row and column
        int[] temp = new int[2];
        temp[0] = RandomUtils.uniform(rand, 70 - 2) + 1;
        temp[1] = RandomUtils.uniform(rand, 40 - 2) + 1;
        return temp;
    }

    private TETile[][] initializeWorld() {
        TETile[][] temp = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                temp[x][y] = Tileset.NOTHING;
            }
        }
        return temp;
    }
}
