import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class gameField {

    private List<Enemy> enemies;
    private List<Tower> towers;
    private Queue<Map> maps;
    private Map map;
    private int heart,gold;
    private int numberEnemy;
    private Game game;

    private static boolean isDragging;
    private int sPX, sPY;

    private int act = 0;

    //map

    private boolean isCount, ss;
    boolean inTarget = false;
    private int wave = 0;

    //tower
    private int towerKind = 0;

    //audio

    private audio pickSound ,deadSound , shootSoud;

    //end game
    private boolean pause, win;

    public gameField (Game game) {
        pause = false;
        win = false;
        //audio
        try {
           //pickSound = new audio("music/sfx/item-collect.wav");
           // deadSound = new audio("music/sfx/enemy-explosion.wav");
            shootSoud = new audio("music/sfx/player-shoot.wav");

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }


        isDragging = false;
        this.game = game;
        heart = 10;
        gold = 50;
        isCount = true;
        numberEnemy = 5;

        //constructor map
        maps = new LinkedList<>();

        //constructor enemy
        enemies = new ArrayList<>();

        for (int i = 0; i < 5; ++i) {
                enemies.add(new NormalEnemy(i*(-100) - 100, 50));
        }

        ss = false;

        //constructor tower and bullet
        towers = new ArrayList<>();
        addMap();
        map = maps.poll();
    }




    public void addMap() {
        maps.add(new Map0());
    }

    public void tick() {
        //System.out.println(enemies.size());

        if (pause || win) {
            if (Game.getMouseManager().leftPressed) {
                game = new Game("defense", 1280, 700);
                game.waiting();
            }
            return;
        }

        map.tick();
        isCount = false;
        if (!ss) {
            if (map.delta < 10) {
                isCount = true;
            } else {
                ss = true;
                isCount = false;
            }
        }

        //if start game or empty enemy in turn
        // create new turn
        /*
        if ((enemys.isEmpty() && map.numberEnemy[map.turn-1] <= 0) || map.turn == 3) {
            isCount = true;

            if (map.delta > 8) {
                for (int i = 0; i < numberEnemy ; ++i) {
                    //after x time 1 enemy appear
                    int ran = (int) Math.random() * (100 - 10 + 1) + 1;

                    enemys.add(new NormalEnemy(-i * 30, 50 + ran * i));
                }
                map.turn --;
            }
            if (map.delta > 8)
                map.delta = -1;

        }




            // after 15 second one new turn enemy create until all enemy of turn run out
            if (map.delta > 15 && map.numberEnemy[map.turn-1] >=0){
                for (int i = 0; i < numberEnemy ; ++i) {
                    //after x time 1 enemy appear
                    int ran = (int) Math.random() * (100 - 10 + 1) + 1;

                    enemys.add(new NormalEnemy(-i * 30, 50 + ran * i));
                }
                map.numberEnemy[map.turn]--;
            }

            if (map.delta >= 15)
                map.delta = -1;
            */
            if (enemies.isEmpty()) {
                if (wave >= 0) {
                    for (int i = 0; i < 10; ++i) {
                        enemies.add(new tank(-i * 100, 50));
                    }

                }
                if (wave >= 1) {
                    enemies.add(new Boss(-100, 50));
                }
                wave ++;
                return;
            }

            if (ss) {
                try {
                    for (Enemy e : enemies) {
                        e.tick();
                        if (e.health <= 0) {
                            //sound
                            deadSound = new audio("music/sfx/enemy-explosion.wav");
                            deadSound.play();

                            enemies.remove(e);
                            gold += 5;
                            continue;
                        }

                        // check enemy is run able

                        if (map.check(e.x, e.y)) {
                            if (map.left(e.x, e.y)) {
                                e.left();
                                // System.out.println("left");
                            }
                            if (map.right(e.x, e.y)) {
                                e.right();
                                //System.out.println("right");
                            }
                            if (map.up(e.x, e.y)) {
                                e.up();
                                //System.out.println("up");
                            }
                            if (map.down(e.x, e.y)) {
                                e.down();
                                // System.out.println("down");
                            }
                            //check if enemy arrived target
                            if (map.isTarget(e)) {
                                enemies.remove(e);
                                heart--;
                            }

                        } else {
                            if (map.isTarget(e)) {
                                enemies.remove(e);
                                heart--;
                            }
                            if (e.x <= 0) {
                                //System.out.println("x out bound right side");
                                e.x += e.speed;
                            } else if (e.x >= 900) {
                                e.x -= e.speed;
                                //System.out.println(" x out bound left side");
                                // System.out.println(e.x);
                            } else if (map.check(e.x, e.y + (int) e.speed)) {
                                // System.out.println("y can't be reach");
                                e.y += e.speed;
                            } else if (map.check(e.x, e.y - (int) e.speed)) {
                                e.y -= e.speed;
                                // System.out.println("y out bound");
                            } else {
                                //System.out.println(e.x + " " + e.y);
                                System.out.println("never call me");
                                e.left();
                            }

                        }


                    }
                } catch (Exception e) {
                    System.out.println("none");
                }

                // status of enemy
                act++;
            }

            for (Tower t : towers) {

                t.fight = false;

                for (int i =0; i < 5 - enemies.size(); ++i) {
                    t.bullet.tick();
                }
                for (int i = 0; i < enemies.size(); ++i) {
                    t.bullet.tick();
                    Enemy e = enemies.get(i);
                    int dis = (int) Math.sqrt((t.x - e.x) * (t.x - e.x) + (t.y - e.y) * (t.y - e.y));

                    //tower active
                    if (dis <= t.scope)
                        t.fight = true;

                    if (dis <= t.scope && e.health > 0) {
                       // System.out.println("choose");
                        t.bullet.choose(e);

                    } else {

                     //   System.out.println("dis " + dis);
                        t.bullet.choose(null);
                    }
                    /*
                    else {
                        t.bullet.reset();
                    }

                     */
                    //Collision
                    // if enemy has collision with bullet

                    if (Math.abs(t.bullet.x - e.x) <= t.bullet.scope && Math.abs(t.bullet.y - e.y) <= t.bullet.scope && e.health > 0) {
                        //System.out.println("collision");
                        if (enemies.get(i).armor <=0 || !t.bullet.armour) {
                            enemies.get(i).health -= t.bullet.dame;
                        } else {
                            enemies.get(i).armor -= t.bullet.dame;
                            enemies.get(i).health -= t.bullet.dame/2;
                        }
                        if (enemies.get(i).speed > 0.5)
                            enemies.get(i).speed -= t.bullet.slowDown;
                        inTarget = true;
                    }

                    if (enemies.get(i).speed < 1)
                           enemies.get(i).speed += 0.002;

                    if (inTarget && Math.abs(t.bullet.x - e.x) > t.bullet.scope +1 &&Math.abs(t.bullet.x - e.x) <= t.bullet.scope*15 && Math.abs(t.bullet.y - e.y) <= t.bullet.scope*10 && e.health > 0) {

                        enemies.get(i).health -= t.bullet.largeDame;

                        inTarget = false;
                    }

                }
            }

            //cacth mouse event

            if (Game.getMouseManager().isLeftPressed()) {

                sPX = Game.getMouseManager().prX;
                sPY = Game.getMouseManager().prY;

                //sound

                try {
                    pickSound = new audio("music/sfx/item-collect.wav");
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
                pickSound.play();


                //pick tower
                if (sPX >= 1000 && sPX <= 1200 && sPY >= 100 && sPY <= 300) {

                    isDragging = true;
                    Game.getMouseManager().leftPressed = false;
                    towerKind = 1;
                } else if (sPX >= 1000 && sPX <= 1200 && sPY >= 300 && sPY < 550) {

                    isDragging = true;
                    Game.getMouseManager().leftPressed = false;
                    towerKind = 2;

                } else if (sPX >= 1000 && sPX <= 1200 && sPY >= 600 && sPY <= 900) {

                    isDragging = true;
                    Game.getMouseManager().leftPressed = false;
                    towerKind = 3;
                }

                if (sPX >= 1190 && sPY >= 660) {
                    game.display.frame.dispatchEvent(new WindowEvent(game.display.frame, WindowEvent.WINDOW_CLOSING));
                }

            }

    }

    public void render(Graphics g) {

        map.render(g);

        g.setFont(new Font("Chiller", Font.BOLD, 30));
        g.setColor(Color.red);
        g.drawString("heart ", 1050, 50);
        g.drawString(heart +"", 1030, 100);

        // lose
        if (heart <= 0) {
            g.setFont(new Font("Chiller", Font.BOLD, 70));
            g.drawString("lose", 500, 350);
            pause = true;
        }
        //win
        if (wave >= 3) {
            win = true;
            g.setFont(new Font("Chiller", Font.BOLD, 70));
            g.drawString("Victory", 500, 350);
        }

        g.setColor(Color.orange);
        g.drawString("Gold ", 1160,50);
        g.drawString(gold+"",1150,100);

        if (act >= Assets.aHead.size()-1)
            act = 0;
        /*
        try {
            for (Enemy e : enemies) {
                e.render(g);
            }
        } catch (Exception e) {
            if (enemies.isEmpty()) {
                g.setColor(Color.red);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 80));
                g.drawString("win", 1050, 500);
            }
        }

         */

        for (Enemy e : enemies) {
            if ( e != null && e.health > 0)
                e.render(g);
        }
        for (Tower t : towers) {
            t.render(g);
            t.bullet.render(g);
        }

        // draw instance tower
        g.drawImage(Assets.resize(Assets.normalTower.get(3), 80,100), 1100, 150, null);
        g.drawImage(Assets.resize(Assets.fireTower.get(3), 120,140), 1100, 350, null);
        g.drawImage(Assets.resize(Assets.landTower.get(3), 120,140), 1100, 550, null);

        //land tower
        if (isDragging) {

            if (Game.getMouseManager().rightPressed)
                isDragging = false;
            int prX = Game.getMouseManager().getMouseX() - 100/2;
            int prY = Game.getMouseManager().getMouseY() - 120/2;

            if (towerKind == 1)
                g.drawImage(Assets.resize(Assets.normalTower.get(3),80,100),Game.getMouseManager().getMouseX() - 80/2, Game.getMouseManager().getMouseY() - 100/2, null);
            else if (towerKind == 2) {
                g.drawImage(Assets.resize(Assets.fireTower.get(3),120,140),Game.getMouseManager().getMouseX() - 120/2, Game.getMouseManager().getMouseY()  - 140/2, null);
            } else if (towerKind == 3) {
                g.drawImage(Assets.resize(Assets.landTower.get(3),120,140),Game.getMouseManager().getMouseX() - 120/2, Game.getMouseManager().getMouseY() - 140/2, null);
            }

            if (Game.getMouseManager().isLeftPressed()) {

                System.out.println("! left press");

                // if position of tower valid
                //land tower
               if (map.isLandTower(prX, prY + Assets.t1.getHeight()) && prX > 0 && prX < 1000 && prY > 0 && prY < 1000 && gold >= 5) {
                   if (towerKind == 1) {
                       towers.add(new NormalTower(prX, prY));
                       gold -= 5;
                   } else if (towerKind == 2) {
                       towers.add(new fireTower(prX, prY));
                       gold -= 10;
                   } else if (towerKind == 3) {
                       towers.add(new landTower(prX, prY));
                       gold -= 10;
                   }
               }
               isDragging = false;
            }


        }

        // draw count if have new turn enemy
        if (map.delta <= 8 && map.delta >= 0 && isCount) {
            int x =8 - (int) map.delta;
            g.setColor(Color.red);
            g.drawString(x + "", 60,110);
        }

        // default quit button
        g.drawImage(Assets.resize(Assets.quitButton, 40,80),1190, 660, null);

    }


}
