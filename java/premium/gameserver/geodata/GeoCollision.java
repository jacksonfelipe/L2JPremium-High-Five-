package premium.gameserver.geodata;

import premium.commons.geometry.Shape;

public interface GeoCollision
{
	public Shape getShape();
	
	public byte[][] getGeoAround();
	
	public void setGeoAround(byte[][] geo);
	
	public boolean isConcrete();
}
