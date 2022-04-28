import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.AboveImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;
import javalib.worldimages.WorldImage;


public class MazeGame extends World {
  int width;
  int height;
  int cellsize;

  // All the nodes of the game
  ArrayList<ArrayList<Vertex>> board;

  // All edges in tree (total)
  ArrayList<Edge> edgesInTree;

  // All edges in graph, sorted by edge weights
  ArrayList<Edge> worklist;

  // representatives
  HashMap<Vertex, Vertex> representatives;

  // player
  Player player;

  // path
  ArrayList<Vertex> solution;

  // seen vertices
  ArrayList<Vertex> alreadySeen;

  // player's path
  ArrayList<Vertex> playerVertices;

  // player's score
  int score;

  // tick
  int tick;

  // number
  int numb = 15;

  MazeGame(int width, int height) {
    this.width = width;
    this.height = height;
    this.playerVertices = new ArrayList<Vertex>();
    this.edgesInTree = new ArrayList<Edge>();
    this.board = this.initialize();
    // connects the vertices (edges)
    this.worklist = this.connectAllEdges();
    this.unionFind();
    this.makePath();
    this.player = new Player(0, 0);
    this.alreadySeen = new ArrayList<Vertex>();
    this.solution = new ArrayList<Vertex>();
    this.score = 0;
  }

  // y-values
  ArrayList<ArrayList<Vertex>> initialize() {
    ArrayList<Vertex> rows = new ArrayList<Vertex>();
    ArrayList<ArrayList<Vertex>> board = new ArrayList<ArrayList<Vertex>>();
    this.board = new ArrayList<ArrayList<Vertex>>();
    for (int i = 0; i < this.height; i++) {
      rows = makeRow(i);
      board.add(rows);
    }

    this.playerVertices.add(board.get(0).get(0));
    return board;
  }

  // the x-values
  ArrayList<Vertex> makeRow(int i) {
    ArrayList<Vertex> row = new ArrayList<Vertex>();
    for (int n = 0; n < this.width; n++) {
      row.add((new Vertex(n, i)));
    }
    return row;
  }

  // connects all edges in the grid
  ArrayList<Edge> connectAllEdges() {
    ArrayList<Edge> finalList = new ArrayList<Edge>();
    for (ArrayList<Vertex> list : this.board) {
      for (Vertex v : list) {
        this.connectEdges(v, finalList);
      }
    }
    Collections.sort(finalList, new WeightCompare());
    return finalList;
  }

  // connects all the vertices using the edges
  void connectEdges(Vertex other, ArrayList<Edge> list) {
    if (other.x != this.width - 1) {
      // sets right
      other.createEdges(board.get(other.y).get(other.x + 1), list);
    }
    if (other.y != this.height - 1) {
      // adds bottom
      other.createEdges(board.get(other.y + 1).get(other.x), list);
    }
  }

  // initialize every node's representative to itself
  void initializeRep() {
    HashMap<Vertex, Vertex> map = new HashMap<Vertex, Vertex>();
    for (ArrayList<Vertex> column : this.board) {
      for (Vertex v : column) {
        map.put(v, v);
      }
    }
    this.representatives = map;
  }

  ArrayList<Edge> unionFind() {
    initializeRep();
    while (this.edgesInTree.size() < this.representatives.size() - 1) {
      Edge next = this.worklist.remove(0);
      if (find(this.representatives, next.to).equals(find(this.representatives, next.from))) {
        // don't do anything
      } else {
        this.edgesInTree.add(next);
        union(this.representatives, find(this.representatives, next.to),
                find(this.representatives, next.from));
      }
    }
    return this.edgesInTree;
  }

  // connects the edges to make a path
  void makePath() {
    for (Edge e : this.edgesInTree) {
      e.connect();
    }
  }

  // merges the representing boxes
  void union(HashMap<Vertex, Vertex> representatives, Vertex node, Vertex link) {
    representatives.put(node, link);
  }

  // finds the representative of the given key
  Vertex find(HashMap<Vertex, Vertex> representatives, Vertex node) {
    Vertex value = representatives.get(node);
    if (value.equals(node)) {
      return representatives.get(node);
    } else {
      return find(representatives, value);
    }
  }

