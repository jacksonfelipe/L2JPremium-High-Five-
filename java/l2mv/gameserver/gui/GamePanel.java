package l2mv.gameserver.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import l2mv.gameserver.Config;
import l2mv.gameserver.GameServer;

public final class GamePanel extends JFrame
{
	private static final long serialVersionUID = 1L;

	private static final Color BG = new Color(13, 14, 20);
	private static final Color SURFACE = new Color(22, 24, 34);
	private static final Color SURFACE_ALT = new Color(31, 34, 48);
	private static final Color BORDER = new Color(61, 66, 88);

	private static final Color ORANGE = new Color(255, 137, 54);
	private static final Color ORANGE_HOVER = new Color(255, 156, 86);

	private static final Color RED = new Color(226, 72, 83);
	private static final Color RED_HOVER = new Color(241, 93, 103);

	private static final Color GREEN = new Color(78, 201, 122);
	private static final Color BLUE = new Color(88, 166, 255);
	private static final Color PURPLE = new Color(159, 122, 234);
	private static final Color YELLOW = new Color(244, 191, 79);

	private static final Color TEXT = new Color(238, 239, 245);
	private static final Color MUTED = new Color(160, 166, 184);

	private final JLabel statusValue = createCardValue("Preparando", YELLOW);
	private final JLabel portsValue = createCardValue("-", TEXT);
	private final JLabel authValue = createCardValue("Aguardando", MUTED);
	private final JLabel playersValue = createCardValue("0 / -", TEXT);
	private final JLabel memoryValue = createCardValue("-", TEXT);
	private final JLabel uptimeValue = createCardValue("00:00:00", TEXT);

	private final JTextPane logPane = new JTextPane();

	private final JButton restartButton = createButton("Reiniciar Game", GameIcons.restart(Color.WHITE), ORANGE, ORANGE_HOVER);
	private final JButton shutdownButton = createButton("Desligar Tudo", GameIcons.power(Color.WHITE), RED, RED_HOVER);
	private final JButton clearButton = createButton("Limpar Logs", GameIcons.broom(Color.WHITE), SURFACE_ALT, new Color(45, 49, 68));

	private final Timer statusTimer;

	public GamePanel()
	{
		super("L2JPremium - Painel do Game Server");

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(1080, 720));
		setSize(1160, 760);
		setLocationRelativeTo(null);

		if (!GameResources.loadWindowIcons().isEmpty())
		{
			setIconImages(GameResources.loadWindowIcons());
		}

