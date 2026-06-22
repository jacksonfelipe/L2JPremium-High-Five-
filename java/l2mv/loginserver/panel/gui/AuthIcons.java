package l2mv.loginserver.panel.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;

public final class AuthIcons
{
	public enum Type
	{
		RESTART,
		POWER,
		CLEAR,
		STATUS,
		CLIENTS,
		GAMESERVER,
		DATABASE,
		LOG,
		WARNING,
		ERROR,
		INFO,
		CLOSE
	}

	private AuthIcons()
	{
	}

	public static Icon of(Type type, Color color, int size)
	{
		return new VectorIcon(type, color, size);
	}

	private static final class VectorIcon implements Icon
	{
		private final Type type;
		private final Color color;
		private final int size;

		private VectorIcon(Type type, Color color, int size)
		{
			this.type = type;
			this.color = color;
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
			g2.setStroke(new BasicStroke(Math.max(1.6f, size / 11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			switch (type)
			{
				case RESTART:
					paintRestart(g2);
					break;
				case POWER:
					paintPower(g2);
					break;
				case CLEAR:
					paintClear(g2);
					break;
				case STATUS:
					paintStatus(g2);
					break;
				case CLIENTS:
					paintClients(g2);
					break;
				case GAMESERVER:
					paintServer(g2);
					break;
				case DATABASE:
					paintDatabase(g2);
					break;
				case LOG:
					paintLog(g2);
					break;
				case WARNING:
					paintWarning(g2);
					break;
				case ERROR:
					paintError(g2);
					break;
				case INFO:
					paintInfo(g2);
					break;
				case CLOSE:
					paintClose(g2);
					break;
				default:
					paintInfo(g2);
					break;
			}

			g2.dispose();
		}

		private void paintRestart(Graphics2D g2)
		{
			int p = size / 5;
			Shape arc = new Arc2D.Double(p, p, size - p * 2, size - p * 2, 35, 285, Arc2D.OPEN);
			g2.draw(arc);

			Path2D arrow = new Path2D.Double();
			arrow.moveTo(size * 0.73, size * 0.15);
			arrow.lineTo(size * 0.90, size * 0.16);
			arrow.lineTo(size * 0.83, size * 0.33);
			g2.fill(arrow);
		}

		private void paintPower(Graphics2D g2)
		{
			int p = size / 5;
			g2.drawArc(p, p + 1, size - p * 2, size - p * 2, 130, 280);
			g2.drawLine(size / 2, p, size / 2, size / 2);
		}

		private void paintClear(Graphics2D g2)
		{
			int p = size / 6;
			g2.drawRoundRect(p + 2, p + 5, size - p * 2 - 4, size - p * 2 - 4, 4, 4);
			g2.drawLine(p + 1, p + 4, size - p - 1, p + 4);
			g2.drawLine(size / 2 - 4, p + 1, size / 2 + 4, p + 1);
			g2.drawLine(size / 2 - 2, p + 10, size / 2 - 2, size - p - 4);
			g2.drawLine(size / 2 + 4, p + 10, size / 2 + 4, size - p - 4);
		}

		private void paintStatus(Graphics2D g2)
		{
			g2.fillOval(size / 6, size / 6, size * 2 / 3, size * 2 / 3);
			g2.setColor(new Color(13, 14, 20));
			g2.setStroke(new BasicStroke(Math.max(1.7f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawLine(size * 38 / 100, size * 53 / 100, size * 48 / 100, size * 64 / 100);
			g2.drawLine(size * 48 / 100, size * 64 / 100, size * 66 / 100, size * 39 / 100);
		}

		private void paintClients(Graphics2D g2)
		{
			g2.drawOval(size / 3, size / 8, size / 3, size / 3);
			g2.drawArc(size / 4, size / 2, size / 2, size / 3, 0, 180);
			g2.drawOval(size / 10, size / 4, size / 4, size / 4);
			g2.drawOval(size * 66 / 100, size / 4, size / 4, size / 4);
			g2.drawArc(size / 20, size * 60 / 100, size / 3, size / 4, 0, 180);
			g2.drawArc(size * 63 / 100, size * 60 / 100, size / 3, size / 4, 0, 180);
		}

		private void paintServer(Graphics2D g2)
		{
			int p = size / 6;
			g2.drawRoundRect(p, p, size - p * 2, size / 4, 6, 6);
			g2.drawRoundRect(p, size * 38 / 100, size - p * 2, size / 4, 6, 6);
			g2.drawRoundRect(p, size * 66 / 100, size - p * 2, size / 4, 6, 6);
			g2.fillOval(size * 70 / 100, size * 23 / 100, 3, 3);
			g2.fillOval(size * 70 / 100, size * 49 / 100, 3, 3);
			g2.fillOval(size * 70 / 100, size * 77 / 100, 3, 3);
		}

		private void paintDatabase(Graphics2D g2)
		{
			int p = size / 6;
			g2.drawOval(p, p, size - p * 2, size / 4);
			g2.drawLine(p, size / 4, p, size * 72 / 100);
			g2.drawLine(size - p, size / 4, size - p, size * 72 / 100);
			g2.drawArc(p, size * 55 / 100, size - p * 2, size / 4, 180, 180);
			g2.drawArc(p, size * 35 / 100, size - p * 2, size / 4, 180, 180);
		}

		private void paintLog(Graphics2D g2)
		{
			int p = size / 5;
			g2.drawRoundRect(p, p, size - p * 2, size - p * 2, 5, 5);
			g2.drawLine(size * 35 / 100, size * 37 / 100, size * 68 / 100, size * 37 / 100);
			g2.drawLine(size * 35 / 100, size * 52 / 100, size * 68 / 100, size * 52 / 100);
			g2.drawLine(size * 35 / 100, size * 67 / 100, size * 58 / 100, size * 67 / 100);
		}

		private void paintWarning(Graphics2D g2)
		{
			Path2D triangle = new Path2D.Double();
			triangle.moveTo(size / 2.0, size * 0.12);
			triangle.lineTo(size * 0.90, size * 0.84);
			triangle.lineTo(size * 0.10, size * 0.84);
			triangle.closePath();
			g2.draw(triangle);
			g2.drawLine(size / 2, size * 35 / 100, size / 2, size * 60 / 100);
			g2.fillOval(size / 2 - 2, size * 68 / 100, 4, 4);
		}

		private void paintError(Graphics2D g2)
		{
			g2.drawOval(size / 6, size / 6, size * 2 / 3, size * 2 / 3);
			paintClose(g2);
		}

		private void paintInfo(Graphics2D g2)
		{
			g2.drawOval(size / 6, size / 6, size * 2 / 3, size * 2 / 3);
			g2.drawLine(size / 2, size * 45 / 100, size / 2, size * 68 / 100);
			g2.fillOval(size / 2 - 2, size * 30 / 100, 4, 4);
		}

		private void paintClose(Graphics2D g2)
		{
			g2.drawLine(size * 32 / 100, size * 32 / 100, size * 68 / 100, size * 68 / 100);
			g2.drawLine(size * 68 / 100, size * 32 / 100, size * 32 / 100, size * 68 / 100);
		}
	}
}
