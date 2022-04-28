
import java.awt.*;
import java.util.ArrayList;

import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

// the player in the game 
class Player {
  int x;
  int y;

  Player(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // draws player
  WorldImage draw() {
    return new RectangleImage(8, 8, OutlineMode.SOLID, Color.gray.brighter());
  }

  // draws playerVertex
  boolean validMove(int changeX, int changeY, ArrayList<Edge> edgesInTree) {
    Vertex from = new Vertex(this.x, this.y);
    Vertex to = new Vertex(changeX, changeY);
    return edgesInTree.contains(new Edge(from, to));
  }
}