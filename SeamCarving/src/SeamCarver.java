import java.awt.Color;

public class SeamCarver {
	//possible improvements:
	
	//calling findVerticalSeam and then removeVerticalSeam (same with hor) makes transposes necessary
	//no performance gain if calling 50 remove rows after each other
	//can be solved by: adapting the remove method, multiple arraycopies per line (would be more calculation intensive)
	// or by adapting the findSeam method so it works in the other direction
	
	// memory could be saved by changing energy to short and not storing the 2 transposed matrixes
	// but it would require extra transposes => slower calculation
	private int[][] energy; 
	private int[][] energyTransposed;
	private int[][] newpic;
	private int[][] newpicTransposed;
	private int cols, rows;
	private boolean energyValid, energyTransposedValid, picValid, picTransposedValid;
	
	public SeamCarver(Picture picture) {
		// create a seam carver object based on the given picture
		cols = picture.width();
		rows = picture.height();
		energy = new int[cols][rows];
		energyTransposed = new int[rows][cols];
		newpic= new int[cols][rows];
		newpicTransposed = new int[rows][cols];

		//extra loop needed to fill in newpic before performing energy calculation
		for (int i = 0; i < cols ; i++)
			for (int j = 0; j < rows ; j++) {
				Color color = picture.get(i, j);
				newpic[i][j] = color.getRGB();
				newpicTransposed[j][i] = newpic[i][j];
			}
		picValid = true;
		picTransposedValid = true;

		for (int i = 0; i < cols ; i++)
			for (int j = 0; j < rows ; j++) {
				energy[i][j] = (int) energy(i,j);
				energyTransposed[j][i] = energy[i][j];
			}
		energyValid = true;
		energyTransposedValid = true;
	}
	
   public Picture picture() {
	   if (!picValid) {
		   newpic = transpose(newpicTransposed);
		   picValid = true;
	   }
	   Picture newPicture = new Picture(cols,rows);
	   for (int i = 0; i < cols; i++) {
		   for(int j = 0; j < rows; j++) {
			   newPicture.set(i, j, new Color(newpic[i][j]));
		   }
	   }
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
	   if (x >= energy.length || y >= energy[0].length || x < 0 || y < 0)
		   throw new IndexOutOfBoundsException();
	   // energy of pixel at column x and row y
	   if (x == width() - 1 || x == 0 || y == height() - 1 || y == 0)
		   return 195075; //3 * 255^2
	   return dSquared(x,y,true) + dSquared(x,y,false);
   }
   
   private int dSquared(int x, int y, boolean horizontal) {
	   //should not be called with a border pixel
	   Color left,right;
	   if (horizontal) {
		   left = new Color(newpic[x - 1][y]);
		   right = new Color(newpic[x + 1][y]);
	   }
	   else {
		   left = new Color(newpic[x][y - 1]);
		   right = new Color(newpic[x][y + 1]);
	   }
	   int R = left.getRed() - right.getRed();
	   int G = left.getGreen() - right.getGreen();
	   int B = left.getBlue() - right.getBlue();
	   
	   return R*R + G*G + B*B;
   }
   
   public int[] findHorizontalSeam() {
	   // sequence of indices for horizontal seam
	   return findVerticalSeam(false); 
   }
   
   public int[] findVerticalSeam() {
	   // sequence of indices for vertical seam
	   return findVerticalSeam(true); 
   }
   
   private int[] findVerticalSeam(boolean vertical) { 
	   //TODO:
	   // in this version we use transposed energy for verticalseam
	   // and the original energy for finding horizontalseam
	   int width, height;
	   int[][] currentEnergy;
	   if (!vertical) { 
		   width = rows;
		   height = cols;
		   if (!energyTransposedValid) { 
			   energyTransposed = transpose(energy);
			   energyTransposedValid = true;
		   }
		   currentEnergy = energyTransposed; 
	   }
	   else { //horizontalseam
		   width = cols;
		   height = rows;
		   if (!energyValid) { 
			   energy = transpose(energyTransposed);
			   energyValid = true;
		   }
		   currentEnergy = energy;
	   }
	   int[] seam = new int[height];
	   int[] distTo = new int[width]; //= values at bottom row of compound energy
	   int[][] Path = new int[width][height];
	   int[][] nrgCompounded = new int[width][height];
	   
	   for (int j = 0 ; j < height ; j++) {
		   for (int i = 0 ; i < width ; i++) {
			   if (j == height - 1)
				   distTo[i] = relax(i,j, currentEnergy, nrgCompounded, Path);
			   else
				   relax(i,j, currentEnergy, nrgCompounded, Path);
		   }
	   }
	   
	   // find lowest element of bottom row of compound energy matrix to find seam start
	   int pos = -1;
	   int min = Integer.MAX_VALUE;
	   for (int i = 0 ; i < width ; i++) { //exclude first and last: incorrect?
		   if (nrgCompounded[i][height - 1] < min) {
			   min = nrgCompounded[i][height - 1];
			   pos = i;
		   }
	   }

	   //test
//	   show(energy);
//	   show(nrgCompounded);
//	   show(Path);
	   
	   // construct seam from path
	   for (int j = height - 1 ; j >= 0 ; j--) {
		   seam[j] = pos;
		   pos += Path[pos][j];
	   }
	   
	   return seam;
   }
		   
   
   private int relax(int i, int j, int[][] energy, int[][] nrgCompounded, int[][] Path) {

	   if (j == 0) 
		   return nrgCompounded[i][j] = energy[i][j];

	   int smallest = Integer.MAX_VALUE;
	   for (int k = -1 ; k < 2 ; k++) {
		   if (i + k > nrgCompounded.length - 1 || i + k < 0)
			   continue;
		   if (nrgCompounded[i + k][j - 1] < smallest) {
			   smallest = nrgCompounded[i + k][j - 1];
			   nrgCompounded[i][j] = energy[i][j] + nrgCompounded[i + k][j - 1];
			   Path[i][j] = k;
		   }
	   }
	   return nrgCompounded[i][j];
   }
   
