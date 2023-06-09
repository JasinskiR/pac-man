package src.ghost;

import src.Board;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DijkstraGhost extends Ghost {
  private List<Point> shortestPath;
  private Queue<Point> pathQueue;

  public DijkstraGhost(String imagePath, float speed, Point startPos) {
    super(imagePath, speed, startPos);
    shortestPath = new ArrayList<>();
    pathQueue = new LinkedList<>();
  }

  @Override
  void Move() {
    if (shortestPath.isEmpty()) {
      calculateShortestPath();
      if (shortestPath.isEmpty()) {
        return; // No valid path found
      }
      pathQueue.addAll(shortestPath);
      pathQueue.poll(); // Skip the first position (current position)
    }

    if (pathQueue.isEmpty()) {
      return; // No more moves in the path
    }

    Point nextPos = pathQueue.poll();
    moveVector.x = nextPos.x - pos.x;
    moveVector.y = nextPos.y - pos.y;

    pos.setLocation(nextPos);
    wallCollision(moveVector);
  }

  private void calculateShortestPath() {
    int[][] distance = new int[Board.ROWS][Board.COLUMNS];
    Point[][] previous = new Point[Board.ROWS][Board.COLUMNS];

    for (int i = 0; i < Board.ROWS; i++) {
      for (int j = 0; j < Board.COLUMNS; j++) {
        distance[i][j] = Integer.MAX_VALUE;
        previous[i][j] = null;
      }
    }

    distance[pos.y][pos.x] = 0;

    Queue<Point> queue = new LinkedList<>();
    queue.offer(pos);

    while (!queue.isEmpty()) {
      Point current = queue.poll();

      for (int i = 0; i < 4; i++) {
        int dx = 0, dy = 0;
        if (i == 0) dy = -1; // Up
        else if (i == 1) dx = 1; // Right
        else if (i == 2) dy = 1; // Down
        else if (i == 3) dx = -1; // Left

        int newX = current.x + dx;
        int newY = current.y + dy;

        if (newX >= 0 && newX < Board.COLUMNS && newY >= 0 && newY < Board.ROWS &&
                Board.MAP[newY][newX] == 0 && distance[newY][newX] == Integer.MAX_VALUE) {
          distance[newY][newX] = distance[current.y][current.x] + 1;
          previous[newY][newX] = current;
          queue.offer(new Point(newX, newY));
        }
      }
    }

    shortestPath.clear();
    Point target = Board.getPlayer().getPos();
    Point current = target;

    while (previous[current.y][current.x] != null) {
      shortestPath.add(0, current);
      current = previous[current.y][current.x];
    }

    if (!shortestPath.isEmpty()) {
      shortestPath.add(0, current); // Add the start position to the path
    }
  }
}
