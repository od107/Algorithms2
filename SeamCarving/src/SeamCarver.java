import java.awt.Color;

public class SeamCarver {
	private Picture pic;
	// these can be removed or moved in the related methods
	// putting them here improves reuse between calls
	
	private int[][] energy; // save memory and change to short?
	private int[][] nrgCompounded;
	private int[][] energyTransposed;
	private int[][] nrgCompoundedTransposed;
	private boolean[][] deleted; //to keep track which pixels have been deleted
	private LastCalc lastcalc;
	
	private enum LastCalc {HOR, VERT};
	
   public SeamCarver(Picture picture) {
	   // create a seam carver object based on the given picture
	   pic = picture;
	   energy = new int[pic.width()][pic.height()];
	   nrgCompounded = new int[pic.width()][pic.height()];
	   energyTransposed = new int[pic.height()][pic.width()];
	   nrgCompoundedTransposed = new int[pic.height()][pic.width()];
	   deleted = new boolean[pic.width()][pic.height()];
	   
	   //fill energy and energytransposed
	   for (int i = 0; i < pic.width() ; i++)
		   for (int j = 0; j < pic.height() ; j++) {
			   energy[i][j] = (int) energy(i,j);
			   energyTransposed[j][i] = (int) energy(i,j);
		   }
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
   
   public int[] findHorizontalSeam() {
	   // sequence of indices for horizontal seam
	   
	   //to be used if we're not storing transposed anymire
	   if (lastcalc != LastCalc.HOR) {
		   
	   }
	   return findVerticalSeam(energyTransposed, nrgCompoundedTransposed);
   }
   
   private int[][] transpose(int[][] energy) {
	   int[][] transposed = new int[energy[0].length][energy.length];
   
	   for (int j = 0 ; j < energy[0].length ; j++) {
		   for (int i = 0 ; i < energy.length ; i++) {
			   transposed[j][i] = energy[i][j];
		   }
	   }
	   return transposed;
   }
   
   public int[] findVerticalSeam() {
	   // sequence of indices for vertical seam
	   //transform energy matrix to compound energy matrix
	   return findVerticalSeam(energy, nrgCompounded);
   }
   
   private int[] findVerticalSeam(int[][] energy, int[][] nrgCompounded) {
	   int[] seam = new int[energy[0].length];
	   int[] distTo = new int[energy.length]; //= values at bottom row of compound energy
	   int[][] Path = new int[energy.length][energy[0].length];
	   
	   for (int j = 0 ; j < energy[0].length ; j++) {
		   for (int i = 0 ; i < energy.length ; i++) {
			   if (j == energy[0].length - 1)
				   distTo[i] = relax(i,j, energy, nrgCompounded, Path);
			   else
				   relax(i,j, energy, nrgCompounded, Path);
		   }
	   }
	   
	   // find lowest element of bottom row of compound energy matrix to find seam start
	   int pos = -1;
	   int min = Integer.MAX_VALUE;
	   for (int i = 1 ; i < energy.length - 1 ; i++) {
		   if (nrgCompounded[i][energy[0].length - 1] < min) {
			   min = nrgCompounded[i][energy[0].length - 1];
			   pos = i;
		   }
	   }

	   //test
//	   show(nrgCompounded);
//	   show(Path);
	   
	   // construct seam from path
	   for (int j = energy[0].length - 1 ; j >= 0 ; j--) {
		   seam[j] = pos;
		   pos += Path[pos][j];
	   }
	   
	   return seam;
   }
   
   private void show(int[][] array) {
	   //for testing purposes
	   for (int j = 0; j < array[0].length; j++) {
           for (int i = 0; i < array.length ; i++)
               System.out.print(array[i][j] +"\t");
           System.out.println();
       }
	   System.out.println();
   }
   
   private int relax(int i, int j, int[][] energy, int[][] nrgCompounded, int[][] Path) {
	   // in a later phase remove nrgcompounded and only create the single last line

	   if (j == 0) 
		   return nrgCompounded[i][j] = energy[i][j];

	   int smallest = Integer.MAX_VALUE;
	   for (int k = -1 ; k < 2 ; k++) {
		   if (i + k > energy.length - 1 || i + k < 0)
			   continue;
		   if (nrgCompounded[i + k][j - 1] < smallest) {
			   //this line is not correct
			   smallest = nrgCompounded[i + k][j - 1];
			   nrgCompounded[i][j] = energy[i][j] + nrgCompounded[i + k][j - 1];
			   Path[i][j] = k;
		   }
	   }
	   return nrgCompounded[i][j];
   }
   
   public void removeHorizontalSeam(int[] seam) {
	   // remove horizontal seam from current picture
	   
   }
   public void removeVerticalSeam(int[] seam) {
	   // remove vertical seam from current picture
	   for (int i=0 ; i < seam.length ; i++) {
		   int index = seam[i];
		   deleted[index][i] = true;
//		   System.arraycopy(energy, index + 1, energy, index, energy.length - index);
		   
	   }
   }
   
   
}