  // breath first search
  void bfs() {
    HashMap<Vertex, Vertex> camefromEdge = new HashMap<Vertex, Vertex>();
    ArrayList<Vertex> wlist = new ArrayList<Vertex>();

    wlist.add(this.board.get(0).get(0));

    while (!wlist.isEmpty()) {
      // contain the first vertex of wlist
      Vertex nextv = wlist.remove(0);
      Vertex finalv = this.board.get(this.height - 1).get(this.width - 1);

      if (nextv.equals(finalv)) {
        this.reconstruct(camefromEdge, nextv);
        return;
      }

      for (int i = 0; i < nextv.outedges.size(); i++) {
        Edge e = nextv.outedges.get(i);
        if (!this.alreadySeen.contains(e.to) && nextv.equals(e.from)) {

          wlist.add(e.to);
          wlist.add(e.to);
          this.alreadySeen.add(nextv);
          camefromEdge.put(e.to, nextv);
        } else if (!this.alreadySeen.contains(e.from) && nextv.equals(e.to)) {
          wlist.add(e.from);
          this.alreadySeen.add(nextv);
          camefromEdge.put(e.from, nextv);
        }
      }
    }
  }

  // depth first search
  void dfs() {
    HashMap<Vertex, Vertex> camefromEdge = new HashMap<Vertex, Vertex>();
    ArrayList<Vertex> wlist = new ArrayList<Vertex>();

    wlist.add(this.board.get(0).get(0));

    while (!wlist.isEmpty()) {
      // contain the first vertex of wlist
      Vertex nextv = wlist.remove(0);
      Vertex finalv = this.board.get(this.height - 1).get(this.width - 1);

      if (nextv.equals(finalv)) {
        this.reconstruct(camefromEdge, nextv);
        return;
      }

      for (int i = 0; i < nextv.outedges.size(); i++) {
        Edge e = nextv.outedges.get(i);
        if (!this.alreadySeen.contains(e.to) && nextv.equals(e.from)) {
          wlist.add(0, e.to);
          this.alreadySeen.add(nextv);
          camefromEdge.put(e.to, nextv);
        } else if (!this.alreadySeen.contains(e.from) && nextv.equals(e.to)) {
          wlist.add(0, e.from);
          this.alreadySeen.add(nextv);
          camefromEdge.put(e.from, nextv);
        }
      }
    }
  }

  // reconstructs the path from end to start
  void reconstruct(HashMap<Vertex, Vertex> cameFromEdge, Vertex next) {
    this.solution.add(this.board.get(this.board.size() - 1).get(this.board.get(0).size() - 1));
    Vertex start = this.board.get(0).get(0);
    while (start != next) {
      this.solution.add(cameFromEdge.get(next));
      next = cameFromEdge.get(next);
    }
  }

  // draws the grid of the game
  WorldScene drawCells(WorldScene image) {
    for (ArrayList<Vertex> list : this.board) {
      for (Vertex v : list) {
        image.placeImageXY(v.drawCell(), v.x * numb + 6, v.y * numb + 6);
      }
    }
    return image;
  }

  // draws the edges
  WorldScene drawEdges(WorldScene image) {
    for (Edge e : this.edgesInTree) {
      image.placeImageXY(e.drawEdge(), (e.to.x + e.from.x) * numb / 2 + 6,
              (e.to.y + e.from.y) * numb / 2 + 6);
    }
    return image;
  }

  // draws the path of the game
  WorldScene drawPath(WorldScene image) {
    // draws the path
    for (Vertex v : this.solution) {
      image.placeImageXY(v.drawPath(), v.x * numb + 6, v.y * numb + 6);
    }
    return image;
  }

  // draws the alreadySeen of the game
  WorldScene drawsAlreadySeen(WorldScene image) {
    // draws the path
    for (Vertex v : this.alreadySeen) {
      image.placeImageXY(v.drawSeen(), v.x * numb + 6, v.y * numb + 6);
    }
    return image;
  }

  // draws the player's path in the game
  WorldScene drawPlayer(WorldScene image) {
    // draws the path
    for (Vertex v : this.playerVertices) {
      image.placeImageXY(v.drawPlayer(), v.x * numb + 6, v.y * numb + 6);
    }
    return image;
  }

