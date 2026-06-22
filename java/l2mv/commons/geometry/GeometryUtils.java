package l2mv.commons.geometry;

public class GeometryUtils
{
	private GeometryUtils()
	{
		
	}
	
	public static boolean checkIfLinesIntersects(Point2D a, Point2D b, Point2D c, Point2D d)
	{
		return checkIfLinesIntersects(a, b, c, d, null);
	}
	
 
	public static boolean checkIfLinesIntersects(Point2D a, Point2D b, Point2D c, Point2D d, Point2D r)
	{
		double distAB, theCos, theSin, newX, ABpos;
		
		// Fail if either line is undefined.
		if (a.x == b.x && a.y == b.y || c.x == d.x && c.y == d.y)
		{
			return false;
		}
		
		// (1) Translate the system so that point A is on the origin.
		double Bx = b.x - a.x;
		double By = b.y - a.y;
		double Cx = c.x - a.x;
		double Cy = c.y - a.y;
		double Dx = d.x - a.x;
		double Dy = d.y - a.y;
		
		// Discover the length of segment A-B.
		distAB = Math.sqrt(Bx * Bx + By * By);
		
		// (2) Rotate the system so that point B is on the positive X axis.
		theCos = Bx / distAB;
		theSin = By / distAB;
		newX = Cx * theCos + Cy * theSin;
		Cy = (int) (Cy * theCos - Cx * theSin);
		Cx = newX;
		newX = Dx * theCos + Dy * theSin;
		Dy = (int) (Dy * theCos - Dx * theSin);
		Dx = newX;
		
		// Fail if the lines are parallel.
		if (Cy == Dy)
		{
			return false;
		}
		
		// (3) Discover the position of the intersection point along line A-B.
		ABpos = Dx + (Cx - Dx) * Dy / (Dy - Cy);
		
		// (4) Apply the discovered position to line A-B in the original coordinate system.
		if (r != null)
		{
			r.x = (int) (a.x + ABpos * theCos);
			r.y = (int) (a.y + ABpos * theSin);
		}
		
		// Success.
		return true;
	}
	
	public static boolean checkIfLineSegementsIntersects(Point2D a, Point2D b, Point2D c, Point2D d)
	{
		return checkIfLineSegementsIntersects(a, b, c, d, null);
	}
	
 
	public static boolean checkIfLineSegementsIntersects(Point2D a, Point2D b, Point2D c, Point2D d, Point2D r)
	{
		double distAB, theCos, theSin, newX, ABpos;
		
		// Fail if either line is undefined.
		if (a.x == b.x && a.y == b.y || c.x == d.x && c.y == d.y)
		{
			return false;
		}
		
		// Fail if the segments share an end-point.
		if (a.x == c.x && a.y == c.y || b.x == c.x && b.y == c.y || a.x == d.x && a.y == d.y || b.x == d.x && b.y == d.y)
		{
			return false;
		}
		
		// (1) Translate the system so that point A is on the origin.
		double Bx = b.x - a.x;
		double By = b.y - a.y;
		double Cx = c.x - a.x;
		double Cy = c.y - a.y;
		double Dx = d.x - a.x;
		double Dy = d.y - a.y;
		
		// Discover the length of segment A-B.
		distAB = Math.sqrt(Bx * Bx + By * By);
		
		// (2) Rotate the system so that point B is on the positive X axis.
		theCos = Bx / distAB;
		theSin = By / distAB;
		newX = Cx * theCos + Cy * theSin;
		Cy = (int) (Cy * theCos - Cx * theSin);
		Cx = newX;
		newX = Dx * theCos + Dy * theSin;
		Dy = (int) (Dy * theCos - Dx * theSin);
		Dx = newX;
		
		// Fail if segment C-D doesn't cross line A-B.
		if (Cy < 0. && Dy < 0. || Cy >= 0. && Dy >= 0.)
		{
			return false;
		}
		
		// (3) Discover the position of the intersection point along line A-B.
		ABpos = Dx + (Cx - Dx) * Dy / (Dy - Cy);
		
		// Fail if segment C-D crosses line A-B outside of segment A-B.
		if (ABpos < 0. || ABpos > distAB)
		{
			return false;
		}
		
		// (4) Apply the discovered position to line A-B in the original coordinate system.
		if (r != null)
		{
			r.x = (int) (a.x + ABpos * theCos);
			r.y = (int) (a.y + ABpos * theSin);
		}
		
		// Success.
		return true;
	}
}
