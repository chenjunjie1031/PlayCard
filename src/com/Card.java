package com;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Card extends JLabel implements MouseListener{
	//						位置				鼠标事件监听MouseListener 1、鼠标按下 、松开 、进入、移除等时发生动作事件

	Main main;//Main类的引用
	String name;//图片url名字
	boolean up;//是否正反面
	boolean canClick=false;//是否可被点击
	boolean clicked=false;//是否点击过
	
	public Card(Main m,String name,boolean up){
		this.main=m;
		this.name=name;
		this.up=up;
	    if(this.up)//如果是真就调用正面方法
	    	this.turnFront();
	    else {
			this.turnRear();//如果是假就调用反面方法
		}
		this.setSize(71, 96);//设置窗口的长宽
		this.setVisible(true);//可以运行开始画图了
		this.addMouseListener(this);//鼠标监听
	}
	/**
	 * 正面
	 */
	public void turnFront() {
		this.setIcon(new ImageIcon("images/" + name + ".gif"));
		//new了一个对象,加上images的图片和名字和.gif图;
		this.up = true;
	}
	
	/**
	 * 反面
	 */
	public void turnRear() {
		this.setIcon(new ImageIcon("images/rear.gif"));
		//加载背面的卡片
		this.up = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void mousePressed(MouseEvent e) {
		if(canClick){//如果可以被点击
			Point from=this.getLocation();//返回一个存取当前鼠标窗口坐标的x和y的point对象。
			int step; //移动的距离
			if(clicked)//如果被点击过了
				step=-20;//向上移动20
			else {
				step=20;//否则复原位置
			}
			clicked=!clicked; //反向
			//当被选中的时候，向前移动一步/后退一步
			Common.move(this,from,new Point(from.x,from.y-step));
		}
	}
}
