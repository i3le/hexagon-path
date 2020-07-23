package org.tool.hexagon;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

/**
 * 六边形节点
 */
public class Hexagon {

    public int x;
    public int y;
    public int speed;
    public boolean isBlock;

    public int F;
    public int G;
    public int H;
    public Hexagon parent;

    public GeneralPath gp;

    public Hexagon(int x, int y) {
        this.x = x;
        this.y = y;
        isBlock = false;
    }

    public void clear() {
        F = 0;
        G = 0;
        H = 0;
        parent = null;
    }

    Hexagon findUp() {
        return new Hexagon(x, y - 1);
    }

    Hexagon findDown() {
        return new Hexagon(x, y + 1);
    }

    Hexagon findUpL() {
        if (x % 2 == 0) {
            return new Hexagon(x - 1, y - 1);
        } else {
            return new Hexagon(x - 1, y);
        }
    }

    Hexagon findUpR() {
        if (x % 2 == 0) {
            return new Hexagon(x + 1, y - 1);
        } else {
            return new Hexagon(x + 1, y);
        }
    }

    Hexagon findDownL() {
        if (x % 2 == 0) {
            return new Hexagon(x - 1, y);
        } else {
            return new Hexagon(x - 1, y + 1);
        }
    }

    Hexagon findDownR() {
        if (x % 2 == 0) {
            return new Hexagon(x + 1, y);
        } else {
            return new Hexagon(x + 1, y + 1);
        }
    }

    public List<Hexagon> findNear() {
        ArrayList<Hexagon> res = new ArrayList<>();
        res.add(findUp());
        res.add(findUpR());
        res.add(findDownR());
        res.add(findDown());
        res.add(findDownL());
        res.add(findUpL());
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        Hexagon inC = (Hexagon) obj;
        if (inC.x == x && inC.y == y) {
            return true;
        }
        return false;
    }

    public void addToPath(List<Hexagon> pathC) {
        pathC.add(this);
        if (parent != null) {
            parent.addToPath(pathC);
        }
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}