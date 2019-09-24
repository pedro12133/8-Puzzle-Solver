
import java.io.*;
import java.util.*;
import java.lang.Math;
import java.util.List;
import java.util.PriorityQueue;


public class Puzzle {

    //function to print puzzle
    public static void printPuzzle(int [] puzzle) {
        if(puzzle != null) {
            for(int x = 0; x < 9; x++) {
                System.out.print(puzzle[x]+ " ");
                if(x == 2 || x == 5)
                    System.out.println();
            }
        }
        else
            System.out.println("Puzzle is null.");
    }

    //function to generate a random 8-puzzle
    public static int [] randomPuzzle() {
        //initialize list with solution state
        List<Integer> puzzle = Arrays.asList(0,1,2,3,4,5,6,7,8);

        //shuffle elements
        Collections.shuffle(puzzle);

        //copy list elements into array
        int [] randomPuzzle = new int [puzzle.size()];
        for(int i = 0; i < puzzle.size(); i++)
            randomPuzzle[i] = puzzle.get(i);

        return randomPuzzle;
    }

    //function to validate an int
    public static boolean isInt(String s) {
        boolean isValid = false;
        try {
            Integer.parseInt(s);
            isValid = true;
        }
        catch (NumberFormatException e){}
        return isValid;
    }

    //function to validate user input
    public static boolean isValidPuzzle(String input) {
        //split user input at each space
        String [] elements = input.split(" ");

        //check for valid size
        if(elements.length != 9) return false;

        //array to track ints used
        boolean [] intsUsed = {false,false,false,false,false,false,false,false,false};

        //check each element is a valid int
        for(String element: elements) {
            if(isInt(element)) {
                int userInt = Integer.parseInt(element);
                if( (userInt > 8) || (userInt < 0) || intsUsed[userInt]) return false;
                intsUsed[userInt] = true;
            }
            else return false;
        }
        return true;
    }

    //function to generate a custom 8-puzzle
    public static int [] customPuzzle() {
        int [] customPuzzle = null;

        //loop while puzzle config has no solution
        while(!isSolvable(customPuzzle)) {

            //if it loops, config has no solution
            if(customPuzzle != null) System.out.println("There is no solution to that configuration.\n");

            //prompt user and retrieve input
            Scanner s = new Scanner(System.in);
            System.out.print("Enter a valid puzzle configuration: ");
            String input = "";
            input = s.nextLine();

            //if input is invalid, return null
            if(!isValidPuzzle(input)) {
                System.out.println("Invalid input.");
                return null;
            }

            //convert string to int array
            String [] numbers = input.split(" ");
            customPuzzle = new int [9];
            for(int i = 0; i < numbers.length; i++)
                customPuzzle[i] = Integer.parseInt(numbers[i]);
        }

        return customPuzzle;
    }

    //function to that determines if an puzzle is solvable
    public static boolean isSolvable(int [] puzzle) {

        boolean isSolvable = false;

        //return false if puzzle is null
        if(puzzle != null){
            int inversions = 0;
            //compare every number with every other number, skip empty space, count inversion
            for(int x = 0; x < 8; x++) {
                if(puzzle[x] == 0) continue;
                for(int y = x+1; y < 9; y++) {
                    if(puzzle[y] == 0) continue;
                    if(puzzle[x] > puzzle[y]) inversions++;
                }
            }
            if(inversions % 2 == 0) isSolvable = true;
        }
        return isSolvable;
    }

    //function to calculate number of misplaced tiles
    public static int h1(int [] puzzle) {
        int sum = 0;
        if(puzzle != null)
            for(int x = 0; x < 9; x++) {
                if (puzzle[x] == 0) continue;
                if (puzzle[x] != x)
                    sum++;
            }
        return sum;
    }

    //function to calculate manhattan distance
    public static int h2(int [] puzzle) {
        int sum = 0;
        if(puzzle != null)
            for(int i = 0; i < 9; i++) {
                int tileVal = puzzle[i];
                if(tileVal == 0) continue;
                int h = Math.abs(i%3 - tileVal%3);
                int v = Math.abs(tileVal/3 - i/3);
                sum += h+v;
            }
        return sum;
    }

