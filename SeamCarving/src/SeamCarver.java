import java.awt.Color;

public class SeamCarver {
	private Picture pic;
	// these can be removed or moved in the related methods
	// putting them here improves reuse between calls
	
	private int[][] energy; // save memory and change to short?
//	private int[][] nrgCompounded; // should this even be stored?
	private int[][] energyTransposed;
//	private int[][] nrgCompoundedTransposed;
	private boolean[][] deleted; //to keep track which pixels have been deleted
	private int[][] newpic;
	private int[][] newpicTransposed;
	private LastCalc lastcalc;
	private int cols, rows;
	
	private enum LastCalc {HOR, VERT};
	
   public SeamCarver(Picture picture) {
	   // create a seam carver object based on the given picture
	   pic = picture;
	   cols = pic.width();
	   rows = pic.height();
	   energy = new int[cols][rows];
//	   nrgCompounded = new int[cols][rows];
	   energyTransposed = new int[rows][cols];
//	   nrgCompoundedTransposed = new int[rows][cols];
	   deleted = new boolean[cols][rows];
	   newpic= new int[cols][rows];
	   newpicTransposed = new int[rows][cols];

	   //fill energy and energytransposed
	   for (int i = 0; i < cols ; i++)
		   for (int j = 0; j < rows ; j++) {
			   energy[i][j] = (int) energy(i,j);
			   energyTransposed[j][i] = (int) energy(i,j);
			   Color color = pic.get(i, j);
			   newpic[i][j] = color.getRGB();
			   newpicTransposed[j][i] = color.getRGB();
		   }
	   
	   
   }
   public Picture picture() {
	   // old method
//	   Picture newpic = new Picture(cols,rows);
//	   
//	   for (int j = 0; j < pic.height() ; j++) {
//		   int vertRemoved = 0;
//	   	   for (int i = 0; i < pic.width() ; i++) {
//			   int horRemoved = 0;
//			   if (!deleted[i][j]) {
//				   newpic.set(i-horRemoved, j-vertRemoved, pic.get(i, j));
//				   
//			   }
//			   else {
//				   
//			   }
//		   }
//	   }
	   Picture newPicture = new Picture(cols,rows);
	   for (int i = 0; i < cols; i++) {
		   for(int j = 0; j < rows; j++) {
			   newPicture.set(i, j, new Color(newpic[i][j]));
		   }
	   }
	   
	   //TODO: keep into account the deleted rows and columns
	   return newPicture;
   }
   
   public     int width() {
	   // width of current picture
	   return cols;
   }
   
   public     int height() {
	   // height of current picture
	   return rows;
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
	   
	   lastcalc = LastCalc.HOR;
	   return findVerticalSeam(true); //, nrgCompoundedTransposed);
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
	   lastcalc = LastCalc.VERT;
	   return findVerticalSeam(false); //, nrgCompounded);
   }
   
   private int[] findVerticalSeam(boolean transposed) { //, int[][] nrgCompounded) {
	   int width, height;
	   int[][] energy;
	   if (transposed) {
		   width = rows;
		   height = cols;
		   energy = this.energyTransposed; // alternative would be to transpose here
	   }
	   else {
		   width = cols;
		   height = rows;
		   energy = this.energy;
	   }
		   
	   int[] seam = new int[height];
	   int[] distTo = new int[width]; //= values at bottom row of compound energy
	   int[][] Path = new int[width][height];
	   int[][] nrgCompounded = new int[width][height];
	   
	   for (int j = 0 ; j < height ; j++) {
		   for (int i = 0 ; i < width ; i++) {
			   if (j == height - 1)
				   distTo[i] = relax(i,j, energy, nrgCompounded, Path);
			   else
				   relax(i,j, energy, nrgCompounded, Path);
		   }
	   }
	   
	   // find lowest element of bottom row of compound energy matrix to find seam start
	   int pos = -1;
	   int min = Integer.MAX_VALUE;
	   for (int i = 1 ; i < width - 1 ; i++) {
		   if (nrgCompounded[i][height - 1] < min) {
			   min = nrgCompounded[i][height - 1];
			   pos = i;
		   }
	   }

	   //test
//	   show(nrgCompounded);
//	   show(Path);
	   
	   // construct seam from path
	   for (int j = height - 1 ; j >= 0 ; j--) {
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
		   if (i + k > nrgCompounded.length - 1 || i + k < 0)
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
	   rows--;
	   
	   for (int i=0 ; i < seam.length ; i++) {
		   int index = seam[i];
		   deleted[i][index] = true;
		   System.arraycopy(energyTransposed, index + 1, energyTransposed, index, rows - index);
		   System.arraycopy(newpicTransposed, index + 1, newpicTransposed, index, rows - index);
	   }
	   energy = transpose(energyTransposed);
	   newpic = transpose(newpicTransposed);
	   
	   recalc(seam, false);
   }
   public void removeVerticalSeam(int[] seam) {
	   // remove vertical seam from current picture
	   cols--;
	   for (int i=0 ; i < seam.length ; i++) {
		   int index = seam[i];
		   deleted[index][i] = true;
		   System.arraycopy(energy, index + 1, energy, index, cols - index);
		   System.arraycopy(newpic, index + 1, newpic, index, cols - index);
	   }
	   energyTransposed = transpose(energy); // or cheaper to shift in transposed?
	   newpicTransposed = transpose(newpic);
	   
	   recalc(seam, true);
   }
   
   private void recalc(int[] seam, boolean vertical) {
	   if (vertical) {
		   for (int i=0 ; i < seam.length ; i++) {
			   int index = seam[i];
			   if (index > 0) {
				   energy[index-1][i] = (int) energy(index-1,i);
				   energyTransposed[i][index-1] = energy[index-1][i];
			   }
			   energy[index][i] = (int) energy(index,i);
			   energyTransposed[i][index] = energy[index][i];
		   }
	   }
	   else {
		   for (int i=0 ; i < seam.length ; i++) {
			   int index = seam[i];
			   if (index > 0) {
				   energy[i][index-1] = (int) energy(i,index-1);
				   energyTransposed[index-1][i] = energy[i][index-1];
			   }
			   energy[i][index] = (int) energy(i,index);
			   energyTransposed[index][i] = energy[i][index];
		   }
	   }
   }
   
   
}