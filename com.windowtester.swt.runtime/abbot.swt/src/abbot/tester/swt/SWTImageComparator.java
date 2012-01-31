package abbot.tester.swt;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

/**
 * Compares <code>org.eclipse.swt.graphics.Image</code> objects on a per-pixel 
 * basis.
 * 
 * @author Kevin T Dale
 */
public class SWTImageComparator implements java.util.Comparator{
	public static final String copyright = "Licensed Materials	-- Property of IBM\n"+
	"(c) Copyright International Business Machines Corporation, 2003\nUS Government "+
	"Users Restricted Rights - Use, duplication or disclosure restricted by GSA "+
	"ADP Schedule Contract with IBM Corp.";
	
	public int compare(Object obj1, Object obj2){
		if(obj1 instanceof Image && obj2 instanceof Image){
			// compare pixel-by-pixel
			ImageData image1 = ((Image)(obj1)).getImageData();
			ImageData image2 = ((Image)(obj2)).getImageData();
			
			if(image1.width != image2.width)
				return image1.width - image2.width;
			if(image1.height != image2.height)
				return image1.height - image2.height;
							
//			int numPixels1 = image1.width*image1.height; 
//			int numPixels2 = image1.width*image1.height;
//			
//			int pixel1, pixel2;
//			
//			boolean ok;
//			int count = 0;
			RGB rgb1,rgb2;
			for(int i=0; i<image1.width; i++){
				for(int j=0; j<image1.height; j++){
					rgb1 = image1.palette.getRGB(image1.getPixel(i,j));
					rgb2 = image2.palette.getRGB(image2.getPixel(i,j));
					
					if(rgb1.red!=rgb2.red)
						return rgb1.red-rgb2.red;
					if(rgb1.green!=rgb2.green)
						return rgb1.green - rgb2.green;
					 if(rgb1.blue!=rgb2.blue)						
						return rgb1.blue - rgb2.blue;
				}
				
			}
			return 0;
		}
		return -1;
	}
}
