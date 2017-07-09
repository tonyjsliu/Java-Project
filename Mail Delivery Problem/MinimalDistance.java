import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
public class MinimalDistance {
	// define class Point to represent the position of S, X, G
	class Point {
		int r, c;
		Point(int r, int c) {
			this.r = r;
			this.c = c;
		}
		Point(Point p) {
			this.r = p.r;
			this.c = p.c;
		}
		@Override
		public boolean equals(Object object) {
			if (object != null && object instanceof Point) {
				Point p = (Point)object;
				return r == p.r && c == p.c;
			}
			return false;
		}
		@Override
		public int hashCode() {
			return r + c;
		}
	}
	
	// compute the minimal distance from S to G through all X
	public int minimalDistance(char[][] config) {
		
		// 1. use square matrix to build graph with the points S, X, G
		int count = 0, R = config.length, C = config[0].length;
		Map<Point, Integer> map = new HashMap<Point, Integer>(); // mapping: point -> index
		int X = 0;  
		for (int i = 0; i < R; i++)
			for (int j = 0; j < C; j++)
				if (config[i][j] == 'X')
					X++;
		int findStart = 0, findGoal = 0;
		for (int i = 0; i < R; i++) {
			for (int j = 0; j < C; j++) {
				if (config[i][j] == 'S') {  // mapping: S -> last but one 
					map.put(new Point(i, j), X);
					findStart++;
				}
				else if (config[i][j] == 'G') {  //mapping: G -> last 
					map.put(new Point(i, j), X + 1);
					findGoal++;
				}
				else if (config[i][j] == 'X')
					map.put(new Point(i, j), count++);
			}
		}
		if (findStart != 1 && findGoal != 1) // if the numbers of S and G are not exact one
			return -1;
		int[][] graph = new int[count + 2][count + 2]; //based on each point's map value, build graph
		int[] dir={0,1,0,-1,0};
		
		// for each point, use BFS to compute shortest distance for this point to other points
		for (Point start: map.keySet()) {
			Queue<Point> queue = new LinkedList<Point>();
			queue.offer(start);
			Set<Point> set = new HashSet<Point>();
			set.add(start);
			int level = 1;
			while (!queue.isEmpty()) {
				int size = queue.size();
				for (int i = 0; i < size; i++) {
					Point p = queue.poll();
					for (int j = 0; j < dir.length-1; j++) {
						Point pDir = new Point(p.r+dir[j], p.c+dir[j+1]);
						if (pDir.r < 0 || pDir.r >= R || pDir.c < 0 || pDir.c >= C 
								|| config[pDir.r][pDir.c] == '#' || set.contains(pDir))
							continue;
						queue.offer(pDir);
						set.add(pDir);
						if (map.containsKey(pDir))
							graph[map.get(start)][map.get(pDir)] = graph[map.get(pDir)][map.get(start)] = level;
					}
				}
				level++;
			}
		}
		// check whether G is accessible to other points
		for(int i = 0; i<graph.length-1;i++)
			if(graph[graph.length-1][i]==0)
				return -1;
		
		
		// 2. use DP to compute the shortest distance from S to G through all X
		// for dp[k][N], k is the last point's index, N is a bit vector which is an integer to store index of point with bit,
		// for example, the minimal distance from S to X5 through X0, X3, X7 is dp[5][2^0+2^3+2^7]
		// for N=2^k1+2^k2+...2^km, dp[k][N]=min(dp[ki][N-2^k]+dis(ki,k), i=1:m);
		
		// compute from S to X through no X points
		int m = (int)Math.pow(2, count);
		int[][] dp = new int[count][m];
		for (int i = 0; i < dp.length; i++)
			dp[i][(int)Math.pow(2, i)] = graph[count][i];
		// compute from S to X through some X points
		for (int i = 1; i < dp.length; i++) {
			for (int j = 0; j < dp.length; j++) {
				for (int k = 0; k < dp.length; k++) {
					if(j == k)
						continue;
					for (int l = 1; l < m; l++) {
						if (dp[k][l] == 0)
							continue;
						int tmp = (int)Math.pow(2, j);
						if ((l & tmp) != 0)
							continue;
						dp[j][l + tmp] = dp[j][l + tmp] == 0? dp[k][l] + graph[k][j]
								: Math.min(dp[j][l + tmp], dp[k][l] + graph[k][j]);
					}
				}
			}
		}
		// compute from S to G through all X points
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < dp.length; i++)
			min = Math.min(dp[i][m - 1] + graph[i][count + 1], min);
		
		return min;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MinimalDistance m = new MinimalDistance();
		char[][] config1 = {{'#', '#', '#', '#', '#', '#', '#', '#'}, 
					{'#', '.', '.', '.', 'G', '.', 'X', '#'},
					{'#', '.', '#', '#', '.', 'X', '.', '#'}, 
					{'#', 'S', '.', 'X', '.', '.', '.', '#'},
					{'#', '#', '#', '#', '#', '#', '#', '#'}};
		System.out.println(m.minimalDistance(config1));
		char[][] config2 = {{'#', '#', '#', '#', '#', '#', '#', '#'}, 
				   	{'#', '.', 'X', '.', 'G', '.', 'X', '#'},
				   	{'#', '#', '#', '#', '.', 'X', '.', '#'}, 
				   	{'#', 'S', '.', 'X', '#', '.', '.', '#'},
				   	{'#', '#', '#', '#', '#', '#', '#', '#'}};
		System.out.println(m.minimalDistance(config2));
		char[][] config3 = {{'#', '#', '#', '#', '#', '#', '#', '#'}, 
					{'#', 'X', '.', '.', 'G', '.', 'X', '#'},
					{'#', '.', '#', '#', '.', 'X', '.', '#'}, 
					{'#', 'S', '.', '.', '#', '.', 'X', '#'},
					{'#', '#', '#', '#', '#', '#', '#', '#'}};
		System.out.println(m.minimalDistance(config3));
		char[][] config4 = {{'#', '#', '#', '#', '#', '#', '#', '#'}, 
					{'#', '.', '.', '.', 'G', '.', 'X', '#'},
					{'#', '.', '#', '#', '.', 'X', '.', '#'}, 
					{'#', 'S', '#', 'X', '#', '.', '.', '#'},
					{'#', '#', '#', '#', '#', '#', '#', '#'}};
		System.out.println(m.minimalDistance(config4));
	}

}
