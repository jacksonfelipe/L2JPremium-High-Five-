package l2mv.commons.net.nio;

public abstract class SendablePacket<T> extends AbstractPacket<T>
{
	public void writeC(int data)
	{
		getByteBuffer().put((byte) data);
	}
	
	protected void writeF(double value)
	{
		getByteBuffer().putDouble(value);
	}
	
	protected void writeH(int value)
	{
		getByteBuffer().putShort((short) value);
	}
	
	public void writeD(int value)
	{
		getByteBuffer().putInt(value);
	}
	
	public void writeQ(long value)
	{
		getByteBuffer().putLong(value);
	}
	
	protected void writeB(byte[] data)
	{
		getByteBuffer().put(data);
	}
	
	public void writeS(CharSequence charSequence)
	{
		if (charSequence != null)
		{
			int length = charSequence.length();
			for (int i = 0; i < length; i++)
			{
				getByteBuffer().putChar(charSequence.charAt(i));
			}
		}
		getByteBuffer().putChar('\000');
	}
	
	protected abstract boolean write();
}
