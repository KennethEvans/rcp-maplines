package net.kenevans.maplines.ui;

import net.kenevans.maplines.lines.Line;
import net.kenevans.maplines.lines.Lines;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/*
 * Created on Aug 23, 2010
 * By Kenneth Evans, Jr.
 */

public class EditLinesDialog extends Dialog
{
    // private static final int TEXT_COLS_LARGE = 50;
    // private static final int TEXT_COLS_SMALL = 10;
    private boolean success = false;

    private MapLinesView view;
    private Lines lines;
    private List list;

    /**
     * Constructor.
     * 
     * @param parent
     */
    public EditLinesDialog(Shell parent, MapLinesView view) {
        // We want this to be modeless
        this(parent, SWT.DIALOG_TRIM | SWT.NONE, view);
    }

    /**
     * Constructor.
     * 
     * @param parent The parent of this dialog.
     * @param style Style passed to the parent.
     */
    public EditLinesDialog(Shell parent, int style, MapLinesView view) {
        super(parent, style);
        this.view = view;
        this.lines = view.getLines();
    }

    /**
     * Convenience method to open the dialog.
     * 
     * @return Whether OK was selected or not.
     */
    public boolean open() {
        Shell shell = new Shell(getParent(), getStyle() | SWT.RESIZE);
        shell.setText("Edit Lines");
        // It can take a long time to do this so use a wait cursor
        // Probably not, though
        Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
        if(waitCursor != null) getParent().setCursor(waitCursor);
        createContents(shell);
        getParent().setCursor(null);
        waitCursor.dispose();
        shell.pack();
        shell.open();
        Display display = getParent().getDisplay();
        while(!shell.isDisposed()) {
            if(!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return success;
    }

    /**
     * Creates the contents of the dialog.
     * 
     * @param shell
     */
    private void createContents(final Shell shell) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        shell.setLayout(gridLayout);

        // Create the groups
        createListGroup(shell);
        createActionsGroup(shell);

        // Create the buttons
        // Make a zero margin composite for the OK and Cancel buttons
        Composite composite = new Composite(shell, SWT.NONE);
        // Change END to FILL to center the buttons
        GridDataFactory.fillDefaults().align(SWT.END, SWT.FILL)
            .grab(true, false).applyTo(composite);
        gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 3;
        composite.setLayout(gridLayout);

        Button button;

        button = new Button(composite, SWT.PUSH);
        button.setText("OK");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL)
            .grab(true, true).applyTo(button);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                success = true;
                shell.close();
                if(lines == null) {
                    return;
                }
                for(Line line : lines.getLines()) {
                    line.setSelected(false);
                }
                view.getViewer().getCanvas().redraw();
            }
        });
        shell.setDefaultButton(button);
    }

    /**
     * Creates the List group.
     * 
     * @param shell
     */
    private void createListGroup(final Shell shell) {
        Group box = new Group(shell, SWT.BORDER);
        box.setText("Lines");
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        box.setLayout(gridLayout);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(box);

        // Make a zero margin composite
        Composite composite = new Composite(box, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
            .grab(true, false).applyTo(composite);
        gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 1;
        composite.setLayout(gridLayout);

        list = new List(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(list);
        list.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                int[] selections = list.getSelectionIndices();
                for(Line line : lines.getLines()) {
                    line.setSelected(false);
                }
                Line line;
                for(int i = 0; i < selections.length; i++) {
                    line = lines.getLines().get(selections[i]);
                    line.setSelected(true);
                }
                view.getViewer().getCanvas().redraw();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                int[] selections = list.getSelectionIndices();
                for(Line line : lines.getLines()) {
                    line.setSelected(false);
                }
                Line line;
                for(int i = 0; i < selections.length; i++) {
                    line = lines.getLines().get(selections[i]);
                    line.setSelected(true);
                }
                view.getViewer().getCanvas().redraw();
            }
        });

        resetList();
    }

    /**
     * Creates the actions group.
     * 
     * @param shell
     */
    private void createActionsGroup(final Shell shell) {
        Group box = new Group(shell, SWT.BORDER);
        box.setText("Actions");
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        box.setLayout(gridLayout);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(box);

        // Make a zero margin composite
        Composite composite = new Composite(box, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
            .grab(true, false).applyTo(composite);
        gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 5; // Equal to number of buttons
        composite.setLayout(gridLayout);

        Button button;
        button = new Button(composite, SWT.PUSH);
        button.setText("Delete");
        button.setToolTipText("Delete selected lines.");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL)
            .grab(true, true).applyTo(button);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if(list == null || list.getSelectionCount() == 0) {
                    return;
                }
                if(lines == null) {
                    return;
                }
                int[] indices = list.getSelectionIndices();
                for(int i : indices) {
                    lines.removeLine(i);
                }
                resetList();
                view.getViewer().getCanvas().redraw();
            }
        });

        button = new Button(composite, SWT.PUSH);
        button.setText("Rename");
        button.setToolTipText("Rename selected lines.");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL)
            .grab(true, true).applyTo(button);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if(list == null || list.getSelectionCount() == 0) {
                    return;
                }
                if(lines == null) {
                    return;
                }
                int[] indices = list.getSelectionIndices();
                String desc;
                Line line;
                for(int i : indices) {
                    line = lines.getLines().get(i);
                    desc = line.getDesc();
                    InputDialog dlg = new InputDialog(null, "Description",
                        "Enter a description:", desc, null);
                    dlg.setBlockOnOpen(true);
                    int res = dlg.open();
                    if(res == org.eclipse.jface.dialogs.Dialog.OK) {
                        String val = dlg.getValue();
                        if(val != null) {
                            line.setDesc(val);
                        }
                    }
                }
                resetList();
                // Reset what was selected
                list.setSelection(indices);
                for(int i : indices) {
                    line = lines.getLines().get(i);
                    desc = line.getDesc();
                    line.setSelected(true);
                }
                view.getViewer().getCanvas().redraw();
            }
        });

        button = new Button(composite, SWT.PUSH);
        button.setText("Color");
        button.setToolTipText("Set the color for the selected lines.");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL)
            .grab(true, true).applyTo(button);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if(list == null || list.getSelectionCount() == 0) {
                    return;
                }
                if(lines == null) {
                    return;
                }

                ListDialog dialog = new ListDialog(shell);
                dialog.setTitle("Color");
                dialog.setMessage("Set the line color");
                dialog.setContentProvider(new ArrayContentProvider());
                dialog.setLabelProvider(new LabelProvider());
                // This will be overwritten by the buttons if it is too small
                dialog.setWidthInChars(12);

                // Define an array of color values and a coordinated list of
                // color names
                int colors[] = {SWT.COLOR_RED, SWT.COLOR_GREEN, SWT.COLOR_BLUE,
                    SWT.COLOR_MAGENTA, SWT.COLOR_CYAN, SWT.COLOR_YELLOW,
                    SWT.COLOR_GRAY, SWT.COLOR_BLACK, SWT.COLOR_WHITE,
                    SWT.COLOR_DARK_RED, SWT.COLOR_DARK_GREEN,
                    SWT.COLOR_DARK_BLUE, SWT.COLOR_DARK_MAGENTA,
                    SWT.COLOR_DARK_CYAN, SWT.COLOR_DARK_YELLOW,
                    SWT.COLOR_DARK_GRAY,};
                String colorNames[] = {"Red", "Green", "Blue", "Magenta",
                    "Cyan", "Yellow", "Gray", "Black", "White", "Dark Red",
                    "Dark Green", "Dark Blue", "Dark Magenta", "Dark Cyan",
                    "Dark Yellow", "Dark Gray",};
                dialog.setInput(colorNames);
                if(dialog.open() == Window.OK) {
                    Object[] result = dialog.getResult();
                    // Use the first value
                    if(result.length > 0) {
                        String colorName = (String)result[0];
                        Line line;
                        for(int i = 0; i < colorNames.length; i++) {
                            // Look for the matching color value
                            if(colorName.equals(colorNames[i])) {
                                // Change all the selected lines
                                int[] indices = list.getSelectionIndices();
                                for(int j : indices) {
                                    line = lines.getLines().get(j);
                                    line.setColor(colors[i]);
                                }
                            }
                        }
                        view.getViewer().getCanvas().redraw();
                    }
                }
            }
        });

        button = new Button(composite, SWT.PUSH);
        button.setText("Refresh");
        button.setToolTipText("Refresh the list.");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL)
            .grab(true, true).applyTo(button);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                resetList();
            }
        });

        button = new Button(composite, SWT.PUSH);
        button.setText("Deselect All");
        button.setToolTipText("Deselect all lines.");
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL)
            .grab(true, true).applyTo(button);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if(list == null || list.getSelectionCount() == 0) {
                    return;
                }
                list.deselectAll();
                if(lines == null) {
                    return;
                }
                for(Line line : lines.getLines()) {
                    line.setSelected(false);
                }
                view.getViewer().getCanvas().redraw();
            }
        });

    }

    private void resetList() {
        if(list == null) {
            return;
        }
        list.deselectAll();
        list.removeAll();
        if(lines == null) {
            return;
        }
        for(Line line : lines.getLines()) {
            list.add(line.getDesc());
            line.setSelected(false);
        }
        view.getViewer().getCanvas().redraw();
    }

}