   public void removeHorizontalSeam(int[] seam) {
	   // remove horizontal seam from current picture
	   if (seam.length != cols)
		   throw new java.lang.IllegalArgumentException();
	   if (!energyValid) {
		   energy = transpose(energyTransposed);
		   energyValid = true;
	   }
	   if (!picValid) {
		   newpic = transpose(newpicTransposed);
		   picValid = true;
	   }
	   
	   for (int i=0 ; i < seam.length ; i++) {
		   if (i > 0 && (Math.abs(seam[i] - seam[i-1]) > 1))
			   throw new java.lang.IllegalArgumentException(); 
		   int index = seam[i];
		   System.arraycopy(energy[i], index + 1, energy[i], index, rows - index - 1);
		   System.arraycopy(newpic[i], index + 1, newpic[i], index, rows - index - 1);
	   }
//	   show(energy);
//	   lastRemoved = LastRemoved.HOR;
	   recalcSeamBorders(seam, false);
	   
	   energyTransposedValid = false;
	   picTransposedValid = false;
//	   energyTransposed = transpose(energy); 
//	   newpicTransposed = transpose(newpic);
	   rows--;
   }
   public void removeVerticalSeam(int[] seam) {
	   // remove vertical seam from current picture
	   if (seam.length != rows)
		   throw new java.lang.IllegalArgumentException();
	   if (!energyTransposedValid) {
		   energyTransposed = transpose(energy); // or cheaper to shift in transposed?
		   energyTransposedValid = true;
	   }
	   if (!picTransposedValid) {
		   newpicTransposed = transpose(newpic);
		   picTransposedValid = true;
	   }
   
	   for (int i=0 ; i < seam.length ; i++) {
		   if (i > 0 && (Math.abs(seam[i] - seam[i-1]) > 1))
				   throw new java.lang.IllegalArgumentException();   
		   int index = seam[i];
		   System.arraycopy(energyTransposed[i], index + 1, energyTransposed[i], index, cols - index - 1);
		   System.arraycopy(newpicTransposed[i], index + 1, newpicTransposed[i], index, cols - index - 1);
	   }
	   newpic = transpose(newpicTransposed);
	   picValid = true;
	   
	   energyValid = false;
//	   picValid = false;
	   
//	   show(energy);
//	   lastRemoved = LastRemoved.VERT;
	   recalcSeamBorders(seam, true); //do this before transposing
	   
//	   energy = transpose(energyTransposed);
	   cols--;
   }
   
   private void recalcSeamBorders(int[] seam, boolean vertical) {
	   if (vertical) {
		   for (int i=0 ; i < seam.length ; i++) {
			   int index = seam[i];
			   if (index > 0) {
//				   energy[index-1][i] = (int) energy(index-1,i);
//				   if (index-1 == 0)
//					   energyTransposed[i][index-1] = 195075;
//				   else
					   energyTransposed[i][index-1] = (int) energy(index-1,i);//energy[index-1][i];
				   
			   }
//			   energy[index][i] = (int) energy(index,i);
//			   if (index == cols - 1)
//				   energyTransposed[i][index] = 195075;
//			   else
				   energyTransposed[i][index] = (int) energy(index,i);//energy[index][i];
		   }
	   }
	   else {
		   for (int i=0 ; i < seam.length ; i++) {
			   int index = seam[i];
			   if (index > 0) {
//				   if (index-1 == 0)
//					   energy[i][index-1] = 195075;
//				   else
					   energy[i][index-1] = (int) energy(i,index-1);
//				   energyTransposed[index-1][i] = energy[i][index-1];
			   }
//			   if (index == rows - 1)
//				   energy[i][index] = 195075;
//			   else
				   energy[i][index] = (int) energy(i,index);
//			   energyTransposed[index][i] = energy[i][index];
		   }
	   }
   }
   
   private int[][] transpose(int[][] matrix) {
	   int[][] transposed = new int[matrix[0].length][matrix.length];
   
	   for (int j = 0 ; j < matrix[0].length ; j++) {
		   for (int i = 0 ; i < matrix.length ; i++) {
			   transposed[j][i] = matrix[i][j];
		   }
	   }
	   return transposed;
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
}