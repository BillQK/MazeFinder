
import java.awt.*;

import javalib.worldimages.OutlineMode;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

public class Edge {
  Vertex from;
  Vertex to;
  int weight;

  int numb = 12;

  public Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  public Edge(Vertex from, Vertex to) {
    this.from = from;
    this.to = to;
  }

  // compares the edge weight
  public int compare(Edge edge) {
    if (this.weight < edge.weight) {
      return -1;
    }
    if (this.weight > edge.weight) {
      return 1;
    } else {
      return 0;
    }
  }

  // connects the vertices via the edge
  public void connect() {
    this.from.outedges.add(this);
    this.to.outedges.add(this);
  }

  // draws the edge
  public WorldImage drawEdge() {
    return new RectangleImage(numb, numb, OutlineMode.SOLID, Color.white);
  }

  @Override
  // are they equal?
  public boolean equals(Object other) {
    if (other instanceof Edge) {
      Edge e = (Edge) other;
      return (this.from.equals(e.from) && this.to.equals(e.to))
              || (this.from.equals(e.to) && this.to.equals(e.from));
    }
    return false;
  }

  public int hashCode() {
    return 0;
  }
}