  public WorldScene makeScene() {
    WorldScene ws = getEmptyScene();
    WorldScene grid = this.drawCells(ws);
    WorldScene edges = this.drawEdges(grid);
    // endPoint
    edges.placeImageXY(new RectangleImage(13, 13, OutlineMode.SOLID, Color.red.darker()),
            width * 15 - 8, height * 15 - 8);
    WorldScene searchHelper = this.drawsAlreadySeen(edges);
    WorldScene solution = this.drawPath(searchHelper);
    WorldScene playersPath = this.drawPlayer(solution);
    // draws player
    playersPath.placeImageXY(this.player.draw(), player.x * 15 + 5, player.y * 15 + 5);

    return edges;
  }

  public WorldScene lastScene(String msg) {
    // displays total moves
    WorldImage image = new OverlayImage(
            new AboveImage(new TextImage(msg, this.width - 5, Color.black),
                    new TextImage("Score: " + this.score, this.width - 5, Color.black)),
            new RectangleImage(width * 10, 2 * (this.height * this.width / 8), OutlineMode.SOLID,
                    Color.white));
    WorldScene background = this.makeScene();
    background.placeImageXY(image, width * 5, height * 5);
    return background;
  }

  // Onkey Event
  public void onKeyEvent(String ke) {
    Vertex endV = this.board.get(this.height - 1).get(this.width - 1);
    if (ke.equals("left")
            && this.player.validMove(this.player.x - 1, this.player.y, this.edgesInTree)) {
      this.player.x = this.player.x - 1;
      this.playerVertices.add(this.board.get(this.player.y).get(this.player.x));
      this.score++;
      if (this.player.x == endV.x && this.player.y == endV.y) {
        this.endOfWorld("YOU SOLVED THE MAZE!");
      }
    }
    if (ke.equals("right")
            && this.player.validMove(this.player.x + 1, this.player.y, this.edgesInTree)) {
      this.player.x = this.player.x + 1;
      this.playerVertices.add(this.board.get(this.player.y).get(this.player.x));
      this.score = this.score + 1;
      if (this.player.x == endV.x && this.player.y == endV.y) {
        this.endOfWorld("YOU SOLVED THE MAZE!");
      }
    }
    if (ke.equals("up")
            && this.player.validMove(this.player.x, this.player.y - 1, this.edgesInTree)) {
      this.player.y = this.player.y - 1;
      this.playerVertices.add(this.board.get(this.player.y).get(this.player.x));
      this.score++;
      if (this.player.x == endV.x && this.player.y == endV.y) {
        this.endOfWorld("YOU SOLVED THE MAZE!");
      }
    }
    if (ke.equals("down")
            && this.player.validMove(this.player.x, this.player.y + 1, this.edgesInTree)) {
      this.player.y = this.player.y + 1;
      this.playerVertices.add(this.board.get(this.player.y).get(this.player.x));
      this.score++;
      if (this.player.x == endV.x && this.player.y == endV.y) {
        this.endOfWorld("YOU SOLVED THE MAZE!");
      }
    }
    if (ke.equals("b")) {
      this.tick = 0;
      this.bfs();
    }
    if (ke.equals("d")) {
      this.tick = 0;
      this.dfs();
    }
    if (ke.equals("r")) {
      this.playerVertices = new ArrayList<Vertex>();
      this.edgesInTree = new ArrayList<Edge>();
      this.board = this.initialize();
      // connects the vertices (edges)
      this.worklist = this.connectAllEdges();
      this.unionFind();
      this.makePath();
      this.player = new Player(0, 0);
      this.alreadySeen = new ArrayList<Vertex>();
      this.solution = new ArrayList<Vertex>();
      this.score = 0;
    }
  }

  // draws a vertex of the seen and then the path
  public void onTick() {
    int reset = this.tick - this.alreadySeen.size();
    if (this.tick >= 0) {
      this.tick++;
    }
    if (this.alreadySeen.size() > 0 && this.tick < this.alreadySeen.size()) {
      Vertex s = this.alreadySeen.get(this.tick);
      s.explored = true;
    }
    if (this.tick > this.alreadySeen.size()) {
      if (reset < this.solution.size()) {
        Vertex p = this.solution.get(reset);
        p.path = true;
      }
    }
  }
}
