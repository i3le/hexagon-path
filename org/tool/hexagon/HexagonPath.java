package org.tool.hexagon;

import java.util.LinkedList;
import java.util.List;

/**
 * 六边形蜂窝寻路算法
 */
public class HexagonPath {

    public LinkedList<Hexagon> openList;
    public LinkedList<Hexagon> closeList;

    private Hexagon[][] map;
    private LinkedList<Hexagon> path;

	public int depth = 5000;    // 算法深度

    public HexagonPath(Hexagon[][] map) {
        this.map = map;
        openList = new LinkedList<>();
        closeList = new LinkedList<>();
        path = new LinkedList<>();
    }

    public Hexagon find(Hexagon in) {
        if(in == null) {
            return null;
        }
        return find(in.x, in.y);
    }

    public Hexagon find(int mx, int my) {
        if(mx < 0 || my < 0 || mx >= map.length || my >= map[0].length) {
            return null;
        }
        return map[mx][my];
    }

    public List<Hexagon> findPath(Hexagon begin, Hexagon end) {
        Hexagon src = find(begin);
        Hexagon dest = find(end);
        if (src == null || dest == null) {
            System.out.println("起点或者终点有误" + begin + " - " + end);
            return null;
        }
        if (src == dest) {
            System.out.println("起点和终点一致");
            return null;
        }
        boolean srcBlock = src.isBlock;
        boolean destBlock = dest.isBlock;
        if (src.isBlock || dest.isBlock) {
            // 强制通行
            src.isBlock = false;
            dest.isBlock = false;
//            return null;
        }
        openList.clear();
        closeList.clear();
        path.clear();
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                map[x][y].clear();
            }
        }

        boolean isFind = makeOne(src, dest);
        long count = 0;
        while (isFind == false && openList.size() > 0 && count < depth) {
            count++;
            Hexagon minNode = getMinFNode();
            if (minNode != null) {
                isFind = makeOne(minNode, dest);
            }
        }
        src.isBlock = srcBlock;
        dest.isBlock = destBlock;
        System.out.println("累计搜索了:" + count);
        return path;
    }


    private boolean makeOne(Hexagon curr, Hexagon dest) {
        closeList.add(curr);
        openList.remove(curr);
        List<Hexagon> nears = curr.findNear();
        for (Hexagon p2 : nears) {
            Hexagon temp = find(p2);
            if (temp != null && temp.isBlock == false && !closeList.contains(temp)) {
                int tempG = curr.G + temp.speed;
                if (tempG < temp.G || temp.parent == null) {
                    temp.parent = curr;
                    temp.G = tempG;
                    temp.H = getHValue(temp, dest);
                    temp.F = temp.G + temp.H;
                }
                if (!openList.contains(temp)) {
                    openList.add(temp);
                }
                if (temp.equals(dest)) {
                    //找到目的地
                    temp.addToPath(path);
                    return true;
                }
            }
        }
        return false;
    }

    private Hexagon getMinFNode() {
        Hexagon min = openList.peek();
        if(min == null) return null;
        for (Hexagon p2 : openList) {
            if (p2.F < min.F) {
                min = p2;
            }
        }
        return min;
    }

    private static int getHValue(Hexagon src, Hexagon dest) {
        int sx = src.x * 10;
        int sy = -src.y * 10;
        if (src.x % 2 != 0) {
            sy -= 5;
        }
        int dx = dest.x * 10;
        int dy = -dest.y * 10;
        if (dest.x % 2 != 0) {
            dy -= 5;
        }
        int _x = Math.abs(sx - dx);
        int _y = Math.abs(sy - dy);
        return (int) Math.sqrt(_x * _x + _y * _y);
    }

}
