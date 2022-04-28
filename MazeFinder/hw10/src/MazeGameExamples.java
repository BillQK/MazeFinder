import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import tester.Tester;

public class MazeGameExamples {
  MazeGame world;
  ArrayList<ArrayList<Vertex>> board1;
  ArrayList<ArrayList<Vertex>> board2;
  ArrayList<ArrayList<Vertex>> board3;
  ArrayList<Vertex> reconstructedpath;
  ArrayList<Vertex> list1;
  ArrayList<Vertex> list2;
  ArrayList<Vertex> list3;
  ArrayList<Vertex> list4;
  ArrayList<Vertex> gridlist1;
  ArrayList<Vertex> gridlist2;
  ArrayList<Vertex> gridlist3;
  ArrayList<Edge> edges;
  ArrayList<Edge> edges2;
  ArrayList<Edge> sortededges;
  ArrayList<Edge> edgesintree;
  Vertex a;
  Vertex b;
  Vertex c;
  Vertex d;
  Vertex e;
  Vertex f;
  Edge eToC;
  Edge cToD;
  Edge aToB;
  Edge bToE;
  Edge bToC;
  Edge fToD;
  Edge aToE;
  Edge bToF;
  HashMap<Vertex, Vertex> rep;
  HashMap<Vertex, Vertex> linkedrep;
  HashMap<Vertex, Vertex> cameFromEdge;
  Player player;

  MazeGameExamples() {
  }

  public void init() {
    this.world = new MazeGame(2, 3);
    // vertices of map
    this.a = new Vertex(0, 0);
    this.b = new Vertex(0, 1);
    this.c = new Vertex(0, 2);
    this.d = new Vertex(1, 0);
    this.e = new Vertex(1, 1);
    this.f = new Vertex(1, 2);

    // example of edges in a map
    this.eToC = new Edge(this.e, this.c, 15);
    this.cToD = new Edge(this.c, this.d, 25);
    this.aToB = new Edge(this.a, this.b, 30);
    this.bToE = new Edge(this.b, this.e, 35);
    this.bToC = new Edge(this.b, this.c, 40);
    this.fToD = new Edge(this.f, this.d, 50);
    this.aToE = new Edge(this.a, this.e, 50);
    this.bToF = new Edge(this.b, this.f, 50);

    this.a.outedges.add(this.aToB);
    this.a.outedges.add(this.aToE);
    this.b.outedges.add(this.bToC);
    this.b.outedges.add(this.bToE);
    this.b.outedges.add(this.bToF);
    this.c.outedges.add(this.cToD);
    this.f.outedges.add(this.fToD);
    this.e.outedges.add(this.eToC);

    this.list1 = new ArrayList<Vertex>(Arrays.asList(this.a, this.b, this.c));
    this.list2 = new ArrayList<Vertex>(Arrays.asList(this.d, this.e, this.f));
    this.board2 = new ArrayList<ArrayList<Vertex>>(Arrays.asList(this.list1, this.list2));

    this.edges = new ArrayList<Edge>(Arrays.asList(this.aToB, this.bToC, this.bToF, this.eToC,
            this.aToE, this.bToE, this.cToD, this.fToD));

    this.sortededges = new ArrayList<Edge>(Arrays.asList(this.eToC, this.cToD, this.aToB, this.bToE,
            this.bToC, this.fToD, this.aToE, this.bToF));

    this.linkedrep = new HashMap<Vertex, Vertex>();
    this.linkedrep.put(this.a, this.e);
    this.linkedrep.put(this.b, this.a);
    this.linkedrep.put(this.c, this.e);
    this.linkedrep.put(this.d, this.e);
    this.linkedrep.put(this.e, this.e);
    this.linkedrep.put(this.f, this.d);

    this.edgesintree = new ArrayList<Edge>(
            Arrays.asList(this.eToC, this.cToD, this.aToB, this.bToE, this.fToD));

    this.player = new Player(0, 0);

    this.reconstructedpath = new ArrayList<Vertex>(
            Arrays.asList(this.a, this.b, this.b, this.d, this.f));

  }

  void testGame(Tester t) {
    MazeGame g = new MazeGame(30, 30);
    g.bigBang(g.width * 15, g.width * 15, 0.05);
  }

  // Testing initialize()
  public void testInitialize(Tester t) {
    init();
    this.world.initialize();
    Vertex v1 = this.world.board.get(0).get(0);
    Vertex v2 = this.world.board.get(0).get(1);
    Vertex v3 = this.world.board.get(0).get(2);
    Vertex v4 = this.world.board.get(1).get(0);
    Vertex v5 = this.world.board.get(1).get(1);
    Vertex v6 = this.world.board.get(1).get(2);

    t.checkExpect(v1, a);
    t.checkExpect(v2, b);
    t.checkExpect(v3, c);
    t.checkExpect(v4, d);
    t.checkExpect(v5, e);
    t.checkExpect(v6, f);

  }

