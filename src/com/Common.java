package com;

import java.awt.Point;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Delayed;

public class Common {

    /**
     * 判断牌型
     *
     * @param list
     * @return
     */
    public static CardType jugdeType(List<Card> list) {
        // 因为之前排序过所以比较好判断
        int len = list.size();//打出去牌的长度; 3,4,5,6,7 长度5
        // 单牌,对子，3不带，4个一样炸弹
        if (len <= 4) { // 如果第一个和最后个相同，说明全部相同
            if (list.size() > 0
                    && Common.getValue(list.get(0)) == Common.getValue(list
                    .get(len - 1))) {
                switch (len) {//判断小于等于四张却相同的牌是什么牌型
                    case 1:
                        return CardType.c1;//如果是一张就是单牌
                    case 2:
                        return CardType.c2;//如果是两张就是对子
                    case 3:
                        return CardType.c3;//三张则是三不带
                    case 4:
                        return CardType.c4;//四张便是炸弹
                }
            }
            // 双王,化为对子返回
            if (len == 2 && Common.getColor(list.get(1)) == 5)//如果长度是2,(?并且第二张牌的值是5就是双王返回对子)
                return CardType.c2;
            // 当第一个和最后个不同时,3带1
            if (len == 4
                    && ((Common.getValue(list.get(0)) == Common.getValue(list
                    .get(len - 2))) || Common.getValue(list.get(1)) == Common
                    .getValue(list.get(len - 1))))
                //aaab和baaa牌型的三带一
                return CardType.c31;//返回三带一
            else {//否则
                return CardType.c0;//返回不能出牌
            }
        }
        // 当5张以上时，连字，3带2，飞机，2顺，4带2等等
        if (len >= 5) {// 现在按相同数字最大出现次数 (如果出的牌长度大于5或等于)
            Card_index card_index = new Card_index();//创建了一个card_index类
            for (int i = 0; i < 4; i++)//for循环,循环了四次
                card_index.a[i] = new Vector<Integer>();//card_index的a属性是一个元素为int型列表的数组
            // 求出各种数字出现频率								（其实可以理解为a是一个二维数组）
            Common.getMax(card_index, list); // a[0,1,2,3]分别表示重复1,2,3,4次的牌
            // 3带2 -----必含重复3次的牌
            if (card_index.a[2].size() == 1 && card_index.a[1].size() == 1
                    //如果对子(两张)有一个,是二维数组里面的长度;[1,2]的长度是2 [1]的长度是一
                    && len == 5)
                return CardType.c32;
            // 4带(单,双)
            if (card_index.a[3].size() == 1 && len == 6)
                //如果炸弹(四张)有一个,那他带单双是无所谓的
                return CardType.c411;
            if (card_index.a[3].size() == 1 && card_index.a[1].size() == 2
                    //如果炸弹(四张)有一个,并且对子(两张)有两个,那么就是四带二
                    //[],[2,3],[],[5]
                    //单张 对子 三张 炸弹
                    && len == 8)
                return CardType.c422;
            // 单连,保证不存在王
            if ((Common.getColor(list.get(0)) != 5)
                    //单连不能有王
                    && (card_index.a[0].size() == len)
                    //并且单张的二维数组的长度需要等于出牌的长度
                    //[3,4,5,6,7],[],[],[](二维数组) [0]的长度等于len
                    && (Common.getValue(list.get(0)) - Common.getValue(list.get(len - 1)) == len - 1))
                    //  排好序的牌是 从左往右 从大到小 list是集合从下标0开始
                    //  Common.getValue(list.get(0))是拿到左边第一张牌名字的数字二
                    //  Common.getValue(list.get(len - 1) 是拿到长度减一右边第一张牌名字的数字二
                    //  (Common.getValue(list.get(0)) - Common.getValue(list.get(len - 1)) == len - 1)
                    //  等于最大的值减最小的值等于他们的长度减一 ;最大10 最小3 长度8 10-3=(8-1)
                    //图片名字的数字二的数字减去点击牌的长度减一等于长度减一
                return CardType.c123;

            // 连对
            if (card_index.a[1].size() == len / 2
                    //对子的长度等于长度除二   [][3,4,5][][] [1].size() = 6/2
                    && len % 2 == 0
                    //同时是长度是一个偶数
                    && len / 2 >= 3
                    //偶数的长度必须大于3等于 aa bb cc 长度等于3
                    && (Common.getValue(list.get(0))
                    //左边最大的数二减去最小的数二等于长度除二减去一
                    - Common.getValue(list.get(len - 1)) == (len / 2 - 1)))
                //大5 小3 等 (3-1)
                return CardType.c1122;
            // 飞机(不带)
            if (card_index.a[2].size() == len / 3
                    //三张的长度等于长度除3   2 == 6/3
                    && (len % 3 == 0)
                    //必须是3个一组
                    && (Common.getValue(list.get(0))
                    //最大的数字二减去
                    - Common.getValue(list.get(len - 1)) == (len / 3 - 1)))
                    //最小的数字二等于长度除3减一        5-4==(6/3-1)
                return CardType.c111222;
            // 飞机带n单,n/2对
            if (card_index.a[2].size() == len / 4
                    //三张的长度等于长度除4  2==(8/2)
                    && ((Integer) (card_index.a[2].get(len / 4 - 1))
                                    //拿到对子最大的数二
                    - (Integer) (card_index.a[2].get(0)) == len / 4 - 1))
                //减去拿到对子最小的数二等于长度除四减一      9 - 8 = (8/4-1)
                return CardType.c11122234;
            //区别list.get(); 是出的牌是 从大到小 从左往右 牌的下标也是0
            //区别card_index.a[?].get(); 是单双三炸的次数里面的是牌的数字二, 从小到大 从左往右 下标也是0 他是以下标来判断的所以小的在前面



            // 飞机带n双
            if (card_index.a[2].size() == len / 5
                    //三张的长度等于长度除5    [][][2,3][] [2]的长度是2
                    && card_index.a[2].size() == len / 5
                    //三张的长度等于长度除5     [][][2,3][] [2]的长度是2
                    && ((Integer) (card_index.a[2].get(len / 5 - 1))
                    //三张最大的数字2减去
                    - (Integer) (card_index.a[2].get(0)) == len / 5 - 1))
                //三张最小的数字2等于长度除五减一    9 - 8 = (10/5-1)
                return CardType.c1112223344;

        }
        return CardType.c0;
    }