		setContentPane(createRoot());

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				requestShutdown();
			}
		});

		statusTimer = new Timer(1000, e -> refreshLiveStats());
		statusTimer.setRepeats(true);
		statusTimer.start();

		log("INFO", "Painel visual carregado. Aguardando inicialização do Game Server...", null);
	}

	public void setStarting()
	{
		SwingUtilities.invokeLater(() ->
		{
			statusValue.setText("Inicializando");
			statusValue.setForeground(YELLOW);

			authValue.setText("Conectando");
			authValue.setForeground(YELLOW);

			portsValue.setText("Carregando");
			portsValue.setForeground(YELLOW);

			restartButton.setEnabled(true);
			shutdownButton.setEnabled(true);
		});
	}

	public void setOnline()
	{
		SwingUtilities.invokeLater(() ->
		{
			statusValue.setText("Online");
			statusValue.setForeground(GREEN);

			portsValue.setText(getPortsText());
			portsValue.setForeground(BLUE);

			authValue.setText("Conectado ao Login");
			authValue.setForeground(GREEN);

			playersValue.setText(getPlayersText());
			playersValue.setForeground(PURPLE);

			refreshLiveStats();
		});
	}

	public void setError(String message)
	{
		SwingUtilities.invokeLater(() ->
		{
			statusValue.setText(message);
			statusValue.setForeground(RED);

			authValue.setText("Verifique os logs");
			authValue.setForeground(RED);

			restartButton.setEnabled(true);
			shutdownButton.setEnabled(true);
		});
	}

	public void log(String level, String message, Throwable throwable)
	{
		SwingUtilities.invokeLater(() ->
		{
			try
			{
				StyledDocument document = logPane.getStyledDocument();

				String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
				String line = "[" + time + "] [" + level + "] " + message + "\n";

				document.insertString(document.getLength(), line, getStyleFor(level));

				if (throwable != null)
				{
					StringWriter sw = new StringWriter();
					throwable.printStackTrace(new PrintWriter(sw));

					document.insertString(document.getLength(), sw.toString() + "\n", getStyleFor("ERRO"));
				}

				if (document.getLength() > 140000)
				{
					document.remove(0, 35000);
				}

				logPane.setCaretPosition(document.getLength());
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		});
	}

	private JPanel createRoot()
	{
		JPanel root = new JPanel(new BorderLayout(18, 18));
		root.setBackground(BG);
		root.setBorder(new EmptyBorder(18, 18, 18, 18));

		root.add(createHeader(), BorderLayout.NORTH);
		root.add(createCenter(), BorderLayout.CENTER);

		return root;
	}

	private Component createHeader()
	{
		RoundPanel header = new RoundPanel(SURFACE);
		header.setLayout(new BorderLayout(18, 0));
		header.setBorder(new EmptyBorder(18, 20, 18, 20));

		JLabel logo = createLogo();

		JPanel titleBox = new JPanel(new BorderLayout(0, 5));
		titleBox.setOpaque(false);

		JLabel title = new JLabel("L2JPremium World Server");
		title.setForeground(TEXT);
		title.setFont(new Font("Segoe UI", Font.BOLD, 28));

		JLabel subtitle = new JLabel("Painel de controle.");
		subtitle.setForeground(MUTED);
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

		titleBox.add(title, BorderLayout.NORTH);
		titleBox.add(subtitle, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));
		buttons.setOpaque(false);
		buttons.add(restartButton);
		buttons.add(shutdownButton);
		buttons.add(clearButton);

		restartButton.addActionListener(e -> requestRestart());
		shutdownButton.addActionListener(e -> requestShutdown());
		clearButton.addActionListener(e -> clearLogs());

		header.add(logo, BorderLayout.WEST);
		header.add(titleBox, BorderLayout.CENTER);
		header.add(buttons, BorderLayout.EAST);

		return header;
	}

	private Component createCenter()
	{
		JPanel center = new JPanel(new BorderLayout(16, 16));
		center.setOpaque(false);

		JPanel cards = new JPanel(new GridLayout(2, 3, 14, 14));
		cards.setOpaque(false);

		cards.add(createInfoCard("Status", statusValue, GameIcons.status(GREEN)));
		cards.add(createInfoCard("Portas do Game", portsValue, GameIcons.network(BLUE)));
		cards.add(createInfoCard("Login Server", authValue, GameIcons.database(PURPLE)));
		cards.add(createInfoCard("Jogadores", playersValue, GameIcons.players(PURPLE)));
		cards.add(createInfoCard("Memória", memoryValue, GameIcons.memory(ORANGE)));
		cards.add(createInfoCard("Tempo Online", uptimeValue, GameIcons.clock(YELLOW)));

		center.add(cards, BorderLayout.NORTH);
		center.add(createLogArea(), BorderLayout.CENTER);

		return center;
	}

	private Component createLogArea()
	{
		RoundPanel wrapper = new RoundPanel(SURFACE);
		wrapper.setLayout(new BorderLayout(0, 12));
		wrapper.setBorder(new EmptyBorder(16, 16, 16, 16));

		JLabel title = new JLabel("Console Visual do Game");
		title.setForeground(TEXT);
		title.setFont(new Font("Segoe UI", Font.BOLD, 17));

		JLabel subtitle = new JLabel("Carregamento de scripts, spawns, eventos, banco de dados, avisos e erros.");
		subtitle.setForeground(MUTED);
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		JPanel titleBox = new JPanel(new BorderLayout());
		titleBox.setOpaque(false);
		titleBox.add(title, BorderLayout.NORTH);
		titleBox.add(subtitle, BorderLayout.CENTER);

		logPane.setEditable(false);
		logPane.setBackground(new Color(10, 11, 16));
		logPane.setForeground(TEXT);
		logPane.setFont(new Font("Consolas", Font.PLAIN, 13));
		logPane.setBorder(new EmptyBorder(12, 12, 12, 12));

		createStyles();

		JScrollPane scroll = new JScrollPane(logPane);
		scroll.setBorder(BorderFactory.createLineBorder(BORDER));
		scroll.getViewport().setBackground(new Color(10, 11, 16));

		wrapper.add(titleBox, BorderLayout.NORTH);
		wrapper.add(scroll, BorderLayout.CENTER);

		return wrapper;
	}

	private static JPanel createInfoCard(String title, JLabel value, Icon icon)
	{
		RoundPanel card = new RoundPanel(SURFACE);
		card.setLayout(new BorderLayout(10, 8));
		card.setBorder(new EmptyBorder(15, 16, 15, 16));

		JLabel iconLabel = new JLabel(icon);
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setPreferredSize(new Dimension(34, 44));

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(MUTED);
		titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		JPanel textBox = new JPanel(new BorderLayout(0, 6));
		textBox.setOpaque(false);
		textBox.add(titleLabel, BorderLayout.NORTH);
		textBox.add(value, BorderLayout.CENTER);

		card.add(iconLabel, BorderLayout.WEST);
		card.add(textBox, BorderLayout.CENTER);

		return card;
	}

	private static JLabel createCardValue(String text, Color color)
	{
		JLabel value = new JLabel(text);
		value.setForeground(color);
		value.setFont(new Font("Segoe UI", Font.BOLD, 16));
		value.setHorizontalAlignment(SwingConstants.LEFT);
		return value;
	}

	private static JLabel createLogo()
	{
		File logoFile = GameResources.findFirst(
			"img/splash_l2jpremium.png",
			"img/splash_gameserver.png",
			"splash_l2jpremium.png"
		);

		if (logoFile == null)
		{
			JLabel fallback = new JLabel("L2JP");
			fallback.setForeground(ORANGE);
			fallback.setFont(new Font("Segoe UI", Font.BOLD, 28));
			fallback.setHorizontalAlignment(SwingConstants.CENTER);
			fallback.setPreferredSize(new Dimension(140, 78));
			return fallback;
		}

		ImageIcon original = new ImageIcon(logoFile.getAbsolutePath());

		if (original.getIconWidth() <= 0)
		{
			JLabel fallback = new JLabel("L2JP");
			fallback.setForeground(ORANGE);
			fallback.setFont(new Font("Segoe UI", Font.BOLD, 28));
			fallback.setHorizontalAlignment(SwingConstants.CENTER);
			fallback.setPreferredSize(new Dimension(140, 78));
			return fallback;
		}

		int width = 140;
		int height = Math.max(52, original.getIconHeight() * width / original.getIconWidth());

		Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

		JLabel label = new JLabel(new ImageIcon(scaled));
		label.setPreferredSize(new Dimension(width + 10, 82));
		return label;
	}

	private void requestRestart()
	{
		boolean accepted = GameDialog.confirm(
			this,
			"Reiniciar Game Server",
			"O Game Server será reiniciado agora.\n\nO painel será fechado e aberto novamente pelo start_gameserver.vbs.",
			GameDialog.DialogType.WARNING
		);

		if (!accepted)
		{
			return;
		}

		restartButton.setEnabled(false);
		shutdownButton.setEnabled(false);

		log("AVISO", "Reinicialização solicitada pelo painel administrativo.", null);
		log("AVISO", "Encerrando processo atual com código 2 para o VBS relançar o Game Server.", null);

		Timer timer = new Timer(900, e ->
		{
			((Timer) e.getSource()).stop();
			Runtime.getRuntime().exit(2);
		});

		timer.setRepeats(false);
		timer.start();
	}

	private void requestShutdown()
	{
		boolean accepted = GameDialog.confirm(
			this,
			"Desligar Game Server",
			"Deseja realmente desligar o Game Server?\n\nO processo será encerrado e não será reiniciado automaticamente.",
			GameDialog.DialogType.DANGER
		);

		if (!accepted)
		{
			return;
		}

		restartButton.setEnabled(false);
		shutdownButton.setEnabled(false);

		log("AVISO", "Desligamento solicitado pelo painel administrativo.", null);
		log("AVISO", "Encerrando Game Server com segurança.", null);

		Timer timer = new Timer(900, e ->
		{
			((Timer) e.getSource()).stop();
			Runtime.getRuntime().exit(0);
		});

		timer.setRepeats(false);
		timer.start();
	}

	private void clearLogs()
	{
		logPane.setText("");
		log("INFO", "Console visual limpo pelo administrador.", null);
	}

	private static JButton createButton(String text, Icon icon, Color normal, Color hover)
	{
		JButton button = new JButton(text, icon);
		button.setForeground(TEXT);
		button.setBackground(normal);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setPreferredSize(new Dimension(150, 46));
		button.setIconTextGap(8);

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

	private void createStyles()
	{
		StyledDocument document = logPane.getStyledDocument();

		createStyle(document, "INFO", BLUE);
		createStyle(document, "AVISO", YELLOW);
		createStyle(document, "ERRO", RED);
		createStyle(document, "CONSOLE", MUTED);
	}

	private static void createStyle(StyledDocument document, String name, Color color)
	{
		Style style = document.addStyle(name, null);
		StyleConstants.setForeground(style, color);
		StyleConstants.setFontFamily(style, "Consolas");
		StyleConstants.setFontSize(style, 13);
	}

	private Style getStyleFor(String level)
	{
		Style style = logPane.getStyledDocument().getStyle(level);

		if (style == null)
		{
			style = logPane.getStyledDocument().getStyle("CONSOLE");
		}

		return style;
	}

	private void refreshLiveStats()
	{
		memoryValue.setText(getMemoryText());
		memoryValue.setForeground(BLUE);

		uptimeValue.setText(getUptimeText());
		uptimeValue.setForeground(YELLOW);

		playersValue.setText(getPlayersText());
		playersValue.setForeground(PURPLE);
	}

	private static String getPortsText()
	{
		try
		{
			if (Config.PORTS_GAME == null || Config.PORTS_GAME.length == 0)
			{
				return "-";
			}

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < Config.PORTS_GAME.length; i++)
			{
				if (i > 0)
				{
					sb.append(", ");
				}

				sb.append(Config.PORTS_GAME[i]);
			}

			return sb.toString();
		}
		catch (Throwable t)
		{
			return "-";
		}
	}

	private static String getPlayersText()
	{
		int online = getOnlinePlayersReflective();
		int max = -1;

		try
		{
			max = Config.MAXIMUM_ONLINE_USERS;
		}
		catch (Throwable ignored)
		{
		}

		if (max > 0)
		{
			return online + " / " + max;
		}

		return String.valueOf(online);
	}

	private static int getOnlinePlayersReflective()
	{
		try
		{
			Class<?> clazz = Class.forName("l2mv.gameserver.model.GameObjectsStorage");
			Method count = clazz.getMethod("getRealOnlinePlayersCount");
			Object value = count.invoke(null);

			if (value instanceof Number)
			{
				return ((Number) value).intValue();
			}
		}
		catch (Throwable ignored)
		{
		}

		String[][] methods =
		{
			{ "l2mv.gameserver.model.World", "getInstance", "getAllPlayersCount" },
			{ "l2mv.gameserver.model.World", "getInstance", "getPlayersCount" },
			{ "l2mv.gameserver.model.World", "getInstance", "getOnlinePlayersCount" }
		};

		for (String[] methodData : methods)
		{
			try
			{
				Class<?> clazz = Class.forName(methodData[0]);
				Method getInstance = clazz.getMethod(methodData[1]);
				Object instance = getInstance.invoke(null);
				Method count = clazz.getMethod(methodData[2]);
				Object value = count.invoke(instance);

				if (value instanceof Number)
				{
					return ((Number) value).intValue();
				}
			}
			catch (Throwable ignored)
			{
			}
		}

		return 0;
	}

	private static String getMemoryText()
	{
		Runtime runtime = Runtime.getRuntime();

		long used = runtime.totalMemory() - runtime.freeMemory();
		long max = runtime.maxMemory();

		return toMb(used) + " MB / " + toMb(max) + " MB";
	}

	private static long toMb(long bytes)
	{
		return bytes / 1024L / 1024L;
	}

	private static String getUptimeText()
	{
		try
		{
			GameServer gameServer = GameServer.getInstance();

			if (gameServer == null)
			{
				return "00:00:00";
			}

			long seconds = gameServer.uptime();
			long hours = seconds / 3600L;
			long minutes = (seconds % 3600L) / 60L;
			long remain = seconds % 60L;

			return two(hours) + ":" + two(minutes) + ":" + two(remain);
		}
		catch (Throwable t)
		{
			return "00:00:00";
		}
	}

	private static String two(long value)
	{
		return value < 10L ? "0" + value : String.valueOf(value);
	}

	private static final class RoundPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private final Color color;

		private RoundPanel(Color color)
		{
			this.color = color;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(color);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

			g2.setColor(BORDER);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);

			g2.dispose();

			super.paintComponent(g);
		}
	}
}
