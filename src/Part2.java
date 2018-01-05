import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Part2 extends JFrame
{
	public static void main(String[] args)
	{
		Part2 window = new Part2();
		window.setVisible(true);
		window.setSize(800, 700);
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container cont = window.getContentPane();
		cont.setLayout(new BorderLayout());

		//This panel will contain the grid and the circle
		GridPanel gridPanel = new GridPanel();
		gridPanel.setPreferredSize(new Dimension(400, 500));
		cont.add(gridPanel, BorderLayout.PAGE_START);

		JLabel jLabel = new JLabel("Click on a Grid Points to select/Deselect it. Click 'Generate' to start generating circle.");
		gridPanel.add(jLabel, BorderLayout.PAGE_START);
		
		JLabel jLabel2 = new JLabel("You can now add or delete points, the circle will automatically transform. Click 'Clear' to reset.");
		gridPanel.add(jLabel2, BorderLayout.PAGE_START);
		
		// This panel will contain the Generate and Clear button
		JPanel buttonPanel = new JPanel();
		window.AddGenerateButton(gridPanel, buttonPanel);
		window.AddClearButton(gridPanel, buttonPanel);		
		cont.add(buttonPanel, BorderLayout.CENTER);
	}
	
	// Adds generate button to the panel
	private void AddGenerateButton(GridPanel gridPanel, JPanel buttonPanel)
	{
		JButton button1;
        button1 = new JButton("Generate Circle");
        buttonPanel.add(button1);
        
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	gridPanel.generate();
            }
        });
	}
	
	// Adds clear button to the panel
	private void AddClearButton(GridPanel gridPanel, JPanel buttonPanel)
	{
		JButton button2;
        button2 = new JButton("Clear");
		buttonPanel.add(button2);
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				gridPanel.clear();
			}
		});
	}
}

class GridPanel extends JPanel
{
	private class Grid
	{
		int d = 20;
		private Point[][] points;
		// This boolean suggest whether to draw the circle
		public boolean generate;
		
		public Grid()
		{
			points = new Point[20][20];
			int startX = 200;
			int startY = 50;
			
			for(int i=0;i<20;i++)
			{
				int x = startX + (d*i);
				for(int j=0;j<20;j++)
				{
					int y = startY + (d*j);
					points[i][j] = new Point(x,y);
				}  
			}
		}  

		// Draws the grid and the circle is present
		public void drawGrid(Graphics g) 
		{
			// Draw GridPoints
			for(int i=0;i<20;i++)
			{
				for(int j=0;j<20;j++)
				{
					Point p = points[i][j];
					p.draw(g);
				}  
			}
			
			// Draw the circle
			if(generate)
			{
				ArrayList<Point> ls = filterPoints();
				generateCircle(g, ls);
			}
		}
		
		// Filters the selected GridPoints out
		private ArrayList<Point> filterPoints()
		{
			ArrayList<Point> ls = new ArrayList<>();
			for(int i=0;i<20;i++)
			{
				for(int j=0;j<20;j++)
				{
					Point p = points[i][j];
					if(p.selected)
					{
						ls.add(p);
					}
				}  
			}
			return ls;
		}
		
		private void generateCircle(Graphics g, ArrayList<Point> ls)
		{
			int size = ls.size();
			double[] dist = new double[ls.size()];
			
			// This will hold the best fitted point's data
			double min = Integer.MAX_VALUE;
			int minMean = -1;
			int minX = -1, minY = -1;
			
			// For each point on the window test if is the center of the circle
			for(int startX=0;startX<800;startX+=d/2)
			{
				for(int startY=0;startY<700;startY+=d/2)
				{
					double sum = 0;
					
					for(int i=0;i<size;i++)
					{
						Point p = ls.get(i);
						// Find the distance between the point and the GridPoint
						dist[i] = Math.hypot(p.x-startX, p.y-startY);
						sum+=dist[i];
					}
					// Calculate mean of the distances
					double mean = sum/size;
					sum = 0;
					
					for(int j=0;j<size;j++)
					{
						sum += Math.pow((dist[j] - mean), 2);
					}
					
					// Sum holds the Variance and is used to find the minimum variance
					if(sum < min)
					{
						min = sum;
						minMean = (int)mean;
						minX = startX;
						minY = startY;
					}
				}  
			}
			
			// Draw the best fitted circle using the point identified as centre and the radius
			int SIZE = 2;
			g.setColor(Color.red);
			g.drawOval(minX-minMean,minY-minMean,2*minMean,2*minMean);
		}

		private class Point
		{
			private static final int SIZE =2;
			public int x,y;
			public boolean selected = false;
			
			public Point(int xx, int yy)
			{
				x = xx;
				y = yy;
			}
			
			// Toggle if the point is selected
			public void clicked()
			{
				selected = !selected;
			}

			// Draw the point
			public void draw(Graphics g)
			{
				if(selected)
				{
					g.setColor(Color.blue);
				}
				else
				{
					g.setColor(Color.gray);
				}
				g.fillOval(x-SIZE,y-SIZE,2*SIZE,2*SIZE);
			}
		}

		// Check if a click on the grid is actually on a GridPoint
		public void clickedAt(int x, int y) 
		{
			int maxDist = 2;
			
			Point sPoint = points[0][0];
			Point ePoint = points[19][19];
			if(x<(sPoint.x-maxDist) || x>(ePoint.x+maxDist) 
					|| y<(sPoint.y-maxDist) || y>(ePoint.y+maxDist))
			{
				return;
			}
								
			int xd = Math.abs(x - sPoint.x);
			int yd = Math.abs(y - sPoint.y);
			
			if((xd%d <= 2 || xd%d >=8) && (yd%d <= 2 || yd%d >=8))
			{
				int xi = xd/d + ((xd%d <= 2) ? 0 : 1);
				int yi = yd/d + ((yd%d <= 2) ? 0 : 1);
				
				points[xi][yi].clicked();
			}
		}
		
		// reset the grid and unselect all GridPoints
		public void clear() {
			// TODO Auto-generated method stub
			generate = false;
			for(int i=0;i<20;i++)
			{
				for(int j=0;j<20;j++)
				{
					points[i][j].selected = false;
				}
			}
		}
	}

	private Grid grid;

	public GridPanel()
	{
		grid = new Grid();
		setBackground(Color.WHITE);

		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent m)
			{
				grid.clickedAt(m.getX(), m.getY());
				repaint();
			}
		});

		repaint();
	}

	public void generate() {
		// TODO Auto-generated method stub
		grid.generate = true;
		repaint();
	}

	public void paintComponent(Graphics g)
	{ 
		super.paintComponent(g);
		grid.drawGrid(g);
	}

	public void clear() 
	{
		grid.clear();
		repaint();
	}
}