    //function to calculate heuristic h1 or h2
    public static int heuristicX(int [] configuration, int x) {
        int h = -1;
        switch (x) {
            case 1:
                h = h1(configuration);
                break;
            case 2:
                h = h2(configuration);
                break;
            default:
                break;
        }
        return h;
    }

    //Node class for a puzzle configuration
    public static class Node {

        //attributes
        private Node parent;
        private int [] puzzleConfig;
        private int heuristic;
        private int depth;
        private int cost;
        private int searchCost;

        //default constructor
        public Node() {
            this.parent = null;
            this.puzzleConfig = null;
            this.heuristic = 0;
            this.depth = 0;
            this.cost = 0;
            this.searchCost = 0;
        }

        //constructor
        Node(Node parentNode, int [] puzzleConfig, int heuristic) {
            if(parentNode != null) {
                this.parent = parentNode;
                this.puzzleConfig = Arrays.copyOf(puzzleConfig,puzzleConfig.length);
                this.heuristic = heuristic;
                this.depth = parentNode.getDepth() + 1;
                this.cost = this.depth + this.heuristic;
                this.searchCost = 0;
            }
            else {
                this.parent = null;
                this.puzzleConfig = null;
                this.heuristic = 0;
                this.depth = 0;
                this.cost = 0;
                this.searchCost = 0;
            }

        }


        //setters
        public void setParent(Node parent) { this.parent = parent; }
        public void setPuzzleConfig(int [] puzzleConfig) {
            this.puzzleConfig = Arrays.copyOf(puzzleConfig, puzzleConfig.length);

        }
        public void setHeuristic(int heuristic) {
            this.heuristic = heuristic;
            this.cost = this.heuristic + this.depth;
        }
        public void setDepth(int depth) {
            this.depth = depth;
            this.cost = this.heuristic + this.depth;
        }
        public void setSearchCost(int searchCost) { this.searchCost = searchCost; }

        //getters
        public int [] getPuzzleConfig() { return this.puzzleConfig; }
        public int getCost() { return this.cost; }
        public int getDepth() { return this.depth; }
        public int getHeuristic() { return this.heuristic; }
        public int getSearchCost() { return this.searchCost; }
        public Node getParent() { return this.parent; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Arrays.equals(puzzleConfig, node.puzzleConfig);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(puzzleConfig);
        }
    }

    //function to configure blank space moving left
    public static int [] moveLeft(int [] configuration) {
        String configStr = Arrays.toString(configuration).replace("[","").replace("]","").replace(" ","").replace(",","");
        int positionOfBlank = configStr.indexOf('0');
        int [] newConfig = Arrays.copyOf(configuration,configuration.length);
        if(positionOfBlank%3 != 0) {
            newConfig[positionOfBlank] = newConfig[positionOfBlank-1];
            newConfig[positionOfBlank-1] = 0;
        }
        else
            newConfig = null;
        return newConfig;
    }

    //function to configure blank space moving right
    public static int [] moveRight(int [] configuration) {
        String configStr = Arrays.toString(configuration).replace("[","").replace("]","").replace(" ","").replace(",","");
        int positionOfBlank = configStr.indexOf('0');
        int [] newConfig = Arrays.copyOf(configuration,configuration.length);
        if(positionOfBlank%3 != 2) {
            newConfig[positionOfBlank] = newConfig[positionOfBlank+1];
            newConfig[positionOfBlank+1] = 0;;
        }
        else
            newConfig = null;
        return newConfig;
    }

    //function to configure blank space moving up
    public static int [] moveUp(int [] configuration) {
        String configStr = Arrays.toString(configuration).replace("[","").replace("]","").replace(" ","").replace(",","");
        int positionOfBlank = configStr.indexOf('0');
        int [] newConfig = Arrays.copyOf(configuration,configuration.length);
        if(positionOfBlank > 2) {
            newConfig[positionOfBlank] = newConfig[positionOfBlank-3];
            newConfig[positionOfBlank-3] = 0;
        }
        else
            newConfig = null;
        return newConfig;
    }

