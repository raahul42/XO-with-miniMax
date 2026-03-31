
import java.util.*;

public class XO {

	public static void main(String[] args) throws Exception {

		System.out.println("\n-->> GAME STARTS <<--\n");
		printBoard();

		// Fix: Scanner created once outside the loop, closed after the loop ends
		Scanner sc = new Scanner(System.in);

		while (true) {
			// Thread.sleep(900);
			System.out.println("available positions: " + availablePos()); // list of unused positions (1-9)
			// Thread.sleep(900);
			System.out.print("Enter your placement : ");
			int pos = sc.nextInt();

			pos = isPosValid(pos, sc); // checking if position is already taken

			UserPlay(pos);
			// Thread.sleep(900);
			printBoard();

			if (checkWinner()) {
				if (winner != null) {
					System.out.println(winner + " won!");
				} else {
					System.out.println("It's a tie!");
				}
				break;
			}

			Thread.sleep(900);
			System.out.println("CPU's turn");
			Thread.sleep(1000);

			CPUplay(getCPUpos());
			printBoard();

			if (checkWinner()) {
				if (winner != null) {
					System.out.println(winner + " won!");
				} else {
					System.out.println("It's a tie!");
				}
				break;
			}

		}

		sc.close();
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
		// Fix: use .equals() for string comparison throughout
		if (player.equals("Human")) {
			piece = 'X';
		}
		if (player.equals("CPU")) {
			piece = 'O';
		}
		else if (player.equals("human-undo")) {
			UserPositions.remove(UserPositions.indexOf(pos));

		}
		else if (player.equals("cpu-undo")) {
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

	public static int getCPUpos() throws Exception {
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

		CPUPositions.add(pos);
		placePiece(pos, "CPU");
	}

	static void UserPlay(int pos) {
		UserPositions.add(pos);
		placePiece(pos, "Human");
	}

	// Fix: pure helper that returns the game result without modifying the static
	// `winner` field -- used by minMax so it stays isolated from game state
	static String computeWinner() {
		List<List<Integer>> winPos = Arrays.asList(
			Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9),
			Arrays.asList(1, 4, 7), Arrays.asList(2, 5, 8), Arrays.asList(3, 6, 9),
			Arrays.asList(1, 5, 9), Arrays.asList(3, 5, 7)
		);
		for (List<Integer> winningSet : winPos) {
			if (UserPositions.containsAll(winningSet)) return "Human";
			if (CPUPositions.containsAll(winningSet)) return "CPU";
		}
		if ((UserPositions.size() + CPUPositions.size()) == 9) return "tie";
		return null;
	}

	static boolean checkWinner() {
		String result = computeWinner();
		if (result == null) return false;
		winner = result.equals("tie") ? null : result;
		return true;
	}

	static int minMax(String player) throws Exception {

		ArrayList<Integer> avail = availablePos();

		// Fix: use computeWinner() so minMax never reads or writes the static winner field
		String result = computeWinner();
		if (result != null) {
			if (result.equals("Human")) {
				return (-1 * (1 + avail.size()));
			} else if (result.equals("CPU")) {
				return (1 + avail.size());
			}
			return 0; // tie
		}

		int min_score = Integer.MAX_VALUE;
		int max_score = Integer.MIN_VALUE;

		for (int pos : avail) {

			// Fix: use .equals() for string comparison
			if (player.equals("CPU")) {
				CPUplay(pos);
				int currentScore = minMax("Human");
				placePiece(pos, "cpu-undo");

				if (currentScore > max_score) {
					best_position = pos;
					max_score = currentScore;
				}
			}

			else {
				UserPlay(pos);
				int currentScore = minMax("CPU");
				placePiece(pos, "human-undo");
				min_score = Math.min(min_score, currentScore);
			}
		}

		return player.equals("CPU") ? max_score : min_score;

	}
}
