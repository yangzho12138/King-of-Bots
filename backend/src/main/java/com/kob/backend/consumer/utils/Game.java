package com.kob.backend.consumer.utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

// 地图同步逻辑——地图需要在后端生成后发送给匹配的两个前端
public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g; // 1 - wall, 0 - road
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        this.playerA = new Player(idA, this.rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, 1, this.cols - 2, new ArrayList<>());
    }

    public Player getPlayerA(){
        return this.playerA;
    }

    public Player getPlayerB(){
        return this.playerB;
    }

    public int[][] getG(){
        return g;
    }

    // 检查source与destination之间是否连通 FloodFill
    private boolean check_connectivity(int sx, int sy, int tx, int ty){
        if(sx == tx && sy == ty)
            return true;
        g[sx][sy] = 1;

        for(int i = 0; i < 4; i++){
            int x = sx + dx[i], y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0){
                if(check_connectivity(x, y, tx, ty)){
                    g[sx][sy] = 0; // 恢复现场
                    return true;
                }
            }
        }
        g[sx][sy] = 0; // 恢复现场
        return false;
    }

    private boolean draw(){
        for(int i = 0; i < this.rows; i++)
            for(int j = 0; j < this.cols; j++)
                g[i][j] = 0;

        for(int r = 0; r < this.rows; r++)
            g[r][0] = g[r][this.cols - 1] = 1;

        for(int c = 0; c < this.cols; c++)
            g[0][c] = g[this.rows - 1][c] = 1;

        // 生成障碍物
        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count; i++){
            for(int j = 0; j < 1000; j++){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 -c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap(){
        for(int i=0; i < 1000; i++){
            if(draw())
                break;
        }
    }

    // Thread receiving message from Clients will modify the nextStep of A and B
    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        }finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    // waiting for the next step operation
    // Thread judging the movement will read the nextStep of A and B
    // Two Threads will read/write the same variable
    private boolean nextStep(){
        lock.lock();
        try{
            if(nextStepA != null || nextStepB != null) // get any input of any players
                return true;
        }finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public void run() {

    }
}
