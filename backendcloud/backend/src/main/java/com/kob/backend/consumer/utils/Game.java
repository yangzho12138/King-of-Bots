package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

// 地图同步逻辑——地图需要在后端生成后发送给匹配的两个前端
// 胜负判断逻辑——后端判定
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
    private String status = "playing"; // playing -> finished
    private String loser = ""; // all, A, B

    private long lastOpTimeA = 0; // 不允许操作太快，设置间隔时间
    private long lastOpTimeB = 0;
    private double gapTime = 0.5; // 0.5s

    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB, Bot botA, Bot botB){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";

        if(botA != null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }

        if(botB != null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        this.playerA = new Player(idA, botIdA, botCodeA, this.rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, botIdB, botCodeB, 1, this.cols - 2, new ArrayList<>());
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
        long opTimeA = System.currentTimeMillis() / 1000;
        if(opTimeA - lastOpTimeA >= gapTime){
            lock.lock();
            try {
                this.nextStepA = nextStepA;
            }finally {
                lock.unlock();
            }
            lastOpTimeA = System.currentTimeMillis() / 1000;
        }
    }

    public void setNextStepB(Integer nextStepB){
        long opTimeB = System.currentTimeMillis() / 1000;
        if(opTimeB - lastOpTimeB >= gapTime){
            lock.lock();
            try {
                this.nextStepB = nextStepB;
            }finally {
                lock.unlock();
            }
            lastOpTimeB = System.currentTimeMillis() / 1000;
        }
    }

    // get the game situation in live -> String
    private String getInput(Player player){
        Player me, you;
        if(playerA.getId().equals(player.getId())){
            me = playerA;
            you = playerB;
        }else{
            me = playerB;
            you = playerA;
        }
        // compress state --> map#我的起始坐标x#我的起始坐标y#(我的操作序列)#你的起始坐标x#你的起始坐标y#(你的操作序列) 因为操作序列可能为空，所以用()括起来
        return getMapString() + "#" + me.getSx() + "#" + me.getSy() + "#(" + me.getStepsString() +")#" + you.getSx() + "#" + you.getSy() + "#(" + me.getStepsString() +")";
    }

    private void sendBotCode(Player player){
        if(player.getBotId() == -1) // manual
            return ;
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input",getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    // waiting for the next step operation
    // Thread judging the movement will read the nextStep of A and B
    // Two Threads will read/write the same variable
    private void nextStep(){
        sendBotCode(playerA);
        sendBotCode(playerB);
        for(int i = 0; i < 500; i++) { // get nothing in 10s --> game end
            try {
                Thread.sleep(200);
                lock.lock();
                try{
                    if(nextStepA != null || nextStepB != null) { // get any input of any players
                        if(nextStepA != null)
                            playerA.getSteps().add(nextStepA);
                        if(nextStepB != null)
                            playerB.getSteps().add(nextStepB);
                        return ;
                    }
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendAllMessage(String message){
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(WebSocketServer.users.get(playerB.getId()) != null)
        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendMove(){
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event","move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB =  null;
        }finally {
            lock.unlock();
        }
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n-1);
        if(g[cell.x][cell.y] == 1) // wall
            return false;

        for(int i = 0; i < cellsA.size() - 1; i ++){ // A是否撞上了自己的身体
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }

        for(int i = 0; i < cellsB.size() - 1; i ++){ // A是否撞上了B的身体
            if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }

        return true;
    }

    private void judge(){ // judge whether the next step of 2 players is valid
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);

        if(!validA || !validB){
            status = "finished";

            if(!validA && !validB)
                loser = "all";
            else if(!validA)
                loser = "A";
            else
                loser = "B";
        }
    }

    // send game result to 2 players
    private void sendResult(){
        JSONObject resp = new JSONObject();
        resp.put("event","result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }


    private void saveToDatabase(){
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }


    @Override
    public void run() {
        for(int i = 0; i < 1000; i++){ // ensure the game will end within 1000 loops —— avoid endless loop
            nextStep(); // update the move
            if(nextStepA != null || nextStepB != null){
                judge();
                if(status.equals("playing")){
                    sendMove();
                }else{
                    sendResult();
                    break;
                }
            }else{ // A or B not move -- game over
                status = "finished";
                lock.lock();
                try {
                    if(nextStepA == null && nextStepB == null){
                        if(lastOpTimeA < lastOpTimeB)
                            loser = "A";
                        else if(lastOpTimeA > lastOpTimeB)
                            loser = "B";
                        else
                            loser = "all";
                    }
                    else if(nextStepA == null)
                        loser = "A";
                    else
                        loser = "B";
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