    /**
     * 移动效果的函数,用于发牌
     *
     * @param card
     * @param from
     * @param to
     */
    public static void move(Card card, Point from, Point to) {
        if (to.x != from.x) {
            double k = (1.0) * (to.y - from.y) / (to.x - from.x);
            double b = to.y - to.x * k;
            int flag = 0;// 判断向左还是向右移动步幅
            if (from.x < to.x)
                flag = 20;
            else {
                flag = -20;
            }
            for (int i = from.x; Math.abs(i - to.x) > 20; i += flag) {
                double y = k * i + b;// 这里主要用的数学中的线性函数

                card.setLocation(i, (int) y);
                try {
                    Thread.sleep(3); // 延迟，可自己设置
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // 位置校准
        card.setLocation(to);
    }

    /**
     * 对玩家手中的牌 list排序
     *
     * @param list
     */
    public static void order(List<Card> list) {
        Collections.sort(list, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                // TODO Auto-generated method stub
                int a1 = Integer.parseInt(o1.name.substring(0, 1));// 花色
                int a2 = Integer.parseInt(o2.name.substring(0, 1));
                int b1 = Integer.parseInt(o1.name.substring(2, o1.name.length()));// 数值
                int b2 = Integer.parseInt(o2.name.substring(2, o2.name.length()));
                int flag = 0;
                // 如果是王的话
                if (a1 == 5)
                    b1 += 100;
                if (a1 == 5 && b1 == 1)
                    b1 += 50;
                if (a2 == 5)
                    b2 += 100;
                if (a2 == 5 && b2 == 1)
                    b2 += 50;
                // 如果是A或者2
                if (b1 == 1)
                    b1 += 20;
                if (b2 == 1)
                    b2 += 20;
                if (b1 == 2)
                    b1 += 30;
                if (b2 == 2)
                    b2 += 30;
                flag = b2 - b1;
                if (flag == 0)
                    return a2 - a1;
                else {
                    return flag;
                }
            }
        });
    }

    /**
     * 重新定位
     * flag代表电脑1 ,2 或者是我
     *
     * @param m
     * @param list
     * @param flag
     */
    public static void rePosition(Main m, List<Card> list, int flag) {
        Point p = new Point();
        if (flag == 0) {
            p.x = 50;
            p.y = (450 / 2) - (list.size() + 1) * 15 / 2;
        }
        if (flag == 1) {// 我的排序 _y=450 width=830
            p.x = (800 / 2) - (list.size() + 1) * 21 / 2;
            p.y = 450;
        }
        if (flag == 2) {
            p.x = 700;
            p.y = (450 / 2) - (list.size() + 1) * 15 / 2;
        }
        int len = list.size();
        for (int i = 0; i < len; i++) {
            Card card = list.get(i);
            Common.move(card, card.getLocation(), p);
            m.container.setComponentZOrder(card, 0);
            if (flag == 1)
                p.x += 21;
            else
                p.y += 15;
        }
    }

    /**
     * 地主牌权值，看[电脑玩家]是否抢地主
     *
     * @param list
     * @return
     */
    public static int getScore(List<Card> list) {
        int count = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            Card card = list.get(i);
            if (card.name.substring(0, 1).equals("5")) {
                // System.out.println(card.name.substring(0, 1));
                count += 5;
            }
            if (card.name.substring(2, card.name.length()).equals("2")) {
                // System.out.println(2);
                count += 2;
            }
        }
        return count;
    }

