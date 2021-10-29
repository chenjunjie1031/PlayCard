package com;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Main extends JFrame implements ActionListener {

    public Container container = null;// 定义容器
    JMenuItem start, exit, about;// 定义菜单按钮
    JButton landlord[] = new JButton[2];// 抢地主按钮
    JButton publishCard[] = new JButton[2];// 出牌按钮
    int dizhuFlag;// 地主标志
    int turn;
    JLabel dizhu; // 地主图标
    List<Card> currentList[] = new Vector[3]; // 当前的出牌
    List<Card> playerList[] = new Vector[3]; // 3个玩家(列表)

    List<Card> lordList;// 地主牌

    Card card[] = new Card[56]; // 定义54张牌
    JTextField time[] = new JTextField[3]; // 计时器
    Time t; // 定QQ时器（线程）
    boolean nextPlayer = false; // 转换角色

    public Main() {

        Init();// 初始化窗体
        SetMenu();// 创建菜单 按钮(抢地主，发牌,计时器)
        this.setVisible(true);

        CardInit();// 发牌
        getLord(); // 发完牌开始抢地主
        time[1].setVisible(true);
        // 线程安全性,把非主线程的UI控制放到里面
        t = new Time(this, 15);// 从15开始倒计时
        t.start();
    }

    /**
     * 初始化窗体
     */
    public void Init() {

        this.setTitle("斗地主游戏---by 小柒,qq361106306");
        this.setSize(830, 620);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(getOwner()); // 屏幕居中

        container = this.getContentPane();
        container.setLayout(null);
        container.setBackground(new Color(0, 175, 250)); // 背景为蓝色
    }

    /**
     * 创建菜单 功能按钮
     */
    public void SetMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu game = new JMenu("游戏");
        JMenu help = new JMenu("帮助");

        start = new JMenuItem("新游戏");
        exit = new JMenuItem("退出");
        about = new JMenuItem("关于");

        start.addActionListener(this);
        exit.addActionListener(this);
        about.addActionListener(this);

        game.add(start);
        game.add(exit);
        help.add(about);

        jMenuBar.add(game);
        jMenuBar.add(help);
        this.setJMenuBar(jMenuBar);

        landlord[0] = new JButton("抢地主");
        landlord[1] = new JButton("不     抢");
        publishCard[0] = new JButton("出牌");
        publishCard[1] = new JButton("不要");
        for (int i = 0; i < 2; i++) {
            publishCard[i].setBounds(320 + i * 100, 400, 60, 20);
            landlord[i].setBounds(320 + i * 100, 400, 75, 20);
            container.add(landlord[i]);
            landlord[i].addActionListener(this);
            landlord[i].setVisible(false);
            container.add(publishCard[i]);
            publishCard[i].setVisible(false);
            publishCard[i].addActionListener(this);
        }
        for (int i = 0; i < 3; i++) {
            time[i] = new JTextField("倒计时:");
            time[i].setEditable(false);
            time[i].setVisible(false);
            container.add(time[i]);
        }
        time[0].setBounds(140, 230, 60, 20);//三个计时器的大小
        time[1].setBounds(374, 360, 60, 20);
        time[2].setBounds(620, 230, 60, 20);

        for (int i = 0; i < 3; i++) {
            currentList[i] = new Vector<Card>();
        }
    }

    /**
     * 洗牌发牌
     */
    public void CardInit() {

        int count = 1;
        // 初始化牌
        for (int i = 1; i <= 5; i++) {//5是图片的花色一共五种(images)
            for (int j = 1; j <= 13; j++) {//13是A,2,3,...Q,K的牌(images)
                if ((i == 5) && (j > 2))//5是两个地主牌
                    break;//跳出牌发完了
                else {
                    card[count] = new Card(this, i + "-" + j, false);
                    //传入一共Main对象,和一个字符串(牌的名字),和布尔值正反面(这里全是false所以全是反面)
                    card[count].setLocation(350, 50);
                    //将组件移到新位置，用x 和 y 参数来指定新位置的左上角
                    container.add(card[count]);
                    //用来装java对象的瓶子，预处理对象的时候往里面丢啊丢，用的时候抠啊抠。
                    count++;
                    //数组下标
                }
            }
        }

        Random random = new Random();//随机数
        // 打乱顺序
        for (int i = 0; i < 100; i++) {
            int a = random.nextInt(54) + 1;
            int b = random.nextInt(54) + 1;
            //随机1到54的数

            Card k = card[a];//2牌互换
            card[a] = card[b];
            card[b] = k;
            //冒泡(随机打乱顺序100次)
        }

        // 开始发牌
        for (int i = 0; i < 3; i++)
            playerList[i] = new Vector<Card>(); // 玩家牌
        //创建了3个集合里面是3个玩家的牌
        lordList = new Vector<Card>();// 地主牌三张
        int t = 0;
        for (int i = 1; i <= 54; i++) {
            if (i >= 52) { // 地主牌
                Common.move(card[i], card[i].getLocation(), new Point(300 + (i - 52) * 80, 10));
                //调用Common的方法move(传入一个Card类,2个Point类的位置x,y)
                lordList.add(card[i]);
                //把地主牌的三张放在lordList集合中
                continue;
                //满足某种条件则跳出本层循环体
            }

            switch ((t++) % 3) {
                //如果t除3余0,1,2
                case 0:
                    // 左边玩家
                    Common.move(card[i], card[i].getLocation(), new Point(50, 60 + i * 5));
                    //调用move方法
                    playerList[0].add(card[i]);
                    //把牌加入了List容器中,[0]是左玩家的牌
                    break;
                case 1:
                    // 我
                    Common.move(card[i], card[i].getLocation(), new Point(180 + i * 7, 450));
                    playerList[1].add(card[i]);
                    //把牌加入了List容器中,[0]是本玩家的牌
                    card[i].turnFront(); // 显示正面
                    break;
                case 2:
                    // 右边玩家
                    Common.move(card[i], card[i].getLocation(), new Point(700, 60 + i * 5));
                    playerList[2].add(card[i]);
                    //把牌加入了List容器中,[0]是右玩家的牌
                    break;
            }
            // card[i].turnFront(); //显示正面
            container.setComponentZOrder(card[i], 0);
        }
        // 发完牌排序，从大到小
        for (int i = 0; i < 3; i++) {
            //把三个玩家的牌都排序
            Common.order(playerList[i]);
            //此方法返回一个比较器，该比较器对Comparable对象施加自然排序。
            Common.rePosition(this, playerList[i], i);// 重新定位
            //重新定位 flag代表电脑1 ,2 或者是我
        }
        dizhu = new JLabel(new ImageIcon("images/dizhu.gif"));
        //拿到地主图标
        dizhu.setVisible(false);
        //setVisible(boolean)方法是用来显示/隐藏GUI组件的。
        //需要显示则使用true，需要隐藏则使用false。这里是地主图标的显示,谁抢到地主后地主的标志到哪个人身上
        dizhu.setSize(40, 40);
        //地主标志的大小
        //setSize(w,h);大小
        container.add(dizhu);
        //往容器里面添加这个地主图标
    }

    /**
     * 抢地主
     */
    public void getLord() {
        for (int i = 0; i < 2; i++)
            landlord[i].setVisible(true);
        //抢地主的按钮显示setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //这是接口ActionListener里面定义的一个抽象方法，所有实现这个接口的类都要重写这个方法。
        //一般情况下，这是在编写GUI程序时，组件发生“有意义”的事件时会调用这个方法，
        // 比如按钮被按下，文本框内输入回车时都会触发这个事件，然后调用你编写的事件处理程序。
        if (e.getSource() == exit) {
            //返回的当前动作所指向的对象，包含对象的所有信息。()
            //如果点击的是exit(退出),那么执行下面的
            this.dispose();
            //是关闭窗体，关释放资源
            System.exit(0);
            //status为0时为正常退出程序，也就是结束当前正在运行中的java虚拟机。
            //不用这个无法退出
        }
        if (e.getSource() == about) {
            //如果点击的是about(关于),那么执行下面的
            JOptionPane.showMessageDialog(this, "你瞅啥");
            //这个方法会弹出信息的提示框，默认带有一个信息图标null也可以
        }
        if (e.getSource() == start) {
            //如果点击的是start(新游戏),那么执行下面的
//			 this.restart();
            this.dispose();
            //是关闭窗体，关释放资源
            new Main();
            //重新创建Main对象
        }
        if (e.getSource() == landlord[0]) {
            //如果点击了抢地主就执行下面的
            time[1].setText("抢地主");
            //点击了计时器就变成抢地主
            t.isRun = false; // 时钟终结
            //抢了地主开始抢地主的倒计时就结束了进入出牌的倒计时
        }
        if (e.getSource() == landlord[1]) {
            time[1].setText("不抢");
            //点击了计时器就变成不抢
            t.isRun = false; // 时钟终结
            //不抢地主开始进入出牌的倒计时
        }
        // 如果是不要
        if (e.getSource() == publishCard[1]) {
            //如果点击的是不要就运行下面的
            this.nextPlayer = true;
            //就跳过当前出牌计时器
            currentList[1].clear();
            //就删除玩家集合1中的出牌
            time[1].setText("不要");
            //计时器显示不要
        }
        // 如果是出牌按钮
        if (e.getSource() == publishCard[0]) {
            //如果点击的是出牌就运行下面的
            List<Card> c = new Vector<Card>();
            //创建了一个Vector的接口
            // 点选出牌
            for (int i = 0; i < playerList[1].size(); i++) {
                Card card = playerList[1].get(i);
                if (card.clicked) {
                    c.add(card);
                    //把点击的牌一个一个遍历到c集合中
                }
            }
            int flag = 0;
            // 如果我主动出牌
            if (time[0].getText().equals("不要") && time[2].getText().equals("不要")) {
                //如果人机1和2都不要的话那么运行下面的
                if (Common.jugdeType(c) != CardType.c0)
                    //如果我不能"不要"
                    flag = 1;// 表示可以出牌
            } else {// 如果我跟牌
                flag = Common.checkCards(c, currentList, this);
                //集合,当前出的牌,当前对象
            }

            // 判断是否符合出牌
            if (flag == 1) {
                //如果可以出牌
                currentList[1] = c;
                //玩家出的牌放在c集合中
                playerList[1].removeAll(currentList[1]);// 移除走的牌
                //在玩家牌的集合中移除出 出牌集合中的牌
                // 定位出牌
                Point point = new Point();
                //一个Point的类,有x和y轴
                point.x = (770 / 2) - (currentList[1].size() + 1) * 15 / 2;
                //定义了point的x轴(出的牌在牌桌上的位置)
                point.y = 300;
                //定义了point的y轴(出的牌在牌桌上的位置)
                for (int i = 0, len = currentList[1].size(); i < len; i++) {
                    //遍历了我出的牌
                    Card card = currentList[1].get(i);
                    //把我每次出的牌给了card   Location
                    Common.move(card, card.getLocation(), point);
                    //移动效果的函数,用于发牌 Common类的move方法
                    point.x += 15;
                    //牌的上下+=15是正的
                }

                // 抽完牌后重新整理牌
                Common.rePosition(this, playerList[1], 1);
                //重新定位 flag代表电脑1 ,2 或者是我 playerList[1]是整理我出后的手牌
                time[1].setVisible(true);
                //需要显示则使用true，需要隐藏则显示false。
                this.nextPlayer = true;
                //需要跳过此玩家出牌(我出了牌的条件上)   ture:跳过  false:不跳过

            }

        }
    }

    public static void main(String args[]) {
        new Main();
        //创建Main类
    }

}