    //function to configure blank space moving down
    public static int [] moveDown(int [] configuration) {
        String configStr = Arrays.toString(configuration).replace("[","").replace("]","").replace(" ","").replace(",","");
        int positionOfBlank = configStr.indexOf('0');
        int [] newConfig = Arrays.copyOf(configuration,configuration.length);
        if(positionOfBlank < 6) {
            newConfig[positionOfBlank] = newConfig[positionOfBlank+3];
            newConfig[positionOfBlank+3] = 0;
        }
        else
            newConfig = null;
        return newConfig;
    }

    //function that adds leaf nodes of a given parent node to the frontier
    public static void makeLeaves(Node parentNode, PriorityQueue<Node> frontier, int x) {

        //get puzzle config
        int [] configuration = parentNode.getPuzzleConfig();

        //for every action, make a node
        int [][] leafConfigs = new int [4][configuration.length];
        leafConfigs[0] = moveLeft(configuration); //left move configuration
        leafConfigs[1] = moveRight(configuration); //right move configuration
        leafConfigs[2] = moveUp(configuration); //up move configuration
        leafConfigs[3] = moveDown(configuration); //down move configuration

        //if node is not null add nodes to frontier
        for(int i = 0; i < leafConfigs.length; i++)
            if(leafConfigs[i] != null) {
                Node leaf = new Node(parentNode, leafConfigs[i], heuristicX(leafConfigs[i], x));
                frontier.add(leaf);
            }
    }

    //overloaded function that adds leaf nodes to the frontier
    public static void makeLeaves(Node parentNode, PriorityQueue<Node> frontier, HashSet<Node> explored, int x) {

        //get puzzle config
        int [] configuration = parentNode.getPuzzleConfig();

        //for every action, make a node
        int [][] leafConfigs = new int [4][configuration.length];
        leafConfigs[0] = moveLeft(configuration); //left move configuration
        leafConfigs[1] = moveRight(configuration); //right move configuration
        leafConfigs[2] = moveUp(configuration); //up move configuration
        leafConfigs[3] = moveDown(configuration); //down move configuration

        //if node is not null add nodes to frontier
        for(int i = 0; i < leafConfigs.length; i++)
            if(leafConfigs[i] != null) {
                Node leaf = new Node(parentNode, leafConfigs[i], heuristicX(leafConfigs[i], x));
                if(!frontier.contains(leaf) && !explored.contains(leaf))
                    frontier.add(leaf);
            }

    }

    //custom comparator for the node class, compares by cost
    public static final Comparator<Node> nodeComparator = new Comparator<Node>() {
        @Override
        public int compare(Node n1, Node n2) {
            return n1.getCost() - n2.getCost();
        }
    };

    //A* tree search
    public static Node aStarTreeSearch(Node root, int x) {

        //initialize solution config
        int [] solutionConfig = {0,1,2,3,4,5,6,7,8};

        //initialize the frontier using the initial state of problem
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(nodeComparator);
        frontier.add(root);

        //loop do
        int searchCost = 0;
        while(true) {
            if(searchCost > 7000000) {
                System.out.println("Not enough memory to reach solution.");
                System.out.println("search cost > 7,000,000 nodes");
                break;
            }

            //if the frontier is empty then return failure
            if(frontier.isEmpty()) break;
            //choose node with lowest cost and remove from frontier
            Node current = frontier.remove();

            //if the node contains the goal state, return the solution node
            if (Arrays.equals(current.getPuzzleConfig(),solutionConfig)) {
                current.setSearchCost(searchCost);
                return current;
            }

            //expand the chosen node and add leaves to frontier and count expanded node
            makeLeaves(current,frontier,x);
            searchCost++;
        }
        return null;
    }