    /**
     * 返回花色
     *
     * @param card
     * @return
     */
    public static int getColor(Card card) {
        //牌名字的第一个下标转成整数
        return Integer.parseInt(card.name.substring(0, 1));
    }

    // 返回值
    public static int getValue(Card card) {
        int i = Integer.parseInt(card.name.substring(2));
        //i等于牌名字截取从第三个下标开始(images)转成整数
        if (card.name.substring(2).equals("2"))
            //牌名字第三个和2比较如果是等于2,i就加等13
            i += 13;//13
        if (card.name.substring(2).equals("1"))
            //牌名字第三个和2比较如果是等于1,i就加等13
            i += 13;
        if (Common.getColor(card) == 5)
            //截取第一个下标,如果是5就是王,就i加等2
            i += 2;// 是王
        return i;
        //返回i
    }

    // 得到最大相同数
    public static void getMax(Card_index card_index, List<Card> list) {//方法.1:返回一个Card_index的对象,2:返回一个List的接口(出的牌)
        int count[] = new int[14];// 1-13各算一种,王算第14种
        //(大小王),2,3,4,5,6,7,8,9,10,J,Q,K,A总共14张
        for (int i = 0; i < 14; i++)
            count[i] = 0;//把count数组中的每个下标赋值为0
        for (int i = 0, len = list.size(); i < len; i++) {//
            if (Common.getColor(list.get(i)) == 5)
                count[0]++; // 王 如果拿到的值是5就是王就把count下标的0加一
            else
                count[Common.getValue(list.get(i)) - 1]++;//否则
            //数组的下标加一
        }
        for (int i = 0; i < 14; i++) {
            switch (count[i]) {
            //这里是相同数的数量
                case 1:
                    card_index.a[0].add(i + 1);
                    //出现了一次
                    break;
                case 2:
                    card_index.a[1].add(i + 1);
                    //出现了二次
                    break;
                case 3:
                    card_index.a[2].add(i + 1);
                    //出现了三次
                    break;
                case 4:
                    card_index.a[3].add(i + 1);
                    //出现了四次
                    break;
            }
        }
    }

    // 拆牌
    public static Model getModel(List<Card> list, int[] orders) {
        // 先复制一个list
        List list2 = new Vector<Card>(list);
        //复制打出的牌
        Model model = new Model();

        for (int i = 0; i < orders.length; i++)
            showOrders(orders[i], list2, model);
        return model;
    }

