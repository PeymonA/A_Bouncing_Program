/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * YOUR UPI: PARM175
 * ==========================================================================================
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListDataListener;
import java.lang.reflect.Field;

@SuppressWarnings("serial")
class AnimationViewer extends JComponent implements Runnable, TreeModel {
	private Thread animationThread = null;		// the thread for animation
	private static int DELAY = 120;				 // the current animation speed
	private ShapeType currentShapeType=Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType=Shape.DEFAULT_PATHTYPE;	// the current path type
	private Color currentColor=Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private int currentPanelWidth=Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT, currentWidth=Shape.DEFAULT_WIDTH, currentHeight=Shape.DEFAULT_HEIGHT;
	private NestedShape root;
	private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

	public AnimationViewer() {
		start();
		addMouseListener(new MyMouseAdapter());
		root = new NestedShape(currentPanelWidth, currentPanelHeight);
	}
	public NestedShape getRoot() {
	    return root;
	}
	class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked( MouseEvent e ) {
			boolean found = false;
			for (Shape currentShape: root.getAllInnerShapes())
				if ( currentShape.contains( e.getPoint()) ) { // if the mousepoint is within a shape, then set the shape to be selected/deselected
					currentShape.setSelected( ! currentShape.isSelected() );
					found = true;
					
				}
			if (!found){
				root.createInnerShape(e.getX(), e.getY(), currentWidth, currentHeight, currentColor, currentPathType, currentShapeType);
				insertNodeInto(root.getInnerShapeAt(root.getSize() - 1), root);
			}
		}
	}
	public void setCurrentColor(Color bc) {
		currentColor = bc;
		for (Shape currentShape: root.getAllInnerShapes())
			if ( currentShape.isSelected())
				currentShape.setColor(currentColor);
	}
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Shape currentShape: root.getAllInnerShapes()) {
		currentShape.move();
		currentShape.draw(g);
		currentShape.drawHandles(g);
		}
	}
	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight() ;
		for (Shape currentShape: root.getAllInnerShapes())
			currentShape.setPanelSize(currentPanelWidth,currentPanelHeight );
	}
    @Override
    public boolean isLeaf(Object node) {
    	if (!(node instanceof NestedShape)) {
		    return true;
	    }
	    return false;
    }
    public boolean isRoot(Shape selectedNode) {
	     if (selectedNode == root) {
	    	 return true;
	     }
	     return false;
    }
    @Override
    public Object getChild(Object parent, int index) {
    	if (!(parent instanceof NestedShape)) {
	    	return null;
	    }
    	try {
    		return ((NestedShape) parent).getInnerShapeAt(index);
    	}
    	catch (Exception e) {
    		return null;
    	}
    }
    @Override
    public int getChildCount(Object parent) {
    	if (!(parent instanceof NestedShape)) {
    	    return 0;
    	}
    	return ((NestedShape) parent).getSize();
    }
    @Override
    public int getIndexOfChild(Object parent, Object child) {
    	if (!(parent instanceof NestedShape)) {
    	    return -1;
    	}
    	for (int i = 0; i < ((NestedShape) parent).getSize(); i++) {
    		if (((NestedShape) parent).getInnerShapeAt(i) == child) {
    			return i;
    		}
    	}
    	return -1;
    }
    @Override
    public void addTreeModelListener(final TreeModelListener tml) {
	    treeModelListeners.add(tml);
    }
    @Override
    public void removeTreeModelListener(final TreeModelListener tml) {
    	treeModelListeners.remove(tml);	 
    }
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {}
    public void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
    	TreeModelEvent temp = new TreeModelEvent(source, path, childIndices, children);
        for (int i = 0; i < treeModelListeners.size(); i++) {
        	treeModelListeners.get(i).treeNodesInserted(temp);
        }
    }
    public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children) {
    	TreeModelEvent temp = new TreeModelEvent(source, path, childIndices, children);
        for (int i = 0; i < treeModelListeners.size(); i++) {
        	treeModelListeners.get(i).treeNodesRemoved(temp);
        }
    }
    public void insertNodeInto(Shape newChild, NestedShape parent) {
    	int[] childIndices = {parent.getSize()};
    	Object[] children = {newChild};
    	fireTreeNodesInserted(this, parent.getPath(), childIndices, children);
    }
    public void addShapeNode(NestedShape selectedNode) {
    	if (selectedNode == root) {
    		selectedNode.createInnerShape(0, 0, currentWidth, currentHeight, currentColor, currentPathType, currentShapeType);
    	}
    	else {
    		selectedNode.createInnerShape(0, 0, currentWidth / 2, currentHeight / 2, currentColor, currentPathType, currentShapeType);
    	}
    	insertNodeInto(selectedNode.getInnerShapeAt(selectedNode.getSize() - 1), selectedNode);
    }   
    public void removeNodeFromParent(Shape selectedNode) {
    	Shape parentNode = selectedNode.getParent();
    	int index = ((NestedShape) parentNode).indexOf(selectedNode);
    	int[] childIndices = {index};
    	Object[] children = {selectedNode};
    	((NestedShape) parentNode).removeInnerShape(selectedNode);
    	fireTreeNodesRemoved(this, parentNode.getPath(), childIndices, children);
    }
	// you don't need to make any changes after this line ______________
	public void setCurrentShapeType(ShapeType value) { currentShapeType = value; }
	public void setCurrentPathType(PathType value) { currentPathType = value; }
	public ShapeType getCurrentShapeType() { return currentShapeType; }
	public PathType getCurrentPathType() { return currentPathType; }
	public int getCurrentWidth() { return currentWidth; }
	public int getCurrentHeight() { return currentHeight; }
	public Color getCurrentColor() { return currentColor; }
	public void update(Graphics g){ paint(g); }
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}
	public void run() {
		Thread myThread = Thread.currentThread();
		while(animationThread==myThread) {
			repaint();
			pause(DELAY);
		}
	}
	private void pause(int milliseconds) {
		try {
			Thread.sleep((long)milliseconds);
		} catch(InterruptedException ie) {}
	}
	class Main {
		public static void main(String[] args) { 
			AnimationViewer p  = new AnimationViewer();
		
		}	
	}
}
