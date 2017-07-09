package project;
import java.util.List;
import java.util.ArrayList;

public class ConvexHull {
	List<Point> list = new ArrayList<Point>();
	// define class Point to represent 2D point
	class Point {
		int x, y;
		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}
	
	// check the direction of three points
	// if p1,p2,p3 are in the same line, return 0
	// if p1,p2,p3 are counterclockwise, return 1, else return -1
	private int findDirection(Point p1, Point p2, Point p3) {
		int dir = -(p2.y - p1.y) * (p3.x - p2.x) + (p2.x - p1.x) * (p3.y - p2.y);
		return dir == 0? 0: dir > 0? 1: -1;
	}
	
	// return square of the distance of two points
	private int squareOfDis(Point p1, Point p2){
		return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
	}
	
	//constructor, store convex points to the list
	public ConvexHull(int[][] nums) throws Exception {
		// convert array to point
		int len = nums.length;
		if (len == 0)
			throw new Exception("No points!");
		Point[] points = new Point[len];
		for (int i = 0; i < len; i++)
			points[i] = new Point(nums[i][0], nums[i][1]);
		if (len < 3){  // if size is less than 3, then it is a convex hull
			for(int i = 0; i < len; i++)
				list.add(points[i]);
			return;
		}
		
		// find left and bottom most point to start
		int left = 0;
		for (int i = 1; i < len; i++) 
			if (points[i].x < points[left].x || (points[i].x == points[left].x && points[i].y < points[left].y))
				left = i;
		
		// each time find convex hull point with counterclockwise
		int i1 = left;
		do {
			list.add(points[i1]);
			int i2 = (i1 + 1) % len;
			for (int i3 = 0; i3< len ;i3++){
				int dir = findDirection(points[i1], points[i2], points[i3]);
				// find convex hull point with counterclockwise and discard point in the same line
				if (dir < 0 || (dir ==0 && squareOfDis(points[i1], points[i2]) < squareOfDis(points[i1], points[i3])))
					i2 = i3;
			}
			i1 = i2;
		} while (i1 != left);
	}
	
	// output convex hull with counterclockwise points
	public void output() {
		System.out.println(list);
	}
	
	// check whether a point is in the convex
	public boolean checkInside(int[] num) {
		Point newP = new Point(num[0], num[1]);
		for(int i1 = 0; i1 < list.size(); i1++) {
			int i2 = (i1 + 1) % list.size();
			if (findDirection(list.get(i1), list.get(i2), newP) <= 0)
				return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int[][] nums1={{0, 3}, {4, 4}, {5, 5}, {1,9}, {6,2}};
		ConvexHull convex1=new ConvexHull(nums1);
		convex1.output();
		System.out.println(convex1.checkInside(new int[]{0,1}));
		int[][] nums2={{0, 0}, {4, 0}, {10, 0}, {0,3}, {10,4}, {4,1}};
		ConvexHull convex2=new ConvexHull(nums2);
		convex2.output();
		System.out.println(convex2.checkInside(new int[]{5,0}));
	}

}
