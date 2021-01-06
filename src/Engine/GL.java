package Engine;

import Primitives.Triangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import java.util.Vector;

public class GL {
    private static Vector<Triangle> trisToSort = new Vector<>();
    private static Vector<Color> colorsToSort = new Vector<>();
    private static Vector<Float> trisDistances = new Vector<>();

    public static void DrawTris(GraphicsContext gc) {
        if (trisDistances.size() < 2) return;
        Triangle tbuff;
        Color cbuff;
        float fbuff;
        for (int i = 0; i < trisToSort.size(); i++) {
            int smallest = i;
            for (int j = i+1; j < trisToSort.size(); j++) {
                if (trisDistances.get(j) > trisDistances.get(smallest)) {
                    smallest = j;
                }
            }
            tbuff = trisToSort.get(i);
            cbuff = colorsToSort.get(i);
            fbuff = trisDistances.get(i);
            trisDistances.set(i, trisDistances.get(smallest));
            trisDistances.set(smallest, fbuff);
            trisToSort.set(i, trisToSort.get(smallest));
            trisToSort.set(smallest, tbuff);
            colorsToSort.set(i, colorsToSort.get(smallest));
            colorsToSort.set(smallest, cbuff);
            //if (i > smallest) i--;
        }

        for (int i = 0; i < trisToSort.size(); i++) {
            Triangle tri = trisToSort.get(i);
            double[] xS = new double[] {
                tri.VFirst().x,
                tri.VSecond().x,
                tri.VThird().x
            };
            double[] yS = new double[] {
                    tri.VFirst().y,
                    tri.VSecond().y,
                    tri.VThird().y
            };
            gc.setStroke(colorsToSort.get(i));
            gc.setFill(colorsToSort.get(i));
            gc.fillPolygon(xS, yS, 3);
            gc.strokePolygon(xS, yS, 3);
        }
        trisToSort.clear();
        colorsToSort.clear();
        trisDistances.clear();
    }

    public static void DrawLine(int sx, int ex, int ny, Color color, PixelWriter pw) {
        for (int i = sx; i <= ex; i++) pw.setColor(i, ny, color);
    }

    public static void FillTriangle(Triangle tri, float distance, Color color) {
        trisToSort.add(tri);
        trisDistances.add(distance);
        colorsToSort.add(color);
    }

