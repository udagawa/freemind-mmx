/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/*$Id: MindMapController.java,v 1.14 2001-03-24 22:45:46 ponder Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import java.io.File;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JColorChooser;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

public class MindMapController extends ControllerAdapter {

    Mode mode;
    private JPopupMenu popupmenu;
    private JToolBar toolbar;

    Action newMap = new NewMapAction(this);
    Action open = new OpenAction(this);
    Action save = new SaveAction(this);
    Action saveAs = new SaveAsAction(this);

    Action edit = new EditAction();
    Action addNew = new AddNewAction();
    Action remove = new RemoveAction();
    Action toggleFolded = new ToggleFoldedAction();
    Action toggleChildrenFolded = new toggleChildrenFoldedAction();
    Action setLinkByFileChooser = new SetLinkByFileChooserAction();
    Action setLinkByTextField = new SetLinkByTextFieldAction();
    Action followLink = new FollowLinkAction();
    Action exportBranch = new ExportBranchAction();
    Action importBranch = new ImportBranchAction();
    Action importLinkedBranch = new ImportLinkedBranchAction();
    Action importLinkedBranchWithoutRoot = new ImportLinkedBranchWithoutRootAction();
    Action nodeUp = new NodeUpAction();
    Action nodeDown = new NodeDownAction();

    Action fork = new ForkAction();
    Action bubble = new BubbleAction();
    Action nodeColor = new NodeColorAction();
    Action edgeColor = new EdgeColorAction();
    Action linear = new LinearAction();
    Action bezier = new BezierAction();
    Action italic = new ItalicAction(this);
    Action bold = new BoldAction(this);
    //    Action underline = new UnderlineAction(this);
    Action normalFont = new NormalFontAction(this);

    Action increaseNodeFont = new IncreaseNodeFontAction();
    Action decreaseNodeFont = new DecreaseNodeFontAction();

    Action increaseBranchFont = new IncreaseBranchFontAction();
    Action decreaseBranchFont = new DecreaseBranchFontAction();

    // Extension Actions
    Action evalPositive = new EvaluatePositiveAction();
    Action evalNegative = new EvaluateNegativeAction();
    Action evalNeutral = new EvaluateNeutralAction();

    Action evalBranchPositive = new EvaluateBranchPositiveAction();
    Action evalBranchNegative = new EvaluateBranchNegativeAction();
    Action evalBranchNeutral = new EvaluateBranchNeutralAction();

    // Branch Font Actions
    Action boldifyBranch = new BoldifyBranchAction();
    Action nonBoldifyBranch = new NonBoldifyBranchAction();
    Action toggleBoldBranch = new ToggleBoldBranchAction();

    Action italiciseBranch = new ItaliciseBranchAction();
    Action nonItaliciseBranch = new NonItaliciseBranchAction();
    Action toggleItalicBranch = new ToggleItalicBranchAction();


    FileFilter filefilter = new MindMapFilter();

    public MindMapController(Mode mode) {
	super(mode);
	popupmenu = new MindMapPopupMenu(this);
	toolbar = new MindMapToolBar(this);
	setAllActions(false);
    }

    public MapAdapter newModel() {
	return new MindMapMapModel(getFrame());
    }

    public void save(File file) {
	getModel().save(file);
    }

    public FileFilter getFileFilter() {
	return filefilter;
    }

    public void doubleClick() {
	if (getFrame().getProperty("mindmap_doubleclick").equals("toggle_folded")) {
	    toggleFolded();
	} else {
	    loadURL();
	}
    }

    //Node editing
    void setFontSize(int fontSize) {
	getModel().setFontSize(getSelected(),fontSize);
    }

    void setFont(String font) {
	getModel().setFont(getSelected(),font);
    }

    protected MindMapNode newNode() {
	return new MindMapNodeModel(getFrame().getResources().getString("new_node"),getFrame());
    }

    //get/set methods

    JMenu getEditMenu() {
	JMenu editMenu = new JMenu();
	editMenu.add(getNodeMenu());
	editMenu.add(getBranchMenu());
	editMenu.add(getEdgeMenu());
	editMenu.add(getExtensionMenu());
	JMenuItem cutItem = editMenu.add(cut);
 	cutItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_cut")));
	JMenuItem pasteItem = editMenu.add(paste);
 	pasteItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_paste")));
	return editMenu;
    }

    JMenu getFileMenu() {
	JMenu fileMenu = new JMenu();
	JMenuItem newMapItem = fileMenu.add(newMap);
 	newMapItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_newMap")));
	JMenuItem openItem = fileMenu.add(open);
 	openItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_open")));
	JMenuItem saveItem = fileMenu.add(save);
 	saveItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_save")));
	JMenuItem saveAsItem = fileMenu.add(saveAs);
 	saveAsItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_saveAs")));
	return fileMenu;
    }

    JMenu getExtensionMenu() {
	JMenu extensionMenu = new JMenu(getFrame().getResources().getString("extension_menu"));

	JMenuItem evalNeutralItem = extensionMenu.add(evalNeutral);
	evalNeutralItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_neutral")));
	JMenuItem evalPositiveItem = extensionMenu.add(evalPositive);
	evalPositiveItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_positive")));
	JMenuItem evalNegativeItem = extensionMenu.add(evalNegative);
	evalNegativeItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_negative")));

	extensionMenu.addSeparator();

	JMenuItem evalBranchNeutralItem = extensionMenu.add(evalBranchNeutral);
	evalBranchNeutralItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_branch_neutral")));
	JMenuItem evalBranchPositiveItem = extensionMenu.add(evalBranchPositive);
	evalBranchPositiveItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_branch_positive")));
	JMenuItem evalBranchNegativeItem = extensionMenu.add(evalBranchNegative);
	evalBranchNegativeItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_branch_negative")));

	return extensionMenu;
    }

    JMenu getBranchMenu() {
	JMenu branchMenu = new JMenu(getFrame().getResources().getString("branch"));

	JMenuItem exportBranchItem = branchMenu.add(exportBranch);
	exportBranchItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_export_branch")));
	JMenu importMenu = new JMenu(getFrame().getResources().getString("import"));
	branchMenu.add(importMenu);

	JMenuItem importBranchItem = importMenu.add(importBranch);
	// 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_follow_link")));
	JMenuItem importLinkedBranchItem = importMenu.add(importLinkedBranch);
	// 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_follow_link")));
	JMenuItem importLinkedBranchWithoutRootItem = importMenu.add(importLinkedBranchWithoutRoot);

	branchMenu.addSeparator();

	JMenuItem boldifyBranchItem = branchMenu.add(boldifyBranch);
	JMenuItem nonBoldifyBranchItem = branchMenu.add(nonBoldifyBranch);
	JMenuItem toggleBoldBranchItem = branchMenu.add(toggleBoldBranch);

	branchMenu.addSeparator();

	JMenuItem italiciseBranchItem = branchMenu.add(italiciseBranch);
	JMenuItem nonItaliciseBranchItem = branchMenu.add(nonItaliciseBranch);
	JMenuItem toggleItalicBranchItem = branchMenu.add(toggleItalicBranch);

	branchMenu.addSeparator();

	JMenuItem increaseBranchFontItem = branchMenu.add(increaseBranchFont);
  	increaseBranchFontItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_branch_increase_font_size")));

	JMenuItem decreaseBranchFontItem = branchMenu.add(decreaseBranchFont);
	decreaseBranchFontItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_branch_decrease_font_size")));

	return branchMenu;
    }


    JMenu getNodeMenu() {
	JMenu nodeMenu = new JMenu(getFrame().getResources().getString("node"));
	JMenuItem editItem = nodeMenu.add(edit);
 	editItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_edit")));
 	JMenuItem addNewItem = nodeMenu.add(addNew);
 	addNewItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_add")));
 	JMenuItem removeItem = nodeMenu.add(remove);
 	removeItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_remove")));

 	JMenuItem nodeUpItem = nodeMenu.add(nodeUp);
 	nodeUpItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_up")));
 	JMenuItem nodeDownItem = nodeMenu.add(nodeDown);
 	nodeDownItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_down")));

	nodeMenu.addSeparator();

	JMenuItem followLinkItem = nodeMenu.add(followLink);
 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_follow_link")));
	JMenuItem setLinkByFileChooserItem = nodeMenu.add(setLinkByFileChooser);
 	setLinkByFileChooserItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_set_link_by_filechooser")));
	JMenuItem setLinkByTextFieldItem = nodeMenu.add(setLinkByTextField);
 	setLinkByTextFieldItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_set_link_by_textfield")));

	nodeMenu.addSeparator();

	JMenuItem toggleFoldedItem = nodeMenu.add(toggleFolded);
 	toggleFoldedItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_toggle_folded")));
	JMenuItem toggleChildrenFoldedItem = nodeMenu.add(toggleChildrenFolded);
	toggleChildrenFoldedItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_toggle_children_folded")));

	nodeMenu.addSeparator();

	JMenu nodeStyle = new JMenu(getFrame().getResources().getString("style"));
	nodeMenu.add(nodeStyle);
	nodeStyle.add(fork);
	nodeStyle.add(bubble);
	JMenu nodeFont = new JMenu(getFrame().getResources().getString("font"));
	JMenuItem increaseNodeFontItem = nodeFont.add(increaseNodeFont);
  	increaseNodeFontItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_increase_font_size")));

	JMenuItem decreaseNodeFontItem = nodeFont.add(decreaseNodeFont);
	decreaseNodeFontItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_decrease_font_size")));

	nodeFont.addSeparator();

	nodeFont.add(italic);
	nodeFont.add(bold);
	nodeMenu.add(nodeFont);
	//	nodeFont.add(underline);
	nodeMenu.add(nodeColor);

	return nodeMenu;
    }

    JMenu getEdgeMenu() {
	JMenu edgeMenu = new JMenu(getFrame().getResources().getString("edge"));
	JMenu edgeStyle = new JMenu(getFrame().getResources().getString("style"));
	edgeMenu.add(edgeStyle);
	edgeStyle.add(linear);
	edgeStyle.add(bezier);
	edgeMenu.add(edgeColor);
	return edgeMenu;
    }


    JPopupMenu getPopupMenu() {
	return popupmenu;
    }

    //convenience methods
    private MindMapMapModel getModel() {
	return (MindMapMapModel)getController().getModel();
    }

    private MindMapNodeModel getSelected() {
	return (MindMapNodeModel)getView().getSelected().getModel();
    }

    MindMapToolBar getToolBar() {
	return (MindMapToolBar)toolbar;
    }

    /**
     * Enabled/Disabled all actions that are dependent on
     * whether there is a map open or not.
     */
    protected void setAllActions(boolean enabled) {
	edit.setEnabled(enabled);
	addNew.setEnabled(enabled);
	remove.setEnabled(enabled);
	toggleFolded.setEnabled(enabled);
	toggleChildrenFolded.setEnabled(enabled);
	setLinkByTextField.setEnabled(enabled);
	setLinkByFileChooser.setEnabled(enabled);
	followLink.setEnabled(enabled);
	italic.setEnabled(enabled);
	bold.setEnabled(enabled);
	normalFont.setEnabled(enabled);
	nodeColor.setEnabled(enabled);
	edgeColor.setEnabled(enabled);
	fork.setEnabled(enabled);
	bubble.setEnabled(enabled);
	linear.setEnabled(enabled);
	bezier.setEnabled(enabled);
	save.setEnabled(enabled);
	saveAs.setEnabled(enabled);
	getToolBar().setAllActions(enabled);
	exportBranch.setEnabled(enabled);
	importBranch.setEnabled(enabled);
	importLinkedBranch.setEnabled(enabled);
	importLinkedBranchWithoutRoot.setEnabled(enabled);
    }

    //////////
    // Actions
    /////////

    private class ExportBranchAction extends AbstractAction {
	ExportBranchAction() {
	    super(getFrame().getResources().getString("export_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel node = (MindMapNodeModel)getSelected();

	    //if something is wrong, abort.
	    if(getMap() == null || node == null || node.isRoot()) {
		getFrame().err("Could not export branch.");
		return;
	    }
	    //If the current map is not saved yet, save it first.
	    if (getMap().getFile() == null) {
		getFrame().out("You must save the current map first!");
		save();
	    }

	    //Open FileChooser to choose in which file the exported
	    //branch should be stored
	    JFileChooser chooser;
	    if (getMap().getFile().getParentFile() != null) {
		chooser = new JFileChooser(getMap().getFile().getParentFile());
	    } else {
		chooser = new JFileChooser();
	    }
	    //chooser.setLocale(currentLocale);
	    if (getFileFilter() != null) {
		chooser.addChoosableFileFilter(getFileFilter());
	    }
	    int returnVal = chooser.showSaveDialog(getView());
	    if (returnVal==JFileChooser.APPROVE_OPTION) {
		File f = chooser.getSelectedFile();
		URL link;
		//Force the extension to be .mm
		String ext = Tools.getExtension(f.getName());
		if(!ext.equals("mm")) {
		    f = new File(f.getParent(),f.getName()+".mm");
		}
		try {
		    link = f.toURL();
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getView(),"couldn't create valid URL!");
		    return;
		}

		//Now make a copy from the node, remove the node from the map and create a new
		//Map with the node as root, store the new Map, add the copy of the node to the parent,
		//and set a link from the copy to the new Map.

		MindMapNodeModel parent = (MindMapNodeModel)node.getParent();
		MindMapNodeModel newNode = new MindMapNodeModel(node.toString(),getFrame());
		getModel().removeNodeFromParent(node);
		node.setParent(null);
		MindMapMapModel map = new MindMapMapModel(node,getFrame());
		if (getModel().getFile() != null) {
		    try{
			//set a link from the new root to the old map
			map.setLink(node, Tools.toRelativeURL(f.toURL(), getModel().getFile().toURL()));
		    } catch(MalformedURLException ex) {
		    }
		}
		//		getController().newMapModule(map);
		map.save(f);

		getModel().insertNodeInto(newNode,parent, 0);
		try {
		    String linkString = Tools.toRelativeURL(getModel().getFile().toURL(), f.toURL());
		    getModel().setLink(newNode,linkString);
		} catch (MalformedURLException ex) {
		}
		getModel().save(getModel().getFile());
	    }
	}
    }

    private class ImportBranchAction extends AbstractAction {
	ImportBranchAction() {
	    super(getFrame().getResources().getString("import_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if(parent != null) {
		JFileChooser chooser = new JFileChooser();
		//chooser.setLocale(currentLocale);
		if (getFileFilter() != null) {
		    chooser.addChoosableFileFilter(getFileFilter());
		}
		int returnVal = chooser.showOpenDialog(getView());
		if (returnVal==JFileChooser.APPROVE_OPTION) {
		    MindMapNodeModel node = getModel().loadTree(chooser.getSelectedFile());
		    getModel().paste(node, parent);
		}
	    }
	}
    }

    private class ImportLinkedBranchAction extends AbstractAction {
	ImportLinkedBranchAction() {
	    super(getFrame().getResources().getString("import_linked_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if((parent != null)&&(parent.getLink() != null)) {
		URL absolute = null;
		try {
		    absolute = new URL(getMap().getFile().toURL(), parent.getLink());
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getView(),"couldn't create valid URL!");
		    return;
		}
		MindMapNodeModel node = getModel().loadTree(new File(absolute.getFile()));
		getModel().paste(node, parent);
	    }
	}
    }


    /**
     * This is exactly the opposite of exportBranch.
     */
    private class ImportLinkedBranchWithoutRootAction extends AbstractAction {
	ImportLinkedBranchWithoutRootAction() {
	    super(getFrame().getResources().getString("import_linked_branch_without_root"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if((parent != null)&&(parent.getLink() != null)) {
		URL absolute = null;
		try {
		    absolute = new URL(getMap().getFile().toURL(), parent.getLink());
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getView(),"couldn't create valid URL!");
		    return;
		}
		MindMapNodeModel node = getModel().loadTree(new File(absolute.getFile()));
		for (Enumeration enum=node.children();enum.hasMoreElements();) {
		    getModel().paste((MindMapNodeModel)enum.nextElement(), parent);
		}
		getModel().setLink(parent, null);
	    }
	}
    }

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() {
	    super(getFrame().getResources().getString("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }

    private class ForkAction extends AbstractAction {
	ForkAction() {
	    super(getFrame().getResources().getString("fork"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNodeStyle(getSelected(), "fork");
	}
    }

    private class BubbleAction extends AbstractAction {
	BubbleAction() {
	    super(getFrame().getResources().getString("bubble"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNodeStyle(getSelected(), "bubble");
	}
    }

    private class LinearAction extends AbstractAction {
	LinearAction() {
	    super(getFrame().getResources().getString("linear"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setEdgeStyle(getSelected(), "linear");
	}
    }

    private class BezierAction extends AbstractAction {
	BezierAction() {
	    super(getFrame().getResources().getString("bezier"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setEdgeStyle(getSelected(), "bezier");
	}
    }

    //
    // Fonts
    //
    private class ItalicAction extends AbstractAction {
	ItalicAction(Object controller) {
	    super(getFrame().getResources().getString("italic"), new ImageIcon(getResource("images/Italic24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setItalic(getSelected());
	}
    }

    private class BoldAction extends AbstractAction {
	BoldAction(Object controller) {
	    super(getFrame().getResources().getString("bold"), new ImageIcon(getResource("images/Bold24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setBold(getSelected());
	}
    }

    private class NormalFontAction extends AbstractAction {
	NormalFontAction(Object controller) {
	    super(getFrame().getResources().getString("normal"), new ImageIcon(getResource("images/Normal24.gif")));	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNormalFont(getSelected());
	}
    }

    /**Not yet implemented*/
    private class UnderlineAction extends AbstractAction {
	UnderlineAction(Object controller) {
	    super(getFrame().getResources().getString("underline"), new ImageIcon(getResource("images/Underline24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setUnderlined(getSelected());
	}
    }

    //
    // Color
    //

    private class NodeColorAction extends AbstractAction {
	NodeColorAction() {
	    super(getFrame().getResources().getString("node_color"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = JColorChooser.showDialog(getView(),"Choose Node Color:",getSelected().getColor() );
	    getModel().setNodeColor(getSelected(), color);
	}
    }

    private class EdgeColorAction extends AbstractAction {
	EdgeColorAction() {
	    super(getFrame().getResources().getString("edge_color"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel node = getSelected();
	    Color color = JColorChooser.showDialog(getView(),"Choose Edge Color:",getSelected().getEdge().getColor());
	    getModel().setEdgeColor(node,color);
	}
    }

    private class IncreaseNodeFontAction extends AbstractAction {
	IncreaseNodeFontAction() {
	    super(getFrame().getResources().getString("increase_node_font_size"));
	}

	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel n = getSelected();
	    // we assume you have true type, so +1 works
	    getModel().setFontSize(n,n.getFont().getSize()+1);

	}
    }

    private class DecreaseNodeFontAction extends AbstractAction {
	DecreaseNodeFontAction() {
	    super(getFrame().getResources().getString("decrease_node_font_size"));
	}

	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel n = getSelected();
	    // we assume you have true type, so -1 works
	    // getModel().setFontSize(n,n.getFont().getSize()-1);

	    getModel().increaseBranchFontSize(n,-1);
	}
    }

    private class IncreaseBranchFontAction extends AbstractAction {
	IncreaseBranchFontAction() {
	    super(getFrame().getResources().getString("increase_branch_font_size"));
	}

	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel n = getSelected();
	    // we assume you have true type, so +1 works
	    // getModel().setBranchFontSize(n,n.getFont().getSize()+1);

	    getModel().increaseBranchFontSize(n,1);
	}
    }

    private class DecreaseBranchFontAction extends AbstractAction {
	DecreaseBranchFontAction() {
	    super(getFrame().getResources().getString("decrease_branch_font_size"));
	}

	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel n = getSelected();
	    // we assume you have true type, so -1 works
	    getModel().setBranchFontSize(n,n.getFont().getSize()-1);
	}
    }


    //
    // Evaluation
    //

    private class EvaluatePositiveAction extends AbstractAction {
	EvaluatePositiveAction() {
	    super(getFrame().getResources().getString("evaluate_positive"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = Color.green;

	    String strcolor = getFrame().getProperty("positive_node_color");

	    if (strcolor.length() == 7) {
		color=Tools.xmlToColor(strcolor);
	    }

	    getModel().setNodeColor(getSelected(), color);

	    Font f = new Font(getFrame().getProperty("positive_node_font"),
			      Integer.parseInt(getFrame().getProperty("positive_node_font_style")),
			      Integer.parseInt(getFrame().getProperty("positive_node_font_size")));
	    getModel().setNodeFont(getSelected(), f);
	}
    }

    private class EvaluateNegativeAction extends AbstractAction {
	EvaluateNegativeAction() {
	    super(getFrame().getResources().getString("evaluate_negative"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = Color.green;

	    String strcolor = getFrame().getProperty("negative_node_color");

	    if (strcolor.length() == 7) {
		color=Tools.xmlToColor(strcolor);
	    }

	    getModel().setNodeColor(getSelected(), color);

	    Font f = new Font(getFrame().getProperty("negative_node_font"),
			      Integer.parseInt(getFrame().getProperty("negative_node_font_style")),
			      Integer.parseInt(getFrame().getProperty("negative_node_font_size")));
	    getModel().setNodeFont(getSelected(), f);
	}
    }

    private class EvaluateNeutralAction extends AbstractAction {
	EvaluateNeutralAction() {
	    super(getFrame().getResources().getString("evaluate_neutral"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = Color.green;

	    String strcolor = getFrame().getProperty("standardnodecolor");

	    if (strcolor.length() == 7) {
		color=Tools.xmlToColor(strcolor);
	    }

	    getModel().setNodeColor(getSelected(), color);

	    Font f = new Font(getFrame().getProperty("standardfont"),
			      Integer.parseInt("0"), // FIXME: should be changed in the implementation
			      Integer.parseInt(getFrame().getProperty("standardfontsize")));
	    getModel().setNodeFont(getSelected(), f);
	}
    }

    private class EvaluateBranchPositiveAction extends AbstractAction {
	EvaluateBranchPositiveAction() {
	    super(getFrame().getResources().getString("evaluate_branch_positive"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = Color.green;

	    String strcolor = getFrame().getProperty("positive_node_color");

	    if (strcolor.length() == 7) {
		color=Tools.xmlToColor(strcolor);
	    }

	    getModel().setBranchColor(getSelected(), color);

	    Font f = new Font(getFrame().getProperty("positive_node_font"),
			      Integer.parseInt(getFrame().getProperty("positive_node_font_style").trim()),
			      Integer.parseInt(getFrame().getProperty("positive_node_font_size").trim()));
	    getModel().setBranchFont(getSelected(), f);
	}
    }

    private class EvaluateBranchNegativeAction extends AbstractAction {
	EvaluateBranchNegativeAction() {
	    super(getFrame().getResources().getString("evaluate_branch_negative"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = Color.green;

	    String strcolor = getFrame().getProperty("negative_node_color");

	    if (strcolor.length() == 7) {
		color=Tools.xmlToColor(strcolor);
	    }

	    getModel().setBranchColor(getSelected(), color);

	    Font f = new Font(getFrame().getProperty("negative_node_font"),
			      Integer.parseInt(getFrame().getProperty("negative_node_font_style")),
			      Integer.parseInt(getFrame().getProperty("negative_node_font_size")));
	    getModel().setBranchFont(getSelected(), f);
	}
    }

    private class EvaluateBranchNeutralAction extends AbstractAction {
	EvaluateBranchNeutralAction() {
	    super(getFrame().getResources().getString("evaluate_branch_neutral"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = Color.green;

	    String strcolor = getFrame().getProperty("standardnodecolor");

	    if (strcolor.length() == 7) {
		color=Tools.xmlToColor(strcolor);
	    }

	    getModel().setBranchColor(getSelected(), color);

	    Font f = new Font(getFrame().getProperty("standardfont"),
			      // FIXME: please use only java.awt.Font
			      0,
			      Integer.parseInt(getFrame().getProperty("standardfontsize")));

	    //  			    Integer.parseInt(getFrame().getProperty("0")),
	    //  			    Integer.parseInt(getFrame().getProperty("standardfontsize")));
	    getModel().setBranchFont(getSelected(), f);
	}
    }

    //
    // Branch Format Actions
    //

    private class BoldifyBranchAction extends AbstractAction {
	BoldifyBranchAction() {
	    super(getFrame().getResources().getString("boldify_branch"),
		  new ImageIcon(getResource("images/Bold24.gif")));
	}

	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchBold(getSelected());
	}
    }

    private class NonBoldifyBranchAction extends AbstractAction {
	NonBoldifyBranchAction() {
	    super(getFrame().getResources().getString("nonboldify_branch"));
	}

	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchNonBold(getSelected());
	}
    }

    private class ToggleBoldBranchAction extends AbstractAction {
	ToggleBoldBranchAction() {
	    super(getFrame().getResources().getString("toggle_bold_branch"));
	}

	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchToggleBold(getSelected());
	}
    }


    private class ItaliciseBranchAction extends AbstractAction {
	ItaliciseBranchAction() {
	    super(getFrame().getResources().getString("italicise_branch"),
		  new ImageIcon(getResource("images/Italic24.gif")));
	}

	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchItalic(getSelected());
	}
    }

    private class NonItaliciseBranchAction extends AbstractAction {
	NonItaliciseBranchAction() {
	    super(getFrame().getResources().getString("nonitalicise_branch"));
	}

	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchNonItalic(getSelected());
	}
    }

    private class ToggleItalicBranchAction extends AbstractAction {
	ToggleItalicBranchAction() {
	    super(getFrame().getResources().getString("toggle_italic_branch"));
	}

	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchToggleItalic(getSelected());
	}
    }


    //
    // Other classes
    //
    private class MindMapFilter extends FileFilter {
	public boolean accept(File f) {
	    if (f.isDirectory()) return true;
	    String extension = Tools.getExtension(f.getName());
	    if (extension != null) {
		if (extension.equals("mm")) {
		    return true;
		} else {
		    return false;
		}
	    }
	    return false;
	}
	
	public String getDescription() {
	    return getFrame().getResources().getString("mindmaps");
	}
    }
}
