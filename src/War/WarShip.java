package War;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class WarShip {
    static String playerName1 = "Player #1";
    static String playerName2 = "Player #2";
    static Scanner scanner;
    static int[][] battlefield1;
    static int[][] battlefield2;
    static int[][] monitor1;
    static int[][] monitor2;
    static final String PathToCSV = System.getProperty("user.dir") + "/src/SaveGame.csv";

    public WarShip() {
    }

    public static void main(String[] args) {
        try {
            String readData;
            BufferedReader br = new BufferedReader(new FileReader(PathToCSV));

            if ((readData = br.readLine()) != null) {
                System.out.println("- Do you want to download the last saved game? Write 'yes' !" +
                        "\n- Any key will start the 'new game' ! ");
                readData = scanner.nextLine();

                if (readData.equals("yes")) {
                    readFromCSV(PathToCSV);
                    while (true) {
                        makeTurn(playerName1, monitor1, battlefield2);
                        if (isWinCondition()) {
                            break;

                        }
                        makeTurn(playerName2, monitor2, battlefield1);
                        if (isWinCondition()) {
                            break;
                        }
                        writeToCSV(PathToCSV);
                    }
                }
            } else {
                System.out.println("START THE NEW GAME");
            }
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        System.out.println("Player #1, please enter your name:");
        playerName1 = scanner.nextLine();
        System.out.println("Player #2, please enter your name:");
        playerName2 = scanner.nextLine();
        placeShips(playerName1, battlefield1);
        placeShips(playerName2, battlefield2);

        while (true) {
            makeTurn(playerName1, monitor1, battlefield2);
            if (isWinCondition()) {
                break;

            }
            makeTurn(playerName2, monitor2, battlefield1);
            if (isWinCondition()) {
                break;

            }
            writeToCSV(PathToCSV);
        }
    }

    public static void writeToCSV(String pathToCSVFile) {
        try {
            FileWriter fw = new FileWriter(pathToCSVFile);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(playerName1 + ";" +
                    Arrays.deepToString(battlefield1) + ";" +
                    Arrays.deepToString(monitor1) + ";" +
                    playerName2 + ";" +
                    Arrays.deepToString(battlefield1) + ";" +
                    Arrays.deepToString(monitor2));
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readFromCSV(String pathToCSVFile) {
        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader(pathToCSVFile));
            String[] loadedData = null;

            while ((line = br.readLine()) != null) {
                loadedData = line.split(";");
            }
            assert loadedData != null;
            playerName1 = loadedData[0];
            battlefield1 = parseStringToMatrix(loadedData[1]);
            monitor1 = parseStringToMatrix(loadedData[2]);
            playerName2 = loadedData[3];
            battlefield2 = parseStringToMatrix(loadedData[4]);
            monitor2 = parseStringToMatrix(loadedData[5]);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[][] parseStringToMatrix(String stringToParse) {

        String[] valuesOfMatrixInArray = stringToParse
                .replaceAll("\\[", "")
                .replaceAll("\\]", "")
                .replaceAll(" ", "")
                .split(",");
        int[][] resultMatrix = new int[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                resultMatrix[i][j] = Integer.parseInt(valuesOfMatrixInArray[i * 10 + j]);
            }
        }
        return resultMatrix;
    }

    public static void placeShips(String playerName, int[][] battlefield) {
        int deck = 4;
        while (true) {
            while (deck >= 1) {
                System.out.println();
                System.out.println(playerName + ", please place your " + deck +
                        "-deck ship on the battlefield:");
                System.out.println();
                drawField(battlefield);
                System.out.println("Please enter OX coordinate:");
                int x = scanner.nextInt();
                System.out.println("Please enter OY coordinate:");
                int y = scanner.nextInt();
                System.out.println("Choose direction:");
                System.out.println("1. Vertical.");
                System.out.println("2. Horizontal.");
                int direction = scanner.nextInt();
                if (!isAvailable(x, y, deck, direction, battlefield)) {
                    System.out.println("Wrong coordinates!");
                } else {
                    for (int i = 0; i < deck; i++) {
                        if (direction == 1) {
                            battlefield[x][y + i] = 1;
                        } else {
                            battlefield[x + i][y] = 1;
                        }
                    }
                    deck--;
                }
            }
            return;
        }
    }

    public static void drawField(int[][] battlefield) {
        System.out.println("  0 1 2 3 4 5 6 7 8 9");
        for (int i = 0; i < battlefield.length; i++) {
            System.out.print(i + " ");

            for (int j = 0; j < battlefield[1].length; j++) {
                if (battlefield[j][i] == 0) {
                    System.out.print("- ");
                } else {
                    System.out.print("X ");
                }
            }
            System.out.println();
        }
    }

    public static void makeTurn(String playerName, int[][] monitor, int[][] battlefield) {
        while (true) {
            System.out.println(playerName + ", please, make your turn.");
            System.out.println("  0 1 2 3 4 5 6 7 8 9");
            int i;
            int j;
            for (i = 0; i < monitor.length; i++) {
                System.out.print(i + " ");
                for (j = 0; j < monitor[1].length; j++) {
                    if (monitor[j][i] == 0) {
                        System.out.print("- ");
                    } else if (monitor[j][i] == 1) {
                        System.out.print(". ");
                    } else {
                        System.out.print("X ");
                    }
                }
                System.out.println();
            }

            System.out.println("Please enter OX coordinate:");
            i = scanner.nextInt();
            System.out.println("Please enter OY coordinate:");
            j = scanner.nextInt();
            if (battlefield[i][j] != 1) {
                System.out.println("Miss! Your opponents turn!");
                monitor[i][j] = 1;
                return;
            }
            System.out.println("Hit! Make your turn again!");
            monitor[i][j] = 2;
        }
    }

    public static boolean isWinCondition() {
        int counter1 = 0;
        int counter2;
        int i;
        for (counter2 = 0; counter2 < monitor1.length; counter2++) {
            for (i = 0; i < monitor1[counter2].length; i++) {
                if (monitor1[counter2][i] == 2) {
                    counter1++;
                }
            }
        }

        counter2 = 0;

        for (i = 0; i < monitor2.length; i++) {
            for (int j = 0; j < monitor2[i].length; j++) {
                if (monitor2[i][j] == 2) {
                    counter2++;
                }
            }
        }

        if (counter1 >= 9) {
            System.out.println(playerName1 + " WIN!!!");
            return true;
        } else if (counter2 >= 9) {
            System.out.println(playerName2 + " WIN!!!");
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAvailable(int x, int y, int deck, int rotation, int[][] battlefield) {
        if (rotation == 1 && y + deck > battlefield.length) {
            return false;
        } else if (rotation == 2 && x + deck > battlefield[0].length) {
            return false;
        } else {
            while (deck != 0) {
                for (int i = 0; i < deck; i++) {
                    int xi = 0;
                    int yi = 0;
                    if (rotation == 1) {
                        yi = i;
                    } else {
                        xi = i;
                    }
                    if (x + 1 + xi < battlefield.length && x + 1 + xi >= 0 && battlefield[x + 1 + xi][y + yi] != 0) {
                        return false;
                    }
                    if (x - 1 + xi < battlefield.length && x - 1 + xi >= 0 && battlefield[x - 1 + xi][y + yi] != 0) {
                        return false;
                    }
                    if (y + 1 + yi < battlefield.length && y + 1 + yi >= 0 && battlefield[x + xi][y + 1 + yi] != 0) {
                        return false;
                    }
                    if (y - 1 + yi < battlefield.length && y - 1 + yi >= 0 && battlefield[x + xi][y - 1 + yi] != 0) {
                        return false;
                    }
                }
                deck--;
            }
            return true;
        }
    }

    static {
        scanner = new Scanner(System.in);
        battlefield1 = new int[10][10];
        battlefield2 = new int[10][10];
        monitor1 = new int[10][10];
        monitor2 = new int[10][10];
    }
}
