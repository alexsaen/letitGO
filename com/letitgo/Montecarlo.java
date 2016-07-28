import java.util.*;
import java.io.*;

public class Montecarlo{
	private class Node{
		Node parent;
		ArrayList<Node> children = null;
		Board board; 
		int stoneType;
		Point point;
		Playout playout;
		int wins;
		int games;
		ArrayList<Point> allPossibleMoves;

		Node(Node parent, Board board, Point point, int stoneType){
			this.point = point;
			this.stoneType = stoneType;
			this.parent = parent;
			this.board = new Board(board);
			playout = new Playout();
			wins = 0;
			games = 0;
			if (parent != null && point != null) 		//root node
				this.playout.makeMove(this.board, point, stoneType);

			allPossibleMoves = this.playout.getFreePoints(this.board, Board.getOppositeSide(stoneType));
			if (allPossibleMoves.size() == 0){
				//board.printBoard();
				System.out.printf("\nallPossibleMoves == 0 %d,%d\n", point.i, point.j);
			}
		}
		Node addChild(Point p){
			Node child;
			if (children == null)
				children = new ArrayList<Node>();
			child = new Node(this, board, p, Board.getOppositeSide(stoneType));
			children.add(child);
			return child;
		}
		ArrayList<Node> getChildren(){
			return children;
		}
		void incWins(){
			wins++;
		}
		void incGames(){
			games++;
		}
		int getGames(){
			return games;
		}
		int getWins(){
			return wins;
		}
		Point getPoint(){
			return point;
		}
		int getStoneType(){  
			return stoneType;
		}
		Board getBoard(){
			return board;
		}
		Node getParent(){
			return parent;
		}
		int startPlayout(){
			if (playout == null){
				playout = new Playout();
			}
			return playout.playRandomGame(board, Board.getOppositeSide(stoneType)); 
		}
	}
	Node root;
	Montecarlo(Board board, int whoseTurn){
		root = new Node(null, board, null, whoseTurn);
	}
	public void playOneSequence(){
		Node node = selectNode(root);

		node = expand(node);
		if (node == null){
			return;
		}
		int winner = simulation(node);
		backPropagation(node, winner);

	}
	private Node selectNode(Node node){
		int i, n, w, t; 
		double value, bestValue = -1, c = 0.44;
		Node bestNode = null;
		int MAX_NODES = 10;
		ArrayList<Node> children;
		Node currentNode = node;

		for (children = currentNode.getChildren(); children != null; currentNode = bestNode){
			
			bestNode = null;
			bestValue = -1;
			t = 0;
			if (children.size() < currentNode.allPossibleMoves.size() && children.size() < MAX_NODES)
				break; ///!!!!
			/*if (children == null)
				System.out.printf("children == null");
			else
				System.out.printf("children != null");
				*/
			
			for (Node child: children){
				t += child.getGames();
			}
			

			for (Node child: children){
				n = child.getGames();
				w = child.getWins();

				value = w/n + c * Math.sqrt(Math.log(t)/n);
				
				if (value > bestValue){
					bestValue = value;
					bestNode = child;
				}
			}
			children = currentNode.getChildren();
			
		}
		return currentNode;
		
	}
	
	private Node expand(Node papa){
		ArrayList<Node> papasChildren = papa.getChildren();
		int boardSize = root.getBoard().getSize();
		int[][] childrenBoard = new int[boardSize][boardSize];
		ArrayList<Point> notExpanded = new ArrayList<Point>(); 
		Random random = new Random();
		int randomPoint;
		
		for (int i = 0; i < boardSize; i++)
			Arrays.fill(childrenBoard[i], 0);

		if (papasChildren != null){
			for (Node child: papasChildren){
				childrenBoard[child.getPoint().i][child.getPoint().j] = 1;
			}
		}
		for (Point currentPoint: papa.allPossibleMoves){
			if (childrenBoard[currentPoint.i][currentPoint.j] != 1){
				notExpanded.add(currentPoint);
			}
		}

		if (notExpanded.size() != 0){
			randomPoint = random.nextInt(notExpanded.size());
			return papa.addChild(notExpanded.get(randomPoint));
		}

		return null;
	}
	private int simulation(Node node){
		return node.startPlayout();
	}
	private void backPropagation(Node startNode, int winner){
		Node currentNode = startNode;
		do{
			if (currentNode.getStoneType() == winner)
				currentNode.incWins();
			currentNode.incGames();
			currentNode = currentNode.getParent();
		} while(currentNode != null);
		
	}
	public Point getWinner(){
		ArrayList<Node> children = root.getChildren();
		Node bestChild = null;
		int maxGames = -1, games;
		if (children != null){
			for (Node child : children){
				games = child.getGames();
				if (games > maxGames){
					maxGames = games;
					bestChild = child;
				}

			}
		}

		if (bestChild != null){
			return bestChild.getPoint();
		}
		else {
			return null;
		}
		
		
	}
	//non recursive DFS
	public void printTree(){
		Node node;
		ArrayList<Node> nodeChildren;
		Stack<Node> stack = new Stack<Node>();
		Stack<String> tabs_stack = new Stack<String>();		//TODO: fix this variable
		String tab = "+";
		
		stack.push(root);
		tabs_stack.add(tab);
		System.out.printf("\nMonteCarlo search tree:\n");
		while(!stack.empty()){
			node = stack.pop();
			//node.getBoard().printBoard();
			tab = tabs_stack.pop();
			nodeChildren = node.getChildren();
			
			if (nodeChildren != null){
				for (Node child: nodeChildren){
					tabs_stack.add(tab+"+");
					stack.add(child);
				}
			}
			if (node.getParent() != null){
				System.out.printf("\n%s %s [%d,%d] Games:%d Wins:%d\n", tab,
					node.getStoneType() == Board.FRIENDLY ? "FRIENDLY" : "ENEMY",
				  	node.getPoint().i, node.getPoint().j,
				  	node.getGames(), node.getWins());
			}

		}

	}

}