    // 拆连子
    public static void get123(List<Card> list, Model model) {
        List<Card> del = new Vector<Card>();// 要删除的Cards
        if (list.size() > 0
                && (Common.getValue(list.get(0)) < 7 || Common.getValue(list
                .get(list.size() - 1)) > 10))
            return;
        if (list.size() < 5)
            return;
        // 先要把所有不重复的牌归为一类，防止3带，对子影响
        List<Card> list2 = new Vector<Card>();
        List<Card> temp = new Vector<Card>();
        List<Integer> integers = new Vector<Integer>();
        for (Card card : list2) {
            if (integers.indexOf(Common.getValue(card)) < 0) {
                integers.add(Common.getValue(card));
                temp.add(card);
            }
        }
        Common.order(temp);
        for (int i = 0, len = temp.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (Common.getValue(temp.get(i)) - Common.getValue(temp.get(j)) == j
                        - i) {
                    k = j;
                }
            }
            if (k - i >= 4) {
                String s = "";
                for (int j = i; j < k; j++) {
                    s += temp.get(j).name + ",";
                    del.add(temp.get(j));
                }
                s += temp.get(k).name;
                del.add(temp.get(k));
                model.a123.add(s);
                i = k;
            }
        }
        list.removeAll(del);
    }

    // 拆双顺
    public static void getTwoTwo(List<Card> list, Model model) {
        List<String> del = new Vector<String>();// 要删除的Cards
        // 从model里面的对子找
        List<String> l = model.a2;
        if (l.size() < 3)
            return;
        Integer s[] = new Integer[l.size()];
        for (int i = 0, len = l.size(); i < len; i++) {
            String[] name = l.get(i).split(",");
            s[i] = Integer.parseInt(name[0].substring(2, name[0].length()));
        }
        // s0,1,2,3,4 13,9,8,7,6
        for (int i = 0, len = l.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (s[i] - s[j] == j - i)
                    k = j;
            }
            if (k - i >= 2)// k=4 i=1
            {// 说明从i到k是连队
                String ss = "";
                for (int j = i; j < k; j++) {
                    ss += l.get(j) + ",";
                    del.add(l.get(j));
                }
                ss += l.get(k);
                model.a112233.add(ss);
                del.add(l.get(k));
                i = k;
            }
        }
        l.removeAll(del);
    }

    // 拆飞机
    public static void getPlane(List<Card> list, Model model) {
        List<String> del = new Vector<String>();// 要删除的Cards
        // 从model里面的3带找
        List<String> l = model.a3;
        if (l.size() < 2)
            return;
        Integer s[] = new Integer[l.size()];
        for (int i = 0, len = l.size(); i < len; i++) {
            String[] name = l.get(i).split(",");
            s[i] = Integer.parseInt(name[0].substring(2, name[0].length()));
        }
        for (int i = 0, len = l.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (s[i] - s[j] == j - i)
                    k = j;
            }
            if (k != i) {// 说明从i到k是飞机
                String ss = "";
                for (int j = i; j < k; j++) {
                    ss += l.get(j) + ",";
                    del.add(l.get(j));
                }
                ss += l.get(k);
                model.a111222.add(ss);
                del.add(l.get(k));
                i = k;
            }
        }
        l.removeAll(del);
    }

    // 拆炸弹
    public static void getBoomb(List<Card> list, Model model) {
        List<Card> del = new Vector<Card>();// 要删除的Cards
        if (list.size() < 1)
            return;
        // 王炸
        if (list.size() >= 2 && Common.getColor(list.get(0)) == 5
                && Common.getColor(list.get(1)) == 5) {
            model.a4.add(list.get(0).name + "," + list.get(1).name); // 按名字加入
            del.add(list.get(0));
            del.add(list.get(1));
        }
        // 如果王不构成炸弹咋先拆单
        if (Common.getColor(list.get(0)) == 5
                && Common.getColor(list.get(1)) != 5) {
            del.add(list.get(0));
            model.a1.add(list.get(0).name);
        }
        list.removeAll(del);
        // 一般的炸弹
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 3 < len
                    && Common.getValue(list.get(i)) == Common.getValue(list
                    .get(i + 3))) {
                String s = list.get(i).name + ",";
                s += list.get(i + 1).name + ",";
                s += list.get(i + 2).name + ",";
                s += list.get(i + 3).name;
                model.a4.add(s);
                for (int j = i; j <= i + 3; j++)
                    del.add(list.get(j));
                i = i + 3;
            }
        }
        list.removeAll(del);
    }

    // 拆3带
    public static void getThree(List<Card> list, Model model) {
        List<Card> del = new Vector<Card>();// 要删除的Cards
        // 连续3张相同
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 2 < len
                    && Common.getValue(list.get(i)) == Common.getValue(list
                    .get(i + 2))) {
                String s = list.get(i).name + ",";
                s += list.get(i + 1).name + ",";
                s += list.get(i + 2).name;
                model.a3.add(s);
                for (int j = i; j <= i + 2; j++)
                    del.add(list.get(j));
                i = i + 2;
            }
        }
        list.removeAll(del);
    }

    // 拆对子
    public static void getTwo(List<Card> list, Model model) {
        List<Card> del = new Vector<Card>();// 要删除的Cards
        // 连续2张相同
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 1 < len
                    && Common.getValue(list.get(i)) == Common.getValue(list
                    .get(i + 1))) {
                String s = list.get(i).name + ",";
                s += list.get(i + 1).name;
                model.a2.add(s);
                for (int j = i; j <= i + 1; j++)
                    del.add(list.get(j));
                i = i + 1;
            }
        }
        list.removeAll(del);
    }

    // 拆单牌
    public static void getSingle(List<Card> list, Model model) {
        List<Card> del = new Vector<Card>();// 要删除的Cards
        // 1
        for (int i = 0, len = list.size(); i < len; i++) {
            model.a1.add(list.get(i).name);
            del.add(list.get(i));
        }
        list.removeAll(del);
    }

    // 隐藏之前出过的牌
    public static void hideCards(List<Card> list) {
        for (int i = 0, len = list.size(); i < len; i++) {
            list.get(i).setVisible(false);
        }
    }

    // 检查牌的是否能出
    public static int checkCards(List<Card> c, List<Card>[] current, Main m) {
        // 找出当前最大的牌是哪个电脑出的,c是点选的牌
        List<Card> currentlist;
        if (m.time[0].getText().equals("不要"))
            currentlist = current[2];
        else
            currentlist = current[0];
        CardType cType = Common.jugdeType(c);
        CardType cType2 = Common.jugdeType(currentlist);
        // 如果张数不同直接过滤
        if (cType != CardType.c4 && c.size() != currentlist.size())
            return 0;
        // 比较我的出牌类型
        if (cType != CardType.c4 && Common.jugdeType(c) != Common.jugdeType(currentlist)) {

            return 0;
        }
        // 比较出的牌是否要大
        // 我是炸弹
        if (cType == CardType.c4) {
            if (c.size() == 2)
                return 1;
            if (cType2 != CardType.c4) {
                return 1;
            }
        }

        // 单牌,对子,3带,4炸弹
        if (cType == CardType.c1 || cType == CardType.c2
                || cType == CardType.c3 || cType == CardType.c4) {
            if (Common.getValue(c.get(0)) <= Common
                    .getValue(currentlist.get(0))) {
                return 0;
            } else {
                return 1;
            }
        }
        // 顺子,连队，飞机裸
        if (cType == CardType.c123 || cType == CardType.c1122
                || cType == CardType.c111222) {
            if (Common.getValue(c.get(0)) <= Common
                    .getValue(currentlist.get(0)))
                return 0;
            else
                return 1;
        }
        // 按重复多少排序
        // 3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的
        if (cType == CardType.c31 || cType == CardType.c32
                || cType == CardType.c411 || cType == CardType.c422
                || cType == CardType.c11122234 || cType == CardType.c1112223344) {
            List<Card> a1 = Common.getOrder2(c); // 我出的牌
            List<Card> a2 = Common.getOrder2(currentlist);// 当前最大牌
            if (Common.getValue(a1.get(0)) < Common.getValue(a2.get(0)))
                return 0;
        }
        return 1;
    }

    // 按照重复次数排序
    public static List getOrder2(List<Card> list) {
        List<Card> list2 = new Vector<Card>(list);
        List<Card> list3 = new Vector<Card>();
        List<Integer> list4 = new Vector<Integer>();
        int len = list2.size();
        int a[] = new int[20];
        for (int i = 0; i < 20; i++)
            a[i] = 0;
        for (int i = 0; i < len; i++) {
            a[Common.getValue(list2.get(i))]++;
        }
        int max = 0;
        for (int i = 0; i < 20; i++) {
            max = 0;
            for (int j = 19; j >= 0; j--) {
                if (a[j] > a[max])
                    max = j;
            }

            for (int k = 0; k < len; k++) {
                if (Common.getValue(list2.get(k)) == max) {
                    list3.add(list2.get(k));
                }
            }
            list2.remove(list3);
            a[max] = 0;
        }
        return list3;
    }

    //拆牌循序
    public static void showOrders(int i, List<Card> list, Model model) {
        switch (i) {
            case 1:
                Common.getSingle(list, model);
                break;
            case 2:
                Common.getTwo(list, model);
                Common.getTwoTwo(list, model);
                break;
            case 3:
                Common.getThree(list, model);
                Common.getPlane(list, model);
                break;
            case 4:
                Common.getBoomb(list, model);
                break;
            case 5:
                Common.get123(list, model);
                break;
        }
    }
}

class Card_index {
    List a[] = new Vector[4];// 单张
}