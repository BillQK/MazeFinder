
import java.util.Comparator;

// Compares the weights of Edges
class WeightCompare implements Comparator<Edge> {
  public int compare(Edge e1, Edge e2) {
    return e1.weight - e2.weight;
  }
}