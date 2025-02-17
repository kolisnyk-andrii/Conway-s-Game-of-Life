import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class GameOfLife
{
    //--- НАЛАШТУВАННЯ ---
    private static final int DEFAULT_CELL_SIZE = 20; // Розмір клітинки (Default 16)

    private static int visibleWidth = 32; // Початкова ширина поля
    private static int visibleHeight = 32; // Початкова висота поля
    private static int timerTime = 100; // Час таймеру (Default 100)

    //--- ИНШЕ ---
    private final Set<Point> aliveCells = new HashSet<>();
    private final JPanel gamePanel;
    private Timer animationTimer;
    private int cellSize = DEFAULT_CELL_SIZE;
    private final JFrame frame;

    public GameOfLife()
    {
        frame = new JFrame("Game Of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        gamePanel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                drawGrid(g);
                drawCells(g);
            }
        };

        gamePanel.setPreferredSize(new Dimension(visibleWidth * cellSize, visibleHeight * cellSize));
        gamePanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                toggleCell(e.getX() / cellSize, e.getY() / cellSize);
            }
        });

        //--- КНОПКИ УПРАВЛIННЯ ---
        JPanel buttonPanel = new JPanel();
        JButton toggleAnimationButton = new JButton("СТАРТ/СТОП");
        toggleAnimationButton.addActionListener(e -> toggleAnimation());
        JButton clearButton = new JButton("Очистити поле");
        clearButton.addActionListener(e -> clearField());
        JButton resizeButton = new JButton("Змінити розмір поля");
        resizeButton.addActionListener(e -> resizeField());
        JButton changeCellSizeButton = new JButton("Змінити розмір клітинок");
        changeCellSizeButton.addActionListener(e -> changeCellSize());

        buttonPanel.add(toggleAnimationButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(resizeButton);
        buttonPanel.add(changeCellSizeButton);

        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        //--- ТАЙМЕР ---
        animationTimer = new Timer(timerTime, e -> nextGeneration());
    }

    //--- ПОЛЕ ---
    private void drawGrid(Graphics g)
    {
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= visibleWidth; i++)
        {
            g.drawLine(i * cellSize, 0, i * cellSize, visibleHeight * cellSize);
        }
        for (int i = 0; i <= visibleHeight; i++)
        {
            g.drawLine(0, i * cellSize, visibleWidth * cellSize, i * cellSize);
        }
    }

    //--- ЖИВI КЛIТИНКИ ---
    private void drawCells(Graphics g)
    {
        g.setColor(Color.BLACK); // Колiр (Default "Color.BLACK")
        for (Point cell : aliveCells)
        {
            if (cell.x >= 0 && cell.x < visibleWidth && cell.y >= 0 && cell.y < visibleHeight)
            {
                g.fillRect(cell.x * cellSize, cell.y * cellSize, cellSize, cellSize);
            }
        }
    }

    // Перемикання стану клітинки
    private void toggleCell(int x, int y)
    {
        Point cell = new Point(x, y);
        if (aliveCells.contains(cell))
        {
            aliveCells.remove(cell);
        }
        else
        {
            aliveCells.add(cell);
        }
        gamePanel.repaint();
    }

    // Перехід до наступного покоління
    private void nextGeneration()
    {
        Set<Point> newAliveCells = new HashSet<>();
        Set<Point> candidates = new HashSet<>(aliveCells);

        for (Point cell : aliveCells)
        {
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dy = -1; dy <= 1; dy++)
                {
                    candidates.add(new Point(cell.x + dx, cell.y + dy));
                }
            }
        }

        // Перевірка умов життя і смерті
        for (Point cell : candidates)
        {
            int neighbors = countAliveNeighbors(cell.x, cell.y);
            if (aliveCells.contains(cell) && (neighbors == 2 || neighbors == 3))
            {
                newAliveCells.add(cell);
            } 
            else if (!aliveCells.contains(cell) && neighbors == 3)
            {
                newAliveCells.add(cell);
            }
        }

        aliveCells.clear();
        aliveCells.addAll(newAliveCells);
        gamePanel.repaint();
    }

    // Підрахунок живих сусідів
    private int countAliveNeighbors(int x, int y)
    {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++)
        {
            for (int dy = -1; dy <= 1; dy++)
            {
                if (dx == 0 && dy == 0) continue;
                if (aliveCells.contains(new Point(x + dx, y + dy)))
                {
                    count++;
                }
            }
        }
        return count;
    }

    // Очищення поля
    private void clearField()
    {
        aliveCells.clear();
        gamePanel.repaint();
    }

    // Зміна розміру поля
    private void resizeField()
    {
        String inputWidth = JOptionPane.showInputDialog("Введіть ширину (6-256):");
        String inputHeight = JOptionPane.showInputDialog("Введіть висоту від (6-256):");

        try
        {
            int newWidth = Integer.parseInt(inputWidth);
            int newHeight = Integer.parseInt(inputHeight);

            if (newWidth >= 6 && newWidth <= 256 && newHeight >= 6 && newHeight <= 256)
            {
                visibleWidth = newWidth;
                visibleHeight = newHeight;
                gamePanel.setPreferredSize(new Dimension(visibleWidth * cellSize, visibleHeight * cellSize));
                gamePanel.revalidate();
                frame.pack();
                clearField();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Ширина і висота мають бути в межах від 6 до 256.");
            }
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null, "Введіть коректні числа.");
        }
    }

    private void changeCellSize()
    {
        String inputSize = JOptionPane.showInputDialog("Введіть розмір клітинки (4-64):");
        try
        {
            int newSize = Integer.parseInt(inputSize);

            if (newSize >= 4 && newSize <= 64)
            {
                cellSize = newSize;
                gamePanel.setPreferredSize(new Dimension(visibleWidth * cellSize, visibleHeight * cellSize));
                gamePanel.revalidate();
                frame.pack();
                gamePanel.repaint();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Розмір клітинки має бути в межах від 4 до 64.");
            }
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null, "Введіть коректне число.");
        }
    }

    // Перемикання стану анімації
    private void toggleAnimation() 
    {
        if (animationTimer.isRunning())
        {
            animationTimer.stop();
        }
        else
        {
            animationTimer.start();
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(GameOfLife::new);
    }
}