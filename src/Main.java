import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] argv) throws Exception {

        JComponent com = new DraggableComponent();

        JFrame f = new JFrame();


        f.add(com);
        f.setSize(300, 300);
        f.setVisible(true);
    }
}

class DraggableComponent extends JComponent implements DragGestureListener, DragSourceListener {
    DragSource dragSource;

    public DraggableComponent() {
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
    }

    public void dragGestureRecognized(DragGestureEvent evt) {
        Transferable t = new StringSelection("aString");
        dragSource.startDrag(evt, DragSource.DefaultCopyDrop, t, this);
    }

    public void dragEnter(DragSourceDragEvent evt) {
        System.out.println("enters");
    }

    public void dragOver(DragSourceDragEvent evt) {

        System.out.println("over");
    }

    public void dragExit(DragSourceEvent evt) {
        System.out.println("leaves");
    }

    public void dropActionChanged(DragSourceDragEvent evt) {
        System.out.println("changes the drag action between copy or move");
    }

    public void dragDropEnd(DragSourceDropEvent evt) {
        System.out.println("finishes or cancels the drag operation");
    }
}