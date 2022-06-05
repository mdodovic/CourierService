package rs.etf.sab.student.utility;

import java.math.BigDecimal;

public class Util {
    
    public static double euclidean(int x1, int y1, int x2, int y2) {
        return Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }

    public static BigDecimal getPackagePrice(int type, BigDecimal weight, double distance) {
      switch (type) {
        case 0:
            return new BigDecimal(115.0D * distance);
        case 1:
            return new BigDecimal((175.0D + weight.doubleValue() * 100.0D) * distance);
        case 2:
            return new BigDecimal((250.0D + weight.doubleValue() * 100.0D) * distance);
        case 3:
            return new BigDecimal((350.0D + weight.doubleValue() * 500.0D) * distance);
      } 
      return null;
    }

    public static double getDistance(Pair<Integer, Integer>... addresses) {
        double distance = 0.0D;
        for (int i = 1; i < addresses.length; i++)
            distance += euclidean(((Integer)addresses[i - 1].getKey()).intValue(), ((Integer)addresses[i - 1].getValue()).intValue(), ((Integer)addresses[i]
              .getKey()).intValue(), ((Integer)addresses[i].getValue()).intValue()); 
        return distance;
    }
}
