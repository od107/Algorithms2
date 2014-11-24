import java.awt.Color;

public class SeamCarver {
	private Picture pic;
	private int[][] energy; // save memory and change to short?
	private int[][] path;
	private int[][] nrgcompounded;
	
   public SeamCarver(Picture picture) {
	   // create a seam carver object based on the given picture
	   pic = picture;
	   path = new int[pic.width()][pic.height()];
	   energy = new int[pic.width()][pic.height()];
	   nrgcompounded = new int[pic.width()][pic.height()];
	   
	   for (int i = 0; i < pic.width() ; i++)
		   for (int j = 0; j < pic.height() ; j++)
			   energy[i][j] = (int) energy(i,j);
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
	   if (x == width() - 1 || x == 0 || y == height() - 1 || y == 0)
		   return 195075; //3 * 255^2
	   return dSquared(x,y,true) + dSquared(x,y,false);
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
	   
	   return R*R + G*G + B*B;
   }
   public   int[] findHorizontalSeam() {
	   // sequence of indices for horizontal seam
	   
	   return new int[pic.width()];
   }
   public   int[] findVerticalSeam() {
	   // sequence of indices for vertical seam
	   //transform energy matrix to compound energy matrix
	   int[] seam = new int[pic.height()];
	   
	   for (int j = 0 ; j < pic.height() ; j++) {
		   for (int i = 0 ; i < pic.width() ; i++) {
			   relax(i,j);
		   }
	   }
	   // lowest element bottom row of compound energy matrix indicates seam

	   int pos = Integer.MAX_VALUE;
	   for (int i = 1 ; i < pic.width() - 1 ; i++) {
		   if (nrgcompounded[i][pic.height()-1] < pos)
			   pos = i;
	   }

	   //test
	   for (int j = 0; j < height(); j++)
       {
           for (int i = 0; i < width(); i++)
               System.out.print(nrgcompounded[i][j] +"\t");
           System.out.println();
       }
	   System.out.println();
	   for (int j = 0; j < height(); j++)
	   {
           for (int i = 0; i < width(); i++)
               System.out.print(path[i][j] +"\t");
           System.out.println();
       }
	   System.out.println();
	   
	   // we can look for the smallest of the 3 above or follow the path
	   for (int j = pic.height() - 1 ; j >= 0 ; j--) {
		   seam[j] = pos;
		   pos += path[pos][j];
	   }
	   
	   return seam;
   }
   
   private void relax(int i, int j) {
//	   if (i == width() - 1 || i == 0 || j == height() - 1 || j == 0)
	
	   if (j == 0) {
		   nrgcompounded[i][j] = energy[i][j];
//		   path[i][j] = 0;
		   return;
	   }
	   int smallest = Integer.MAX_VALUE;
	   for (int k = -1 ; k < 2 ; k++) {
		   if (i + k > pic.width() - 1 || i + k < 0)
			   continue;
		   if (nrgcompounded[i + k][j - 1] < smallest) {
			   //this line is not correct
			   smallest = nrgcompounded[i + k][j - 1];
			   nrgcompounded[i][j] = energy[i][j] + nrgcompounded[i + k][j - 1];
			   path[i][j] = k;
		   }
	   }
   }
   
   public    void removeHorizontalSeam(int[] seam) {
	   // remove horizontal seam from current picture
	   
   }
   public    void removeVerticalSeam(int[] seam) {
	   // remove vertical seam from current picture
	   
   }
   
   
}