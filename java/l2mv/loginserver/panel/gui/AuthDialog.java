package l2mv.loginserver.panel.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public final class AuthDialog
{
	public enum Type
	{
		INFO,
		WARNING,
		ERROR
	}

	private static final Color BG = new Color(13, 14, 20);
	private static final Color SURFACE = new Color(22, 24, 34);
	private static final Color SURFACE_ALT = new Color(31, 34, 48);
	private static final Color BORDER = new Color(61, 66, 88);
	private static final Color TEXT = new Color(238, 239, 245);
	private static final Color MUTED = new Color(160, 166, 184);
	private static final Color ORANGE = new Color(255, 137, 54);
	private static final Color ORANGE_HOVER = new Color(255, 156, 86);
	private static final Color RED = new Color(226, 72, 83);
	private static final Color RED_HOVER = new Color(241, 93, 103);
	private static final Color BLUE = new Color(88, 166, 255);

	private AuthDialog()
	{
	}

	public static boolean confirm(Component parent, String title, String message, Type type, String yesText, String noText)
	{
		final AtomicBoolean result = new AtomicBoolean(false);
		JDialog dialog = createBaseDialog(parent, title);

		Color accent = getAccent(type);

		RoundPanel content = new RoundPanel(SURFACE, 24);
		content.setLayout(new BorderLayout(0, 18));
		content.setBorder(new EmptyBorder(18, 18, 18, 18));

		content.add(createHeader(dialog, title, accent), BorderLayout.NORTH);
		content.add(createBody(type, message, accent), BorderLayout.CENTER);
		content.add(createFooter(dialog, result, yesText, noText, accent), BorderLayout.SOUTH);

		dialog.setContentPane(content);
		finishAndShow(parent, dialog);

		return result.get();
	}

	public static void showMessage(Component parent, String title, String message, Type type)
	{
		JDialog dialog = createBaseDialog(parent, title);

		Color accent = getAccent(type);

		RoundPanel content = new RoundPanel(SURFACE, 24);
		content.setLayout(new BorderLayout(0, 18));
		content.setBorder(new EmptyBorder(18, 18, 18, 18));

		content.add(createHeader(dialog, title, accent), BorderLayout.NORTH);
		content.add(createBody(type, message, accent), BorderLayout.CENTER);

		JButton ok = createButton("Entendi", accent, brighter(accent));
		ok.addActionListener(e -> dialog.dispose());

		JPanel footer = new JPanel(new BorderLayout());
		footer.setOpaque(false);
		footer.add(ok, BorderLayout.EAST);

		content.add(footer, BorderLayout.SOUTH);

		dialog.setContentPane(content);
		finishAndShow(parent, dialog);
	}

	private static JDialog createBaseDialog(Component parent, String title)
	{
		Window owner = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
		JDialog dialog = owner == null ? new JDialog((Window) null, title, Dialog.ModalityType.APPLICATION_MODAL) : new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);

		dialog.setUndecorated(true);
		dialog.setResizable(false);
		dialog.setBackground(new Color(0, 0, 0, 0));
		dialog.setIconImages(AuthResources.getApplicationIcons());
		dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder());
		dialog.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					dialog.dispose();
				}
			}
		});

		return dialog;
	}

	private static JPanel createHeader(JDialog dialog, String title, Color accent)
	{
		JPanel header = new JPanel(new BorderLayout(12, 0));
		header.setOpaque(false);

		JLabel icon = new JLabel(AuthResources.getApplicationIcon(24));
		icon.setPreferredSize(new Dimension(30, 30));

		if (icon.getIcon() == null)
		{
			icon.setIcon(AuthIcons.of(AuthIcons.Type.INFO, accent, 24));
		}

		JLabel label = new JLabel(title);
		label.setForeground(TEXT);
		label.setFont(new Font("Segoe UI", Font.BOLD, 17));

		JButton close = new JButton(AuthIcons.of(AuthIcons.Type.CLOSE, MUTED, 16));
		close.setFocusPainted(false);
		close.setBorderPainted(false);
		close.setContentAreaFilled(false);
		close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		close.setPreferredSize(new Dimension(30, 30));
		close.addActionListener(e -> dialog.dispose());

		DragHandler drag = new DragHandler(dialog);
		header.addMouseListener(drag);
		header.addMouseMotionListener(drag);
		label.addMouseListener(drag);
		label.addMouseMotionListener(drag);

		header.add(icon, BorderLayout.WEST);
		header.add(label, BorderLayout.CENTER);
		header.add(close, BorderLayout.EAST);

		return header;
	}

	private static JPanel createBody(Type type, String message, Color accent)
	{
		JPanel body = new JPanel(new BorderLayout(16, 0));
		body.setOpaque(false);

		RoundPanel iconBox = new RoundPanel(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 28), 22);
		iconBox.setLayout(new BorderLayout());
		iconBox.setPreferredSize(new Dimension(68, 68));

		AuthIcons.Type iconType;

		if (type == Type.ERROR)
		{
			iconType = AuthIcons.Type.ERROR;
		}
		else if (type == Type.WARNING)
		{
			iconType = AuthIcons.Type.WARNING;
		}
		else
		{
			iconType = AuthIcons.Type.INFO;
		}

		JLabel icon = new JLabel(AuthIcons.of(iconType, accent, 38), SwingConstants.CENTER);
		iconBox.add(icon, BorderLayout.CENTER);

		JTextArea text = new JTextArea(message);
		text.setEditable(false);
		text.setFocusable(false);
		text.setOpaque(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setForeground(TEXT);
		text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		text.setBorder(new EmptyBorder(3, 0, 0, 0));
		text.setPreferredSize(new Dimension(360, Math.max(74, calculateTextHeight(message))));

		body.add(iconBox, BorderLayout.WEST);
		body.add(text, BorderLayout.CENTER);

		return body;
	}

	private static JPanel createFooter(JDialog dialog, AtomicBoolean result, String yesText, String noText, Color accent)
	{
		JPanel footer = new JPanel(new BorderLayout());
		footer.setOpaque(false);

		JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
		buttons.setOpaque(false);

		JButton yes = createButton(yesText, accent, brighter(accent));
		JButton no = createButton(noText, SURFACE_ALT, new Color(45, 49, 68));

		yes.addActionListener(e -> {
			result.set(true);
			dialog.dispose();
		});

		no.addActionListener(e -> {
			result.set(false);
			dialog.dispose();
		});

		buttons.add(yes);
		buttons.add(no);

		footer.add(buttons, BorderLayout.EAST);

		return footer;
	}

	private static JButton createButton(String text, Color normal, Color hover)
	{
		JButton button = new JButton(text);
		button.setForeground(TEXT);
		button.setBackground(normal);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setPreferredSize(new Dimension(124, 40));

		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (button.isEnabled())
				{
					button.setBackground(hover);
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				button.setBackground(normal);
			}
		});

		return button;
	}

	private static void finishAndShow(Component parent, JDialog dialog)
	{
		dialog.pack();
		dialog.setMinimumSize(new Dimension(520, dialog.getHeight()));
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

	private static Color getAccent(Type type)
	{
		if (type == Type.ERROR)
		{
			return RED;
		}

		if (type == Type.WARNING)
		{
			return ORANGE;
		}

		return BLUE;
	}

	private static Color brighter(Color color)
	{
		if (color.equals(RED))
		{
			return RED_HOVER;
		}

		if (color.equals(ORANGE))
		{
			return ORANGE_HOVER;
		}

		return new Color(Math.min(255, color.getRed() + 22), Math.min(255, color.getGreen() + 22), Math.min(255, color.getBlue() + 22));
	}

	private static int calculateTextHeight(String message)
	{
		if (message == null)
		{
			return 78;
		}

		int lines = Math.max(2, message.split("\\n", -1).length);
		return Math.min(160, lines * 23 + 38);
	}

	private static final class DragHandler extends MouseAdapter
	{
		private final Window window;
		private Point pressed;

		private DragHandler(Window window)
		{
			this.window = window;
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			pressed = e.getPoint();
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			if (pressed == null)
			{
				return;
			}

			Point location = window.getLocation();
			window.setLocation(location.x + e.getX() - pressed.x, location.y + e.getY() - pressed.y);
		}
	}

	private static final class RoundPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private final Color color;
		private final int radius;

		private RoundPanel(Color color, int radius)
		{
			this.color = color;
			this.radius = radius;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(BG);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
			g2.setColor(color);
			g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radius, radius);
			g2.setColor(BORDER);
			g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
			g2.dispose();

			super.paintComponent(g);
		}
	}
}
