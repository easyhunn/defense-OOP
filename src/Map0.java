import java.awt.*;

public class Map0 extends Map {
    int[] maxY = {160,477,695};
    int[] minY = {45,350,612};
    int maxX = 900, minX = -100;


    public Map0() {
        firstLand = 50;
        turn = 3;
        delta = 0;
        lastTime = System.currentTimeMillis();

        rest = 8;
        numberEnemy = new int[turn];
        numberEnemy[0] = 1;
        numberEnemy[1] = 2;
        numberEnemy[2] = 3;
    }

    @Override
    public boolean check(double a, double b) {
        int x = (int)a;
        int y = (int)b;
        if (x >= 104 && x <= 860) {
            for (int i = 0; i < 3; ++i) {
                if (y >= minY[i] && y <= maxY[i])
                    return true;
            }
        }
        if (x > 0 && x < 104) {
            if ((y >= minY[0] && y <= maxY[0]) || (y > minY[1] && y < maxY[2]))
                return true;
        }
        if (x > 860 && x <= 1000) {
            if ((y > minY[0] && y < maxY[2]) || (y > minY[2] && y <= 700))
                return true;
        }

        return false;
    }



    @Override
    public boolean left(double a, double b) {
        int x = (int)a;
        int y = (int)b;
        if ((y >= minY[0] && y <= maxY[0]) && x <= 860)
            return true;
        if ((y >= minY[2] && y <= maxY[2]))
            return true;
        return false;
    }

    @Override
    public boolean right(double a, double b) {
        int x = (int)a;
        int y = (int)b;
        if (y >= minY[1] && y <= maxY[1] && x > 50)
            return true;
        return false;
    }

    @Override
    public boolean up(double a, double b) {
        
        return false;
    }

    @Override
    public boolean down(double a, double b) {

        int x = (int)a;
        int y = (int)b;

        if ((y <= minY[1] + 20 && x >= 860) || (y <= minY[2] +20 && y >= minY[1] && x <= 100)) {

            return true;
        }
        else
            return false;

    }

    @Override
    public boolean isTarget(Enemy e) {
        if (e.y >= minY[2] -100 && e.x >= maxX - 5) {
            //System.out.println("in target");
            return true;
        }
        else return false;
    }

    @Override
    public boolean isLandTower(int x, int y) {

        if (x > 0 && x < 860) {
            if (y >= maxY[0]+20 && y <= minY[1]+20)
                return true;
        }
        if (x > 100 && x < 1000) {
            if (y > maxY[1] + 20 && y < minY[2] + 20)
                return true;
        }
        return false;
    }

    @Override
    public void tick() {
        now = System.currentTimeMillis();
        delta += (now - lastTime)/1000;
        lastTime = now;
       // System.out.println(delta);
    }


    @Override
    public void render(Graphics g) {
        g.drawImage(Assets.bg0,0,0,null);
        g.drawImage(Assets.grass,1000, 0,null);
        g.drawImage(Assets.heart,1000,30,null);
        g.drawImage(Assets.coin,1120,30, null);
    }
}