    //A* graph search
    public static Node aStarGraphSearch(Node root, int x) {

        //initialize solution config
        int [] solutionConfig = {0,1,2,3,4,5,6,7,8};

        //initialize the frontier using the initial state of problem
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(nodeComparator);
        frontier.add(root);

        //initialize the explored set to be empty
        HashSet<Node> explored = new HashSet<Node>();

        //loop do
        int searchCost = 0;
        while(true) {
            //if the frontier is empty then return failure
            if(frontier.isEmpty()) break;
            //choose node with lowest cost and remove from frontier
            Node current = frontier.remove();

            //if the node contains the goal state, return the solution node
            if (Arrays.equals(current.getPuzzleConfig(),solutionConfig)) {
                current.setSearchCost(searchCost);
                return current;
            }

            //add chosen node to explored set
            explored.add(current);
            //expand the chosen node and add leaves to frontier, only if not in frontier or explored set
            makeLeaves(current,frontier,explored,x);
            //count expanded node
            searchCost++;
        }

        return null;
    }

    //function to print solution path
    public static void printSolutionPath(Node n) {
        Node current = n;
        System.out.println("GOAL STATE");
        while(current != null) {
            int d = current.getDepth();
            int h = current.getHeuristic();
            int c = current.getCost();
            printPuzzle(current.getPuzzleConfig());
            System.out.println();
            if(d != 0) {
                System.out.println("depth: "+d);
                System.out.println("heuristic: "+h);
                System.out.println("cost: "+c);
                System.out.println(" /\\");
                System.out.println("/  \\");
                System.out.println(" ||");
                System.out.println(" ||");
            }
            current = current.parent;
        }
        System.out.println("INITIAL STATE");
    }

    //function to test and print algorithms performance
    public static void testAlgorithms () throws FileNotFoundException, UnsupportedEncodingException {

        //data files
        PrintWriter writer1 = new PrintWriter("ASGSH1Data.txt", "UTF-8");
        PrintWriter writer2 = new PrintWriter("ASGShH2Data.txt", "UTF-8");
        PrintWriter writer3 = new PrintWriter("ASTSH1Data.txt", "UTF-8");
        PrintWriter writer4 = new PrintWriter("ASTSH2Data.txt", "UTF-8");

        long start = System.currentTimeMillis();
        //for each algorithm with each heuristic print depth, search cost, execution time
        int tests = 1000;
        for(int i = 0; i < tests; i++) {

            //generate a solvable problem
            Node root = new Node();
            int [] config = randomPuzzle();
            while(!isSolvable(config))
                config = randomPuzzle();
            root.setPuzzleConfig(config);

            //generate solution with each algorithm and heuristic
            //time run time for each algorithm
            long start1 = System.currentTimeMillis();
            Node sol1 = aStarGraphSearch(root,1);
            long end1 = System.currentTimeMillis();

            long start2 = System.currentTimeMillis();
            Node sol2 = aStarGraphSearch(root,2);
            long end2 = System.currentTimeMillis();

            long start3 = System.currentTimeMillis();
            Node sol3 = aStarTreeSearch(root,1);
            long end3 = System.currentTimeMillis();

            long start4 = System.currentTimeMillis();
            Node sol4 = aStarTreeSearch(root,2);
            long end4 = System.currentTimeMillis();

            //write solution data to file if solution was found within 800,000 nodes
            //format: test# depth searchCost runtime
            if(sol1 != null) writer1.println((i+1)+" "+sol1.getDepth()+" "+sol1.getSearchCost()+" "+(end1 - start1));
            else  writer1.println((i+1));

            if(sol2 != null) writer2.println((i+1)+" "+sol2.getDepth()+" "+sol2.getSearchCost()+" "+(end2 - start2));
            else  writer2.println((i+1));

            if(sol3 != null) writer3.println((i+1)+" "+sol3.getDepth()+" "+sol3.getSearchCost()+" "+(end3 - start3));
            else  writer3.println((i+1));

            if(sol4 != null) writer4.println((i+1)+" "+sol4.getDepth()+" "+sol4.getSearchCost()+" "+(end4 - start4));
            else  writer4.println((i+1));

            //print current test# and program runtime
            System.out.println("test: "+(i+1)+" | program runtime: "+((((double)System.currentTimeMillis()/1000)/60) - (((double)start/1000)/60)));
        }
        writer1.close();
        writer2.close();
        writer3.close();
        writer4.close();
    }