  // tests grid
  void testBoard(Tester t) {
    this.init();
    MazeGame game1 = new MazeGame(3, 4);
    t.checkExpect(game1.board.size(), 4);
    t.checkExpect(game1.board.get(0).size(), 3);
    t.checkExpect(game1.board.get(1).size(), 3);
    t.checkExpect(game1.board.get(2).size(), 3);
    for (int i = 0; i < game1.board.size(); i++) {
      for (int j = 0; j < game1.board.get(0).size(); j++) {
        System.out.print("*");
      }
      System.out.println();
    }

  }

  // test connectAllEdges
  void testconnectAllEdges(Tester t) {
    this.init();
    t.checkExpect(this.world.connectAllEdges().size() + 1, this.edges.size());
  }

  // tests onKeyEvent
  void testOnKeyEvent(Tester t) {
    this.init();
    this.world.onKeyEvent("up");
    t.checkOneOf(this.player.y, -1, 0);
    this.world.onKeyEvent("down");
    t.checkOneOf(this.player.y, 0, 1);
    this.world.onKeyEvent("left");
    t.checkOneOf(this.player.x, -1, 0);
    this.world.onKeyEvent("right");
    t.checkOneOf(this.player.x, 0, 1);

    this.world.onKeyEvent("b");
    t.checkExpect(this.world.alreadySeen.size() > 0, true);
    t.checkExpect(this.world.solution.size() > 0, true);

    this.world.onKeyEvent("d");
    t.checkExpect(this.world.alreadySeen.size() > 0, true);
    t.checkExpect(this.world.solution.size() > 0, true);

    this.world.onKeyEvent("r");
    t.checkExpect(this.world.alreadySeen.size() == 0, true);
    t.checkExpect(this.world.solution.size() == 0, true);
    t.checkExpect(this.world.score == -1, false);
  }

  // test makePath
  void testmakePath(Tester t) {
    this.init();
    Edge e = new Edge(this.a, this.f, 10);
    this.world.makePath();
    for (Edge a : this.world.edgesInTree) {
      t.checkExpect(a.from.outedges.contains(a), true);
      t.checkExpect(a.to.outedges.contains(a), true);
      t.checkExpect(a.from.outedges.contains(e), false);
    }
  }

  // tests unionFind
  void testunionFind(Tester t) {
    this.init();
    t.checkExpect(this.world.edgesInTree.size(), this.world.height * this.world.width - 1);
    // edgesinTree are sorted
    int i = 0;
    while (i < this.world.edgesInTree.size() - 1) {
      Edge first = this.world.edgesInTree.get(i);
      Edge second = this.world.edgesInTree.get(i + 1);
      t.checkExpect(first.weight < second.weight, true);
      i++;
    }
  }

  // test find
  void testFind(Tester t) {
    this.init();
    t.checkExpect(this.world.find(this.linkedrep, this.a), this.e);
  }

  // Test PlayerDraw
  void testPlayerDraw(Tester t) {
    this.init();
    t.checkExpect(this.world.player.draw(),
            new RectangleImage(8, 8, OutlineMode.SOLID, Color.gray.brighter()));
  }

  // test validMove
  void testvalidMove(Tester t) {
    this.init();
    t.checkOneOf(this.world.player.validMove(1, 2, this.edgesintree), false, true);
    t.checkOneOf(this.world.player.validMove(0, 1, this.edgesintree), true, false);
    t.checkOneOf(this.world.player.validMove(2, 1, this.edgesintree), false, true);
    t.checkOneOf(this.world.player.validMove(0, 2, this.edgesintree), false, true);

  }

  // test drawCell
  void testdrawCell(Tester t) {
    this.init();
    t.checkExpect(this.a.drawCell(), new RectangleImage(14, 14, OutlineMode.OUTLINE, Color.gray));
  }

  // test drawSeen
  void testDrawSeen(Tester t) {
    this.init();
    this.a.explored = true;
    t.checkExpect(this.a.drawSeen(),
            new RectangleImage(14, 14, OutlineMode.SOLID, Color.pink.darker()));
  }

  void drawPath(Tester t) {
    this.init();
    this.a.path = true;
    t.checkExpect(this.a.drawPath(),
            new RectangleImage(13, 13, OutlineMode.SOLID, Color.pink.darker().darker()));
  }

}
