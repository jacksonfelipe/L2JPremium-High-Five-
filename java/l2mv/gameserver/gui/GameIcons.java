package l2mv.gameserver.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

public final class GameIcons
{
	private GameIcons()
	{
	}

	public static Icon power(Color color)
	{
		return new VectorIcon(color, IconType.POWER);
	}

	public static Icon restart(Color color)
	{
		return new VectorIcon(color, IconType.RESTART);
	}

	public static Icon broom(Color color)
	{
		return new VectorIcon(color, IconType.BROOM);
	}

	public static Icon status(Color color)
	{
		return new VectorIcon(color, IconType.STATUS);
	}

	public static Icon players(Color color)
	{
		return new VectorIcon(color, IconType.PLAYERS);
	}

	public static Icon network(Color color)
	{
		return new VectorIcon(color, IconType.NETWORK);
	}

	public static Icon database(Color color)
	{
		return new VectorIcon(color, IconType.DATABASE);
	}

	public static Icon memory(Color color)
	{
		return new VectorIcon(color, IconType.MEMORY);
	}

	public static Icon clock(Color color)
	{
		return new VectorIcon(color, IconType.CLOCK);
	}

	public static Icon warning(Color color)
	{
		return new VectorIcon(color, IconType.WARNING);
	}

	private enum IconType
	{
		POWER,
		RESTART,
		BROOM,
		STATUS,
		PLAYERS,
		NETWORK,
		DATABASE,
		MEMORY,
		CLOCK,
		WARNING
	}

	private static final class VectorIcon implements Icon
	{
		private final Color color;
		private final IconType type;
		private final int size;

		private VectorIcon(Color color, IconType type)
		{
			this(color, type, 18);
		}

		private VectorIcon(Color color, IconType type, int size)
		{
			this.color = color;
			this.type = type;
			this.size = size;
		}

		@Override
		public int getIconWidth()
		{
			return size;
		}

		@Override
		public int getIconHeight()
		{
			return size;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(x, y);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(color);
			g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			switch (type)
			{
				case POWER:
					g2.drawLine(size / 2, 3, size / 2, 9);
					g2.drawArc(4, 5, size - 8, size - 8, 135, 270);
					break;

				case RESTART:
					g2.drawArc(3, 3, size - 6, size - 6, 35, 280);
					g2.drawLine(size - 4, 4, size - 4, 9);
					g2.drawLine(size - 4, 4, size - 9, 4);
					break;

				case BROOM:
					g2.drawLine(5, 4, 13, 12);
					g2.fillRoundRect(10, 11, 6, 4, 2, 2);
					g2.drawLine(9, 15, 16, 15);
					break;

				case STATUS:
					g2.fillOval(5, 5, 8, 8);
					g2.drawOval(3, 3, 12, 12);
					break;

				case PLAYERS:
					g2.drawOval(3, 3, 5, 5);
					g2.drawOval(10, 3, 5, 5);
					g2.drawArc(2, 10, 7, 6, 0, 180);
					g2.drawArc(9, 10, 7, 6, 0, 180);
					break;

				case NETWORK:
					g2.drawOval(3, 3, 4, 4);
					g2.drawOval(11, 3, 4, 4);
					g2.drawOval(7, 11, 4, 4);
					g2.drawLine(7, 6, 11, 6);
					g2.drawLine(6, 7, 8, 11);
					g2.drawLine(12, 7, 10, 11);
					break;

				case DATABASE:
					g2.drawOval(3, 3, 12, 5);
					g2.drawLine(3, 6, 3, 13);
					g2.drawLine(15, 6, 15, 13);
					g2.drawOval(3, 10, 12, 5);
					break;

				case MEMORY:
					g2.drawRoundRect(4, 4, 10, 10, 2, 2);
					g2.drawLine(7, 1, 7, 4);
					g2.drawLine(11, 1, 11, 4);
					g2.drawLine(7, 14, 7, 17);
					g2.drawLine(11, 14, 11, 17);
					break;

				case CLOCK:
					g2.drawOval(3, 3, 12, 12);
					g2.drawLine(9, 9, 9, 5);
					g2.drawLine(9, 9, 12, 11);
					break;

				case WARNING:
					g2.drawLine(9, 2, 16, 15);
					g2.drawLine(9, 2, 2, 15);
					g2.drawLine(2, 15, 16, 15);
					g2.drawLine(9, 7, 9, 11);
					g2.fillOval(8, 13, 2, 2);
					break;
			}

			g2.dispose();
		}
	}
}