    public static void FillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color color, GraphicsContext gc) {
        int t1x, t2x, y, minx, maxx, t1xp, t2xp;
        boolean changed1 = false;
        boolean changed2 = false;
        int signx1, signx2, dx1, dy1, dx2, dy2;
        int e1, e2;
        // Sort vertices
        if (y1 > y2) {
            int buff = y1;
            y1 = y2;
            y2 = buff;
            buff = x1;
            x1 = x2;
            x2 = buff;
        }
        if (y1 > y3) {
            int buff = y1;
            y1 = y3;
            y3 = buff;
            buff = x1;
            x1 = x3;
            x3 = buff;
        }
        if (y2 > y3) {
            int buff = y2;
            y2 = y3;
            y3 = buff;
            buff = x2;
            x2 = x3;
            x3 = buff;
        }

        t1x = t2x = x1;
        y = y1;   // Starting points
        dx1 = (int) (x2 - x1);
        if (dx1 < 0) {
            dx1 = -dx1;
            signx1 = -1;
        } else signx1 = 1;
        dy1 = (int) (y2 - y1);

        dx2 = (int) (x3 - x1);
        if (dx2 < 0) {
            dx2 = -dx2;
            signx2 = -1;
        } else signx2 = 1;
        dy2 = (int) (y3 - y1);

        if (dy1 > dx1) {
            int buff = dx1;
            dx1 = dy1;
            dy1 = buff;
            changed1 = true;
        }
        if (dy2 > dx2) {
            int buff = dy2;
            dy2 = dx2;
            dx2 = buff;
            changed2 = true;
        }

        e2 = (int) (dx2 >> 1);
        // Flat top, just process the second half
        if (y1 == y2) {
            {
                // Second half
                dx1 = (int) (x3 - x2);
                if (dx1 < 0) {
                    dx1 = -dx1;
                    signx1 = -1;
                } else signx1 = 1;
                dy1 = (int) (y3 - y2);
                t1x = x2;

                if (dy1 > dx1) {   // swap values
                    int buff = dy1;
                    dy1 = dx1;
                    dx1 = buff;
                    changed1 = true;
                } else changed1 = false;

                e1 = (int) (dx1 >> 1);

                for (int i = 0; i <= dx1; i++) {
                    t1xp = 0;
                    t2xp = 0;
                    if (t1x < t2x) {
                        minx = t1x;
                        maxx = t2x;
                    } else {
                        minx = t2x;
                        maxx = t1x;
                    }

                    // process first line until y value is about to change
                    while (i < dx1) {
                        e1 += dy1;
                        while (e1 >= dx1) {
                            e1 -= dx1;
                            if (changed1) {
                                t1xp = signx1;
                            }//t1x += signx1;
                            break;
                        }
                        if (changed1) break;
                        else t1x += signx1;
                        if (i < dx1) i++;
                    }

                    {
                        // process second line until y value is about to change
                        while (t2x != x3) {
                            e2 += dy2;
                            while (e2 >= dx2) {
                                e2 -= dx2;
                                if (changed2) t2xp = signx2;
                                else break;
                            }
                            if (changed2) break;
                            else t2x += signx2;
                        }
                    }

                    {
                        if (minx > t1x) minx = t1x;
                        if (minx > t2x) minx = t2x;
                        if (maxx < t1x) maxx = t1x;
                        if (maxx < t2x) maxx = t2x;
                        DrawLine(minx, maxx, y, color, gc.getPixelWriter());
                        if (!changed1) t1x += signx1;
                        t1x += t1xp;
                        if (!changed2) t2x += signx2;
                        t2x += t2xp;
                        y += 1;
                        if (y > y3) return;
                    }

                }
            }
        }
            e1 = (int) (dx1 >> 1);

            for (int i = 0; i < dx1; ) {
                t1xp = 0;
                t2xp = 0;
                if (t1x < t2x) {
                    minx = t1x;
                    maxx = t2x;
                } else {
                    minx = t2x;
                    maxx = t1x;
                }
                // process first line until y value is about to change
                while (i < dx1) {
                    i++;
                    e1 += dy1;
                    while (e1 >= dx1) {
                        e1 -= dx1;
                        if (changed1) t1xp = signx1;//t1x += signx1;
                        else break;
                    }
                    if (changed1) break;
                    else t1x += signx1;
                }

                // Move line
                {
                    // process second line until y value is about to change
                    while (true) {
                        e2 += dy2;
                        while (e2 >= dx2) {
                            e2 -= dx2;
                            if (changed2) t2xp = signx2;//t2x += signx2;
                            else break;
                        }
                        if (changed2) break;
                        else t2x += signx2;
                    }
                }

                {
                    if (minx > t1x) minx = t1x;
                    if (minx > t2x) minx = t2x;
                    if (maxx < t1x) maxx = t1x;
                    if (maxx < t2x) maxx = t2x;
                    DrawLine(minx, maxx, y, color, gc.getPixelWriter());    // Draw line from min to max points found on the y
                    // Now increase y
                    if (!changed1) t1x += signx1;
                    t1x += t1xp;
                    if (!changed2) t2x += signx2;
                    t2x += t2xp;
                    y += 1;
                    if (y == y2) break;
                }
            }

            {
                // Second half
                dx1 = (int) (x3 - x2);
                if (dx1 < 0) {
                    dx1 = -dx1;
                    signx1 = -1;
                } else signx1 = 1;
                dy1 = (int) (y3 - y2);
                t1x = x2;

                if (dy1 > dx1) {   // swap values
                    int buff = dy1;
                    dy1 = dx1;
                    dx1 = buff;
                    changed1 = true;
                } else changed1 = false;

                e1 = (int) (dx1 >> 1);

                for (int i = 0; i <= dx1; i++) {
                    t1xp = 0;
                    t2xp = 0;
                    if (t1x < t2x) {
                        minx = t1x;
                        maxx = t2x;
                    } else {
                        minx = t2x;
                        maxx = t1x;
                    }

                    // process first line until y value is about to change
                    while (i < dx1) {
                        e1 += dy1;
                        while (e1 >= dx1) {
                            e1 -= dx1;
                            if (changed1) {
                                t1xp = signx1;
                                break;
                            }//t1x += signx1;
                            else break;
                        }
                        if (changed1) break;
                        else t1x += signx1;
                        if (i < dx1) i++;
                    }

                    {
                        // process second line until y value is about to change
                        while (t2x != x3) {
                            e2 += dy2;
                            while (e2 >= dx2) {
                                e2 -= dx2;
                                if (changed2) t2xp = signx2;
                                else break;
                            }
                            if (changed2) break;
                            else t2x += signx2;
                        }
                    }

                    {
                        if (minx > t1x) minx = t1x;
                        if (minx > t2x) minx = t2x;
                        if (maxx < t1x) maxx = t1x;
                        if (maxx < t2x) maxx = t2x;
                        DrawLine(minx, maxx, y, color, gc.getPixelWriter());
                        if (!changed1) t1x += signx1;
                        t1x += t1xp;
                        if (!changed2) t2x += signx2;
                        t2x += t2xp;
                        y += 1;
                        if (y > y3) return;
                    }

                }
            }
    }
}
