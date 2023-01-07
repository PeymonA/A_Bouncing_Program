/*
 *	===============================================================================
 *	NestedShape.java : A shape that can contain shapes.
 *  YOUR UPI: PARM175
 *	=============================================================================== */
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class NestedShape extends RectangleShape {
	private ArrayList<Shape> innerShapes = new ArrayList<Shape>();
	
	public NestedShape() {
		super();
		createInnerShape(0, 0, width/2, height/2, color, PathType.BOUNCE, ShapeType.RECTANGLE);
	}
	public NestedShape(int x, int y, int w, int h, int mw, int mh, Color c, PathType pt) {
		super(x ,y ,w, h ,mw ,mh, c, pt);
		createInnerShape(0, 0, w/2, h/2, c, PathType.BOUNCE, ShapeType.RECTANGLE);
	}
	public NestedShape(int w, int h) {
		super(0, 0, w, h, DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT, Color.black, DEFAULT_PATHTYPE);
	}
	public Shape createInnerShape(int x, int y, int w, int h, Color c, PathType pt, ShapeType st) {
		if (st.toString().equals("RECTANGLE")) {
			Shape newShape = new RectangleShape(x, y, w, h, width, height, c, pt);	
			newShape.parent = this;
			innerShapes.add(newShape);
			return newShape;
		}
		else if (st.toString().equals("OVAL")) {
			Shape newShape = new OvalShape(x, y, w, h, width, height, c, pt);
			newShape.parent = this;
			innerShapes.add(newShape);
			return newShape;
		}
		else if (st.toString().equals("NESTED")) {
			Shape newShape = new NestedShape(x, y, w, h, width, height, c, pt);	
			newShape.parent = this;
			innerShapes.add(newShape);
			return newShape;
		}
		return null;
	}
	public Shape getInnerShapeAt(int index) {
		return innerShapes.get(index);
	}
	public int getSize() {
		return innerShapes.size();
	}
	public int indexOf(Shape s) {
		for (int i = 0; i < innerShapes.size(); i++) {
			if (innerShapes.get(i) == s) {
				return i;
			}
		}
		return -1;
	}
	public void addInnerShape(Shape s) {
		s.parent = this;
		innerShapes.add(s);
	}
	public void removeInnerShape(Shape s) {
		for (int i = 0; i < innerShapes.size(); i++) {
			if (innerShapes.get(i) == s) {
				innerShapes.get(i).parent = null;
				innerShapes.remove(s);
				break;
			}
		}
	}
	public void removeInnerShapeAt(int index) {
		for (int i = 0; i < innerShapes.size(); i++) {
			if (i == index) {
				innerShapes.get(i).parent = null;
				innerShapes.remove(i);
				break;
			}
		}
	}
	public ArrayList<Shape> getAllInnerShapes() {
		return innerShapes;
	}
	@Override
	public void setColor(Color fc) { 
		color = fc;
		for (int i = 0; i < innerShapes.size(); i++) {
			innerShapes.get(i).color = fc;
		}
	}
	public void draw(Graphics g) {
        g.drawRect(x, y, getWidth(), getHeight());
		g.setColor(Color.black);
	    for (int i = 0; i < innerShapes.size(); i++) {
			g.translate(x, y);
			innerShapes.get(i).draw(g);
            g.translate(-x, -y);		
	    }
	}
	@Override
	public void move() {
		path.move();
		for (int i = 0; i < innerShapes.size(); i++) {
			innerShapes.get(i).path.move();
		}
	}
}