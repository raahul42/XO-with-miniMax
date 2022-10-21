
import java.util.*;

public class XO {

	public static void main(String[] args) throws Exception {

		System.out.println("\n-->> GAME STARTS <<--\n");
		printBoard();
		
		while (true) {
			// Thread.sleep(900);
			System.out.println("available positions: " + availablePos()); // list of unused positions (1-9)
			Scanner sc = new Scanner(System.in);
			// Thread.sleep(900);
			System.out.print("Enter your placement : ");
			int pos = sc.nextInt();

			pos = isPosValid(pos, sc); // checking if position is already taken

			UserPlay(pos);
			// Thread.sleep(900);
			printBoard();
			
			if (checkWinner()) {
				sc.close();
				if (winner != null) {
					System.out.println(winner + " won!");
				} else {
					System.out.println("It's a tie!");
				}
				break;
			}

			CPUplay(getCPUpos());
			printBoard();

			if (checkWinner()) {
				sc.close();
				if (winner != null) {
					System.out.println(winner + " won!");
				} else {
					System.out.println("It's a tie!");
				}
				break;
			}

		}
	}

	static char[][] board = { 
			{ ' ', '|', ' ', '|', ' ' }, 
			{ '-', '+', '-', '+', '-' }, 
			{ ' ', '|', ' ', '|', ' ' },
			{ '-', '+', '-', '+', '-' }, 
			{ ' ', '|', ' ', '|', ' ' } };

	static ArrayList<Integer> UserPositions = new ArrayList<>();
	static ArrayList<Integer> CPUPositions = new ArrayList<>();

	static String winner = null;
	static int best_position;
	
	public static void printBoard() {
		for (char[] row : board) {
			for (char c : row) {
				System.out.print(c);
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void placePiece(int pos, String player) {
		char piece = ' ';
		if (player == "Human") {
			piece = 'X';
			UserPositions.add(pos);
		}
		if (player == "CPU") {
			piece = 'O';
			CPUPositions.add(pos);
		} 
		else if (player == "human-undo") {
			UserPositions.remove(UserPositions.indexOf(pos));

		} 
		else if (player == "cpu-undo") {
			CPUPositions.remove(CPUPositions.indexOf(pos));
		}
		switch (pos) {
		case 1:
			board[0][0] = piece;
			break;
		case 2:
			board[0][2] = piece;
			break;
		case 3:
			board[0][4] = piece;
			break;
		case 4:
			board[2][0] = piece;
			break;
		case 5:
			board[2][2] = piece;
			break;
		case 6:
			board[2][4] = piece;
			break;
		case 7:
			board[4][0] = piece;
			break;
		case 8:
			board[4][2] = piece;
			break;
		case 9:
			board[4][4] = piece;
			break;

		}
	}

	static int isPosValid(int pos, Scanner sc) {
		while (UserPositions.contains(pos) || CPUPositions.contains(pos)) {
			System.out.print("Position taken\nEnter a vacant position : ");
			pos = sc.nextInt();
		}
		return pos;
	}

	static ArrayList<Integer> availablePos() {
		ArrayList<Integer> l = new ArrayList<Integer>();

		for (int i = 1; i < 10; i++) {
			if ((!UserPositions.contains(i)) && (!CPUPositions.contains(i))) {
				l.add(i);
			}
		}
		return l;
	}

	public static int getCPUpos() {
		// Random n = new Random(); 
		// int m = n.nextInt(9) + 1;
		// while (UserPositions.contains(m) || CPUPositions.contains(m)) {
		// 	m = n.nextInt(9) + 1;
		// }
		// return m;
		minMax("CPU");
		return best_position; // perfect play for CPU
	}

	static void CPUplay(int pos) throws Exception {
		Thread.sleep(900);
		System.out.println("CPU's turn");
		Thread.sleep(1000);

		placePiece(pos, "CPU");
	}

	static void UserPlay(int pos) {
		placePiece(pos, "Human");
	}

	static boolean checkWinner() {

		HashSet<List<Integer>> winPos = new HashSet<>();
		winPos.add(Arrays.asList(1, 2, 3));
		winPos.add(Arrays.asList(4, 5, 6));
		winPos.add(Arrays.asList(7, 8, 9));   
		winPos.add(Arrays.asList(1, 4, 7));
		winPos.add(Arrays.asList(2, 5, 8));
		winPos.add(Arrays.asList(3, 6, 9));
		winPos.add(Arrays.asList(1, 5, 9));
		winPos.add(Arrays.asList(3, 5, 7));
		
		for (List<Integer> winningSet : winPos) {
			if (UserPositions.containsAll(winningSet)) {
				winner = "Human";
				return true;
			} 
			if (CPUPositions.containsAll(winningSet)) {
				winner = "CPU";
				return true;
			}
		}		
		return (UserPositions.size() + CPUPositions.size()) == 9;
	} 

	static int minMax(String player) {

		ArrayList<Integer> avail = availablePos();

		if (checkWinner()) {
			if (winner == "Human") {
				return (-1 * (1 + avail.size()));
				 
			} else if (winner == "CPU") {
				return (1 + avail.size());
				
			} else if (winner == null) {
				return 0;
			}
		}

		int min_score = Integer.MAX_VALUE;
		int max_score = Integer.MIN_VALUE;

		for (int pos : avail) {

			if (player == "CPU") {
				placePiece(pos, "CPU");
				int currentScore = minMax("Human");
				placePiece(pos, "cpu-undo");
				winner = null;

				if(currentScore > max_score) {
					best_position = pos;
				}
				max_score = Math.max(max_score, currentScore);
			} 
			
			else {
				placePiece(pos, "Human");
				int currentScore = minMax("CPU");
				placePiece(pos, "human-undo");
				winner = null;
				min_score = Math.min(min_score, currentScore);
			}
		}

		return player == "CPU" ? max_score : min_score ;

	}
}