    //function to solve a random puzzle configuration
    public static void runRandomPuzzleSolver(int a, int h) {

        //generate a solvable problem
        Node root = new Node();
        int [] config = randomPuzzle();
        while(!isSolvable(config))
            config = randomPuzzle();
        root.setPuzzleConfig(config);

        //obtain solution
        Node sol = null;
        long start = 0;
        long end = 0;
        if(a == 1 && (h == 1 || h == 2)) {
            start = System.currentTimeMillis();
            sol = aStarTreeSearch(root,h);
            end = System.currentTimeMillis();
        }
        if(a == 2 && (h == 1 || h == 2)) {
            start = System.currentTimeMillis();
            sol = aStarGraphSearch(root,h);
            end = System.currentTimeMillis();
        }

        //display stats & path
        if(sol != null) {
            System.out.println("Depth: "+ sol.getDepth());
            System.out.println("Search Cost: "+sol.getSearchCost());
            System.out.println("Runtime(ms): "+(end-start));

            Scanner s = new Scanner(System.in);
            System.out.println("Press <ENTER> to show solution path.");
            s.nextLine();

            printSolutionPath(sol);
            System.out.println();
        }
    }

    //function to solve a custom puzzle configuration
    public static void runCustomPuzzleSolver(int a, int h) {

        //generate a solvable problem
        Node root = new Node();
        int [] config = customPuzzle();
        if(config != null) {
            root.setPuzzleConfig(config);

            //obtain solution
            Node sol = null;
            long start = 0;
            long end = 0;
            if(a == 1 && (h == 1 || h == 2)) {
                start = System.currentTimeMillis();
                sol = aStarTreeSearch(root,h);
                end = System.currentTimeMillis();
            }
            if(a == 2 && (h == 1 || h == 2)) {
                start = System.currentTimeMillis();
                sol = aStarGraphSearch(root,h);
                end = System.currentTimeMillis();
            }

            //display stats & path
            if(sol != null) {
                //display stats & path
                System.out.println("Depth: "+ sol.getDepth());
                System.out.println("Search Cost: "+sol.getSearchCost());
                System.out.println("Runtime(ms): "+(end-start));

                Scanner s = new Scanner(System.in);
                System.out.println("Press <ENTER> to show solution path.");
                s.nextLine();

                printSolutionPath(sol);
                System.out.println();
            }
        }
    }

    //function to run the main program
    public static void runPuzzleSolver() {

        //prompt user
        Scanner s = new Scanner(System.in);
        System.out.print(
                        "(1) Random 8-Puzzle\n"+
                        "(2) Custom 8-Puzzle\n"+
                        "Choose an option: "
        );
        int choice = Character.getNumericValue(s.nextLine().charAt(0));
        if(choice != 1 && choice != 2) {
            System.out.println("Invalid choice.");
            return;
        }
        System.out.println();
        System.out.print(
                        "(1) A* Tree Search\n"+
                        "(2) A* Graph Search\n"+
                        "Choose an algorithm: "
        );
        int a = Character.getNumericValue(s.nextLine().charAt(0));
        if(a != 1 && a != 2) {
            System.out.println("Invalid choice.");
            return;
        }
        System.out.println();
        System.out.print(
                        "(1) H1: Misplaced Tiles\n"+
                        "(2) H2: Manhattan Distance\n"+
                        "Choose a heuristic: "
        );
        int h = Character.getNumericValue(s.nextLine().charAt(0));
        if(h != 1 && h != 2) {
            System.out.println("Invalid choice.");
            return;
        }
        System.out.println();

        //execute choice
        switch (choice) {
            case 1:
                runRandomPuzzleSolver(a,h);
                break;
            case 2:
                runCustomPuzzleSolver(a, h);
                break;
            default:
                break;
        }
    }

    public static void main(String [] args) throws IOException{
        runPuzzleSolver();
    }
}