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
/*$Id: LinearEdgeView.java,v 1.6 2001-03-24 22:45:46 ponder Exp $*/

package freemind.view.mindmapview;

import freemind.modes.MindMapEdge;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * This class represents a single Edge of a MindMap.
 */
public class LinearEdgeView extends EdgeView {

    public LinearEdgeView(NodeView source, NodeView target) {
	super(source,target);
	update();
    }

    public void update() {
	super.update();
    }

    public void paint(Graphics2D g) {
	update();
	g.setColor(getColor());
	g.drawLine(start.x,start.y,end.x,end.y);
	super.paint(g);
    }
    
    public Color getColor() {
	return getModel().getColor();
    }
}
