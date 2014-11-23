import java.lang.*;
import java.util.*;
import java.awt.Color;

public class SeamCarver {
	Picture pic;
	int[][] energy;
	
   public SeamCarver(Picture picture) {
	   // create a seam carver object based on the given picture
	   pic = picture;
   }
   public Picture picture() {
	   // current picture
	   return pic;
   }
   public     int width() {
	   // width of current picture
	   return pic.width();
   }
   public     int height() {
	   // height of current picture
	   return pic.height();
   }
   public  double energy(int x, int y) {
	   // energy of pixel at column x and row y
	   if (x == width() || x == 0 || y == height() || y == 0)
		   return 195075; //3 * 255^2
	   return dSquared(x,y,true) + dSquared(x,y, false);
   }
   
   private int dSquared(int x, int y, boolean horizontal) {
	   //should not be called with a border pixel
	   Color left,right;
	   if (horizontal) {
		   left = pic.get(x - 1, y);
		   right = pic.get(x + 1, y);
	   }
	   else {
		   left = pic.get(x, y - 1);
		   right = pic.get(x, y + 1);
	   }
	   int R = left.getRed() - right.getRed();
	   int G = left.getGreen() - right.getGreen();
	   int B = left.getBlue() - right.getBlue();
	   
	   return R^2 + G^2 + B^2;
   }
   public   int[] findHorizontalSeam() {
	   // sequence of indices for horizontal seam
	   
   }
   public   int[] findVerticalSeam() {
	   // sequence of indices for vertical seam
	   
   }
   public    void removeHorizontalSeam(int[] seam) {
	   // remove horizontal seam from current picture
	   
   }
   public    void removeVerticalSeam(int[] seam) {
	   // remove vertical seam from current picture
	   
   }
}