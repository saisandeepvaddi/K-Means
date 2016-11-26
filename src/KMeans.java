import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * About the code :
 */


class Cluster {
    public int id = 0;
    public Centroid c = null;
    public List points = new ArrayList<Point>();
}


class Centroid {
    public double x = 0;
    public double y = 0;
    public int id = 0;

    public Centroid(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}

class Point {
    public double x = 0;
    public double y = 0;
    public int id = 0;
    public Centroid centroid = null;

    public Point(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}


public class KMeans {
    static List points = new ArrayList<Point>();
    static List centroids = new ArrayList<Centroid>();

    public static void main(String[] args) throws IOException {
        int k = Integer.parseInt(args[0]);
        BufferedReader in = new BufferedReader(new FileReader(args[1].toString()));
        BufferedReader centroidFile = new BufferedReader(new FileReader("initialSeeds.data"));
        PrintWriter out = new PrintWriter(new FileWriter(args[2].toString()));
        StringTokenizer tokenizer = null;
        String line = "";

        int point_count = -1;
        int line_no = 0;
        int i = 0;

        while ((line = in.readLine()) != null) {
            if (line_no == 0) {
                line_no++;
                continue;
            }

            if (tokenizer == null || !tokenizer.hasMoreElements()) {
                tokenizer = new StringTokenizer(line);
            }
            int id = Integer.parseInt(tokenizer.nextToken());
            double x = Double.parseDouble(tokenizer.nextToken());
            double y = Double.parseDouble(tokenizer.nextToken());
            Point p = new Point(id, x, y);
            points.add(p);
        }


        String cenLine = "";
        int cenId = 1;
        for (int cen = 0; cen < k; cen++) {
            cenLine = centroidFile.readLine();
            String seedStr = cenLine.substring(0, cenLine.length() - 1);
            long cenSeed = Long.parseLong(seedStr);
            Random kRand = new Random(cenSeed);
            double x = kRand.nextDouble();
            double y = kRand.nextDouble();
            Centroid centroid = new Centroid(cenId++, x, y);
            centroids.add(centroid);
        }

        List clusters = coreAlgorithm();

        for (int cl = 0; cl < clusters.size(); cl++) {
            Cluster cluster = (Cluster) clusters.get(cl);
            out.print(cluster.id);
            out.print(" ");
            for (int p = 0; p < cluster.points.size(); p++) {
                Point po = (Point) cluster.points.get(p);
                out.print(po.id + ",");
            }
            out.println();
        }


        in.close();
        out.close();
    }

    private static List coreAlgorithm() {
        List clusters = new ArrayList<Cluster>();
        for (int i = 0; i < 25; i++) {
            for (int p = 0; p < points.size(); p++) {
                Point point = (Point) points.get(p);
                Centroid closestCentroid = null;
                double min = Double.MAX_VALUE;
                for (int c = 0; c < centroids.size(); c++) {
                    Centroid centroid = (Centroid) centroids.get(c);
                    double distance = getEucledianDistance(point, centroid);
                    if (distance < min) {
                        min = distance;
                        closestCentroid = centroid;
                    }
                }
                point.centroid = closestCentroid;
            }
            modifyCentroids();
        }

        for (int i = 0; i < centroids.size(); i++) {
            Cluster cluster = new Cluster();
            Centroid centroid = (Centroid) centroids.get(i);
            cluster.id = centroid.id;
//            List cPoints = new ArrayList<Point>();

            for (int p = 0; p < points.size(); p++) {
                Point point = (Point) points.get(p);
                if (point.centroid.id == centroid.id) {
//                    cPoints.add(point);
                    cluster.points.add(point);
                }
            }

            clusters.add(cluster);


        }


        return clusters;
    }

    private static void modifyCentroids() {
        List cPoints = new ArrayList<Point>();
        for (int i = 0; i < centroids.size(); i++) {
            Centroid centroid = (Centroid) centroids.get(i);
            for (int p = 0; p < points.size(); p++) {
                Point point = (Point) points.get(p);
                if (point.centroid.id == centroid.id) {
                    cPoints.add(point);
                }
            }
            double new_x = 0;
            double new_y = 0;
            double total_points = (double) cPoints.size();
            for (int p = 0; p < cPoints.size(); p++) {
                Point point = (Point) cPoints.get(p);
                new_x += point.x;
                new_y += point.y;
            }
            new_x /= total_points;
            new_y /= total_points;

            centroid.x = new_x;
            centroid.y = new_y;
        }
    }

    private static double getEucledianDistance(Point point, Centroid centroid) {
        double distance = 0.0d;
        double pX = point.x;
        double pY = point.y;
        double cX = centroid.x;
        double cY = centroid.y;
        double X2 = (pX - cX) * (pX - cX);
        double Y2 = (pY - cY) * (pY - cY);
        distance = Math.sqrt(X2 + Y2);

        return distance;
    }
}
